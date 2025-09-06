# 🚀 SafeCom - Task Management System

A modern, responsive task management system built with vanilla JavaScript and connected to a cloud-hosted backend.

## ✨ Features

- 🔐 **User Authentication** - Secure login/logout with JWT tokens
- 📋 **Task Management** - Create, update, delete, and track tasks  
- 💬 **Real-time Messaging** - Team communication system
- 📱 **Responsive Design** - Works on desktop, tablet, and mobile
- 🎨 **Modern UI** - Clean, intuitive interface
- ☁️ **Cloud Backend** - Hosted on Render.com for reliability

## 🌐 Live Demo

- **Frontend**: [https://safecom-frontend-tempo-render.onrender.com](https://safecom-frontend-tempo-render.onrender.com)
- **Backend API**: `https://safecom-backend-render-tempo.onrender.com`

## 🏗️ Project Structure

```
SafeCom/
├── index.html              # Landing page
├── login.html              # Authentication page
├── dashboard.html          # Main dashboard
├── tasks.html              # Task management
├── messages.html           # Team messaging
├── my-tasks.html           # Personal tasks
├── team.html               # Team overview
├── signup.html             # User registration
├── css/                    # Stylesheets
│   ├── styles.css          # Main styles
│   ├── dashboard.css       # Dashboard specific styles
│   └── auth.css            # Authentication styles
├── js/                     # JavaScript modules
│   ├── main.js             # Main application logic
│   ├── auth.js             # Authentication handling
│   ├── messages.js         # Messaging functionality
│   ├── api-config.js       # API configuration
│   └── theme.js            # Theme management
├── img/                    # Images & icons
│   ├── logo.svg
│   └── avatar.svg
├── package.json            # Dependencies for Render
├── render.yaml             # Render deployment config
├── _redirects              # Render routing rules
└── README.md               # Documentation
```

## 🚀 Quick Start

### Option 1: Live Application (Recommended)
Visit: [https://safecom-frontend-tempo-render.onrender.com](https://safecom-frontend-tempo-render.onrender.com)

### Option 2: Local Development
```bash
# Clone the repository
git clone https://github.com/Pushkarjay/SafeCom.git
cd SafeCom

# Install dependencies and start server
npm install
npm start

# Open http://localhost:8080
```

## 🌐 Render Deployment

This project is configured for automatic deployment on Render.com:

### Automatic Deployment
1. Connect your GitHub repository to Render
2. Render will automatically detect the static site
3. Deployment happens on every push to main branch
4. Live at: `https://safecom-frontend.onrender.com`

### Manual Deployment
```bash
# Build and deploy to Render
npm run deploy
```

## 🔧 Configuration

### Environment Variables (Render)
```bash
# Render automatically sets these
NODE_ENV=production
RENDER=true
```

### API Configuration
The frontend connects to the hosted backend automatically:
```javascript
const API_BASE = 'https://safecom-backend-render-tempo.onrender.com/api'
```

## 📱 Pages Overview

- **`/`** - Landing page with feature overview
- **`/login.html`** - User authentication
- **`/signup.html`** - New user registration  
- **`/dashboard.html`** - Main dashboard with stats and overview
- **`/tasks.html`** - Task management and creation
- **`/my-tasks.html`** - Personal task tracking
- **`/messages.html`** - Team communication
- **`/team.html`** - Team member overview

## 🎯 Key Features

### Authentication System
- JWT token-based authentication
- Secure login/logout
- User session management
- Password validation

### Task Management
- Create, edit, delete tasks
- Assign tasks to team members
- Set priorities and deadlines
- Track task progress
- Filter and search tasks

### Messaging System
- Real-time team communication
- One-on-one and group messaging
- Message history and search
- Unread message indicators

### Dashboard Analytics
- Task completion statistics
- Team performance metrics
- Visual charts and progress tracking
- Activity timeline

## 🔧 API Integration

All frontend components connect to the production backend:

```javascript
const API_BASE = 'https://safecom-backend-render-tempo.onrender.com/api'

// Authentication
POST /api/auth/login
POST /api/auth/register
POST /api/auth/logout

// Tasks
GET /api/tasks
POST /api/tasks
PUT /api/tasks/:id
DELETE /api/tasks/:id

// Messages
GET /api/messages
POST /api/messages/send
```

## 🚀 Deployment Options

### 1. Render.com (Current Setup)
- **URL**: https://safecom-frontend.onrender.com
- **Auto-deployment**: Enabled on git push
- **Custom domain**: Available
- **HTTPS**: Automatic
- **Global CDN**: Included

### 2. Alternative Deployments
- **Netlify**: Drag & drop deployment
- **Vercel**: Connect GitHub repository
- **GitHub Pages**: Enable in repository settings

## 🛠️ Development

### Local Development Server
```bash
# Using npm
npm start

# Using Python
python -m http.server 8080

# Using Node.js
npx http-server . -p 8080 -o
```

### File Structure
- **HTML files**: Core application pages
- **CSS files**: Styling and responsive design
- **JavaScript files**: Application logic and API integration
- **Images**: Icons and visual assets

### Adding New Features
1. Create/modify HTML templates
2. Add corresponding CSS styling
3. Implement JavaScript functionality
4. Test with the production API
5. Deploy to Render (automatic on git push)

## 🔒 Security Features

- HTTPS enforced on Render
- JWT token-based authentication
- Secure API communication
- CORS properly configured
- No sensitive data in frontend

## 📊 Performance

- **Static Site**: Fast loading times
- **CDN**: Global content delivery
- **Caching**: Browser and CDN caching
- **Compression**: Automatic on Render
- **Lighthouse Score**: 90+ performance

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature-name`
3. Commit changes: `git commit -am 'Add feature'`
4. Push to branch: `git push origin feature-name`
5. Submit a Pull Request

## 📱 Mobile App

An Android application version is planned. See [`Android_SRS_SafeCom.md`](Android_SRS_SafeCom.md) for detailed specifications.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

For support, email: pushkarjay.ajay1@gmail.com

## 🔗 Links

- **Live Application**: [https://safecom-frontend-tempo-render.onrender.com](https://safecom-frontend-tempo-render.onrender.com)
- **Backend API**: [https://safecom-backend-render-tempo.onrender.com](https://safecom-backend-render-tempo.onrender.com)
- **GitHub Repository**: [https://github.com/Pushkarjay/SafeCom](https://github.com/Pushkarjay/SafeCom)

---
**SafeCom** - Making task management simple and efficient! 🎯

**Deployed with ❤️ on Render.com**