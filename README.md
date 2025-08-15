# SafeCom Task Management App

A comprehensive task management application built with Android (Kotlin) frontend and Node.js backend, featuring real-time messaging, push notifications, and collaborative task management.

**Developed by:** [Pushkarjay Ajay](mailto:pushkarjay.ajay1@gmail.com)  
**Organization:** SafeCom  
**Repository:** https://github.com/Pushkarjay/SafeCom-App  

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

## 📚 Documentation

### ⚡ Quick References
- **[Quick Start Guide](docs/QUICK_START.md)** - One-minute setup overview for experienced developers
- **[Troubleshooting Guide](docs/TROUBLESHOOTING.md)** - Common issues and solutions

### 📋 Setup Guides
- **[README (This file)](README.md)** - Project overview and detailed setup guide
- **[APK Build Guide](docs/APK_BUILD_GUIDE.md)** - Detailed Android APK building instructions
- **[Signed APK Guide](docs/SIGNED_APK_GUIDE.md)** - 🔐 Production-ready signed APK building
- **[Backend Setup Guide](docs/BACKEND_SETUP.md)** - Complete backend setup and configuration
- **[Firebase Setup Guide](docs/FIREBASE_SETUP.md)** - Step-by-step Firebase configuration

### 📄 Project Documentation
- **[Software Requirements Specification (SRS)](SRS.md)** - Complete system requirements and specifications
- **[Credits](CREDITS.md)** - Project contributors and acknowledgments
- **[License](LICENSE)** - MIT License details

