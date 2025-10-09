# This file defines the core resources for a Virtual Private Cloud (VPC) in AWS.
# This module is designed to be reusable for different environments (e.g., testing, pre-prod).

# Specifies the required Terraform providers and their versions.
# Pinning versions is a best practice to ensure consistent behavior.
terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "5.99.1"
    }
  }
}

# Configures the AWS provider with the region specified by an input variable.
provider "aws" {
  region = var.aws_region
}

# --- Core VPC Resource ---
# Creates the main VPC, which is an isolated network environment in AWS.
resource "aws_vpc" "main" {
  cidr_block = var.vpc_cidr_block

  tags = {
    Name = var.vpc_name
  }
}

# --- Internet Connectivity for Public Subnets ---
# An Internet Gateway allows resources in the public subnets to communicate with the internet.
resource "aws_internet_gateway" "main" {
  vpc_id = aws_vpc.main.id

  tags = {
    Name = "${var.vpc_name}-igw"
  }
}

# --- Subnets ---
# Creates a set of public subnets. Resources in these subnets can be directly accessible from the internet.
# `for_each` creates one subnet for each CIDR block defined in the input variables.
resource "aws_subnet" "public" {
  for_each                = { for i, cidr in var.public_subnet_cidr_blocks : i => cidr }
  vpc_id                  = aws_vpc.main.id
  cidr_block              = each.value
  availability_zone       = var.availability_zones[each.key]
  # This setting ensures that instances launched in this subnet get a public IP address by default.
  map_public_ip_on_launch = true

  tags = {
    Name = "${var.vpc_name}-public-subnet-${each.key + 1}"
  }
}

# Creates a set of private subnets. Resources here are not directly accessible from the internet, providing a layer of security.
resource "aws_subnet" "private" {
  for_each          = { for i, cidr in var.private_subnet_cidr_blocks : i => cidr }
  vpc_id            = aws_vpc.main.id
  cidr_block        = each.value
  availability_zone = var.availability_zones[each.key]

  tags = {
    Name = "${var.vpc_name}-private-subnet-${each.key + 1}"
  }
}

# --- NAT Gateway (Temporarily Disabled) ---
# A NAT Gateway would allow instances in private subnets to initiate outbound traffic to the internet (e.g., for updates),
# while still preventing inbound traffic from the internet.
# These resources are commented out due to SCP permission restrictions in the user's AWS account.

# EIP (Elastic IP) - a static IP address for the NAT Gateway
/*
resource "aws_eip" "nat" {
  domain = "vpc"

  tags = {
    Name = "${var.vpc_name}-nat-eip"
  }
}
*/

# The NAT Gateway itself (placed in the first public subnet)
/*
resource "aws_nat_gateway" "main" {
  allocation_id = aws_eip.nat.id
  subnet_id     = values(aws_subnet.public)[0].id

  tags = {
    Name = "${var.vpc_name}-nat-gw"
  }
  depends_on = [aws_internet_gateway.main]
}
*/

# --- Routing ---
# Route table for public subnets. It directs all outbound traffic (0.0.0.0/0) to the Internet Gateway.
resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.main.id
  }

  tags = {
    Name = "${var.vpc_name}-public-rt"
  }
}

# Route table for private subnets.
# The route to the NAT Gateway is commented out, so these subnets are currently fully isolated.
resource "aws_route_table" "private" {
  vpc_id = aws_vpc.main.id

  /*
  route {
    cidr_block     = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.main.id
  }
  */

  tags = {
    Name = "${var.vpc_name}-private-rt"
  }
}

# --- Route Table Associations ---
# Associates the public route table with all public subnets.
resource "aws_route_table_association" "public" {
  for_each       = aws_subnet.public
  subnet_id      = each.value.id
  route_table_id = aws_route_table.public.id
}

# Associates the private route table with all private subnets.
resource "aws_route_table_association" "private" {
  for_each       = aws_subnet.private
  subnet_id      = each.value.id
  route_table_id = aws_route_table.private.id
}