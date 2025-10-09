# terragrunt/modules/vpc/outputs.tf

output "vpc_id" {
  description = "The ID of the created VPC."
  value       = aws_vpc.main.id
}

output "public_subnet_ids" {
  description = "The list of public subnet IDs."
  value       = [for s in aws_subnet.public : s.id]
}

output "private_subnet_ids" {
  description = "The list of private subnet IDs."
  value       = [for s in aws_subnet.private : s.id]
}