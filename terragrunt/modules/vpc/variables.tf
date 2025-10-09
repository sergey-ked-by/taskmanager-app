# terragrunt/modules/vpc/variables.tf

variable "aws_region" {
  description = "AWS region for deployment"
  type        = string
}

variable "environment" {
  description = "The name of the environment: (e.g., testing, pre-prod)."
  type        = string
}

variable "vpc_name" {
  description = "The name for the VPC."
  type        = string
}

variable "vpc_cidr_block" {
  description = "The main CIDR block for the VPC."
  type        = string
  default     = "10.0.0.0/16"
}

variable "availability_zones" {
  description = "A list of Availability Zones to create subnets in."
  type        = list(string)
}

variable "public_subnet_cidr_blocks" {
  description = "A list of CIDR blocks for the public subnets."
  type        = list(string)
}

variable "private_subnet_cidr_blocks" {
  description = "A list of CIDR blocks for the private subnets."
  type        = list(string)
}