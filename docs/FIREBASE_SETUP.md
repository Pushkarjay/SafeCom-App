# Firebase Setup Guide for SafeCom Task Management

**Project:** SafeCom Task Management App  
**Developer:** Pushkarjay Ajay (pushkarjay.ajay1@gmail.com)  
**Organization:** SafeCom  
**Date:** August 15, 2025

## � Prerequisites

### Required Accounts & Tools
- **Google Account**: For Firebase Console access
- **Internet Connection**: Stable connection for Firebase configuration
- **Web Browser**: Chrome, Firefox, Safari, or Edge (latest versions)
- **Development Environment**: Android Studio + Node.js backend setup

### Access Requirements
- Administrator access to create Firebase projects
- Ability to download configuration files
- Permission to enable billing (for production features, optional for development)

## 🔥 Step-by-Step Firebase Configuration

### Step 1: Create Firebase Project

#### 1.1 Access Firebase Console
1. Open your web browser
2. Navigate to [Firebase Console](https://console.firebase.google.com/)
3. Sign in with your Google account
4. Click **"Create a project"** or **"Add project"**

#### 1.2 Project Configuration
1. **Project Name**: Enter `SafeCom Task Management` or `safecom-task-management`
2. **Project ID**: Firebase will auto-generate (e.g., `safecom-task-management-12345`)
   - Note: Project ID cannot be changed later
   - Write down your project ID for later use
3. **Google Analytics**: 
   - Toggle **Enable Google Analytics** (recommended for production)
   - Choose existing Analytics account or create new one
4. Click **"Create project"**

#### 1.3 Wait for Project Creation
- Firebase will set up your project (usually takes 30-60 seconds)
- Click **"Continue"** when setup is complete

### Step 2: Add Android App to Firebase

#### 2.1 Add Android Application
1. In Firebase Console, click the **Android icon** (or "Add app" → Android)
2. **Register App Dialog** appears

#### 2.2 App Registration Details
1. **Android package name**: `com.safecom.taskmanagement`
   - **Important**: Must match exactly with your Android app's package name
   - Found in `android/app/src/main/AndroidManifest.xml`
2. **App nickname**: `SafeCom Android App` (optional but recommended)
3. **Debug signing certificate SHA-1**: (Optional for development, required for production)
   
   **To get SHA-1 certificate fingerprint:**
   ```bash
   # For debug certificate (development)
   keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
   
   # For release certificate (production)
   keytool -list -v -keystore your-release-key.keystore -alias your-key-alias
   ```
   
   **Windows users:**
   ```cmd
   keytool -list -v -keystore %USERPROFILE%\.android\debug.keystore -alias androiddebugkey -storepass android -keypass android
   ```

4. Click **"Register app"**

### Step 3: Download and Configure google-services.json

#### 3.1 Download Configuration File
1. Click **"Download google-services.json"**
2. Save the file (do not rename it)

#### 3.2 Place Configuration File
1. Copy `google-services.json` to: `SafeCom-App/android/app/google-services.json`
2. **Verify location**: The file should be directly inside the `app` folder, not in subfolders

#### 3.3 Security Note
⚠️ **Important**: Never commit `google-services.json` to public repositories
- Add to `.gitignore` if not already included
- Contains sensitive project configuration

### Step 4: Enable Firebase Services

#### 4.1 Authentication Setup
1. **Navigate**: Go to **Authentication** in Firebase Console sidebar
2. **Get Started**: Click **"Get started"** if first time
3. **Sign-in Methods**:
   - Click **"Sign-in method"** tab
   - **Enable Email/Password**:
     - Click on "Email/Password" provider
     - Toggle **"Enable"** switch
     - Toggle **"Email link (passwordless sign-in)"** if needed
     - Click **"Save"**
   - **Enable Google Sign-in** (Optional):
     - Click on "Google" provider
     - Toggle **"Enable"** switch
     - Enter **Project support email**
     - Click **"Save"**

4. **Authorized Domains**: 
   - Ensure `localhost` is in authorized domains for development
   - Add your production domain when deploying

#### 4.2 Cloud Firestore Setup
1. **Navigate**: Go to **Firestore Database** in sidebar
2. **Create Database**:
   - Click **"Create database"**
   - **Security Rules**: Choose **"Start in test mode"** for development
     - Test mode allows all read/write operations
     - Remember to update rules before production
   - **Location**: Choose region closest to your users
     - Cannot be changed later
     - Recommended: `us-central1` (Iowa) for general use
   - Click **"Done"**

3. **Database Structure** (Created automatically by app):
   ```
   /users/{userId}
   /tasks/{taskId}
   /messages/{messageId}
   /conversations/{conversationId}
   ```

#### 4.3 Cloud Storage Setup
1. **Navigate**: Go to **Storage** in sidebar
2. **Get Started**:
   - Click **"Get started"**
   - **Security Rules**: Choose **"Start in test mode"**
   - **Location**: Use same location as Firestore
   - Click **"Done"**

3. **Storage Buckets** (for file uploads):
   - Default bucket: `your-project-id.appspot.com`
   - Used for: User avatars, task attachments, message files

#### 4.4 Cloud Messaging (FCM) Setup
1. **Navigate**: Go to **Cloud Messaging** in sidebar
2. **Configuration**: FCM is automatically configured when you add Android app
3. **Server Key**: 
   - Go to **Project Settings** → **Cloud Messaging** tab
   - Copy **Server key** for backend configuration
   - Note **Sender ID** for client configuration

### Step 5: Backend Firebase Configuration

#### 5.1 Generate Service Account Key
1. **Navigate**: Go to **Project Settings** (gear icon) → **Service accounts** tab
2. **Generate Key**:
   - Select **"Firebase Admin SDK"**
   - Click **"Generate new private key"**
   - Confirm by clicking **"Generate key"**
   - Save the downloaded JSON file securely

#### 5.2 Extract Configuration Values
From the downloaded JSON file, extract these values for your backend `.env`:

```json
{
  "type": "service_account",
  "project_id": "your-project-id",
  "private_key_id": "key-id",
  "private_key": "-----BEGIN PRIVATE KEY-----\n...\n-----END PRIVATE KEY-----\n",
  "client_email": "firebase-adminsdk-xxxxx@your-project.iam.gserviceaccount.com",
  "client_id": "client-id",
  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
  "token_uri": "https://oauth2.googleapis.com/token",
  "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
  "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/..."
}
```

#### 5.3 Configure Backend Environment
Add to your backend `.env` file:

```env
# Firebase Configuration
FIREBASE_PROJECT_ID=your-project-id
FIREBASE_CLIENT_EMAIL=firebase-adminsdk-xxxxx@your-project.iam.gserviceaccount.com
FIREBASE_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----\nYour-Private-Key-Here\n-----END PRIVATE KEY-----\n"
```

**Note**: Keep the quotes around FIREBASE_PRIVATE_KEY and preserve the \n characters

### Step 6: Security Rules Configuration

#### 6.1 Firestore Security Rules
1. **Navigate**: Go to **Firestore Database** → **Rules** tab
2. **Update Rules**: Replace default rules with:

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

3. Click **"Publish"**

#### 6.2 Storage Security Rules
1. **Navigate**: Go to **Storage** → **Rules** tab
2. **Update Rules**: Replace default rules with:

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    // Allow authenticated users to upload files
    match /uploads/{userId}/{allPaths=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Allow public read access to user avatars
    match /avatars/{allPaths=**} {
      allow read: if true;
      allow write: if request.auth != null;
    }
  }
}
```

3. Click **"Publish"**

## ✅ Verification & Testing

### Step 1: Verify Android Configuration
1. **Build Project**: Ensure Android project builds successfully
2. **Check Logs**: Look for Firebase initialization messages in Android Studio logs
3. **Test Authentication**: Try registering a test user

### Step 2: Verify Backend Configuration
1. **Start Backend**: Ensure backend starts without Firebase errors
2. **Check Logs**: Look for "Firebase Admin initialized successfully" message
3. **Test API**: Try creating a test user via API

### Step 3: Test Firebase Services

#### Authentication Test
```bash
# Test user registration via API
curl -X POST http://localhost:3000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"testpass123","name":"Test User"}'
```

#### Firestore Test
1. Go to Firestore Database in Firebase Console
2. Check if collections are created when app is used
3. Verify data appears when users interact with app

#### Storage Test
1. Try uploading a file through the app
2. Check Storage section in Firebase Console
3. Verify files appear in the bucket

## 🔧 Configuration Summary

After completing all steps, you should have:

### ✅ Firebase Project Setup
- [x] Firebase project created
- [x] Android app registered
- [x] google-services.json downloaded and placed

### ✅ Services Enabled
- [x] Authentication (Email/Password)
- [x] Cloud Firestore (with security rules)
- [x] Cloud Storage (with security rules)
- [x] Cloud Messaging (FCM)

### ✅ Backend Integration
- [x] Service account key generated
- [x] Environment variables configured
- [x] Firebase Admin SDK initialized

### ✅ Security Configuration
- [x] Firestore security rules
- [x] Storage security rules
- [x] Authentication domains configured

## 🔑 Important Security Notes

1. **Never commit sensitive files**:
   - `google-services.json`
   - Service account JSON files
   - Private keys in `.env` files

2. **Update security rules for production**:
   - Current rules are for development only
   - Implement proper data validation
   - Add additional security constraints

3. **Monitor usage**:
   - Set up Firebase usage alerts
   - Monitor authentication attempts
   - Review security rules regularly

## 📞 Support

If you encounter issues:
1. Check Firebase Console for error messages
2. Review Android Studio and backend logs
3. Verify all configuration files are in correct locations
4. Ensure all required services are enabled
5. Contact: pushkarjay.ajay1@gmail.com
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
