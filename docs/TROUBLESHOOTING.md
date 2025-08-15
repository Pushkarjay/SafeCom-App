# SafeCom App Troubleshooting Guide

**Project:** SafeCom Task Management App  
**Developer:** Pushkarjay Ajay (pushkarjay.ajay1@gmail.com)  
**Organization:** SafeCom  
**Date:** August 15, 2025

## 🔧 Common Issues & Solutions

### Android Build Issues

#### 1. Gradle Sync Failed
**Problem**: Gradle sync fails with various errors

**Solutions**:
```bash
# Check Java version
java -version  # Should be JDK 11+

# Check JAVA_HOME
echo $JAVA_HOME  # Should point to JDK installation

# Clear Gradle cache
./gradlew clean
./gradlew --stop
rm -rf ~/.gradle/caches/  # Linux/macOS
rmdir /s %USERPROFILE%\.gradle\caches  # Windows

# Invalidate caches in Android Studio
File → Invalidate and Restart → Invalidate and Restart
```

#### 2. "SDK not found" Error
**Problem**: Android SDK not properly configured

**Solutions**:
```bash
# Set ANDROID_HOME environment variable
export ANDROID_HOME=$HOME/Android/Sdk  # Linux/macOS
set ANDROID_HOME=C:\Android\Sdk  # Windows

# Add to PATH
export PATH=$PATH:$ANDROID_HOME/platform-tools:$ANDROID_HOME/tools

# Verify SDK location in Android Studio
File → Project Structure → SDK Location
```

#### 3. Compilation Errors (Fixed in latest version)
**Problem**: Missing parameters in mapper functions

**Status**: ✅ **RESOLVED** - Fixed in commit 4b95b09
- Fixed UserDto.toDomainModel() missing parameters
- Removed duplicate TaskDto.toDomainModel() function
- Fixed User.toUpdateProfileDto() missing settings parameter

**If you encounter compilation errors**:
```bash
# Pull latest changes
git pull origin main

# Clean and rebuild
./gradlew clean
./gradlew assembleDebug
```

#### 4. APK Build Fails
**Problem**: APK generation fails with various errors

**Solutions**:
```bash
# Check available space
df -h  # Linux/macOS
dir   # Windows

# Clean previous builds
./gradlew clean
rm -rf app/build  # Linux/macOS
rmdir /s app\build  # Windows

# Build with verbose logging
./gradlew assembleRelease --info --stacktrace

# Check for missing dependencies
./gradlew dependencies
```

### Backend Issues

#### 1. "npm install" Fails
**Problem**: Node.js dependencies installation fails

**Solutions**:
```bash
# Clear npm cache
npm cache clean --force

# Delete node_modules and package-lock.json
rm -rf node_modules package-lock.json
npm install

# Use different registry if needed
npm install --registry https://registry.npmjs.org/

# Check Node.js version
node --version  # Should be 16+
npm --version   # Should be 8+
```

#### 2. MongoDB Connection Error
**Problem**: Cannot connect to MongoDB database

**Solutions**:
```bash
# Check if MongoDB is running
# Local MongoDB
mongo --eval 'db.runCommand({ connectionStatus: 1 })'

# Check MongoDB service status
# Windows
net start | findstr MongoDB
# Linux
sudo systemctl status mongod
# macOS
brew services list | grep mongodb

# Start MongoDB service
# Windows
net start MongoDB
# Linux
sudo systemctl start mongod
# macOS
brew services start mongodb/brew/mongodb-community

# For MongoDB Atlas
# Verify connection string in .env
# Check network access settings in Atlas
# Ensure IP address is whitelisted
```

#### 3. Firebase Authentication Error
**Problem**: Firebase Admin SDK initialization fails

**Solutions**:
```bash
# Verify service account key file exists
ls -la firebase-service-account.json

# Check environment variables
echo $FIREBASE_PROJECT_ID
echo $FIREBASE_CLIENT_EMAIL

# Verify private key format (should have \n characters)
# Ensure FIREBASE_PRIVATE_KEY is properly escaped

# Test Firebase connection
node -e "
const admin = require('firebase-admin');
const serviceAccount = require('./firebase-service-account.json');
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});
console.log('Firebase initialized successfully');
"
```

#### 4. Port Already in Use
**Problem**: Backend cannot start because port 3000 is occupied

**Solutions**:
```bash
# Find process using port 3000
# Linux/macOS
lsof -i :3000
netstat -tulpn | grep :3000

# Windows
netstat -ano | findstr :3000

# Kill process
# Linux/macOS
sudo kill -9 <PID>

# Windows
taskkill /PID <PID> /F

# Or change port in .env file
PORT=3001
```

### Firebase Issues

#### 1. google-services.json Not Found
**Problem**: Android build fails due to missing Firebase configuration

**Solutions**:
```bash
# Verify file location
ls -la android/app/google-services.json

# Download again from Firebase Console
# Project Settings → General → Your apps → Download google-services.json

# Ensure file is not in .gitignore (for development)
# But add to .gitignore for production
```

#### 2. Firebase Authentication Not Working
**Problem**: Users cannot sign in/register

**Solutions**:
1. **Check Firebase Console**:
   - Authentication → Sign-in method
   - Ensure Email/Password is enabled
   - Check authorized domains

2. **Check Network Connection**:
   ```bash
   # Test Firebase connectivity
   curl -I https://firebase.googleapis.com/
   ```

3. **Verify API Keys**:
   - Check google-services.json is current
   - Verify SHA-1 certificate if using Google Sign-in

#### 3. Firestore Permission Denied
**Problem**: Database operations fail with permission denied

**Solutions**:
1. **Check Security Rules**:
   ```javascript
   // Temporary test rules (DEVELOPMENT ONLY)
   rules_version = '2';
   service cloud.firestore {
     match /databases/{database}/documents {
       match /{document=**} {
         allow read, write: if true;
       }
     }
   }
   ```

