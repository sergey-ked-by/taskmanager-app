# This file contains all the environment-specific variables for the 'pre-prod' environment.
# It is loaded by the terragrunt.hcl files in the subdirectories.
locals {
  # The AWS region where the infrastructure will be deployed.
  aws_region = "eu-north-1"

  # A list of Availability Zones within the specified region.
  # Using multiple AZs provides high availability for our subnets.
  availability_zones = ["eu-north-1a", "eu-north-1b", "eu-north-1c"]

  # A unique name for the environment, used for tagging and identification.
  environment = "pre-prod"
  
  # --- VPC Variables ---
  vpc_name    = "pre-prod-vpc"
  # Unique IP address range for this VPC to avoid conflicts with other VPCs.
  vpc_cidr_block           = "10.20.0.0/16"
  public_subnet_cidr_blocks  = ["10.20.1.0/24", "10.20.2.0/24", "10.20.3.0/24"]
  private_subnet_cidr_blocks = ["10.20.101.0/24", "10.20.102.0/24", "10.20.103.0/24"]

  # --- EKS Variables ---
  cluster_name   = "pre-prod-eks-cluster"
  instance_types = ["t3.medium"] # Can be changed to larger instances for pre-prod
  desired_size   = 2
  max_size       = 3
  min_size       = 2 # Higher min size for more stability
}