
# main.tf for azure-db-postgres module

# For generating a random password
resource "random_password" "password" {
  length           = 32
  special          = true
  override_special = "!@#$%&" # Limit special characters for compatibility with Azure
}

# Create the PostgreSQL server itself
resource "azurerm_postgresql_flexible_server" "postgres_server" {
  name                   = var.server_name
  resource_group_name    = var.resource_group_name
  location               = var.location
  version                = "14" # PostgreSQL version
  delegated_subnet_id    = var.delegated_subnet_id
  private_dns_zone_id    = null   # For simplicity, we won't use a private DNS zone

  administrator_login    = var.admin_login
  administrator_password = random_password.password.result

  sku_name   = "B_Standard_B1ms" # The smallest and cheapest SKU
  storage_mb = 32768             # 32 GB

  zone = "1" # Place in the first availability zone
}

# Create a database within the server
resource "azurerm_postgresql_flexible_server_database" "db" {
  name      = var.db_name
  server_id = azurerm_postgresql_flexible_server.postgres_server.id
  charset   = "UTF8"
  collation = "en_US.utf8"
}