2. **Verify Authentication**:
   - Ensure user is properly authenticated
   - Check auth token validity

### Network & Connectivity Issues

#### 1. API Endpoints Not Reachable
**Problem**: Android app cannot connect to backend

**Solutions**:
```bash
# Check backend server status
curl http://localhost:3000/api/health

# For Android emulator, use 10.0.2.2 instead of localhost
# Update base URL in Android app
BASE_URL=http://10.0.2.2:3000

# Check firewall settings
# Windows: Allow Node.js through Windows Firewall
# Linux: Check iptables rules
# macOS: Check Security & Privacy settings
```

#### 2. CORS Errors
**Problem**: Cross-origin requests blocked

**Solutions**:
```javascript
// Update CORS configuration in backend
const corsOptions = {
  origin: [
    'http://localhost:3000',
    'http://localhost:8080',
    'http://10.0.2.2:3000',  // Android emulator
    'http://192.168.1.100:3000'  // Local network
  ],
  credentials: true
};
```

### Development Environment Issues

#### 1. Android Studio Performance
**Problem**: Android Studio is slow or unresponsive

**Solutions**:
```bash
# Increase heap size
# Add to studio.vmoptions:
-Xms2048m
-Xmx8192m
-XX:ReservedCodeCacheSize=1024m

# Disable unnecessary plugins
File → Settings → Plugins

# Clear caches
File → Invalidate and Restart

# Use offline Gradle
File → Settings → Build → Gradle → Work offline
```

#### 2. VS Code Extensions for Backend Development
**Recommended Extensions**:
- Node.js Extension Pack
- ESLint
- Prettier
- MongoDB for VS Code
- Firebase Explorer
- REST Client

### Performance Issues

#### 1. Slow App Performance
**Problem**: Android app is slow or laggy

**Solutions**:
```bash
# Enable R8/ProGuard for release builds
# In app/build.gradle:
buildTypes {
    release {
        minifyEnabled true
        proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
    }
}

# Optimize images and resources
# Use vector drawables instead of PNG when possible
# Compress images before adding to project
```

#### 2. Backend Performance Issues
**Problem**: API responses are slow

**Solutions**:
```javascript
// Add database indexing
// In MongoDB:
db.users.createIndex({ "email": 1 })
db.tasks.createIndex({ "assignedTo": 1, "status": 1 })
db.messages.createIndex({ "conversationId": 1, "timestamp": -1 })

// Implement response caching
// Add compression middleware
const compression = require('compression');
app.use(compression());

// Optimize database queries
// Use projection to limit returned fields
// Implement pagination for large datasets
```

## 🔍 Debugging Steps

### Android Debugging
```bash
# View device logs
adb logcat

# Filter logs by package
adb logcat | grep com.safecom.taskmanagement

# View app-specific logs
adb logcat -s "SafeComApp"

# Check app memory usage
adb shell dumpsys meminfo com.safecom.taskmanagement

# Check app permissions
adb shell dumpsys package com.safecom.taskmanagement
```

### Backend Debugging
```bash
# Start with debug logging
NODE_ENV=development npm run dev

# Use debugger
node --inspect src/app.js

# Monitor processes
ps aux | grep node

# Check memory usage
node --max-old-space-size=4096 src/app.js
```

### Database Debugging
```bash
# MongoDB query profiler
db.setProfilingLevel(2)
db.system.profile.find().pretty()

# Check database stats
db.stats()
db.tasks.stats()

# Monitor real-time operations
db.currentOp()
```

## 📞 Getting Help

### Before Seeking Help
1. **Check this troubleshooting guide**
2. **Review error messages carefully**
3. **Check recent changes to code**
4. **Verify all prerequisites are met**
5. **Test with minimal configuration**

### Information to Provide
- **Operating System** and version
- **Node.js version** (`node --version`)
- **Java version** (`java -version`)
- **Android Studio version**
- **Complete error messages** (with stack traces)
- **Steps to reproduce** the issue
- **Configuration files** (sanitized, remove secrets)

### Contact Information
- **Developer**: Pushkarjay Ajay
- **Email**: pushkarjay.ajay1@gmail.com
- **GitHub Issues**: [SafeCom-App Issues](https://github.com/Pushkarjay/SafeCom-App/issues)

### Emergency Fixes
If you need to quickly get the app working:

1. **Use Debug Build**:
   ```bash
   ./gradlew assembleDebug
   ```

2. **Bypass Firebase** (temporary):
   ```javascript
   // Comment out Firebase initialization in Android app
   // Use mock data for testing
   ```

3. **Use Test Database**:
   ```env
   MONGODB_URI=mongodb://localhost:27017/safecom_test
   ```

4. **Disable Authentication** (development only):
   ```javascript
   // Temporarily disable auth middleware
   // Add mock user data
   ```

## 🏗️ Recovery Procedures

### Project Recovery (if files are corrupted)
```bash
# Reset to last working commit
git log --oneline
git reset --hard <commit-hash>

# Or pull fresh copy
rm -rf SafeCom-App
git clone https://github.com/Pushkarjay/SafeCom-App.git
```

### Database Recovery
```bash
# MongoDB backup
mongodump --db safecom_dev --out ./backup

# MongoDB restore
mongorestore --db safecom_dev ./backup/safecom_dev
```

### Environment Recovery
```bash
# Recreate environment files
cp .env.example .env
# Reconfigure with your settings

# Reinstall dependencies
rm -rf node_modules package-lock.json
npm install
```

---

**Remember**: Most issues can be resolved by carefully following the setup guides and checking prerequisites. When in doubt, start with a clean environment and follow the setup steps exactly as documented.
