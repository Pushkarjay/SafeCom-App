# 🔑 SafeCom Credentials & Configuration Setup Guide

**Project:** SafeCom Task Management App  
**Developer:** Pushkarjay Ajay (pushkarjay.ajay1@gmail.com)  
**Organization:** SafeCom  
**Date:** August 16, 2025  
**Version:** 1.0

---

## 📋 **Overview**

This guide provides step-by-step instructions to configure all required credentials and services for the SafeCom Task Management application. The app now uses **centralized configuration** for easy multi-client deployment.

🏢 **Multi-Client Ready:** For deploying to multiple companies, see [MULTI_CLIENT_SETUP.md](./MULTI_CLIENT_SETUP.md)

---

## 🎯 **Quick Setup Priority**

| Priority | Component | Time Required | Status |
|----------|-----------|---------------|--------|
| **1** | Configuration Setup | 2 minutes | ❌ **CRITICAL** |
| **2** | MongoDB Database | 15 minutes | ⚠️ **REQUIRED** |
| **3** | Firebase Service Account | 10 minutes | ⚠️ **PUSH NOTIFICATIONS** |
| **4** | Email Configuration | 10 minutes | ⚠️ **PASSWORD RESET** |
| **5** | Production URLs | Variable | ⚠️ **DEPLOYMENT** |

---

## ⚙️ **1. CENTRALIZED CONFIGURATION SETUP**

### **🔧 Backend Configuration**

**📍 Location:** `backend/.env`

**✅ Quick Setup:**
```bash
# The app now uses centralized configuration!
# Simply update these values in backend/.env:

API_BASE_URL=http://localhost:3000/api          # Your backend URL
COMPANY_NAME=Your Company Name                  # Client branding
APP_NAME=Your App Name                         # App title
SUPPORT_EMAIL=support@yourcompany.com          # Support contact
```

### **📱 Android Configuration**

**📍 Location:** `android/gradle.properties`

**✅ Quick Setup:**
```bash
# Copy the template file:
cp android/app.config.properties android/gradle.properties

# Then edit android/gradle.properties:
API_BASE_URL=http://10.0.2.2:3000/api/         # For emulator
# OR  
API_BASE_URL=http://192.168.1.XXX:3000/api/    # For physical device

COMPANY_NAME=Your Company Name
APP_NAME=Your App Name  
SUPPORT_EMAIL=support@yourcompany.com
```

### **🎯 Environment-Specific Setup**

**Development (Local):**
```properties
# android/gradle.properties
API_BASE_URL=http://10.0.2.2:3000/api/
DEBUG_MODE=true
```

**Production:**
```properties
# android/gradle.properties  
API_BASE_URL=https://api.yourcompany.com/api/
DEBUG_MODE=false
```

---

## 🗄️ **2. DATABASE CONFIGURATION**

### **MongoDB Setup Options**

#### **Option A: MongoDB Atlas (Recommended - FREE)**

**✅ Advantages:** 
- Free tier available (512MB)
- No local installation required
- Automatic backups
- Global accessibility

**📋 Setup Steps:**

1. **Create Atlas Account**
   - Visit: https://www.mongodb.com/cloud/atlas
   - Click "Try Free"
   - Sign up with email

2. **Create Cluster**
   - Select "Build a Database"
   - Choose "FREE" shared cluster
   - Select region closest to you
   - Keep default cluster name

3. **Create Database User**
   - Go to "Database Access"
   - Click "Add New Database User"
   - Username: `safecom-admin`
   - Password: Generate secure password
   - Database User Privileges: "Read and write to any database"

4. **Configure Network Access**
   - Go to "Network Access"
   - Click "Add IP Address"
   - Select "Allow Access from Anywhere" (0.0.0.0/0)
   - For production: Add specific IPs only

5. **Get Connection String**
   - Go to "Database" → "Connect"
   - Select "Connect your application"
   - Copy connection string
   - Replace `<password>` with your database user password

6. **Update Backend Configuration**
   ```env
   # Edit: backend/.env
   MONGODB_URI=mongodb+srv://safecom-admin:<password>@cluster0.xxxxx.mongodb.net/safecom?retryWrites=true&w=majority
   DATABASE_NAME=safecom
   ```

