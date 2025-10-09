# This is the root Terragrunt configuration.
# It is included by all environment-specific terragrunt.hcl files to share the remote state configuration.
remote_state {
  # Configure the S3 backend for Terraform state storage.
  # This tells Terraform to store the .tfstate file in an S3 bucket.
  backend = "s3"

  # Instructs Terragrunt to generate a backend.tf file automatically in the .terragrunt-cache directory.
  # This is how Terragrunt passes the backend configuration to Terraform.
  generate = {
    path      = "backend.tf"
    if_exists = "overwrite_terragrunt"
  }

  # Configuration for the S3 backend.
  config = {
    # Encrypt the state file at rest for security.
    encrypt        = true
    
    # The name of the S3 bucket where the state file will be stored.
    bucket         = "taskmanager-tfstate-533267189928"
    
    # The path to the state file within the bucket.
    # The `path_relative_to_include()` function ensures that each environment (e.g., 'envs/testing')
    # gets its own separate state file in a corresponding folder.
    key            = "${path_relative_to_include()}/terraform.tfstate"
    
    # The AWS region where the S3 bucket and DynamoDB table are located.
    region         = "eu-north-1"
    
    # The name of the DynamoDB table used for state locking.
    # This prevents multiple users from running `apply` at the same time, which could corrupt the state.
    dynamodb_table = "taskmanager-tf-lock"
  }
}