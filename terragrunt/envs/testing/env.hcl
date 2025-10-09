# This file contains all the environment-specific variables for the 'testing' environment.
# It is loaded by the terragrunt.hcl in the same directory.
locals {
  # The AWS region where the infrastructure will be deployed.
  aws_region = "eu-north-1"

  # A list of Availability Zones within the specified region.
  # Using multiple AZs provides high availability for our subnets.
  availability_zones = ["eu-north-1a", "eu-north-1b", "eu-north-1c"]

  # A unique name for the environment, used for tagging and identification.
  environment = "testing"
  # A unique name for the VPC resource.
  vpc_name    = "testing-vpc"

  # Unique IP address range for this VPC to avoid conflicts with other VPCs (e.g., pre-prod).
  vpc_cidr_block           = "10.10.0.0/16"
  public_subnet_cidr_blocks  = ["10.10.1.0/24", "10.10.2.0/24", "10.10.3.0/24"]
  private_subnet_cidr_blocks = ["10.10.101.0/24", "10.10.102.0/24", "10.10.103.0/24"]
}