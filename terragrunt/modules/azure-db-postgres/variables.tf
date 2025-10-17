
# variables.tf for azure-db-postgres module

variable "server_name" {
  description = "The name of the PostgreSQL flexible server."
  type        = string
}

variable "resource_group_name" {
  description = "The name of the resource group."
  type        = string
}

variable "location" {
  description = "The Azure region."
  type        = string
}

variable "delegated_subnet_id" {
  description = "The ID of the subnet to delegate to the PostgreSQL server."
  type        = string
}

variable "private_dns_zone_id" {
  description = "The ID of the Private DNS Zone to associate with the server."
  type        = string
}

variable "admin_login" {
  description = "The administrator login name for the PostgreSQL server."
  type        = string
  default     = "pgadmin"
}

variable "db_name" {
  description = "The name of the database to create on the server."
  type        = string
}
