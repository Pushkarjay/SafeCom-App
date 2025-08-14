# SafeCom Task Management Backend API

## 🌐 Backend Setup Options

### Option 1: Node.js + Express Backend

#### Prerequisites
- Node.js 16+ 
- MongoDB or PostgreSQL
- Firebase Admin SDK

#### Quick Start
```bash
mkdir safecom-backend
cd safecom-backend
npm init -y
npm install express mongoose cors helmet dotenv
npm install firebase-admin jsonwebtoken bcryptjs
npm install multer cloudinary socket.io
npm install --save-dev nodemon
```

#### Project Structure
```
safecom-backend/
├── src/
│   ├── controllers/
│   │   ├── authController.js
│   │   ├── taskController.js
│   │   ├── userController.js
│   │   └── messageController.js
│   ├── models/
│   │   ├── User.js
│   │   ├── Task.js
│   │   └── Message.js
│   ├── middleware/
│   │   ├── auth.js
│   │   └── upload.js
│   ├── routes/
│   │   ├── auth.js
│   │   ├── tasks.js
│   │   ├── users.js
│   │   └── messages.js
│   ├── config/
│   │   ├── database.js
│   │   └── firebase.js
│   └── app.js
├── package.json
└── .env
```

### Option 2: Python + FastAPI Backend

#### Prerequisites
- Python 3.8+
- FastAPI
- SQLAlchemy/MongoDB
- Firebase Admin SDK

#### Quick Start
```bash
mkdir safecom-backend-python
cd safecom-backend-python
pip install fastapi uvicorn sqlalchemy psycopg2-binary
pip install firebase-admin python-jose[cryptography] passlib[bcrypt]
pip install python-multipart cloudinary
```

### Option 3: Firebase-Only Backend

#### Using Firebase Functions + Firestore
- No dedicated server needed
- Serverless architecture
- Auto-scaling
- Integrated with Firebase ecosystem

## 🔧 Backend Configuration

### Environment Variables (.env)
```env
# Server Configuration
PORT=3000
NODE_ENV=development

# Database
MONGODB_URI=mongodb://localhost:27017/safecom
# OR for PostgreSQL
DATABASE_URL=postgresql://username:password@localhost/safecom

# JWT Configuration
JWT_SECRET=your-super-secret-jwt-key-here
JWT_EXPIRES_IN=24h
REFRESH_TOKEN_SECRET=your-refresh-token-secret

# Firebase Configuration
FIREBASE_PROJECT_ID=safecom-task-management
FIREBASE_PRIVATE_KEY_ID=your-private-key-id
FIREBASE_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----\nYOUR_KEY\n-----END PRIVATE KEY-----\n"
FIREBASE_CLIENT_EMAIL=firebase-adminsdk-xxxxx@safecom-task-management.iam.gserviceaccount.com
FIREBASE_CLIENT_ID=your-client-id

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
