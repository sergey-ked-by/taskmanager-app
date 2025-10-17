
# terragrunt/modules/azure-vnet/main.tf

# Create the main resource group for the entire environment.
# A resource group is a logical container for Azure resources.
resource "azurerm_resource_group" "rg" {
  name     = var.resource_group_name
  location = var.location
}

# Create the Virtual Network (VNet).
# This provides a private network space in Azure for our resources.
resource "azurerm_virtual_network" "vnet" {
  name                = var.vnet_name
  address_space       = [var.address_space]
  location            = var.location
  resource_group_name = azurerm_resource_group.rg.name # Explicit dependency on the resource group
}

# Create a dedicated subnet for the Azure Kubernetes Service (AKS) cluster.
# Subnets allow segmenting the VNet into smaller networks.
resource "azurerm_subnet" "aks_subnet" {
  name                 = "aks-subnet"
  resource_group_name  = azurerm_resource_group.rg.name
  virtual_network_name = azurerm_virtual_network.vnet.name
  address_prefixes     = [var.aks_subnet_address_prefix]
}

# Create a dedicated subnet for the PostgreSQL database.
# It's a good practice to place databases in a separate subnet for security.
resource "azurerm_subnet" "db_subnet" {
  name                 = "db-subnet"
  resource_group_name  = azurerm_resource_group.rg.name
  virtual_network_name = azurerm_virtual_network.vnet.name
  address_prefixes     = [var.db_subnet_address_prefix]

  # A service endpoint allows the subnet to securely access Azure services like PostgreSQL.
  service_endpoints = ["Microsoft.Sql"]

  # Delegation gives the PostgreSQL service permission to manage networking resources in this subnet.
  # This is required for VNet integration.
  delegation {
    name = "fs"
    service_delegation {
      name    = "Microsoft.DBforPostgreSQL/flexibleServers"
      actions = ["Microsoft.Network/virtualNetworks/subnets/join/action"]
    }
  }
}

# Create a Private DNS Zone for PostgreSQL.
# This allows resources within the VNet to resolve the database's name to its private IP address.
resource "azurerm_private_dns_zone" "postgres_dns_zone" {
  name                = "${var.vnet_name}.private.postgres.database.azure.com"
  resource_group_name = azurerm_resource_group.rg.name
}

# Link the Private DNS Zone to our Virtual Network.
# This makes the DNS zone active for all resources within the VNet.
resource "azurerm_private_dns_zone_virtual_network_link" "vnet_link" {
  name                  = "${var.vnet_name}-postgres-dns-link"
  resource_group_name   = azurerm_resource_group.rg.name
  private_dns_zone_name = azurerm_private_dns_zone.postgres_dns_zone.name
  virtual_network_id    = azurerm_virtual_network.vnet.id
}
