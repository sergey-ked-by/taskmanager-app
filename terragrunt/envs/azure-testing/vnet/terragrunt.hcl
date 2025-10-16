
# terragrunt.hcl for the vnet component

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
    # The key is now unique for each component
    key                  = "vnet.tfstate"
  }
}

# 2. Provider Configuration
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

# 3. Module Source
terraform {
  source = "../../../modules/azure-vnet"
}

# 4. Inputs for the module
inputs = {
  vnet_name           = "vnet-testing"
  resource_group_name = "rg-taskmanager-testing"
  location            = "westeurope"
}
