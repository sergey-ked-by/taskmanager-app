
# This resource generates a random password.

# We use this to ensure the database has a strong, unique password without having to hardcode it.

resource "random_password" "password" {

  length           = 32

  special          = true

  # Limit the set of special characters to avoid issues with some database clients.

  override_special = "!@#$%&"

}



# This is the main resource for the PostgreSQL Flexible Server.

resource "azurerm_postgresql_flexible_server" "postgres_server" {

  name                   = var.server_name

  resource_group_name    = var.resource_group_name

  location               = var.location

  version                = "14" # Specify the PostgreSQL version.



  # VNet Integration Settings

  delegated_subnet_id    = var.delegated_subnet_id

  private_dns_zone_id    = var.private_dns_zone_id

  public_network_access_enabled = false # Disable public access for security.



  # Administrator credentials.

  # The password is taken from the `random_password` resource created above.

  administrator_login    = var.admin_login

  administrator_password = random_password.password.result



  # Server SKU (instance type).

  # B_Standard_B1ms is a burstable, low-cost SKU suitable for development/testing.

  sku_name   = "B_Standard_B1ms"

  storage_mb = 32768 # 32 GB of storage.

}



# This resource creates a specific database (e.g., 'taskmanagerdb') on the server we just defined.

resource "azurerm_postgresql_flexible_server_database" "db" {

  name      = var.db_name

  server_id = azurerm_postgresql_flexible_server.postgres_server.id

  charset   = "UTF8"

  collation = "en_US.utf8"

}


