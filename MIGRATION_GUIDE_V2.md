# SafeCom v2 - Tech Stack Migration Guide

## 🚀 Quick Start (30 minutes)

### Step 1: Prerequisites
```bash
# Make sure you have:
- Node.js 18+ 
- npm or yarn
- Google Cloud account
- Firebase project (linked to GCP)
```

### Step 2: Setup Firebase & GCP

#### Create GCP Project
1. Go to [Google Cloud Console](https://console.cloud.google.com)
2. Create new project: "SafeCom-v2"
3. Enable APIs:
   - Cloud Run API
   - Cloud Firestore API
   - Cloud Storage API
   - Firebase Admin SDK

#### Create Firebase Project
1. Go to [Firebase Console](https://console.firebase.google.com)
2. Create new project (or link to existing GCP)
3. Enable:
   - Authentication (Email/Password, Google OAuth)
   - Firestore Database
   - Storage
4. Copy credentials to `.env` file

### Step 3: Environment Setup

Create `.env` in root:
```env
# Firebase Web Config
REACT_APP_FIREBASE_API_KEY=your_api_key
REACT_APP_FIREBASE_AUTH_DOMAIN=your_project.firebaseapp.com
REACT_APP_FIREBASE_PROJECT_ID=your_project_id
REACT_APP_FIREBASE_STORAGE_BUCKET=your_project.appspot.com
REACT_APP_FIREBASE_MESSAGING_SENDER_ID=your_sender_id
REACT_APP_FIREBASE_APP_ID=your_app_id

# Backend
NODE_ENV=development
FIREBASE_PROJECT_ID=your_project_id
GOOGLE_CLOUD_REGION=us-central1
API_PORT=3000
```

Create `backend/.env`:
```env
FIREBASE_PROJECT_ID=your_project_id
GOOGLE_APPLICATION_CREDENTIALS=./src/config/firebase-adminsdk.json
DATABASE_URL=firestore://your_project_id
CLOUD_STORAGE_BUCKET=your_project.appspot.com

# Demo mode
ENABLE_DEMO_MODE=true
DEMO_USERS_ENABLED=true
```

### Step 4: Install Dependencies

```bash
# React Frontend
cd safecom-react
npm install
npm install firebase react-router-dom axios

# Backend updates
cd ../server
npm install firebase-admin @google-cloud/firestore

# Firestore migration
npm install
node firestore-schema/migrate-to-firestore.js
```

### Step 5: Start Development

```bash
# Terminal 1 - React Frontend (port 3000)
cd safecom-react
npm start

# Terminal 2 - Backend (port 3001)
cd server
npm run dev

# Terminal 3 - Flutter (optional)
cd mobile
flutter run -d chrome
```

---

## 📁 Project Structure

```
SafeCom-App/
├── safecom-react/               # NEW: React frontend
│   ├── src/
│   │   ├── components/
│   │   │   ├── Login.tsx
│   │   │   ├── Dashboard.tsx
│   │   │   ├── dashboards/
│   │   │   │   ├── AdminDashboard.tsx
│   │   │   │   ├── CustomerDashboard.tsx
│   │   │   │   └── EmployeeDashboard.tsx
│   │   │   └── common/
│   │   │       ├── Sidebar.tsx
│   │   │       └── Header.tsx
│   │   ├── context/
│   │   │   └── AuthContext.tsx
│   │   ├── services/
│   │   │   ├── firebase.ts
│   │   │   └── api.ts
│   │   ├── pages/
│   │   ├── App.tsx
│   │   └── index.tsx
│   ├── public/
│   └── package.json
│
├── server/                      # Updated: Firestore backend
│   ├── src/
│   │   ├── services/
│   │   │   └── firestoreService.js  # NEW
│   │   ├── controllers/
│   │   ├── routes/
│   │   └── config/
│   │       └── firebase-adminsdk.json
│   ├── firestore-schema/        # NEW: Schema & migration
│   │   ├── schema.json
│   │   ├── firestore.rules
│   │   └── migrate-to-firestore.js
│   └── package.json
│
├── mobile/                      # Unchanged: Flutter app
│   ├── lib/
│   ├── pubspec.yaml
│   └── ...
│
└── firestore-schema/            # NEW: Firestore docs
    ├── schema.json
    ├── firestore.rules
    └── migrate-to-firestore.js
```

---

## 🔄 Data Migration: MongoDB → Firestore

### Option 1: Automatic Migration Script
```bash
cd firestore-schema
node migrate-to-firestore.js
```

This will:
- Connect to MongoDB
- Migrate all Users, Tasks, Messages
- Create demo test accounts
- Create Firestore indexes

### Option 2: Manual Migration (Step-by-step)

#### Create Firestore Collections
```javascript
// In Firebase Console > Firestore > Create Collections:
- users/
  - admin@safecom.test
  - customer@safecom.test
  - employee@safecom.test
  - manager@safecom.test

- tasks/
  - [auto-generated IDs]

- messages/
  - [auto-generated IDs]

- notifications/
  - [auto-generated IDs]
```

#### Deploy Firestore Rules
```bash
npm install -g firebase-tools
firebase login
firebase deploy --only firestore:rules
```

---

## 🌐 Backend API Endpoints (Node.js + Firestore)

### Authentication
```
POST /api/auth/login
POST /api/auth/signup
POST /api/auth/logout
GET  /api/auth/me
```

### Tasks
```
GET    /api/tasks                 # All tasks (admin)
GET    /api/tasks/my              # Current user's tasks
GET    /api/tasks/:id             # Single task
POST   /api/tasks                 # Create task
PATCH  /api/tasks/:id             # Update task
DELETE /api/tasks/:id             # Delete task
```

### Messages
```
GET    /api/messages/:userId      # Conversation with user
POST   /api/messages              # Send message
GET    /api/conversations         # List conversations
PATCH  /api/messages/:id/read     # Mark as read
```

### Users
```
GET    /api/users                 # All users (admin)
GET    /api/users/:id             # User profile
PATCH  /api/users/:id             # Update profile
DELETE /api/users/:id             # Delete user
```

---

## ☁️ Deploy to Google Cloud Run

### Build Docker Image
```bash
cd server
docker build -t safecom-backend:v2 .
docker tag safecom-backend:v2 gcr.io/PROJECT_ID/safecom-backend:v2
```

### Push to Cloud Run
```bash
gcloud auth login
gcloud config set project PROJECT_ID
gcloud run deploy safecom-backend \
  --image gcr.io/PROJECT_ID/safecom-backend:v2 \
  --platform managed \
  --region us-central1 \
  --set-env-vars FIREBASE_PROJECT_ID=PROJECT_ID
```

### Deploy React Frontend
```bash
cd safecom-react
npm run build

# Option A: Cloud Run
gcloud run deploy safecom-frontend \
  --source .

# Option B: Netlify
netlify deploy --prod --dir=build

# Option C: Firebase Hosting
firebase deploy --only hosting
```

---

## 🧪 Testing All Roles

### Admin Account
- Email: admin@safecom.test
- Password: Demo@1234
- Dashboard: Admin Panel (all features)

### Customer Account
- Email: customer@safecom.test
- Password: Demo@1234
- Dashboard: Customer Panel (create orders, track status)

### Employee Account
- Email: employee@safecom.test
- Password: Demo@1234
- Dashboard: Employee Panel (assigned tasks)

### Manager Account
- Email: manager@safecom.test
- Password: Demo@1234
- Dashboard: Customer Panel (team management)

### Test Checklist
- [ ] Login with each account works
- [ ] Correct dashboard loads
- [ ] Demo mode works offline
- [ ] Firestore data reads/writes correctly
- [ ] Real-time updates working
- [ ] File uploads to Cloud Storage work
- [ ] Notifications display properly
- [ ] Role-based permissions enforced

---

## 🚨 Troubleshooting

### Firebase Connection Error
```
Error: Could not auto-load credentials. Tried: ...
```
**Solution**: Set `GOOGLE_APPLICATION_CREDENTIALS` or provide service account key in environment

### Firestore Rules Error
```
Permission denied: Missing or insufficient permissions
```
**Solution**: 
1. Check firestore.rules are deployed correctly
2. Verify user has proper custom claims
3. Check Collection path matches rules

### Cloud Run Deployment Fails
```
Deployment failed with code 13
```
**Solution**:
1. Check Docker build: `docker build -t test .`
2. Verify environment variables are set
3. Check Firebase Admin SDK credentials

---

## 📊 Performance Optimization

### Firestore Best Practices
- Enable caching: Browser caches queries
- Index compound queries for common patterns
- Use pagination for large datasets
- Limit real-time listeners

### React Optimization
- Code splitting with React.lazy()
- Image optimization with Cloudinary or Cloud Storage CDN
- Memoization for expensive computations
- Virtual scrolling for large lists

### Backend Optimization
- Cache user roles in custom JWT claims
- Use Firestore batch operations for bulk writes
- Implement rate limiting per user/API
- Use Cloud Storage CDN for static assets

---

## 📚 Additional Resources

- [Firebase Documentation](https://firebase.google.com/docs)
- [Cloud Run Guide](https://cloud.google.com/run/docs)
- [Firestore Best Practices](https://firebase.google.com/docs/firestore/best-practices)
- [React Documentation](https://react.dev)
- [TypeScript Handbook](https://www.typescriptlang.org/docs/)

---

## 🎯 Next Steps

1. ✅ Set up Firebase & GCP projects
2. ✅ Create `.env` configuration
3. ✅ Migrate data from MongoDB to Firestore
4. ✅ Start React development server
5. ✅ Test login with demo accounts
6. ✅ Verify dashboards load correctly
7. ✅ Deploy backend to Cloud Run
8. ✅ Deploy frontend to Cloud Hosting
9. ✅ Set up CI/CD pipeline
10. ✅ Monitor with Google Cloud Monitoring

---

Last Updated: April 20, 2026
