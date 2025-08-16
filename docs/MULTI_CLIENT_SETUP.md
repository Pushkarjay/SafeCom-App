# 🏢 SafeCom Multi-Client Deployment Guide

**Project:** SafeCom Task Management App  
**Developer:** Pushkarjay Ajay (pushkarjay.ajay1@gmail.com)  
**Organization:** SafeCom  
**Date:** August 16, 2025  
**Version:** 1.0

---

## 🎯 **Multi-Client Architecture Overview**

The SafeCom app is designed for **white-label deployment** to multiple clients/companies. Each client gets:
- ✅ **Isolated Database** (MongoDB)
- ✅ **Separate Firebase Project** (Push notifications)
- ✅ **Custom Branding** (Company name, colors, logos)
- ✅ **Dedicated Backend Instance** (API endpoints)
- ✅ **Client-specific Configuration** (Easy setup)

---

## 🚀 **Quick Multi-Client Setup**

### **For Each New Client (5 Companies = 5 Separate Setups)**

| Component | Configuration File | Action Required |
|-----------|-------------------|-----------------|
| **Backend** | `.env` | Copy template & modify |
| **Android** | `gradle.properties` | Copy config & modify |
| **Database** | MongoDB | Create new database |
| **Firebase** | `google-services.json` | New Firebase project |
| **Domain** | DNS | Point to backend instance |

---

## 📋 **Step-by-Step Client Setup**

### **Step 1: Backend Configuration (Per Client)**

#### **1.1 Choose Client Template**
```bash
# Available templates:
backend/.env.client-a.template  # → Client A Corporation
backend/.env.client-b.template  # → Client B Industries
backend/.env.example           # → Generic template
```

#### **1.2 Create Client Environment**
```bash
# For Client A
cp backend/.env.client-a.template backend/.env

# For Client B  
cp backend/.env.client-b.template backend/.env
```

#### **1.3 Modify Client-Specific Values**
```env
# Edit backend/.env
COMPANY_NAME=Your Client Name
APP_NAME=Your Client App Name
API_BASE_URL=https://api.yourclient.com/api
MONGODB_URI=mongodb+srv://client-admin:<pass>@client-cluster.mongodb.net/client-db
SUPPORT_EMAIL=support@yourclient.com
```

### **Step 2: Android Configuration (Per Client)**

#### **2.1 Choose Client Template**
```bash
# Available templates:
android/client-a.config.properties
android/client-b.config.properties
android/app.config.properties  # Default
```

#### **2.2 Update Gradle Configuration**
```bash
# Copy to gradle.properties
cp android/client-a.config.properties android/gradle.properties
```

#### **2.3 Modify Client Values**
```properties
# Edit android/gradle.properties
API_BASE_URL=https://api.yourclient.com/api/
COMPANY_NAME=Your Client Name
APP_NAME=Your Client App
SUPPORT_EMAIL=support@yourclient.com
```

### **Step 3: Database Setup (Per Client)**

#### **3.1 MongoDB Atlas (Recommended)**
```bash
# 1. Create new MongoDB Atlas cluster for each client
# 2. Database naming convention:
#    - Client A: clienta-tasks
#    - Client B: clientb-tasks
#    - Client C: clientc-tasks

# 3. Connection string format:
mongodb+srv://client-admin:<password>@client-cluster.mongodb.net/client-db
```

#### **3.2 Database Isolation Benefits**
- ✅ **Complete data separation** between clients
- ✅ **Individual backups** and recovery
- ✅ **Client-specific scaling**
- ✅ **Compliance requirements** (GDPR, HIPAA)

### **Step 4: Firebase Setup (Per Client)**

#### **4.1 Create Separate Firebase Projects**
```bash
# Project naming convention:
# Client A: clienta-task-management
# Client B: clientb-project-hub
# Client C: clientc-task-system
```

#### **4.2 Download Client-Specific google-services.json**
```bash
# 1. Create Firebase project for client
# 2. Add Android app with client package name
# 3. Download google-services.json
# 4. Replace: android/app/google-services.json
```

### **Step 5: Domain & Hosting (Per Client)**

#### **5.1 Domain Setup**
```bash
# Domain examples:
# Client A: api.clienta.com → Backend instance
# Client B: api.clientb.com → Backend instance
# Client C: api.clientc.com → Backend instance
```

#### **5.2 Backend Deployment Options**

**Option A: Separate Hosting (Recommended)**
```bash
# Each client gets their own server/container
Railway/Heroku App 1 → Client A
Railway/Heroku App 2 → Client B  
Railway/Heroku App 3 → Client C
```

**Option B: Shared Hosting with Subdomains**
```bash
# Same server, different subdomains
api.safecom.com/clienta → Client A
api.safecom.com/clientb → Client B
api.safecom.com/clientc → Client C
```

---

## 🏭 **Production Deployment Examples**

### **Scenario 1: 5 Different Companies**

#### **Company A: Manufacturing Corp**
```env
# Backend (.env)
COMPANY_NAME=Manufacturing Corp
API_BASE_URL=https://api.manufacturing-corp.com/api
MONGODB_URI=mongodb+srv://mfg-admin:pass@mfg-cluster.mongodb.net/mfg-tasks
```

```properties
# Android (gradle.properties)
API_BASE_URL=https://api.manufacturing-corp.com/api/
COMPANY_NAME=Manufacturing Corp
APP_NAME=Manufacturing Tasks
```

#### **Company B: Tech Startup**
```env
# Backend (.env)
COMPANY_NAME=Tech Startup
API_BASE_URL=https://api.techstartup.io/api  
MONGODB_URI=mongodb+srv://tech-admin:pass@tech-cluster.mongodb.net/tech-tasks
```

