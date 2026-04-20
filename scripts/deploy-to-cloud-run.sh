#!/bin/bash
# Deploy to Google Cloud Run

set -e

PROJECT_ID=${1:-"safecom-v2"}
REGION=${2:-"us-central1"}
SERVICE_NAME=${3:-"safecom-backend"}

echo "=================================================="
echo "  Deploying to Google Cloud Run"
echo "=================================================="
echo "Project: $PROJECT_ID"
echo "Region: $REGION"
echo "Service: $SERVICE_NAME"
echo ""

# Authenticate
echo "🔐 Authenticating with Google Cloud..."
gcloud auth login
gcloud config set project $PROJECT_ID

# Build image
echo "🔨 Building Docker image..."
docker build -t gcr.io/$PROJECT_ID/$SERVICE_NAME:latest ./server
docker push gcr.io/$PROJECT_ID/$SERVICE_NAME:latest

# Deploy
echo "🚀 Deploying to Cloud Run..."
gcloud run deploy $SERVICE_NAME \
  --image gcr.io/$PROJECT_ID/$SERVICE_NAME:latest \
  --platform managed \
  --region $REGION \
  --allow-unauthenticated \
  --set-env-vars FIREBASE_PROJECT_ID=$PROJECT_ID \
  --memory 512Mi \
  --cpu 1 \
  --timeout 3600 \
  --max-instances 100

echo ""
echo "✅ Deployment complete!"
echo ""
echo "Service URL:"
gcloud run services describe $SERVICE_NAME --region $REGION --format="value(status.url)"
