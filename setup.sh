#!/bin/bash
# SafeCom v2 - Complete Setup Script
# Automated setup for React + Firestore + Cloud Run deployment

set -e

echo "=================================================="
echo "  SafeCom v2 - Complete Setup Script"
echo "=================================================="
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check prerequisites
echo "📋 Checking prerequisites..."

if ! command -v node &> /dev/null; then
    echo -e "${RED}❌ Node.js not found. Please install Node.js 18+${NC}"
    exit 1
fi

if ! command -v npm &> /dev/null; then
    echo -e "${RED}❌ npm not found. Please install npm${NC}"
    exit 1
fi

if ! command -v gcloud &> /dev/null; then
    echo -e "${YELLOW}⚠️  gcloud CLI not found. Skipping Cloud Run setup${NC}"
fi

echo -e "${GREEN}✅ Prerequisites OK${NC}\n"

# Step 1: React Setup
echo "🚀 Step 1: Setting up React Frontend..."
cd safecom-react

if [ ! -d "node_modules" ]; then
    echo "Installing React dependencies..."
    npm install
    npm install firebase react-router-dom axios @react-icons/all-files
else
    echo "React dependencies already installed"
fi

echo -e "${GREEN}✅ React setup complete${NC}\n"

# Step 2: Backend Setup
echo "🔧 Step 2: Setting up Node.js Backend..."
cd ../server

if [ ! -d "node_modules" ]; then
    echo "Installing backend dependencies..."
    npm install
    npm install firebase-admin @google-cloud/firestore @google-cloud/storage
else
    echo "Backend dependencies already installed"
fi

echo -e "${GREEN}✅ Backend setup complete${NC}\n"

# Step 3: Environment Setup
echo "⚙️  Step 3: Setting up environment variables..."

if [ ! -f ".env" ]; then
    echo "Creating .env file (edit with your Firebase credentials)..."
    cat > .env << 'EOF'
# Firebase Config
FIREBASE_PROJECT_ID=your-project-id
GOOGLE_APPLICATION_CREDENTIALS=./src/config/firebase-adminsdk.json

# Server Config
NODE_ENV=development
API_PORT=3001
CORS_ORIGIN=http://localhost:3000

# Features
ENABLE_DEMO_MODE=true
EOF
    echo -e "${YELLOW}⚠️  Please update .env with your Firebase credentials${NC}"
else
    echo ".env already exists"
fi

cd ..

if [ ! -f ".env" ]; then
    echo "Creating root .env file..."
    cat > .env << 'EOF'
# React Firebase Config
REACT_APP_FIREBASE_API_KEY=your-api-key
REACT_APP_FIREBASE_AUTH_DOMAIN=your-project.firebaseapp.com
REACT_APP_FIREBASE_PROJECT_ID=your-project-id
REACT_APP_FIREBASE_STORAGE_BUCKET=your-project.appspot.com
REACT_APP_FIREBASE_MESSAGING_SENDER_ID=your-sender-id
REACT_APP_FIREBASE_APP_ID=your-app-id
EOF
    echo -e "${YELLOW}⚠️  Please update root .env with your Firebase web config${NC}"
fi

echo -e "${GREEN}✅ Environment setup complete${NC}\n"

# Step 4: Firestore Migration
echo "🗄️  Step 4: Database Migration (Optional)"
echo "Run this command to migrate MongoDB → Firestore:"
echo -e "${YELLOW}  node firestore-schema/migrate-to-firestore.js${NC}\n"

# Step 5: Start Services
echo "🎯 Setup Complete! Next Steps:"
echo ""
echo "1️⃣  Update your Firebase credentials in .env files"
echo ""
echo "2️⃣  Start services:"
echo "   Terminal 1 (React):   cd safecom-react && npm start"
echo "   Terminal 2 (Backend): cd server && npm run dev"
echo "   Terminal 3 (Flutter): cd mobile && flutter run -d chrome"
echo ""
echo "3️⃣  Test with demo accounts:"
echo "   - Admin:    admin@safecom.test / Demo@1234"
echo "   - Customer: customer@safecom.test / Demo@1234"
echo "   - Employee: employee@safecom.test / Demo@1234"
echo ""
echo "4️⃣  Deploy to Cloud Run:"
echo "   ${YELLOW}./scripts/deploy-to-cloud-run.sh${NC}"
echo ""
echo -e "${GREEN}🎉 Happy coding!${NC}"
