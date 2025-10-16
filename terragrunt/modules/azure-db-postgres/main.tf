
# main.tf for azure-db-postgres module

# Для генерации случайного пароля
resource "random_password" "password" {
  length           = 32
  special          = true
  override_special = "!@#$%&" # Ограничиваем спецсимволы для совместимости с Azure
}

# Создаем сам сервер PostgreSQL
resource "azurerm_postgresql_flexible_server" "postgres_server" {
  name                   = var.server_name
  resource_group_name    = var.resource_group_name
  location               = var.location
  version                = "14" # Версия PostgreSQL
  delegated_subnet_id    = var.delegated_subnet_id
  private_dns_zone_id    = "None" # Для простоты не будем использовать private DNS

  administrator_login    = var.admin_login
  administrator_password = random_password.password.result

  sku_name   = "B_Standard_B1ms" # Самый маленький и дешевый SKU
  storage_mb = 32768             # 32 GB

  zone = "1" # Размещаем в первой зоне доступности
}

# Создаем базу данных внутри сервера
resource "azurerm_postgresql_flexible_server_database" "db" {
  name      = var.db_name
  server_id = azurerm_postgresql_flexible_server.postgres_server.id
  charset   = "UTF8"
  collation = "en_US.utf8"
}
