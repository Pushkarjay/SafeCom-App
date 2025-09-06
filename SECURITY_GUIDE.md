# 🔒 SafeCom Security Configuration Guide

## 🚨 Security Alert Resolution

This guide addresses the GitGuardian security alert and provides proper configuration for SafeCom project security.

## ✅ Recent Security Fixes Applied

### 1. Removed Hardcoded Credentials
- **Issue**: Demo passwords were hardcoded in `SafeCom-Frontend/js/auth.js`
- **Fix**: Removed hardcoded passwords, now requires manual entry
- **Commit**: `351434c` in SafeCom-Frontend repository

### 2. Updated Firebase Configuration
- **Issue**: Incomplete Firebase configuration
- **Fix**: Added proper Firebase project configuration with environment variables

## 🔧 Required Configuration Steps

### Step 1: Backend Environment Variables

Create `backend/.env` file with these values:

```bash
# Environment Configuration
NODE_ENV=development
PORT=3000

# Database Configuration
MONGODB_URI=mongodb://localhost:27017/safecom
DATABASE_NAME=safecom

# JWT Configuration (CHANGE THESE!)
JWT_SECRET=your-super-secret-jwt-key-change-this-in-production
JWT_EXPIRES_IN=24h
REFRESH_TOKEN_SECRET=your-refresh-token-secret-change-this
REFRESH_TOKEN_EXPIRES_IN=7d

# Firebase Configuration
FIREBASE_PROJECT_ID=safecom-task-management
FIREBASE_PROJECT_NUMBER=344799045102
FIREBASE_WEB_API_KEY=AIzaSyCxoXVuxbQxiNMkJnXyF1PKpXmLoEo-cMU
FIREBASE_PRIVATE_KEY_ID=your-private-key-id-from-service-account-json
FIREBASE_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----\nyour-private-key-from-service-account-json\n-----END PRIVATE KEY-----\n"
FIREBASE_CLIENT_EMAIL=firebase-adminsdk-xxxxx@safecom-task-management.iam.gserviceaccount.com
FIREBASE_CLIENT_ID=your-client-id-from-service-account-json
FIREBASE_CLIENT_X509_CERT_URL=https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-xxxxx%40safecom-task-management.iam.gserviceaccount.com

# For deployment platforms like Render (alternative to FIREBASE_PRIVATE_KEY)
# FIREBASE_PRIVATE_KEY_BASE64=your-base64-encoded-private-key

# CORS Configuration
ALLOWED_ORIGINS=http://localhost:3000,http://localhost:8080,https://yourdomain.com
```

### Step 2: Firebase Service Account Setup

1. **Go to Firebase Console**: https://console.firebase.google.com/
2. **Select Project**: SafeCom Task Management
3. **Navigate to**: Project Settings → Service Accounts
4. **Generate New Private Key**: Download the JSON file
5. **Save File**: As `backend/src/config/safecom-task-management-firebase-adminsdk-xxxxx.json`
6. **Extract Values**: Copy the values to your `.env` file

### Step 3: Flutter Firebase Configuration

1. **Download google-services.json**: From Firebase Console → Project Settings → Your Android App
2. **Place File**: In `flutter_app/android/app/google-services.json`
3. **Update Config**: The file `flutter_app/lib/config/firebase_config.dart` is already configured

### Step 4: Web Frontend Firebase Configuration

The web frontend configuration is already updated in `SafeCom-Frontend/js/config.js`. To add Web app support:

1. **Firebase Console**: Add a Web app to your project
2. **Copy Web App ID**: Replace `YOUR_WEB_APP_ID` in `config.js`
3. **Update Domain**: Add your domain to Firebase Auth authorized domains

## 🛡️ Security Best Practices Applied

### ✅ Environment Variables
- All sensitive data moved to environment variables
- Example `.env` file provided without real credentials
- Production values must be set separately

### ✅ .gitignore Protection
```ignore
# Environment files
.env
.env.local
.env.production

# Firebase service account keys
firebase-service-account*.json
*firebase-adminsdk*.json
google-services.json
GoogleService-Info.plist
```

### ✅ Credential Management
- No hardcoded passwords in source code
- Demo credentials require manual entry
- Service account keys excluded from repository

### ✅ Firebase Security Rules
Configure these in Firebase Console:

#### Firestore Rules
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users can only access their own data
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Tasks accessible by authenticated users with proper role
    match /tasks/{taskId} {
      allow read, write: if request.auth != null;
    }
    
    // Messages accessible by authenticated users
    match /messages/{messageId} {
      allow read, write: if request.auth != null;
    }
  }
}
```

#### Storage Rules
```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

## 🔑 Demo Credentials

For testing purposes, create these users in Firebase Auth or your backend:

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@safecom.com | admin@123 |
| Customer | customer@safecom.com | customer@123 |
| Employee | employee@safecom.com | employee@123 |

**⚠️ Important**: These are for development only. Use strong passwords in production.

## 🚀 Deployment Security

### For Render/Heroku
Use base64 encoded Firebase private key:
```bash
# Convert private key to base64
echo "your-private-key-content" | base64

# Set environment variable
FIREBASE_PRIVATE_KEY_BASE64=base64-encoded-content
```

### For Firebase Hosting
```bash
# Deploy frontend
cd SafeCom-Frontend
firebase deploy --only hosting

# Deploy backend functions (if using Firebase Functions)
cd backend
firebase deploy --only functions
```

## 📱 Mobile App Security

### Android
1. **Enable App Signing**: Use Google Play App Signing
2. **Obfuscate Code**: Enable ProGuard/R8 in release builds
3. **Certificate Pinning**: Implement for API calls

### iOS (when ready)
1. **App Transport Security**: Ensure HTTPS only
2. **Keychain**: Store sensitive data in iOS Keychain
3. **Code Obfuscation**: Use appropriate tools

## 🔍 Security Checklist

### Before Production
- [ ] Change all default passwords and secrets
- [ ] Enable Firebase App Check
- [ ] Set up proper CORS origins
- [ ] Configure rate limiting
- [ ] Enable HTTPS only
- [ ] Set up monitoring and alerts
- [ ] Review Firebase security rules
- [ ] Enable 2FA for Firebase project
- [ ] Audit dependencies for vulnerabilities
- [ ] Set up backup strategies

### Monitoring
- [ ] Enable Firebase Security Rules monitoring
- [ ] Set up error tracking (Sentry, etc.)
- [ ] Monitor API usage and unusual patterns
- [ ] Regular security audits
- [ ] Keep dependencies updated

## 🆘 Emergency Response

If you suspect a security breach:

1. **Immediately**: Revoke compromised credentials
2. **Rotate**: All API keys and secrets
3. **Review**: Recent access logs
4. **Update**: All team members
5. **Document**: The incident and response

## 📞 Support

For security questions or concerns:
- Review Firebase Security Documentation
- Check GitHub Security Advisories
- Run `npm audit` for dependency vulnerabilities
- Use GitGuardian monitoring (already enabled)

---

**Last Updated**: September 6, 2025
**Next Review**: Monthly security review recommended
