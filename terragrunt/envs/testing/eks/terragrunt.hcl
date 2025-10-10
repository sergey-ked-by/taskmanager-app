# This is the Terragrunt configuration for the EKS module in the 'testing' environment.

# Include the root configuration to inherit shared settings, such as the S3 backend configuration.
include "root" {
  path = find_in_parent_folders("root.hcl")
}

# Define a dependency on the VPC module for this environment.
# This tells Terragrunt to ensure the VPC is deployed before the EKS cluster
# and allows us to use the VPC's outputs.
dependency "vpc" {
  config_path = "../vpc"
  
  # Mock outputs for local planning, prevents errors if the VPC hasn't been applied yet.
  mock_outputs = {
    vpc_id             = "vpc-00000000000000000"
    private_subnet_ids = ["subnet-00000000000000000"]
  }
}

# Load environment-specific variables from the shared env.hcl file.
locals {
  env_vars = read_terragrunt_config(find_in_parent_folders("env.hcl"))
}

# Configure the Terraform module to be deployed.
terraform {
  source = "../../../modules/eks"
}

# Pass the required variables as inputs to the EKS module.
# We are combining outputs from the 'vpc' dependency with variables from the shared 'env.hcl'.
inputs = {
  cluster_name       = local.env_vars.locals.cluster_name
  vpc_id             = dependency.vpc.outputs.vpc_id
  private_subnet_ids = dependency.vpc.outputs.private_subnet_ids
  instance_types     = local.env_vars.locals.instance_types
  desired_size       = local.env_vars.locals.desired_size
  max_size           = local.env_vars.locals.max_size
  min_size           = local.env_vars.locals.min_size
}