# SafeCom Task Management App

A comprehensive task management application built with Android (Kotlin) frontend and Node.js backend, featuring real-time messaging, push notifications, and collaborative task management.

## 📱 Features

### Task Management
- ✅ Create, assign, and track tasks
- ✅ Set priorities and due dates
- ✅ Add comments and attachments
- ✅ Task status tracking (Pending, In Progress, Completed, Overdue)
- ✅ Subtask support
- ✅ Task categories and tags
- ✅ Time tracking (estimated vs actual hours)

### Real-time Communication
- ✅ Direct messaging between users
- ✅ Group messaging
- ✅ Task-specific chat rooms
- ✅ Message reactions and replies
- ✅ File sharing in messages
- ✅ Read receipts and delivery status

### User Management
- ✅ User authentication and authorization
- ✅ Role-based access control (User, Manager, Admin)
- ✅ User profiles with avatars
- ✅ Password reset functionality

### Notifications
- ✅ Push notifications via Firebase FCM
- ✅ Task assignment notifications
- ✅ Message notifications
- ✅ Due date reminders

### File Management
- ✅ Secure file upload and download
- ✅ Support for images, documents, audio, video
- ✅ File sharing in tasks and messages

## 🏗️ Architecture

### Frontend (Android)
- **Language**: Kotlin
- **Architecture**: MVVM with Clean Architecture
- **UI**: Material Design 3, Jetpack Compose
- **Database**: Room (SQLite)
- **Networking**: Retrofit2 + OkHttp
- **Dependency Injection**: Hilt
- **Navigation**: Navigation Component
- **Real-time**: Socket.IO client
- **Push Notifications**: Firebase FCM
- **Image Loading**: Glide
- **Authentication**: Firebase Auth + JWT

### Backend (Node.js)
- **Runtime**: Node.js
- **Framework**: Express.js
- **Database**: MongoDB with Mongoose
- **Authentication**: JWT + bcrypt
- **Real-time**: Socket.IO
- **Push Notifications**: Firebase Admin SDK
- **File Upload**: Multer
- **Validation**: Express Validator
- **Security**: Rate limiting, CORS, input sanitization

## 📁 Project Structure

```
SafeCom-App/
├── android/                 # Android application
│   ├── app/
│   │   ├── src/main/
│   │   │   ├── java/com/safecom/taskmanagement/
│   │   │   │   ├── data/        # Data layer (repositories, API, database)
│   │   │   │   ├── domain/      # Domain layer (use cases, models)
│   │   │   │   ├── presentation/ # UI layer (activities, fragments, viewmodels)
│   │   │   │   └── di/          # Dependency injection
│   │   │   ├── res/             # Resources (layouts, drawables, strings)
│   │   │   └── AndroidManifest.xml
│   │   ├── build.gradle         # App-level Gradle configuration
│   │   └── google-services.json # Firebase configuration
│   ├── build.gradle             # Project-level Gradle configuration
│   └── buildozer.spec          # Buildozer configuration for builds
├── backend/                 # Node.js backend API
│   ├── src/
│   │   ├── controllers/         # Route controllers
│   │   ├── models/             # Database models
│   │   ├── routes/             # API routes
│   │   ├── middleware/         # Custom middleware
│   │   ├── config/             # Configuration files
│   │   └── app.js              # Main application file
│   ├── uploads/                # File upload directory
│   ├── package.json
│   ├── .env.example
│   └── README.md
├── assets/                  # Shared assets
│   ├── icon.png
│   └── presplash.png
├── main.py                  # Kivy main file (if using Kivy)
├── safecom.db              # Local SQLite database
└── README.md               # This file
```

## 🚀 Quick Start

### Prerequisites
- Node.js 16+ 
- MongoDB 4.4+
- Android Studio
- Firebase Project
- Git

### 1. Clone Repository
```bash
git clone https://github.com/yourusername/SafeCom-App.git
cd SafeCom-App
```

### 2. Backend Setup
```bash
cd backend
npm install
cp .env.example .env
# Edit .env with your configuration
npm run dev
```

### 3. Android Setup
```bash
cd android
# Open in Android Studio
# Sync Gradle files
# Run the app
```

### 4. Firebase Configuration

1. Create a Firebase project at https://console.firebase.google.com
2. Enable Authentication, Firestore, Cloud Messaging, and Storage
3. Download `google-services.json` and place in `android/app/`
4. Download Firebase Admin SDK service account key
5. Add Firebase configuration to backend `.env` file

## 🔧 Configuration

### Backend Environment Variables
```env
# Server
PORT=3000
NODE_ENV=development

# Database
MONGODB_URI=mongodb://localhost:27017/safecom

# JWT
JWT_SECRET=your-jwt-secret-key
JWT_EXPIRES_IN=7d

# Firebase
FIREBASE_PROJECT_ID=safecom-task-management
FIREBASE_PRIVATE_KEY_ID=your-private-key-id
FIREBASE_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----\nyour-private-key\n-----END PRIVATE KEY-----\n"
FIREBASE_CLIENT_EMAIL=firebase-adminsdk-xxxxx@safecom-task-management.iam.gserviceaccount.com
FIREBASE_CLIENT_ID=your-client-id
FIREBASE_CLIENT_X509_CERT_URL=https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-xxxxx%40safecom-task-management.iam.gserviceaccount.com

# CORS
ALLOWED_ORIGINS=http://localhost:3000,http://localhost:8080
```

