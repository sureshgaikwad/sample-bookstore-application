#!/bin/bash

# Bookstore Application - Web UI Testing Script
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
echo -e "${BLUE}Bookstore Web UI Testing Script${NC}"
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

print_status "Testing Web UI at: http://${ROUTE_URL}"

# Test web UI endpoints
echo -e "\n${YELLOW}=== TESTING WEB UI ENDPOINTS ===${NC}"

# Test root redirect
echo -e "\n${BLUE}Testing root redirect...${NC}"
response=$(curl -s -w "\n%{http_code}" -L "http://${ROUTE_URL}/" 2>/dev/null)
http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | head -n -1)

if [ "$http_code" -eq "200" ]; then
    if echo "$body" | grep -q "Bookstore"; then
        print_status "✅ Root redirect working - shows web UI"
    else
        print_warning "⚠️  Root accessible but may not be web UI"
    fi
else
    print_error "❌ Root redirect failed (HTTP $http_code)"
fi

# Test /web/ endpoint
echo -e "\n${BLUE}Testing /web/ endpoint...${NC}"
response=$(curl -s -w "\n%{http_code}" "http://${ROUTE_URL}/web/" 2>/dev/null)
http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | head -n -1)

if [ "$http_code" -eq "200" ]; then
    if echo "$body" | grep -q "Bookstore"; then
        print_status "✅ Web UI home page working"
        
        # Check for specific elements
        if echo "$body" | grep -q "Search books"; then
            print_status "  - Search functionality present"
        fi
        if echo "$body" | grep -q "Add New Book"; then
            print_status "  - Add book functionality present"
        fi
        if echo "$body" | grep -q "Bootstrap"; then
            print_status "  - Bootstrap CSS loaded"
        fi
    else
        print_error "❌ Web UI accessible but content may be wrong"
        echo "First 200 chars: ${body:0:200}"
    fi
else
    print_error "❌ Web UI failed (HTTP $http_code)"
    echo "Response: $body"
fi

# Test add book form
echo -e "\n${BLUE}Testing add book form...${NC}"
response=$(curl -s -w "\n%{http_code}" "http://${ROUTE_URL}/web/add" 2>/dev/null)
http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | head -n -1)

if [ "$http_code" -eq "200" ]; then
    if echo "$body" | grep -q "form" && echo "$body" | grep -q "title"; then
        print_status "✅ Add book form working"
    else
        print_warning "⚠️  Add book form accessible but may have issues"
    fi
else
    print_error "❌ Add book form failed (HTTP $http_code)"
fi

# Test API still works
echo -e "\n${BLUE}Testing API endpoints...${NC}"
response=$(curl -s -w "\n%{http_code}" "http://${ROUTE_URL}/api/books" 2>/dev/null)
http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | head -n -1)

if [ "$http_code" -eq "200" ]; then
    if echo "$body" | grep -q "\["; then
        print_status "✅ REST API still working"
    else
        print_warning "⚠️  API accessible but may return wrong data"
    fi
else
    print_error "❌ REST API failed (HTTP $http_code)"
fi

echo -e "\n${YELLOW}=== APPLICATION STATUS ===${NC}"

# Check pod status
print_status "Checking pod status..."
oc get pods -l app=${APP_NAME} -n ${NAMESPACE}

# Check recent logs for errors
echo -e "\n${BLUE}Recent application logs (last 10 lines):${NC}"
oc logs deployment/${APP_NAME} -n ${NAMESPACE} --tail=10

echo -e "\n${GREEN}==================================================${NC}"
echo -e "${GREEN}Web UI Testing Complete!${NC}"
echo -e "${GREEN}==================================================${NC}"
echo -e "${BLUE}Access URLs:${NC}"
echo -e "${BLUE}Web UI: http://${ROUTE_URL}/web/${NC}"
echo -e "${BLUE}Root: http://${ROUTE_URL}/ (should redirect to web UI)${NC}"
echo -e "${BLUE}API: http://${ROUTE_URL}/api/books${NC}"
echo -e "${GREEN}==================================================${NC}"
