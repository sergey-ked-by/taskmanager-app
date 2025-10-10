# terragrunt/modules/eks/outputs.tf

output "cluster_name" {
   description = "The name of the EKS cluster."
   value       = aws_eks_cluster.main.name
}

output "cluster_endpoint" {
   description = "The API server endpoint for the EKS cluster."
   value       = aws_eks_cluster.main.endpoint
}

output "cluster_certificate_authority_data" {
   description = "The base64 encoded certificate data required to communicate with the cluster."
   value       = aws_eks_cluster.main.certificate_authority[0].data
}
