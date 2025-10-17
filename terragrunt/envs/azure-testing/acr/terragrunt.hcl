
# terragrunt.hcl for the ACR component

# All configuration is now in this file

# 1. Backend Configuration
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
    key                  = "acr.tfstate"
  }
}

# 2. Provider Configuration
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
terraform {
  source = "../../../modules/azure-acr"
}

# 4. Inputs for the module
inputs = {
  # Container Registry names must be globally unique and alphanumeric.
  registry_name       = "acrtaskmanager${formatdate("YYYYMMDD", timestamp())}"
  resource_group_name = "rg-taskmanager-testing"
  location            = "canadacentral"
}
