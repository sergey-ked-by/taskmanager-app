
# variables.tf for azure-acr module

variable "registry_name" {
  description = "The name of the Container Registry. Must be globally unique."
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
