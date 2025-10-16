
# terragrunt.hcl for the database component

# All configuration is now in this file

# 1. Backend Configuration (same, but with a different key)
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
    # The key is now unique for each component
    key                  = "db.tfstate"
  }
}

# 2. Provider Configuration (the same)
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

# 3. Dependency on the Virtual Network (VNet)
# Terragrunt will first apply the VNet, then take the required IDs from its outputs.
dependency "vnet" {
  config_path = "../vnet"
}

# 4. Module Source
terraform {
  source = "../../../modules/azure-db-postgres"
}

# 5. Inputs for the module
inputs = {
  server_name         = "psql-testing-${formatdate("YYYYMMDDhhmmss", timestamp())}"
  resource_group_name = "rg-taskmanager-testing"
  location            = "eastus"
  db_name             = "taskmanagerdb"

  # Get the subnet ID from the vnet module's outputs
  delegated_subnet_id = dependency.vnet.outputs.db_subnet_id
  private_dns_zone_id = dependency.vnet.outputs.private_dns_zone_id
}
