#!/bin/bash

# Bookstore Application - Cleanup Script
# Author: Suresh Gaikwad
# Version: 1.0.0

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
NAMESPACE="bookstore"

echo -e "${BLUE}==================================================${NC}"
echo -e "${BLUE}Bookstore Application Cleanup Script${NC}"
echo -e "${BLUE}Author: Suresh Gaikwad${NC}"
echo -e "${BLUE}==================================================${NC}"

# Function to print status
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

# Check if namespace exists
if kubectl get namespace ${NAMESPACE} > /dev/null 2>&1; then
    print_status "Cleaning up Kubernetes resources in namespace: ${NAMESPACE}..."
    
    # Delete all resources in the namespace
    kubectl delete all --all -n ${NAMESPACE}
    
    # Delete ConfigMaps
    kubectl delete configmaps --all -n ${NAMESPACE}
    
    # Delete Network Policies
    kubectl delete networkpolicies --all -n ${NAMESPACE}
    
    # Delete HPA
    kubectl delete hpa --all -n ${NAMESPACE}
    
    # Delete the namespace
    print_status "Deleting namespace: ${NAMESPACE}..."
    kubectl delete namespace ${NAMESPACE}
    
    print_status "Cleanup completed successfully!"
else
    print_warning "Namespace ${NAMESPACE} does not exist. Nothing to clean up."
fi

echo -e "${GREEN}==================================================${NC}"
