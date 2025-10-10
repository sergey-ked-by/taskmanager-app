# terragrunt/modules/eks/variables.tf

variable "cluster_name" {
  description = "The name for the EKS cluster."
  type        = string
}

variable "vpc_id" {
  description = "The ID of the VPC where the cluster will be deployed."
  type        = string
}

variable "private_subnet_ids" {
  description = "A list of private subnet IDs where the worker nodes will be placed."
  type        = list(string)
}

variable "instance_types" {
  description = "A list of EC2 instance types for the worker nodes."
  type        = list(string)
  default     = ["t3.medium"]
}

variable "desired_size" {
  description = "The desired number of worker nodes."
  type        = number
  default     = 2
}

variable "max_size" {
  description = "The maximum number of worker nodes"
  type        = number
  default     = 3
}

variable "min_size" {
  description = "The minimum number of worker nodes."
  type        = number
  default     = 1
}