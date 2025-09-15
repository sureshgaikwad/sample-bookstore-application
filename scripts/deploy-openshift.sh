#!/bin/bash

# Bookstore Application - OpenShift Deployment Script
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
APPLICATION_NAME="bookstore-app"
GIT_URI=${1:-"https://github.com/yourusername/bookstore-app.git"}
PROJECT_NAME=${2:-"bookstore"}

echo -e "${BLUE}==================================================${NC}"
echo -e "${BLUE}Bookstore Application OpenShift Deployment${NC}"
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

# Check if oc is available
if ! command -v oc &> /dev/null; then
    print_error "OpenShift CLI (oc) is not installed. Please install oc and try again."
    exit 1
fi

# Check if logged in to OpenShift
if ! oc whoami &> /dev/null; then
    print_error "Not logged in to OpenShift. Please run 'oc login' first."
    exit 1
fi

print_status "Current user: $(oc whoami)"
print_status "Current server: $(oc whoami --show-server)"

# Create or switch to project
print_status "Creating/switching to project: ${PROJECT_NAME}..."
oc new-project ${PROJECT_NAME} --display-name="Bookstore Application by Suresh Gaikwad" --description="Simple Bookstore REST API" 2>/dev/null || oc project ${PROJECT_NAME}

# Method 1: Using Source-to-Image (S2I) - Recommended for "Import from Git"
print_status "Creating new application using S2I..."
oc new-app java:17~${GIT_URI} \
    --name=${APPLICATION_NAME} \
    --env SPRING_PROFILES_ACTIVE=openshift \
    --labels="app=${APPLICATION_NAME},author=suresh-gaikwad"

# Expose the service
print_status "Exposing service..."
oc expose svc/${APPLICATION_NAME}

# Set resource limits
print_status "Setting resource limits..."
oc set resources dc/${APPLICATION_NAME} \
    --requests=memory=256Mi,cpu=250m \
    --limits=memory=512Mi,cpu=500m

# Configure health checks
print_status "Configuring health checks..."
oc set probe dc/${APPLICATION_NAME} \
    --liveness \
    --get-url=http://:8080/actuator/health/liveness \
    --initial-delay-seconds=60 \
    --period-seconds=30 \
    --timeout-seconds=5 \
    --failure-threshold=3

oc set probe dc/${APPLICATION_NAME} \
    --readiness \
    --get-url=http://:8080/actuator/health/readiness \
    --initial-delay-seconds=30 \
    --period-seconds=10 \
    --timeout-seconds=3 \
    --failure-threshold=3

# Scale to 2 replicas
print_status "Scaling to 2 replicas..."
oc scale dc/${APPLICATION_NAME} --replicas=2

# Wait for deployment
print_status "Waiting for deployment to complete..."
oc rollout status dc/${APPLICATION_NAME}

# Get route information
ROUTE_URL=$(oc get route ${APPLICATION_NAME} -o jsonpath='{.spec.host}')

print_status "Getting application information..."
oc get all -l app=${APPLICATION_NAME}

echo -e "${GREEN}==================================================${NC}"
echo -e "${GREEN}Deployment completed successfully!${NC}"
echo -e "${GREEN}==================================================${NC}"
echo -e "${BLUE}Application URL: http://${ROUTE_URL}${NC}"
echo -e "${BLUE}API Endpoints: http://${ROUTE_URL}/api/books${NC}"
echo -e "${BLUE}Health Check: http://${ROUTE_URL}/actuator/health${NC}"
echo -e "${BLUE}Application Info: http://${ROUTE_URL}/actuator/info${NC}"
echo -e "${GREEN}==================================================${NC}"

print_status "To view logs: oc logs -f dc/${APPLICATION_NAME}"
print_status "To scale: oc scale dc/${APPLICATION_NAME} --replicas=N"
print_status "To delete: oc delete all -l app=${APPLICATION_NAME}"
