#!/bin/bash

# Bookstore Application - Endpoint Testing Script
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
NAMESPACE=${1:-"bookstore"}
APP_NAME="bookstore-app"

echo -e "${BLUE}==================================================${NC}"
echo -e "${BLUE}Bookstore Application Endpoint Testing${NC}"
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
    print_error "OpenShift CLI (oc) is not installed."
    exit 1
fi

# Get route URL
print_status "Getting route URL..."
ROUTE_URL=$(oc get route ${APP_NAME} -n ${NAMESPACE} -o jsonpath='{.spec.host}' 2>/dev/null)

if [ -z "$ROUTE_URL" ]; then
    print_error "Route not found. Make sure the application is deployed."
    echo "Try: oc get routes -n ${NAMESPACE}"
    exit 1
fi

print_status "Testing application at: http://${ROUTE_URL}"

# Test function
test_endpoint() {
    local endpoint=$1
    local description=$2
    local expected_status=${3:-200}
    
    echo -e "\n${BLUE}Testing: ${description}${NC}"
    echo -e "${BLUE}URL: http://${ROUTE_URL}${endpoint}${NC}"
    
    response=$(curl -s -w "\n%{http_code}" "http://${ROUTE_URL}${endpoint}" 2>/dev/null)
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | head -n -1)
    
    if [ "$http_code" -eq "$expected_status" ]; then
        print_status "✅ SUCCESS (HTTP $http_code)"
        if [ ${#body} -gt 200 ]; then
            echo "Response (truncated): ${body:0:200}..."
        else
            echo "Response: $body"
        fi
    else
        print_error "❌ FAILED (HTTP $http_code, expected $expected_status)"
        echo "Response: $body"
    fi
}

# Test all endpoints
echo -e "\n${YELLOW}=== TESTING WEB UI ===${NC}"

# Web UI endpoints
test_endpoint "/" "Root endpoint (should redirect to web UI)" 302
test_endpoint "/web/" "Web UI home page"
test_endpoint "/web/in-stock" "Books in stock page"
test_endpoint "/web/add" "Add book form"

echo -e "\n${YELLOW}=== TESTING API ENDPOINTS ===${NC}"

# API info endpoint
test_endpoint "/api" "API information endpoint"

# Health endpoints
test_endpoint "/health" "Simple health check"
test_endpoint "/actuator/health" "Detailed health check"
test_endpoint "/actuator/info" "Application info"

# API endpoints
test_endpoint "/api/books" "Get all books"
test_endpoint "/api/books/1" "Get book by ID" 200
test_endpoint "/api/books/isbn/978-0-7432-7356-5" "Get book by ISBN"
test_endpoint "/api/books/search?q=gatsby" "Search books"
test_endpoint "/api/books/author/fitzgerald" "Get books by author"
test_endpoint "/api/books/in-stock" "Get books in stock"

# Test a non-existent endpoint (should return 404)
test_endpoint "/api/books/999" "Get non-existent book" 404

echo -e "\n${YELLOW}=== TESTING COMPLETE ===${NC}"

# Additional diagnostics
echo -e "\n${YELLOW}=== DIAGNOSTICS ===${NC}"

print_status "Checking pod status..."
oc get pods -l app=${APP_NAME} -n ${NAMESPACE}

print_status "Checking service status..."
oc get svc ${APP_NAME} -n ${NAMESPACE}

print_status "Checking route status..."
oc get route ${APP_NAME} -n ${NAMESPACE}

echo -e "\n${GREEN}==================================================${NC}"
echo -e "${GREEN}Endpoint testing completed!${NC}"
echo -e "${GREEN}==================================================${NC}"
echo -e "${BLUE}Quick Access URLs:${NC}"
echo -e "${BLUE}Application Info: http://${ROUTE_URL}/${NC}"
echo -e "${BLUE}All Books: http://${ROUTE_URL}/api/books${NC}"
echo -e "${BLUE}Health Check: http://${ROUTE_URL}/actuator/health${NC}"
echo -e "${GREEN}==================================================${NC}"