#### **Option B: Local MongoDB Installation**

**📋 Setup Steps:**

1. **Download MongoDB Community**
   - Visit: https://www.mongodb.com/try/download/community
   - Select your OS version
   - Download and install

2. **Start MongoDB Service**
   ```bash
   # Windows (as Administrator)
   net start MongoDB
   
   # macOS
   brew services start mongodb-community
   
   # Linux
   sudo systemctl start mongod
   ```

3. **Verify Installation**
   ```bash
   # Connect to MongoDB
   mongosh
   # Should connect to mongodb://localhost:27017
   ```

4. **Backend Configuration (Already Set)**
   ```env
   # backend/.env (already configured)
   MONGODB_URI=mongodb://localhost:27017/safecom
   DATABASE_NAME=safecom
   ```

---

## 🔥 **3. FIREBASE CONFIGURATION**

### **Current Status**
- **Android Config:** ✅ Already configured (`google-services.json` exists)
- **Backend Config:** ❌ Using placeholder values

### **📋 Setup Firebase Service Account**

1. **Access Firebase Console**
   - Visit: https://console.firebase.google.com/
   - Sign in with Google account
   - Select project: `safecom-task-management`

2. **Navigate to Service Accounts**
   - Click "Project Settings" (gear icon)
   - Select "Service accounts" tab
   - Click "Generate new private key"

3. **Download Service Account JSON**
   - Click "Generate key"
   - Save the downloaded JSON file securely
   - **⚠️ IMPORTANT:** Never commit this file to version control

4. **Extract Credentials from JSON**
   ```json
   {
     "type": "service_account",
     "project_id": "safecom-task-management",
     "private_key_id": "abc123...",
     "private_key": "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC...\n-----END PRIVATE KEY-----\n",
     "client_email": "firebase-adminsdk-xxxxx@safecom-task-management.iam.gserviceaccount.com",
     "client_id": "123456789012345678901",
     "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-xxxxx%40safecom-task-management.iam.gserviceaccount.com"
   }
   ```

5. **Update Backend Configuration**
   ```env
   # Edit: backend/.env
   FIREBASE_PROJECT_ID=safecom-task-management
   FIREBASE_PRIVATE_KEY_ID=abc123...  # From JSON
   FIREBASE_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC...\n-----END PRIVATE KEY-----\n"
   FIREBASE_CLIENT_EMAIL=firebase-adminsdk-xxxxx@safecom-task-management.iam.gserviceaccount.com
   FIREBASE_CLIENT_ID=123456789012345678901
   FIREBASE_CLIENT_X509_CERT_URL=https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-xxxxx%40safecom-task-management.iam.gserviceaccount.com
   ```

### **🔔 Enable Firebase Services**

1. **Authentication**
   - Go to "Authentication" → "Sign-in method"
   - Enable "Email/Password"

2. **Cloud Messaging (Push Notifications)**
   - Go to "Cloud Messaging"
   - Note the Server Key (for future use)

3. **Firestore Database (Optional)**
   - Go to "Firestore Database"
   - Create database in test mode

---

## 📧 **4. EMAIL CONFIGURATION (Optional)**

### **For Password Reset & Notifications**

#### **📋 Gmail App Password Setup**

1. **Enable 2-Factor Authentication**
   - Visit: https://myaccount.google.com/security
   - Enable 2-Step Verification

2. **Generate App Password**
   - Go to "App passwords"
   - Select app: "Mail"
   - Select device: "Other (Custom name)"
   - Name: "SafeCom Backend"
   - Copy the generated 16-character password

3. **Update Backend Configuration**
   ```env
   # Edit: backend/.env
   EMAIL_HOST=smtp.gmail.com
   EMAIL_PORT=587
   EMAIL_USER=your-email@gmail.com
   EMAIL_PASS=abcdabcdabcdabcd  # 16-character app password
   ```

#### **📧 Alternative Email Providers**

**SendGrid:**
```env
EMAIL_HOST=smtp.sendgrid.net
EMAIL_PORT=587
EMAIL_USER=apikey
EMAIL_PASS=your-sendgrid-api-key
```

