
# outputs.tf for azure-aks module

output "id" {
  description = "The ID of the AKS cluster."
  value       = azurerm_kubernetes_cluster.aks.id
}

output "name" {
  description = "The name of the AKS cluster."
  value       = azurerm_kubernetes_cluster.aks.name
}

# This output block retrieves the kubeconfig file, which is needed to connect to the cluster.
# The value is marked as sensitive.
output "kube_config" {
  description = "The kubeconfig for the AKS cluster."
  value       = azurerm_kubernetes_cluster.aks.kube_config_raw
  sensitive   = true
}