### Android Configuration
- Update `android/app/src/main/res/values/strings.xml` with your API base URL
- Ensure `google-services.json` is properly configured
- Update package name if different from `com.safecom.taskmanagement`

## 📱 Android App Features

### Screens
- **Authentication**: Login, Register, Forgot Password
- **Dashboard**: Task overview, statistics, quick actions
- **Tasks**: Task list, create/edit task, task details
- **Messages**: Conversation list, chat interface
- **Profile**: User profile, settings, preferences
- **Notifications**: Push notification handling

### Key Components
- **TaskRepository**: Manages task data synchronization
- **MessageRepository**: Handles real-time messaging
- **AuthRepository**: User authentication and session management
- **NotificationService**: FCM push notification handling
- **FileUploadService**: Secure file upload functionality

## 🌐 API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - User login
- `GET /api/auth/profile` - Get user profile
- `PATCH /api/auth/profile` - Update profile

### Tasks
- `GET /api/tasks` - Get tasks with filters
- `POST /api/tasks` - Create new task
- `GET /api/tasks/:id` - Get single task
- `PATCH /api/tasks/:id` - Update task
- `DELETE /api/tasks/:id` - Delete task

### Messages
- `POST /api/messages` - Send message
- `GET /api/messages/conversations` - Get conversations
- `GET /api/messages/conversations/:id` - Get conversation messages

## 🔐 Security Features

- JWT-based authentication
- Password hashing with bcrypt
- Rate limiting on API endpoints
- Input validation and sanitization
- File upload restrictions
- CORS protection
- MongoDB injection protection

## 📊 Database Schema

### User Collection
```javascript
{
  username: String,
  email: String,
  password: String (hashed),
  fullName: String,
  role: String, // 'user', 'manager', 'admin'
  avatar: String,
  fcmTokens: [String],
  isActive: Boolean
}
```

### Task Collection
```javascript
{
  title: String,
  description: String,
  status: String, // 'PENDING', 'IN_PROGRESS', 'COMPLETED', 'OVERDUE', 'CANCELLED'
  priority: String, // 'Low', 'Medium', 'High', 'Critical'
  assignedTo: ObjectId,
  createdBy: ObjectId,
  dueDate: Date,
  category: String,
  tags: [String],
  comments: [Object]
}
```

### Message Collection
```javascript
{
  content: String,
  sender: ObjectId,
  recipient: ObjectId,
  conversationId: String,
  conversationType: String, // 'DIRECT', 'GROUP', 'TASK_CHAT'
  messageType: String, // 'TEXT', 'IMAGE', 'FILE'
  attachments: [Object],
  readBy: [Object]
}
```

## 🧪 Testing

### Backend Testing
```bash
cd backend
npm test
npm run test:coverage
```

### Android Testing
```bash
cd android
./gradlew test
./gradlew connectedAndroidTest
```

## 📦 Building & Deployment

### Backend Deployment
```bash
# Production build
npm run build

# Start production server
npm start

# Using PM2
pm2 start ecosystem.config.js
```

### Android Deployment
```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Using Buildozer (if configured)
buildozer android debug
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Make your changes
4. Add tests for new functionality
5. Commit your changes (`git commit -m 'Add amazing feature'`)
6. Push to the branch (`git push origin feature/amazing-feature`)
7. Open a Pull Request

### Coding Standards
- Follow Kotlin coding conventions for Android
- Use ESLint configuration for backend JavaScript
- Write unit tests for new features
- Update documentation for API changes

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Team

- **Frontend Developer**: Android/Kotlin development
- **Backend Developer**: Node.js/Express API development
- **UI/UX Designer**: Material Design implementation
- **DevOps Engineer**: Deployment and infrastructure

## 📞 Support

For support, email support@safecom.com or join our Slack channel.

## 🚧 Roadmap

### Version 2.0 (Upcoming)
- [ ] Video calling integration
- [ ] Advanced reporting and analytics
- [ ] Dark mode support
- [ ] Offline synchronization
- [ ] Multi-language support
- [ ] Integration with third-party calendar apps
- [ ] Advanced file preview capabilities
- [ ] Team workspace management

### Version 1.1 (Next Release)
- [ ] Task templates
- [ ] Recurring tasks
- [ ] Advanced search filters
- [ ] Bulk task operations
- [ ] Export functionality
- [ ] Enhanced notifications

## 🙏 Acknowledgments

- Material Design 3 guidelines
- Firebase platform
- MongoDB Atlas
- Socket.IO real-time engine
- Express.js framework
- Android Jetpack libraries
