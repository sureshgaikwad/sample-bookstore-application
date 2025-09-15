#!/bin/bash

# Quick Update Script - Updates the running application
# Author: Suresh Gaikwad

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

APP_NAME="sample-bookstore-application"
NAMESPACE="test"

echo -e "${BLUE}==================================================${NC}"
echo -e "${BLUE}Quick Update for Bookstore Application${NC}"
echo -e "${BLUE}Author: Suresh Gaikwad${NC}"
echo -e "${BLUE}==================================================${NC}"

print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if we can access the cluster
if ! oc whoami > /dev/null 2>&1; then
    print_error "Not logged into OpenShift cluster"
    exit 1
fi

# Start a new build
print_status "Starting new build..."
oc start-build ${APP_NAME} -n ${NAMESPACE}

# Wait for build to complete
print_status "Waiting for build to complete..."
oc logs -f bc/${APP_NAME} -n ${NAMESPACE}

# Wait for deployment to rollout
print_status "Waiting for deployment rollout..."
oc rollout status deployment/${APP_NAME} -n ${NAMESPACE}

# Get the route URL
ROUTE_URL=$(oc get route ${APP_NAME} -n ${NAMESPACE} -o jsonpath='{.spec.host}')

print_status "Testing updated application..."

# Test the simple endpoint
echo -e "\n${BLUE}Testing simple web UI...${NC}"
if curl -s "https://${ROUTE_URL}/simple" | grep -q "Bookstore"; then
    print_status "✅ Simple web UI is working!"
else
    print_error "❌ Simple web UI failed"
fi

# Test API
echo -e "\n${BLUE}Testing API...${NC}"
if curl -s "https://${ROUTE_URL}/api/books" | grep -q "title"; then
    print_status "✅ API is working!"
else
    print_error "❌ API failed"
fi

echo -e "\n${GREEN}==================================================${NC}"
echo -e "${GREEN}Update Complete!${NC}"
echo -e "${GREEN}==================================================${NC}"
echo -e "${BLUE}Access URLs:${NC}"
echo -e "${BLUE}- Simple Web UI: https://${ROUTE_URL}/simple${NC}"
echo -e "${BLUE}- API: https://${ROUTE_URL}/api/books${NC}"
echo -e "${BLUE}- Health: https://${ROUTE_URL}/actuator/health${NC}"
echo -e "${GREEN}==================================================${NC}"
