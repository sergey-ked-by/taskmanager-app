
# terragrunt/modules/azure-vnet/outputs.tf

output "vnet_id" {
  description = "The ID of the virtual network."
  value       = azurerm_virtual_network.vnet.id
}

output "aks_subnet_id" {
  description = "The ID of the AKS subnet."
  value       = azurerm_subnet.aks_subnet.id
}

output "db_subnet_id" {
  description = "The ID of the database subnet."
  value       = azurerm_subnet.db_subnet.id
}
