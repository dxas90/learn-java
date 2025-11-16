#!/bin/bash
set -euo pipefail

# Simple Smoke Test for KinD Deployment
# Validates basic service availability

APP_NAME="learn-java"
SERVICE_NAME="learn-java"
NAMESPACE="default"

echo "Running smoke test..."

# Wait for service to be available
echo "Waiting for service ${SERVICE_NAME} to be ready..."
kubectl wait --for=condition=available --timeout=300s deployment/${SERVICE_NAME} -n ${NAMESPACE}

# Get service port
PORT=$(kubectl get service ${SERVICE_NAME} -n ${NAMESPACE} -o jsonpath='{.spec.ports[0].port}')
echo "Service port: ${PORT}"

# Test basic endpoints
echo "Testing /ping endpoint..."
kubectl run test-pod --image=curlimages/curl --rm -i --restart=Never -- curl -f http://learn-java:${PORT}/ping

echo "Testing /healthz endpoint..."
kubectl run test-pod --image=curlimages/curl --rm -i --restart=Never -- curl -f http://learn-java:${PORT}/healthz

echo "Smoke test passed!"
