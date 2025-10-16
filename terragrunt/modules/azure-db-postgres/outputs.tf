
# outputs.tf for azure-db-postgres module

output "server_id" {
  description = "The ID of the PostgreSQL server."
  value       = azurerm_postgresql_flexible_server.postgres_server.id
}

output "server_fqdn" {
  description = "The Fully Qualified Domain Name of the server."
  value       = azurerm_postgresql_flexible_server.postgres_server.fqdn
}

output "db_name" {
  description = "The name of the created database."
  value       = azurerm_postgresql_flexible_server_database.db.name
}

output "admin_login" {
  description = "The admin login for the database."
  value       = azurerm_postgresql_flexible_server.postgres_server.administrator_login
}

output "admin_password" {
  description = "The admin password for the database. This is a sensitive value."
  value       = random_password.password.result
  sensitive   = true # Помечаем пароль как чувствительные данные
}