```properties
# Android (gradle.properties)
API_BASE_URL=https://api.techstartup.io/api/
COMPANY_NAME=Tech Startup
APP_NAME=Tech Tasks Pro
```

### **Scenario 2: Single Company, Multiple Environments**

#### **Development Environment**
```env
# Backend (.env)
NODE_ENV=development
API_BASE_URL=http://localhost:3000/api
MONGODB_URI=mongodb://localhost:27017/safecom-dev
```

```properties
# Android (gradle.properties)
API_BASE_URL=http://10.0.2.2:3000/api/
DEBUG_MODE=true
```

#### **Staging Environment**
```env
# Backend (.env)
NODE_ENV=staging
API_BASE_URL=https://staging-api.safecom.com/api
MONGODB_URI=mongodb+srv://staging-admin:pass@staging-cluster.mongodb.net/safecom-staging
```

```properties
# Android (gradle.properties)
API_BASE_URL=https://staging-api.safecom.com/api/
DEBUG_MODE=true
```

#### **Production Environment**
```env
# Backend (.env)
NODE_ENV=production
API_BASE_URL=https://api.safecom.com/api
MONGODB_URI=mongodb+srv://prod-admin:pass@prod-cluster.mongodb.net/safecom-prod
```

```properties
# Android (gradle.properties)
API_BASE_URL=https://api.safecom.com/api/
DEBUG_MODE=false
```

---

## 🛠️ **Build & Deployment Scripts**

### **Backend Deployment Script**
```bash
#!/bin/bash
# deploy-client.sh

CLIENT_NAME=$1
ENVIRONMENT=$2

if [ -z "$CLIENT_NAME" ]; then
    echo "Usage: ./deploy-client.sh <client-name> <environment>"
    echo "Example: ./deploy-client.sh clienta production"
    exit 1
fi

echo "Deploying SafeCom for $CLIENT_NAME in $ENVIRONMENT environment..."

# Copy client configuration
cp backend/.env.$CLIENT_NAME.template backend/.env

# Deploy based on environment
if [ "$ENVIRONMENT" = "production" ]; then
    # Deploy to production
    docker build -t safecom-$CLIENT_NAME .
    docker run -d --name safecom-$CLIENT_NAME-backend safecom-$CLIENT_NAME
else
    # Start development server
    cd backend && npm start
fi

echo "Deployment complete for $CLIENT_NAME"
```

### **Android Build Script**
```bash
#!/bin/bash
# build-client-app.sh

CLIENT_NAME=$1

if [ -z "$CLIENT_NAME" ]; then
    echo "Usage: ./build-client-app.sh <client-name>"
    echo "Example: ./build-client-app.sh clienta"
    exit 1
fi

echo "Building Android app for $CLIENT_NAME..."

# Copy client configuration
cp android/$CLIENT_NAME.config.properties android/gradle.properties

# Build APK
cd android
./gradlew clean
./gradlew assembleRelease

echo "APK built successfully for $CLIENT_NAME"
echo "Location: android/app/build/outputs/apk/release/"
```

---

## 📊 **Client Management Dashboard**

### **Tracking Multiple Clients**

| Client | Environment | Backend URL | Database | Status |
|--------|-------------|-------------|----------|--------|
| Client A | Production | api.clienta.com | clienta-prod | ✅ Live |
| Client A | Staging | staging.clienta.com | clienta-staging | ⚠️ Testing |
| Client B | Production | api.clientb.com | clientb-prod | ✅ Live |
| Client C | Production | api.clientc.com | clientc-prod | 🚧 Setup |

### **Configuration Management**
```
configs/
├── client-a/
│   ├── backend.env
│   ├── android.properties
│   └── firebase.json
├── client-b/
│   ├── backend.env
│   ├── android.properties
│   └── firebase.json
└── client-c/
    ├── backend.env
    ├── android.properties
    └── firebase.json
```

---

## 🔒 **Security & Best Practices**

### **Client Isolation Checklist**
- [ ] ✅ **Separate databases** for each client
- [ ] ✅ **Unique JWT secrets** per client
- [ ] ✅ **Individual Firebase projects**
- [ ] ✅ **Client-specific domains**
- [ ] ✅ **Isolated cloud storage**
- [ ] ✅ **Separate email configurations**

### **Environment Security**
```env
# Strong, unique secrets for each client
JWT_SECRET=Client-Specific-Secret-Key-$(date +%s)-$(openssl rand -hex 16)
REFRESH_TOKEN_SECRET=Client-Refresh-Secret-$(date +%s)-$(openssl rand -hex 16)
```

---

## 📞 **Support & Maintenance**

### **Client-Specific Support**
Each client gets:
- ✅ **Dedicated support email** (support@clientname.com)
- ✅ **Client-specific documentation**
- ✅ **Isolated monitoring & logs**
- ✅ **Individual backup schedules**

### **Billing & Licensing**
```
Licensing Options:
1. Per-client license: $X/month per company
2. User-based: $Y/user/month
3. Feature-based: Basic/Pro/Enterprise tiers
4. One-time purchase: $Z per client setup
```

---

## ✅ **Multi-Client Setup Checklist**

### **For Each New Client:**
- [ ] Create backend `.env` from template
- [ ] Set up MongoDB database (Atlas recommended)
- [ ] Create Firebase project & download config
- [ ] Configure Android `gradle.properties`
- [ ] Update app branding (name, icons, colors)
- [ ] Set up domain & SSL certificate
- [ ] Deploy backend to hosting platform
- [ ] Build & sign Android APK
- [ ] Test complete workflow
- [ ] Document client-specific setup

---

**🎉 With this multi-client architecture, you can easily deploy SafeCom to unlimited companies with complete isolation and customization!**

---

*Last Updated: August 16, 2025 by Pushkarjay Ajay*
