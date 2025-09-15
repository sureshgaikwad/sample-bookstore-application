#!/bin/bash

# Bookstore Application - Build and Deploy Script
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
IMAGE_NAME="bookstore-app"
IMAGE_TAG="1.0.0"
NAMESPACE="bookstore"

echo -e "${BLUE}==================================================${NC}"
echo -e "${BLUE}Bookstore Application Deployment Script${NC}"
echo -e "${BLUE}Author: Suresh Gaikwad${NC}"
echo -e "${BLUE}==================================================${NC}"

# Function to print status
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    print_error "Docker is not running. Please start Docker and try again."
    exit 1
fi

# Check if kubectl is available
if ! command -v kubectl &> /dev/null; then
    print_error "kubectl is not installed. Please install kubectl and try again."
    exit 1
fi

# Build Maven project
print_status "Building Maven project..."
mvn clean package -DskipTests

# Build Docker image
print_status "Building Docker image: ${IMAGE_NAME}:${IMAGE_TAG}..."
docker build -t ${IMAGE_NAME}:${IMAGE_TAG} .

# Create namespace
print_status "Creating Kubernetes namespace: ${NAMESPACE}..."
kubectl apply -f k8s/namespace.yaml

# Apply ConfigMap
print_status "Applying ConfigMap..."
kubectl apply -f k8s/configmap.yaml

# Apply Deployment
print_status "Applying Deployment..."
kubectl apply -f k8s/deployment.yaml

# Apply Service
print_status "Applying Service..."
kubectl apply -f k8s/service.yaml

# Apply Ingress (optional)
if [ -f "k8s/ingress.yaml" ]; then
    print_status "Applying Ingress..."
    kubectl apply -f k8s/ingress.yaml
fi

# Apply HPA (optional)
if [ -f "k8s/hpa.yaml" ]; then
    print_status "Applying Horizontal Pod Autoscaler..."
    kubectl apply -f k8s/hpa.yaml
fi

# Apply Network Policy (optional)
if [ -f "k8s/networkpolicy.yaml" ]; then
    print_status "Applying Network Policy..."
    kubectl apply -f k8s/networkpolicy.yaml
fi

# Wait for deployment to be ready
print_status "Waiting for deployment to be ready..."
kubectl rollout status deployment/bookstore-app -n ${NAMESPACE}

# Get service information
print_status "Getting service information..."
kubectl get services -n ${NAMESPACE}

print_status "Getting pod information..."
kubectl get pods -n ${NAMESPACE}

echo -e "${GREEN}==================================================${NC}"
echo -e "${GREEN}Deployment completed successfully!${NC}"
echo -e "${GREEN}==================================================${NC}"
echo -e "${BLUE}Access the application:${NC}"
echo -e "${BLUE}NodePort: http://localhost:30080/api/books${NC}"
echo -e "${BLUE}Health Check: http://localhost:30080/actuator/health${NC}"
echo -e "${BLUE}==================================================${NC}"
