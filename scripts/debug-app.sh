#!/bin/bash

# Bookstore Application - Debug Script
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
echo -e "${BLUE}Bookstore Application Debug Script${NC}"
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

print_status "Application URL: http://${ROUTE_URL}"

echo -e "\n${YELLOW}=== BASIC CONNECTIVITY ===${NC}"

# Test basic connectivity
echo -e "\n${BLUE}Testing basic connectivity...${NC}"
if curl -s --connect-timeout 5 "http://${ROUTE_URL}/actuator/health" > /dev/null; then
    print_status "✅ Application is reachable"
else
    print_error "❌ Application is not reachable"
    exit 1
fi

echo -e "\n${YELLOW}=== CHECKING ENDPOINTS ===${NC}"

# Test specific endpoints
endpoints=(
    "/actuator/health:Health Check"
    "/api/books:REST API"
    "/simple:Simple Web UI (NEW)"
    "/:Root Redirect"
    "/web/:Thymeleaf Web UI"
)

for endpoint_desc in "${endpoints[@]}"; do
    endpoint=$(echo $endpoint_desc | cut -d: -f1)
    description=$(echo $endpoint_desc | cut -d: -f2)
    
    echo -e "\n${BLUE}Testing ${description} (${endpoint})...${NC}"
    
    response=$(curl -s -w "\n%{http_code}\n%{redirect_url}" "http://${ROUTE_URL}${endpoint}" 2>/dev/null)
    
    # Parse response
    lines=$(echo "$response" | wc -l)
    http_code=$(echo "$response" | tail -n2 | head -n1)
    redirect_url=$(echo "$response" | tail -n1)
    body=$(echo "$response" | head -n$((lines-2)))
    
    echo "HTTP Code: $http_code"
    if [ ! -z "$redirect_url" ]; then
        echo "Redirect URL: $redirect_url"
    fi
    
    case $http_code in
        200)
            print_status "✅ SUCCESS"
            if [ ${#body} -gt 100 ]; then
                echo "Response preview: ${body:0:100}..."
            else
                echo "Response: $body"
            fi
            ;;
        302|301)
            print_status "✅ REDIRECT"
            ;;
        404)
            print_warning "⚠️  NOT FOUND"
            ;;
        500)
            print_error "❌ SERVER ERROR"
            echo "Error response: $body"
            ;;
        *)
            print_warning "⚠️  UNEXPECTED ($http_code)"
            echo "Response: $body"
            ;;
    esac
done

echo -e "\n${YELLOW}=== APPLICATION LOGS ===${NC}"

print_status "Recent application logs (last 20 lines)..."
oc logs deployment/${APP_NAME} -n ${NAMESPACE} --tail=20

echo -e "\n${YELLOW}=== ENVIRONMENT CHECK ===${NC}"

print_status "Checking environment variables..."
oc exec deployment/${APP_NAME} -n ${NAMESPACE} -- env | grep -E "(SPRING|JAVA)" | head -10

echo -e "\n${YELLOW}=== POD STATUS ===${NC}"

print_status "Pod information..."
oc get pods -l app=${APP_NAME} -n ${NAMESPACE} -o wide

echo -e "\n${GREEN}==================================================${NC}"
echo -e "${GREEN}Debug Complete!${NC}"
echo -e "${GREEN}==================================================${NC}"
echo -e "${BLUE}Key URLs to test:${NC}"
echo -e "${BLUE}- Simple Web UI: http://${ROUTE_URL}/simple${NC}"
echo -e "${BLUE}- API: http://${ROUTE_URL}/api/books${NC}"
echo -e "${BLUE}- Health: http://${ROUTE_URL}/actuator/health${NC}"
echo -e "${GREEN}==================================================${NC}"
