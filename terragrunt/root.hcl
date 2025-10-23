# This is the root Terragrunt configuration.
# It is included by all environment-specific terragrunt.hcl files to share the remote state configuration.
remote_state {
  # Configure the Azure Blob Storage backend for Terraform state storage.
  backend = "azurerm"

  # Instructs Terragrunt to generate a backend.tf file automatically in the .terragrunt-cache directory.
  generate = {
    path      = "backend.tf"
    if_exists = "overwrite_terragrunt"
  }

  # Configuration for the azurerm backend.
  config = {
    resource_group_name  = "rg-taskmanager-tfstate"
    storage_account_name = "rgaccountnamesergeykedby"
    container_name       = "tfstate"
    # The `path_relative_to_include()` function ensures that each environment (e.g., 'envs/azure-testing/acr')
    # gets its own separate state file in a corresponding folder.
    key                  = "${path_relative_to_include()}/terraform.tfstate"
  }
}