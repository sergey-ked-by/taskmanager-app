
# terragrunt.hcl for the database component

include "root" {
  path = "../terragrunt.hcl"
}

# Указываем, что этот компонент зависит от сети (VNet).
# Terragrunt сначала применит VNet, а потом возьмет из его outputs нужные нам ID.
dependency "vnet" {
  config_path = "../"
}

# Подключаем переменные из общего файла env.hcl
locals {
  env_vars = read_terragrunt_config(find_in_parent_folders("env.hcl"))
}

# Описываем наш модуль базы данных
terraform {
  source = "../../../modules/azure-db-postgres"
}

# Передаем нужные переменные в модуль
inputs = {
  server_name         = "psql-${local.env_vars.locals.environment}" # psql-testing
  resource_group_name = local.env_vars.locals.resource_group_name
  location            = local.env_vars.locals.location
  db_name             = "taskmanagerdb"

  # Берем ID подсети из outputs модуля vnet
  delegated_subnet_id = dependency.vnet.outputs.db_subnet_id
}
