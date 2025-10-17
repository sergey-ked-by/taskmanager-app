
# variables.tf for azure-aks module

variable "cluster_name" {
  description = "The name of the AKS cluster."
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

variable "dns_prefix" {
  description = "The DNS prefix for the AKS cluster."
  type        = string
}

variable "node_count" {
  description = "The number of nodes (VMs) in the cluster."
  type        = number
  default     = 1 # Start with one node to save costs
}

variable "vnet_subnet_id" {
  description = "The ID of the subnet where the AKS nodes will be placed."
  type        = string
}

variable "acr_registry_id" {
  description = "The ID of the Azure Container Registry to link to the cluster."
  type        = string
}
