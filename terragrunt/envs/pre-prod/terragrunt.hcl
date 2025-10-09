# This is the Terragrunt configuration for the 'pre-prod' environment.

# Include the root configuration to inherit shared settings, such as the S3 backend configuration.
# This helps keep our code DRY (Don't Repeat Yourself).
include "root" {
  path = find_in_parent_folders("root.hcl")
}

# Load environment-specific variables from the env.hcl file in the same directory.
# This separates data (variables) from logic (module configuration).
locals {
  env_vars = read_terragrunt_config("env.hcl")
}

# Configure the Terraform module to be deployed for this environment.
terraform {
  # The 'source' attribute points to the location of the generic Terraform module.
  # We are using the 'vpc' module for this configuration.
  source = "../../modules/vpc"
}

# Pass the loaded variables as inputs to the Terraform module.
# This is how we provide the 'pre-prod' environment's specific values (like VPC name and CIDR blocks)
# to our generic VPC module.
inputs = {
  aws_region                 = local.env_vars.locals.aws_region
  availability_zones         = local.env_vars.locals.availability_zones
  environment                = local.env_vars.locals.environment
  vpc_name                   = local.env_vars.locals.vpc_name
  vpc_cidr_block             = local.env_vars.locals.vpc_cidr_block
  public_subnet_cidr_blocks  = local.env_vars.locals.public_subnet_cidr_blocks
  private_subnet_cidr_blocks = local.env_vars.locals.private_subnet_cidr_blocks
}
