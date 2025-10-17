
# main.tf for azure-aks module

resource "azurerm_kubernetes_cluster" "aks" {
  name                = var.cluster_name
  location            = var.location
  resource_group_name = var.resource_group_name
  dns_prefix          = var.dns_prefix

  default_node_pool {
    name       = "default"
    node_count = var.node_count
    vm_size    = "Standard_B2s" # A good, cost-effective size for testing
    vnet_subnet_id = var.vnet_subnet_id
  }

  identity {
    type = "SystemAssigned"
  }

  # Network settings
  network_profile {
    network_plugin = "azure"
    service_cidr   = "10.0.3.0/24"
    dns_service_ip = "10.0.3.10"
  }
}

# Create a role assignment to allow the AKS cluster to pull images from the ACR
resource "azurerm_role_assignment" "aks_acr_pull" {
  scope                = var.acr_registry_id
  role_definition_name = "AcrPull"
  principal_id         = azurerm_kubernetes_cluster.aks.identity[0].principal_id
}
