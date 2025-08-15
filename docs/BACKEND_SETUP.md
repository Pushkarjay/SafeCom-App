# SafeCom Task Management Backend Setup Guide

**Project:** SafeCom Task Management Backend API  
**Developer:** Pushkarjay Ajay (pushkarjay.ajay1@gmail.com)  
**Organization:** SafeCom  
**Date:** August 15, 2025  
**Technology Stack:** Node.js + Express.js + MongoDB + Firebase

## 📋 Prerequisites

### System Requirements
- **Operating System**: Windows 10/11, macOS 10.15+, or Ubuntu 18.04+
- **RAM**: Minimum 4GB (8GB recommended)
- **Storage**: At least 2GB free space
- **Internet**: Stable connection for package downloads

### Required Software

1. **Node.js & npm**
   - **Version**: Node.js 16.0.0 or higher (18.x LTS recommended)
   - **Download**: [Node.js Official](https://nodejs.org/)
   - **Verification**: 
     ```bash
     node --version    # Should show v16.0.0 or higher
     npm --version     # Should show 8.0.0 or higher
     ```

2. **Database (Choose One Option)**

   **Option A: MongoDB (Local Installation)**
   - **Version**: MongoDB 4.4 or higher (6.0+ recommended)
   - **Download**: [MongoDB Community Server](https://www.mongodb.com/try/download/community)
   - **Installation**:
     - Windows: Run MSI installer, install as Windows Service
     - macOS: `brew install mongodb/brew/mongodb-community`
     - Ubuntu: Follow [official installation guide](https://docs.mongodb.com/manual/tutorial/install-mongodb-on-ubuntu/)
   - **Start Service**:
     ```bash
     # Windows (as service, auto-starts)
     net start MongoDB
     
     # macOS
     brew services start mongodb/brew/mongodb-community
     
     # Linux
     sudo systemctl start mongod
     sudo systemctl enable mongod
     ```
   - **Verification**: 
     ```bash
     mongo --version
     # Or connect to MongoDB
     mongo mongodb://localhost:27017
     ```

   **Option B: MongoDB Atlas (Cloud Database)**
   - **Account**: Create free account at [MongoDB Atlas](https://www.mongodb.com/atlas)
   - **Setup**: Follow Atlas setup wizard
   - **Connection String**: Get connection string from Atlas dashboard

3. **Git Version Control**
   - **Download**: [Git SCM](https://git-scm.com/)
   - **Verification**: `git --version`

4. **Firebase Project**
   - **Account**: Google account for Firebase Console
   - **Project**: Firebase project with Admin SDK enabled

### Optional Tools
- **MongoDB Compass**: GUI for MongoDB database management
- **Postman** or **Insomnia**: API testing tools
- **VS Code**: Recommended code editor with Node.js extensions

## 🚀 Quick Setup

### Step 1: Clone Repository (if not already done)
```bash
git clone https://github.com/Pushkarjay/SafeCom-App.git
cd SafeCom-App
```

### Step 2: Navigate to Backend Directory
```bash
cd backend
ls -la  # Verify you're in the right directory
```

### Step 3: Install Dependencies
```bash
# Install all Node.js dependencies
npm install

# Verify installation
npm list --depth=0

# Check for security vulnerabilities
npm audit
npm audit fix  # Fix any vulnerabilities if found
```

### Step 4: Environment Configuration

#### Create Environment File
```bash
# Copy the example environment file
cp .env.example .env        # macOS/Linux
copy .env.example .env      # Windows PowerShell
```

#### Configure Environment Variables
Edit the `.env` file with your specific configuration:

```env
# ================================
# SERVER CONFIGURATION
# ================================
PORT=3000
NODE_ENV=development
HOST=localhost

# ================================
# DATABASE CONFIGURATION
# ================================
# Option A: Local MongoDB
MONGODB_URI=mongodb://localhost:27017/safecom_dev

# Option B: MongoDB Atlas
# MONGODB_URI=mongodb+srv://username:password@cluster.mongodb.net/safecom_dev?retryWrites=true&w=majority

# ================================
# JWT AUTHENTICATION
# ================================
JWT_SECRET=your-super-secret-jwt-key-change-this-in-production
JWT_EXPIRES_IN=24h
REFRESH_TOKEN_SECRET=your-refresh-token-secret-change-this-too
REFRESH_TOKEN_EXPIRES_IN=7d

# ================================
# FIREBASE CONFIGURATION
# ================================
FIREBASE_PROJECT_ID=your-firebase-project-id
FIREBASE_CLIENT_EMAIL=firebase-adminsdk-xxxxx@your-project.iam.gserviceaccount.com
FIREBASE_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----\nYOUR_PRIVATE_KEY_HERE\n-----END PRIVATE KEY-----\n"

# ================================
# CORS & SECURITY
# ================================
ALLOWED_ORIGINS=http://localhost:3000,http://localhost:8080,http://10.0.2.2:3000
RATE_LIMIT_WINDOW_MS=900000
RATE_LIMIT_MAX_REQUESTS=100

# ================================
# FILE UPLOAD CONFIGURATION
# ================================
MAX_FILE_SIZE=10485760
UPLOAD_PATH=./uploads
ALLOWED_FILE_TYPES=jpg,jpeg,png,pdf,doc,docx,txt

# ================================
# EMAIL CONFIGURATION (Optional)
# ================================
# SMTP_HOST=smtp.gmail.com
# SMTP_PORT=587
# SMTP_SECURE=false
# SMTP_USER=your-email@gmail.com
# SMTP_PASS=your-app-password
```

### Step 5: Database Setup

#### For Local MongoDB
```bash
# Start MongoDB service (if not running)
# Windows
net start MongoDB

# macOS
brew services start mongodb/brew/mongodb-community

# Linux
sudo systemctl start mongod

# Create database and initial collections (optional)
mongo mongodb://localhost:27017/safecom_dev
```

#### For MongoDB Atlas
1. **Create Cluster**: Login to MongoDB Atlas and create a free cluster
2. **Database Access**: Create a database user with read/write permissions
3. **Network Access**: Add your IP address (or 0.0.0.0/0 for development)
4. **Connection String**: Copy connection string and update MONGODB_URI in .env

### Step 6: Firebase Setup

#### Get Firebase Admin SDK Key
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project
3. Go to **Project Settings** → **Service accounts**
4. Click **Generate new private key**
5. Download the JSON file
6. Extract the required values for your `.env` file:
   - `project_id` → `FIREBASE_PROJECT_ID`
   - `client_email` → `FIREBASE_CLIENT_EMAIL`
   - `private_key` → `FIREBASE_PRIVATE_KEY`

### Step 7: Start the Backend Server

#### Development Mode (with auto-reload)
```bash
npm run dev
```

#### Production Mode
```bash
npm start
```

#### Using PM2 (for production deployment)
```bash
# Install PM2 globally
npm install -g pm2

# Start with PM2
pm2 start src/app.js --name "safecom-backend"

# Monitor
pm2 status
pm2 logs safecom-backend
```

## ✅ Verification & Testing

### Step 1: Check Server Status
```bash
# Server should start without errors
# Look for output similar to:
# "Server running on port 3000"
# "Connected to MongoDB"
# "Firebase Admin initialized"
```

### Step 2: Test API Endpoints

#### Using curl
```bash
# Test health endpoint
curl http://localhost:3000/api/health

# Test CORS
curl -H "Origin: http://localhost:3000" \
     -H "Access-Control-Request-Method: POST" \
     -H "Access-Control-Request-Headers: X-Requested-With" \
     -X OPTIONS \
     http://localhost:3000/api/auth/register
```

#### Using a Web Browser
Navigate to: `http://localhost:3000/api/health`
Expected response: `{"status": "OK", "timestamp": "..."}`

### Step 3: Database Connection Test
```bash
# Check MongoDB connection
mongo mongodb://localhost:27017/safecom_dev

# List databases
show dbs

# Use your database
use safecom_dev

# List collections (should be empty initially)
show collections
```

## 🌐 Backend Architecture Overview

### Project Structure
```
backend/
├── src/
│   ├── controllers/         # Route controllers
│   │   ├── authController.js
│   │   ├── taskController.js
│   │   ├── messageController.js
│   │   └── userController.js
│   ├── models/             # Database models
│   │   ├── User.js
│   │   ├── Task.js
│   │   └── Message.js
│   ├── routes/             # API routes
│   │   ├── auth.js
│   │   ├── tasks.js
│   │   ├── messages.js
│   │   └── users.js
│   ├── middleware/         # Custom middleware
│   │   ├── auth.js
│   │   ├── rateLimiter.js
│   │   └── upload.js
│   ├── config/             # Configuration files
│   │   ├── database.js
│   │   └── firebase.js
│   └── app.js              # Main application file
├── uploads/                # File upload directory
├── package.json
├── .env                    # Environment variables
├── .env.example           # Environment template
└── README.md              # This file
```

### Key Dependencies
- **express**: Web framework
- **mongoose**: MongoDB ODM
- **jsonwebtoken**: JWT authentication
- **bcryptjs**: Password hashing
- **cors**: Cross-origin resource sharing
- **helmet**: Security headers
- **multer**: File upload handling
- **firebase-admin**: Firebase integration
- **express-rate-limit**: Rate limiting
- **express-validator**: Input validation

# Cloud Storage
CLOUDINARY_CLOUD_NAME=your-cloud-name
CLOUDINARY_API_KEY=your-api-key
CLOUDINARY_API_SECRET=your-api-secret

# Email Configuration (for notifications)
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USER=your-email@gmail.com
SMTP_PASS=your-app-password

# Push Notifications
FCM_SERVER_KEY=your-fcm-server-key

# CORS Origins
ALLOWED_ORIGINS=http://localhost:3000,https://yourdomain.com
```

## 📊 Database Schema

### MongoDB Collections / PostgreSQL Tables

#### Users Collection
```json
{
  "_id": "user_id",
  "name": "John Doe",
  "email": "john@example.com",
  "password": "hashed_password",
  "role": "Employee",
  "department": "IT",
  "profileImageUrl": "https://...",
  "fcmToken": "firebase_token",
  "settings": {
    "isDarkModeEnabled": false,
    "isNotificationEnabled": true,
    "language": "en"
  },
  "createdAt": "2025-08-14T00:00:00Z",
  "updatedAt": "2025-08-14T00:00:00Z"
}
```

#### Tasks Collection
```json
{
  "_id": "task_id",
  "title": "Complete API Documentation",
  "description": "Write comprehensive API docs",
  "status": "IN_PROGRESS",
  "priority": "High",
  "assignedTo": "user_id",
  "createdBy": "user_id",
  "dueDate": "2025-08-20T00:00:00Z",
  "tags": ["documentation", "api"],
  "attachments": ["file_url_1", "file_url_2"],
  "comments": [],
  "createdAt": "2025-08-14T00:00:00Z",
  "updatedAt": "2025-08-14T00:00:00Z"
}
```

#### Messages Collection
```json
{
  "_id": "message_id",
  "conversationId": "conversation_id",
  "senderId": "user_id",
  "receiverId": "user_id",
  "content": "Hello, how's the task going?",
  "messageType": "TEXT",
  "attachments": [],
  "isRead": false,
  "timestamp": "2025-08-14T00:00:00Z"
}
```

## 🛠️ API Endpoints

### Authentication Endpoints
```
POST /api/auth/register
POST /api/auth/login
POST /api/auth/refresh
POST /api/auth/logout
POST /api/auth/forgot-password
POST /api/auth/reset-password
```

### Task Endpoints
```
GET    /api/tasks
POST   /api/tasks
GET    /api/tasks/:id
PUT    /api/tasks/:id
DELETE /api/tasks/:id
PATCH  /api/tasks/:id/status
GET    /api/tasks/search?q=query
```

### User Endpoints
```
GET    /api/users/profile
PUT    /api/users/profile
POST   /api/users/avatar
DELETE /api/users/avatar
GET    /api/users/statistics
```

### Message Endpoints
```
GET    /api/conversations
POST   /api/conversations
GET    /api/conversations/:id/messages
POST   /api/messages
PATCH  /api/messages/:id/read
```