**Mailgun:**
```env
EMAIL_HOST=smtp.mailgun.org
EMAIL_PORT=587
EMAIL_USER=postmaster@your-domain.mailgun.org
EMAIL_PASS=your-mailgun-password
```

---

## 🌐 **5. PRODUCTION DEPLOYMENT**

### **🚀 Backend Hosting Options**

#### **Option A: Railway (Recommended)**
```bash
# 1. Install Railway CLI
npm install -g @railway/cli

# 2. Login and deploy
railway login
railway init
railway up

# 3. Add environment variables in Railway dashboard
# 4. Update Android app with Railway URL
```

#### **Option B: Heroku**
```bash
# 1. Install Heroku CLI
# 2. Create app
heroku create safecom-backend

# 3. Add MongoDB addon
heroku addons:create mongolab:sandbox

# 4. Deploy
git push heroku main
```

#### **Option C: DigitalOcean App Platform**
- Connect GitHub repository
- Auto-deploy from main branch
- Add environment variables
- Choose $5/month plan

### **📱 Update Android for Production**

```kotlin
// For production deployment
.baseUrl("https://your-app-name.railway.app/api/")
// OR
.baseUrl("https://your-app-name.herokuapp.com/api/")
```

---

## 🔒 **6. SECURITY BEST PRACTICES**

### **🔐 Environment Variables Security**

**✅ DO:**
- Use strong, unique passwords
- Rotate credentials regularly
- Use environment variables for all secrets
- Enable 2FA on all accounts

**❌ DON'T:**
- Commit `.env` files to git
- Share credentials in plain text
- Use weak passwords
- Hardcode secrets in source code

### **📝 Environment Variables Checklist**

```env
# Essential for Development
✅ MONGODB_URI
✅ JWT_SECRET
✅ NODE_ENV=development

# Essential for Production
✅ MONGODB_URI (production)
✅ JWT_SECRET (strong)
✅ FIREBASE_* (all values)
✅ EMAIL_* (if using)
✅ NODE_ENV=production
```

---

## 🧪 **7. TESTING YOUR SETUP**

### **📋 Backend Testing**
```bash
# 1. Start backend
cd backend
npm start

# 2. Test health endpoint
curl http://localhost:3000/health

# 3. Expected response:
{
  "status": "OK",
  "timestamp": "2025-08-16T...",
  "uptime": 123.45,
  "environment": "development"
}
```

### **📱 Android Testing**
```bash
# 1. Build debug APK
cd android
./gradlew assembleDebug

# 2. Install on device/emulator
adb install app/build/outputs/apk/debug/app-debug.apk

# 3. Test registration/login
```

### **🔍 Troubleshooting Common Issues**

| Issue | Solution |
|-------|----------|
| "Network Error" in Android | Check backend URL in AppModule.kt |
| "Connection refused" | Ensure backend is running on port 3000 |
| "Invalid credentials" | Verify MongoDB connection string |
| "Firebase error" | Check service account JSON values |
| "Email not sending" | Verify Gmail app password |

---

## 📞 **8. SUPPORT & TROUBLESHOOTING**

### **🆘 Getting Help**

**Developer Contact:**
- **Name:** Pushkarjay Ajay
- **Email:** pushkarjay.ajay1@gmail.com
- **Organization:** SafeCom

### **📚 Additional Resources**

- [MongoDB Atlas Documentation](https://docs.atlas.mongodb.com/)
- [Firebase Admin SDK](https://firebase.google.com/docs/admin/setup)
- [Android Network Security Config](https://developer.android.com/training/articles/security-config)
- [Express.js Security Best Practices](https://expressjs.com/en/advanced/best-practice-security.html)

---

## ✅ **SETUP COMPLETION CHECKLIST**

- [ ] Android backend URL updated
- [ ] MongoDB database connected (Atlas or Local)
- [ ] Firebase service account configured
- [ ] Email configuration setup (optional)
- [ ] Backend starts without errors
- [ ] Android app builds successfully
- [ ] Basic authentication tested
- [ ] Push notifications working (if Firebase configured)
- [ ] Email notifications working (if email configured)

---

**🎉 Once all items are checked, your SafeCom Task Management app is ready for development and testing!**

---

*Last Updated: August 16, 2025 by Pushkarjay Ajay*
