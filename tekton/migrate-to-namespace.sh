#!/bin/bash

# Script to migrate Tekton pipeline setup to a new namespace
# Usage: ./migrate-to-namespace.sh <new-namespace>

if [ $# -eq 0 ]; then
    echo "Usage: $0 <new-namespace>"
    echo "Example: $0 production"
    exit 1
fi

NEW_NAMESPACE=$1
CURRENT_DIR=$(dirname "$0")

echo "🚀 Migrating Tekton pipeline setup to namespace: $NEW_NAMESPACE"

# Create namespace if it doesn't exist
echo "📦 Creating namespace $NEW_NAMESPACE..."
oc create namespace $NEW_NAMESPACE --dry-run=client -o yaml | oc apply -f -

# Create temporary files with updated namespace
TEMP_DIR=$(mktemp -d)
echo "📁 Using temporary directory: $TEMP_DIR"

# Update namespace in all files
for file in 02-service-account.yaml 01-quay-secret.yaml 05-github-webhook-secret.yaml 03-pipeline.yaml update-gitops-task-final.yaml 04-triggers.yaml; do
    if [ -f "$CURRENT_DIR/$file" ]; then
        echo "📝 Processing $file..."
        sed "s/namespace: sgaikwad/namespace: $NEW_NAMESPACE/g" "$CURRENT_DIR/$file" > "$TEMP_DIR/$file"
    else
        echo "❌ File $file not found in $CURRENT_DIR"
        exit 1
    fi
done

# Apply files in correct order
echo "🔧 Applying resources in correct order..."

echo "1️⃣ Applying Service Account and RBAC..."
oc apply -f "$TEMP_DIR/02-service-account.yaml"

echo "2️⃣ Applying Secrets..."
oc apply -f "$TEMP_DIR/01-quay-secret.yaml"
oc apply -f "$TEMP_DIR/05-github-webhook-secret.yaml"

echo "3️⃣ Applying Pipeline and Tasks..."
oc apply -f "$TEMP_DIR/03-pipeline.yaml"
oc apply -f "$TEMP_DIR/update-gitops-task-final.yaml"

echo "4️⃣ Applying Triggers and EventListener..."
oc apply -f "$TEMP_DIR/04-triggers.yaml"

# Cleanup
rm -rf "$TEMP_DIR"

echo "✅ Migration complete!"
echo ""
echo "📋 Next steps:"
echo "1. Update your GitHub webhook URL to point to the new route:"
echo "   oc get route bookstore-webhook-route -n $NEW_NAMESPACE -o jsonpath='{.spec.host}'"
echo ""
echo "2. Verify EventListener is running:"
echo "   oc get eventlistener -n $NEW_NAMESPACE"
echo ""
echo "3. Test with a commit to trigger the pipeline"
