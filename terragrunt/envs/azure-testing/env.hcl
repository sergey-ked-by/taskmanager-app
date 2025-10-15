
# terragrunt/envs/azure-testing/env.hcl

# Переменные, которые будут переданы в наши модули
locals {
  # Environment name, will be used in resource names
  environment = "testing"

  # The region where all infrastructure will be deployed
  location = "westeurope"

  # Name for the common resource group where all application components will reside
  resource_group_name = "rg-taskmanager-testing"
}
