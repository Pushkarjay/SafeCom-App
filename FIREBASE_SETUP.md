# Firebase Setup Guide for SafeCom Task Management

## 🔥 Firebase Configuration Steps

### 1. Create Firebase Project
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Create a project"
3. Enter project name: "SafeCom Task Management"
4. Enable Google Analytics (recommended)
5. Choose your analytics location

### 2. Add Android App to Firebase
1. Click "Add app" → Android icon
2. Android package name: `com.safecom.taskmanagement`
3. App nickname: "SafeCom Android"
4. SHA-1 certificate fingerprint (for release builds)

### 3. Download Configuration File
1. Download `google-services.json`
2. Place it in: `android/app/google-services.json`

### 4. Enable Firebase Services

#### Authentication
1. Go to Authentication → Sign-in method
2. Enable Email/Password provider
3. Enable Google Sign-In (optional)
4. Configure authorized domains

#### Cloud Firestore
1. Go to Firestore Database
2. Create database in production mode
3. Set up security rules (see firestore.rules)

#### Cloud Messaging (FCM)
1. Go to Cloud Messaging
2. Server key will be auto-generated
3. Copy server key for backend use

#### Cloud Storage
1. Go to Storage
2. Get started with default bucket
3. Configure security rules

### 5. Firebase SDK Integration
Already configured in build.gradle files.

## 🔑 Firebase Configuration Values

Copy these values from your Firebase project:

```json
{
  "project_id": "your-project-id",
  "project_number": "123456789",
  "api_key": "your-api-key",
  "app_id": "1:123456789:android:abc123",
  "storage_bucket": "your-project.appspot.com",
  "fcm_sender_id": "123456789"
}
```

## 📝 Security Rules

### Firestore Rules (firestore.rules)
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users can read/write their own data
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Tasks - users can see tasks assigned to them or created by them
    match /tasks/{taskId} {
      allow read, write: if request.auth != null && 
        (resource.data.assignedTo == request.auth.uid || 
         resource.data.createdBy == request.auth.uid);
    }
    
    // Messages - users can see conversations they're part of
    match /messages/{messageId} {
      allow read, write: if request.auth != null && 
        (resource.data.senderId == request.auth.uid || 
         resource.data.receiverId == request.auth.uid);
    }
    
    // Conversations
    match /conversations/{conversationId} {
      allow read, write: if request.auth != null && 
        request.auth.uid in resource.data.participantIds;
    }
  }
}
```

### Storage Rules (storage.rules)
```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /users/{userId}/{allPaths=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    match /tasks/{taskId}/{allPaths=**} {
      allow read, write: if request.auth != null;
    }
    
    match /messages/{conversationId}/{allPaths=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```
