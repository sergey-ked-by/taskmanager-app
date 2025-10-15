
# terragrunt/envs/azure-testing/terragrunt.hcl

# Configure remote state storage for Terraform in an Azure Storage Account
remote_state {
  backend = "azurerm"
  generate = {
    path      = "backend.tf"
    if_exists = "overwrite_terragrunt"
  }
  config = {
    resource_group_name  = "rg-taskmanager-tfstate"
    storage_account_name = "rgaccountnamesergeykedby" # The name you provided
    container_name       = "tfstate"
    key                  = "${path_relative_to_include()}/terraform.tfstate"
  }
}

# Read the environment-specific variables from env.hcl
locals {
  env_vars = read_terragrunt_config("env.hcl")
}

# Generate a provider.tf file with the required azurerm provider configuration
generate "provider" {
  path      = "provider.tf"
  if_exists = "overwrite_terragrunt"
  contents  = <<EOF
provider "azurerm" {
  features {}
  subscription_id = "b2e6649f-66b4-41ad-8314-4ed87d905813"
}
EOF
}

# Infrastructure definition
terraform {
  source = "../../modules/azure-vnet"
}

# Pass variables from env.hcl to the module
inputs = {
  vnet_name           = "vnet-${local.env_vars.locals.environment}"
  resource_group_name = local.env_vars.locals.resource_group_name
  location            = local.env_vars.locals.location
}