### 🔗 Quick Links
- [Android Development Setup](#3-android-setup)
- [Backend Development Setup](#2-backend-setup)
- [Firebase Configuration](#4-firebase-configuration)
- [Common Issues & Solutions](docs/TROUBLESHOOTING.md)
- [Contributing Guidelines](#🤝-contributing)
- [Common Issues & Solutions](TROUBLESHOOTING.md)
- [Contributing Guidelines](#🤝-contributing)

## 🚀 Quick Start

### 📋 Prerequisites

#### System Requirements
- **Operating System**: Windows 10/11, macOS 10.15+, or Ubuntu 18.04+
- **RAM**: Minimum 8GB (16GB recommended for Android development)
- **Storage**: At least 10GB free space
- **Internet**: Stable connection for downloading dependencies

#### Required Software

1. **Java Development Kit (JDK)**
   - **Version**: JDK 11 or higher (JDK 17 recommended)
   - **Download**: [Eclipse Temurin](https://adoptium.net/) or [Oracle JDK](https://www.oracle.com/java/technologies/downloads/)
   - **Installation**: Follow platform-specific installation guide
   - **Verification**: Run `java -version` and `javac -version` in terminal

2. **Node.js & npm**
   - **Version**: Node.js 16.0.0 or higher (18.x LTS recommended)
   - **Download**: [Node.js Official](https://nodejs.org/)
   - **Verification**: Run `node --version` and `npm --version` in terminal

3. **Android Studio**
   - **Version**: Android Studio Electric Eel (2022.1.1) or newer
   - **Download**: [Android Studio](https://developer.android.com/studio)
   - **Components**: Install Android SDK, Android SDK Platform-Tools, Android SDK Build-Tools
   - **Android SDK**: API Level 33 (Android 13) minimum, API Level 34 (Android 14) recommended

4. **Git Version Control**
   - **Download**: [Git SCM](https://git-scm.com/)
   - **Verification**: Run `git --version` in terminal

5. **Database (Choose One)**
   - **MongoDB**: Version 4.4+ ([MongoDB Community](https://www.mongodb.com/try/download/community))
   - **MongoDB Atlas**: Cloud-hosted MongoDB ([MongoDB Atlas](https://www.mongodb.com/atlas))

6. **Firebase Project**
   - Google account for Firebase Console access
   - Firebase project with enabled services (Authentication, Firestore, Storage, FCM)

#### Optional Tools
- **Postman** or **Insomnia**: For API testing
- **MongoDB Compass**: GUI for MongoDB
- **VS Code**: For backend development

### 📱 1. Clone Repository
```bash
# Clone the repository
git clone https://github.com/Pushkarjay/SafeCom-App.git

# Navigate to project directory
cd SafeCom-App

# Verify project structure
dir  # On Windows
ls   # On macOS/Linux
```

### 🌐 2. Backend Setup

#### Step 1: Install Dependencies
```bash
# Navigate to backend directory
cd backend

# Install Node.js dependencies
npm install

# Verify installation
npm list
```

#### Step 2: Environment Configuration
```bash
# Copy example environment file
cp .env.example .env    # On macOS/Linux
copy .env.example .env  # On Windows

# Edit .env file with your configuration
# Use your preferred text editor (VS Code, Notepad++, etc.)
notepad .env            # On Windows
nano .env               # On Linux
open -a TextEdit .env   # On macOS
```

#### Step 3: Database Setup
```bash
# Option A: Local MongoDB
# Make sure MongoDB is running locally on port 27017

# Option B: MongoDB Atlas
# Update MONGODB_URI in .env with your Atlas connection string
```

#### Step 4: Start Backend Server
```bash
# Development mode with auto-reload
npm run dev

# Production mode
npm start

# Verify server is running
# Open browser: http://localhost:3000
```

### 📱 3. Android Setup

#### Step 1: Prepare Development Environment
1. **Open Android Studio**
2. **SDK Manager**: Tools → SDK Manager
   - Install Android SDK Platform 33 & 34
   - Install Android SDK Build-Tools 33.0.0+
   - Install Android Emulator (if testing without physical device)

#### Step 2: Import Project
```bash
# Open Android Studio
# File → Open → Navigate to SafeCom-App/android folder
# Select the 'android' folder and click OK
```

#### Step 3: Gradle Sync
1. **Auto-sync**: Android Studio should automatically sync Gradle files
2. **Manual sync**: If not, click "Sync Now" when prompted
3. **Troubleshooting**: If sync fails, check:
   - JDK version (must be JDK 11+)
   - Internet connection
   - Gradle wrapper permissions

#### Step 4: Build Project
```bash
# Command line build (from android directory)
cd android

# Debug build
./gradlew assembleDebug      # On macOS/Linux
.\gradlew.bat assembleDebug  # On Windows

# Release build
./gradlew assembleRelease      # On macOS/Linux
.\gradlew.bat assembleRelease  # On Windows
```

#### Step 5: Run Application
1. **Physical Device**:
   - Enable Developer Options and USB Debugging
   - Connect device via USB
   - Click "Run" in Android Studio

2. **Emulator**:
   - Create AVD (Android Virtual Device) in Android Studio
   - Start emulator and click "Run"

### 🔥 4. Firebase Configuration

#### Step 1: Create Firebase Project
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Create a project" or "Add project"
3. Enter project name: `safecom-task-management`
4. Enable Google Analytics (recommended)
5. Choose or create Analytics account
6. Click "Create project"

#### Step 2: Add Android App
1. In Firebase Console, click "Add app" → Android icon
2. **Android package name**: `com.safecom.taskmanagement`
3. **App nickname**: `SafeCom Android App`
4. **Debug signing certificate SHA-1**: (Optional for development)
   ```bash
   # Get debug certificate fingerprint
   keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
   ```

#### Step 3: Download Configuration
1. Download `google-services.json` file
2. Place in `android/app/google-services.json`
3. **Important**: Never commit this file to public repositories

#### Step 4: Enable Firebase Services
1. **Authentication**:
   - Go to Authentication → Sign-in method
   - Enable "Email/Password" provider
   - Enable "Google" provider (optional)

2. **Cloud Firestore**:
   - Go to Firestore Database
   - Click "Create database"
   - Choose "Start in test mode" (for development)
   - Select location closest to your users

3. **Cloud Storage**:
   - Go to Storage
   - Click "Get started"
   - Choose "Start in test mode"
   - Select storage location

4. **Cloud Messaging (FCM)**:
   - Go to Cloud Messaging
   - Note the Server Key for backend configuration

#### Step 5: Backend Firebase Configuration
1. Go to Project Settings → Service accounts
2. Click "Generate new private key"
3. Download the JSON file
4. Add Firebase configuration to backend `.env`:
```env
FIREBASE_PROJECT_ID=your-project-id
FIREBASE_CLIENT_EMAIL=firebase-adminsdk-xxxxx@your-project.iam.gserviceaccount.com
FIREBASE_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----\nYour-Private-Key\n-----END PRIVATE KEY-----\n"
```

### ✅ 5. Verification & Testing

#### Backend Verification
```bash
# Test API endpoints
curl http://localhost:3000/api/health

# Check MongoDB connection
# Look for "Connected to MongoDB" in server logs
```

#### Android Verification
1. **Build Success**: Ensure Gradle build completes without errors
2. **App Launch**: App should start and show splash screen
3. **Firebase Connection**: Check logs for Firebase initialization success
4. **API Connection**: Verify app can communicate with backend

#### Common Issues & Solutions
- **Gradle sync failed**: Check JDK version and internet connection
- **MongoDB connection error**: Verify MongoDB is running and connection string is correct
- **Firebase authentication failed**: Verify `google-services.json` is in correct location
- **Build tools not found**: Install required Android SDK components

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

## 👥 About

### Developer
**Pushkarjay Ajay**  
- 📧 Email: [pushkarjay.ajay1@gmail.com](mailto:pushkarjay.ajay1@gmail.com)  
- 🏢 Organization: SafeCom  
- 🔗 GitHub: [@Pushkarjay](https://github.com/Pushkarjay)  

### Project Details
- **Project Type:** Full-Stack Mobile Application  
- **Technology Stack:** Android (Kotlin) + Node.js Backend  
- **Development Period:** 2025  
- **Status:** Production Ready  

### Skills Demonstrated
- 📱 **Android Development**: Kotlin, MVVM, Material Design 3, Jetpack Compose
- 🌐 **Backend Development**: Node.js, Express.js, MongoDB, Socket.IO
- 🔐 **Security**: JWT Authentication, Firebase Integration, API Security
- 📊 **Database**: MongoDB, Room (SQLite), Data Modeling
- 🔄 **Real-time Systems**: Socket.IO, Push Notifications, Live Chat
- 🛠️ **DevOps**: Git, API Design, Documentation, Testing

## 👥 Team

## 👥 Development Team

**Lead Developer & Architect:** Pushkarjay Ajay
- Full-stack development (Android + Backend)
- System architecture and design
- Database modeling and API development
- Real-time messaging implementation
- Security and authentication systems

**Organization:** SafeCom

## 📞 Support & Contact

**Developer Contact:**
- 📧 Email: [pushkarjay.ajay1@gmail.com](mailto:pushkarjay.ajay1@gmail.com)
- 🔗 GitHub: [@Pushkarjay](https://github.com/Pushkarjay)
- 🏢 Organization: SafeCom

**Project Repository:**
- 📂 GitHub: [SafeCom-App](https://github.com/Pushkarjay/SafeCom-App)
- 📋 Issues: [Report Issues](https://github.com/Pushkarjay/SafeCom-App/issues)
- 📖 Wiki: [Documentation](https://github.com/Pushkarjay/SafeCom-App/wiki)

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
