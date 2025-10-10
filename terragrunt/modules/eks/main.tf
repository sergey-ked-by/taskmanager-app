# terragrunt/modules/eks/main.tf

# Specifies the required Terraform providers. Pinning versions is a best practice.
terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "5.99.1"
    }
  }
}

# --- IAM Role for the EKS Cluster (Control Plane) ---
# This role is assumed by the EKS control plane (the "brain") to manage 
# AWS resources like Load Balancers and ENIs on your behalf.
resource "aws_iam_role" "eks_cluster_role" {
  name = "${var.cluster_name}-cluster-role"

  # Trust policy that allows the EKS service to assume this role.
  assume_role_policy = jsonencode({
    Version   = "2012-10-17",
    Statement = [
      {
        Effect    = "Allow",
        Principal = {
          Service = "eks.amazonaws.com"
        },
        Action    = "sts:AssumeRole"
      }
    ]
  })
}

# Attach the required AWS-managed policy to the cluster role.
resource "aws_iam_role_policy_attachment" "eks_cluster_policy" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKSClusterPolicy"
  role       = aws_iam_role.eks_cluster_role.name
}


# --- IAM Role for the Worker Nodes ---
# This role is assumed by the EC2 instances (the "workers") that will join the cluster.
# It grants them the permissions they need to operate as Kubernetes nodes.
resource "aws_iam_role" "eks_node_role" {
   name = "${var.cluster_name}-node-role"

   # Trust policy that allows EC2 instances to assume this role.
   assume_role_policy = jsonencode({
     Version   = "2012-10-17",
     Statement = [
       {
         Effect    = "Allow",
         Principal = {
           Service = "ec2.amazonaws.com"
         },
         Action    = "sts:AssumeRole"
       }
     ]
   })
}

# Attach required policies to the node role.
# AmazonEKSWorkerNodePolicy: Core permissions for a node to register with the cluster.
resource "aws_iam_role_policy_attachment" "eks_worker_node_policy" {
   policy_arn = "arn:aws:iam::aws:policy/AmazonEKSWorkerNodePolicy"
   role       = aws_iam_role.eks_node_role.name
}

# AmazonEKS_CNI_Policy: Permissions for the VPC CNI plugin to manage networking (assign IPs to pods).
resource "aws_iam_role_policy_attachment" "eks_cni_policy" {
   policy_arn = "arn:aws:iam::aws:policy/AmazonEKS_CNI_Policy"
   role       = aws_iam_role.eks_node_role.name
}

# AmazonEC2ContainerRegistryReadOnly: Allows nodes to pull Docker images from Amazon ECR.
resource "aws_iam_role_policy_attachment" "ec2_container_registry_read_only" {
   policy_arn = "arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryReadOnly"
   role       = aws_iam_role.eks_node_role.name
}

# Create an instance profile, which is a container for an IAM role that you can use to
# pass role information to an EC2 instance when the instance starts.
resource "aws_iam_instance_profile" "eks_node_instance_profile" {
   name = "${var.cluster_name}-node-instance-profile"
   role = aws_iam_role.eks_node_role.name
}


# --- EKS Cluster Resource ---
# This resource provisions the EKS control plane itself.
resource "aws_eks_cluster" "main" {
   name     = var.cluster_name
   role_arn = aws_iam_role.eks_cluster_role.arn

   # Configure the cluster to use the subnets from our VPC.
   vpc_config {
     subnet_ids = var.private_subnet_ids
   }

   # Explicitly depend on the cluster policy attachment to ensure it's created before the cluster.
   depends_on = [
     aws_iam_role_policy_attachment.eks_cluster_policy,
   ]
}


# --- EKS Worker Node Group ---
# This defines a managed group of EC2 instances that will register as nodes to the EKS cluster.
resource "aws_eks_node_group" "main" {
   cluster_name    = aws_eks_cluster.main.name
   node_group_name = "${var.cluster_name}-node-group"
   node_role_arn   = aws_iam_role.eks_node_role.arn
   subnet_ids      = var.private_subnet_ids

   # Specify the type and size of the worker nodes.
   instance_types = var.instance_types

   # Configure the autoscaling properties for the node group.
   scaling_config {
     desired_size = var.desired_size
     max_size     = var.max_size
     min_size     = var.min_size
   }

   # Explicitly depend on the node policies to ensure they are attached before nodes are created.
   depends_on = [
     aws_iam_role_policy_attachment.eks_worker_node_policy,
     aws_iam_role_policy_attachment.eks_cni_policy,
     aws_iam_role_policy_attachment.ec2_container_registry_read_only,
   ]
}