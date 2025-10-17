
# terragrunt/modules/azure-vnet/variables.tf

variable "vnet_name" {
  description = "The name of the virtual network."
  type        = string
}

variable "resource_group_name" {
  description = "The name of the resource group in which to create the VNet."
  type        = string
}

variable "location" {
  description = "The Azure region where the resources will be created."
  type        = string
}

variable "address_space" {
  description = "The address space for the VNet."
  type        = string
  default     = "10.0.0.0/16"
}

variable "aks_subnet_address_prefix" {
  description = "The address prefix for the AKS subnet."
  type        = string
  default     = "10.0.1.0/24"
}

variable "db_subnet_address_prefix" {
  description = "The address prefix for the database subnet."
  type        = string
  default     = "10.0.2.0/24"
}
