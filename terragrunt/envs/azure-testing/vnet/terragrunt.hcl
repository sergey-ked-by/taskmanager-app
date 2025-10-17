# terragrunt.hcl for the vnet component

# This block contains the full, self-contained configuration for this component.

# 1. Remote State Configuration
# This tells Terragrunt where to store the .tfstate file, which keeps track of the resources created.
# We are using an Azure Storage Account as a remote backend.
remote_state {
  backend = "azurerm"
  generate = {
    path      = "backend.tf"
    if_exists = "overwrite_terragrunt"
  }
  config = {
    resource_group_name  = "rg-taskmanager-tfstate"
    storage_account_name = "rgaccountnamesergeykedby"
    container_name       = "tfstate"
    # The key is the name of the state file within the container.
    # It's good practice to have a separate state file for each component.
    key                  = "vnet.tfstate"
  }
}

# 2. Provider Configuration
# This `generate` block creates a provider.tf file on the fly.
# It configures the Azure provider, setting the version and subscription ID.
# This avoids having to declare the provider in every single module.
generate "provider" {
  path      = "provider.tf"
  if_exists = "overwrite_terragrunt"
  contents  = <<EOF
terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 3.0"
    }
  }
}
provider "azurerm" {
  features {}
  subscription_id = "b2e6649f-66b4-41ad-8314-4ed87d905813"
}
EOF
}

# 3. Module Source
# This tells Terragrunt where to find the Terraform module code for this component.
terraform {
  source = "../../../modules/azure-vnet"
}

# 4. Inputs for the module
# This block passes variables to the Terraform module.
inputs = {
  vnet_name           = "vnet-testing"
  resource_group_name = "rg-taskmanager-testing"
  location            = "canadacentral"
}
