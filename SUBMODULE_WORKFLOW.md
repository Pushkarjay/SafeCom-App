# 🔄 Git Submodule Workflow Guide

## Overview
This project uses **git submodules** to manage the web frontend separately while keeping it locally accessible. Here's how it works:

## 📁 Repository Structure
- **SafeCom-App** (Main Repository): Flutter app, backend, docs, project management
- **SafeCom-Frontend** (Submodule): Web frontend HTML/CSS/JS files

## 🚀 Daily Workflow

### Working on Frontend (HTML/CSS/JS)
When you want to modify the web frontend:

```bash
# 1. Navigate to the frontend submodule
cd SafeCom-Frontend

# 2. Make your changes (edit HTML, CSS, JS files)
# Example: Edit login.html, update styles.css, modify auth.js

# 3. Check what changed
git status
git diff

# 4. Commit to SafeCom-Frontend repository
git add .
git commit -m "Update login page styling and validation"
git push origin main

# 5. Go back to main project
cd ..

# 6. Update submodule reference in SafeCom-App
git add SafeCom-Frontend
git commit -m "Update SafeCom-Frontend to latest version"
git push origin main
```

### Working on Flutter App or Backend
When you want to modify the Flutter app, backend, or documentation:

```bash
# 1. Make changes to flutter_app/, backend/, docs/, etc.
# Example: Update Flutter screens, modify API endpoints

# 2. Commit to SafeCom-App repository (current repo)
git add .
git commit -m "Add new Flutter screen for task management"
git push origin main

# Note: No need to touch SafeCom-Frontend/ for these changes
```

## 🔧 Useful Commands

### Check Submodule Status
```bash
git submodule status
```

### Update Submodule to Latest
```bash
git submodule update --remote
```

### Initialize Submodules (for new clones)
```bash
git submodule update --init --recursive
```

### View Submodule Changes
```bash
cd SafeCom-Frontend
git log --oneline -5
```

## 🌟 Benefits of This Setup

1. **Separate Deployment**: Frontend can be deployed independently to Netlify, Vercel, etc.
2. **Clear History**: Frontend changes have their own git history
3. **Team Collaboration**: Frontend and mobile teams can work independently
4. **Versioning**: Each repository has its own releases and tags
5. **Local Development**: Everything is still available locally in one project

## 🎯 Example Scenarios

### Scenario 1: Fix Frontend Bug
```bash
cd SafeCom-Frontend
# Edit admin-dashboard.js to fix a JavaScript error
git add admin-dashboard.js
git commit -m "Fix admin dashboard data loading bug"
git push origin main
cd ..
git add SafeCom-Frontend
git commit -m "Update frontend with bug fix"
git push origin main
```

### Scenario 2: Add New Flutter Feature
```bash
# Edit files in flutter_app/lib/screens/
git add flutter_app/
git commit -m "Add task filtering feature"
git push origin main
# Frontend is unchanged, no submodule update needed
```

### Scenario 3: Update Both Frontend and Backend
```bash
# 1. Update backend API
git add backend/
git commit -m "Add new API endpoint for user preferences"

# 2. Update frontend to use new API
cd SafeCom-Frontend
# Edit js/api.js to use new endpoint
git add js/api.js
git commit -m "Integrate user preferences API"
git push origin main
cd ..

# 3. Update main repo with both changes
git add SafeCom-Frontend
git commit -m "Update backend API and frontend integration"
git push origin main
```

## ⚠️ Important Notes

1. **Always commit frontend changes first** before updating the main repository
2. **Don't commit SafeCom-Frontend/** directly in the main repo - it's managed as a submodule
3. **Use `cd SafeCom-Frontend` when working on web files**
4. **The SafeCom-Frontend folder points to a specific commit** in the submodule repository

## 🔍 Troubleshooting

### Submodule out of sync
```bash
git submodule update --init --recursive
```

### Want to see submodule changes
```bash
cd SafeCom-Frontend
git status
git diff
```

### Reset submodule to specific commit
```bash
cd SafeCom-Frontend
git checkout COMMIT_HASH
cd ..
git add SafeCom-Frontend
git commit -m "Revert frontend to specific version"
```

This workflow gives you the best of both worlds: unified local development with proper repository separation! 🎉
