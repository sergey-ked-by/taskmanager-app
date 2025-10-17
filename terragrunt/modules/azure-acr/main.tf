
# main.tf for azure-acr module

resource "azurerm_container_registry" "acr" {
  name                = var.registry_name
  resource_group_name = var.resource_group_name
  location            = var.location
  sku                 = "Basic" # The most cost-effective SKU for our needs
  admin_enabled       = true      # Enables an admin user, useful for simple CI/CD setups
}
