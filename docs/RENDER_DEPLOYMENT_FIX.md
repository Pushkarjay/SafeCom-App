# Render Deployment Troubleshooting Guide

## 🚨 Current Issue: Build Command Error

Your deployment failed because Render is looking for `package.json` in the wrong directory.

### Error Analysis:
```
npm error path /opt/render/project/src/package.json
npm error code ENOENT
```

**Problem**: Build command runs from root directory, but backend code is in `backend/` folder.

## 🔧 Quick Fix

### Step 1: Update Render Settings

1. **Go to Render Dashboard**: https://dashboard.render.com
2. **Select Service**: `SafeCom-backend-render-tempo`
3. **Click Settings** (left sidebar)
4. **Update Build Command**:
   ```bash
   cd backend && npm install
   ```
5. **Update Start Command**:
   ```bash
   cd backend && npm start
   ```

### Step 2: Environment Variables

Make sure these are set in **Environment** tab:

```env
NODE_ENV=production
PORT=10000
MONGODB_URI=your_mongodb_connection_string
JWT_SECRET=your_jwt_secret_key_here
FIREBASE_PROJECT_ID=your_firebase_project_id
API_BASE_URL=https://safecom-backend-render-tempo.onrender.com
```

### Step 3: Manual Deploy

1. **Click Manual Deploy** button
2. **Wait for deployment** (should take 2-3 minutes)
3. **Check logs** for success

## 🎯 Expected Success Output

After fixing, you should see:
```
==> Build successful 🎉
==> Starting service with 'cd backend && npm start'
Server running on port 10000
Connected to MongoDB
Firebase Admin initialized successfully
```

## 🔍 Verify Deployment

Once fixed, test these endpoints:

### Health Check:
```
GET https://safecom-backend-render-tempo.onrender.com/
```
Should return: `SafeCom Backend API is running!`

### API Status:
```
GET https://safecom-backend-render-tempo.onrender.com/api/
```
Should return API status information.

## 📱 Android App Update

✅ **Already Updated**: Android app now points to your deployed backend:
- **New API URL**: `https://safecom-backend-render-tempo.onrender.com/api/`
- **Old URL**: `http://10.0.2.2:3000/api/` (localhost)

## 🏗️ Alternative Render Configuration

If the above doesn't work, try this approach:

### Option 1: Root Directory Setting
1. **In Render Settings**
2. **Set Root Directory**: `backend`
3. **Build Command**: `npm install`
4. **Start Command**: `npm start`

### Option 2: Package.json in Root
Create a `package.json` in root directory:
```json
{
  "name": "safecom-app",
  "scripts": {
    "start": "cd backend && npm start",
    "build": "cd backend && npm install"
  }
}
```

## 🔄 Deployment Process

1. **Fix Render settings** → **Manual Deploy**
2. **Wait for deployment** (2-3 minutes)
3. **Check endpoint**: https://safecom-backend-render-tempo.onrender.com
4. **Rebuild Android app** with new backend URL
5. **Test app** with deployed backend

## 📋 Checklist

- [ ] Update Render build command
- [ ] Update Render start command  
- [ ] Set environment variables
- [ ] Manual deploy
- [ ] Test backend endpoints
- [ ] Rebuild Android APK
- [ ] Test app with live backend

## ⚠️ Common Issues

### Issue 1: Cold Start Delay
**Problem**: Free Render instances sleep after 15 minutes
**Solution**: First request may take 50+ seconds

### Issue 2: Environment Variables
**Problem**: Missing MongoDB or Firebase config
**Solution**: Add all required env vars from `.env.example`

### Issue 3: CORS Errors
**Problem**: Android app can't reach backend
**Solution**: Ensure CORS is enabled in backend (already configured)

## 🎉 Success Indicators

✅ **Backend Working**: Deployment shows "Build successful"
✅ **API Accessible**: GET requests return JSON responses
✅ **Android Connected**: App can login/register users
✅ **Database Connected**: MongoDB operations work
✅ **Firebase Working**: Push notifications function

## 🆘 If Still Failing

1. **Check Render logs** for specific errors
2. **Verify all environment variables** are set
3. **Test locally first**: `cd backend && npm start`
4. **Contact support** if Render-specific issues persist

Your backend should be live at: https://safecom-backend-render-tempo.onrender.com once fixed!
