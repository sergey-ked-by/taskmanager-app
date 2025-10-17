
# terragrunt.hcl for the AKS component

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
    key                  = "aks.tfstate"
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

# 3. Dependencies
# AKS depends on both the VNet and the ACR.
dependency "vnet" {
  config_path = "../vnet"
}

dependency "acr" {
  config_path = "../acr"
}

# 4. Module Source
terraform {
  source = "../../../modules/azure-aks"
}

# 5. Inputs for the module
inputs = {
  cluster_name        = "aks-taskmanager-testing"
  resource_group_name = "rg-taskmanager-testing"
  location            = "canadacentral"
  dns_prefix          = "taskmanager-dns"

  # Get required IDs from dependencies
  vnet_subnet_id    = dependency.vnet.outputs.aks_subnet_id
  acr_registry_id   = dependency.acr.outputs.id
}
