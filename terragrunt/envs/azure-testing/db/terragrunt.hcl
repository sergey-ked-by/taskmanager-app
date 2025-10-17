
# terragrunt.hcl for the database component

# This block contains the full, self-contained configuration for this component.

# 1. Backend Configuration
# We use the same storage account as the vnet component, but a different key.
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
    # Using a unique key ensures that the state for the DB doesn't overwrite the state for the VNet.
    key                  = "db.tfstate"
  }
}

# 2. Provider Configuration
# This is identical to the vnet component's provider configuration.
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
# This block tells Terragrunt that the DB component depends on the VNet component.
# Terragrunt will automatically find the outputs from the vnet state file and make them available.
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
  location            = "canadacentral"
  db_name             = "taskmanagerdb"

  # Get the subnet ID and DNS zone ID from the vnet module's outputs.
  # This is how we link the database to the network.
  delegated_subnet_id = dependency.vnet.outputs.db_subnet_id
  private_dns_zone_id = dependency.vnet.outputs.private_dns_zone_id
}
