# SafeCom Backend API

Complete Node.js backend for SafeCom Task Management Android application.

**Developed by:** Pushkarjay Ajay  
**Organization:** SafeCom  
**Contact:** pushkarjay.ajay1@gmail.com  

## Features

- **Authentication & Authorization**
  - JWT-based authentication
  - Role-based access control (user, manager, admin)
  - Password reset functionality
  - FCM token management for push notifications

- **Task Management**
  - Create, read, update, delete tasks
  - Task assignment and status tracking
  - Comments and file attachments
  - Due date and priority management
  - Task statistics and filtering

- **Real-time Messaging**
  - Direct and group messaging
  - Real-time communication via Socket.IO
  - Message reactions and replies
  - File sharing in messages
  - Read receipts and delivery status

- **File Upload**
  - Support for images, documents, audio, video
  - File size and type validation
  - Secure file storage

- **Push Notifications**
  - Firebase Cloud Messaging integration
  - Task assignment notifications
  - Message notifications
  - Custom notification types

## Technology Stack

- **Runtime**: Node.js
- **Framework**: Express.js
- **Database**: MongoDB with Mongoose ODM
- **Authentication**: JWT (JSON Web Tokens)
- **Real-time**: Socket.IO
- **Push Notifications**: Firebase Admin SDK
- **File Upload**: Multer
- **Validation**: Express Validator
- **Security**: bcryptjs, express-rate-limit

## Project Structure

```
backend/
├── src/
│   ├── controllers/          # Route controllers
│   │   ├── authController.js
│   │   ├── taskController.js
│   │   └── messageController.js
│   ├── models/              # Database models
│   │   ├── User.js
│   │   ├── Task.js
│   │   └── Message.js
│   ├── routes/              # API routes
│   │   ├── auth.js
│   │   ├── tasks.js
│   │   └── messages.js
│   ├── middleware/          # Custom middleware
│   │   ├── auth.js
│   │   ├── rateLimiter.js
│   │   └── upload.js
│   ├── firebase.js         # Firebase configuration
│   └── app.js              # Main application file
├── uploads/                 # File upload directory
├── package.json
├── .env.example
└── README.md
```

## Quick Start

1. **Install Dependencies**
   ```bash
   npm install
   ```

2. **Environment Setup**
   ```bash
   cp .env.example .env
   # Edit .env with your configuration
   ```

3. **Firebase Setup**
   - Create a Firebase project
   - Download service account key
   - Place it in the root directory as `firebase-service-account.json`

4. **Start Development Server**
   ```bash
   npm run dev
   ```

5. **Start Production Server**
   ```bash
   npm start
   ```

## Environment Variables

Required environment variables (see `.env.example`):

```env
# Server Configuration
PORT=3000
NODE_ENV=development

# Database
MONGODB_URI=mongodb://localhost:27017/safecom

# JWT
JWT_SECRET=your-jwt-secret-key
JWT_EXPIRES_IN=7d

# Firebase
FIREBASE_PROJECT_ID=your-firebase-project-id

# CORS
ALLOWED_ORIGINS=http://localhost:3000,http://localhost:8080
```

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - User login
- `GET /api/auth/profile` - Get user profile
- `PATCH /api/auth/profile` - Update profile
- `POST /api/auth/change-password` - Change password
- `POST /api/auth/forgot-password` - Request password reset
- `POST /api/auth/reset-password` - Reset password
- `POST /api/auth/logout` - Logout user
- `GET /api/auth/users` - Get all users

### Tasks
- `POST /api/tasks` - Create new task
- `GET /api/tasks` - Get tasks with filters
- `GET /api/tasks/:id` - Get single task
- `PATCH /api/tasks/:id` - Update task
- `DELETE /api/tasks/:id` - Delete task
- `POST /api/tasks/:id/comments` - Add comment to task
- `GET /api/tasks/stats` - Get task statistics

### Messages
- `POST /api/messages` - Send message
- `GET /api/messages/conversations` - Get conversations
- `GET /api/messages/conversations/:id` - Get conversation messages
- `PATCH /api/messages/conversations/:id/read` - Mark messages as read
- `GET /api/messages/unread-count` - Get unread count
- `GET /api/messages/search` - Search messages
- `PATCH /api/messages/:id` - Edit message
- `DELETE /api/messages/:id` - Delete message
- `POST /api/messages/:id/reactions` - Add reaction
- `DELETE /api/messages/:id/reactions` - Remove reaction

## Socket.IO Events

### Client to Server
- `join_conversation` - Join a conversation room
- `leave_conversation` - Leave a conversation room
- `typing_start` - User started typing
- `typing_stop` - User stopped typing

### Server to Client
- `new_message` - New message received
- `message_edited` - Message was edited
- `message_deleted` - Message was deleted
- `messages_read` - Messages marked as read
- `reaction_added` - Reaction added to message
- `reaction_removed` - Reaction removed from message
- `user_typing` - Another user is typing
- `user_stopped_typing` - User stopped typing

## Rate Limiting

- General API: 100 requests per 15 minutes
- Authentication: 5 requests per 15 minutes
- Password Reset: 3 requests per hour
- Messages: 30 requests per minute
- File Upload: 20 requests per 15 minutes

## Security Features

- JWT authentication with expiration
- Password hashing with bcrypt
- Input validation and sanitization
- Rate limiting on all endpoints
- CORS protection
- File upload restrictions
- MongoDB injection protection

## File Upload

Supported file types:
- Images: JPEG, PNG, GIF, WebP
- Documents: PDF, Word, Excel, PowerPoint, TXT, CSV
- Archives: ZIP, RAR, 7Z
- Audio: MP3, WAV, OGG, MP4
- Video: MP4, MPEG, QuickTime, WebM

Maximum file size: 50MB
Maximum files per request: 10

## Push Notifications

The system supports Firebase Cloud Messaging for:
- Task assignments
- Task status updates
- New messages
- Task comments
- Custom notifications

## Error Handling

All API responses follow a consistent format:
```json
{
  "success": true/false,
  "message": "Description",
  "data": {}, // Only on success
  "errors": [] // Only on validation errors
}
```

## Development

### Running Tests
```bash
npm test
```

### Code Linting
```bash
npm run lint
```

### Database Seeding
```bash
npm run seed
```

## Deployment

### Production Checklist
- [ ] Set NODE_ENV=production
- [ ] Configure production MongoDB
- [ ] Set secure JWT_SECRET
- [ ] Configure Firebase production project
- [ ] Set up SSL/TLS
- [ ] Configure reverse proxy (Nginx)
- [ ] Set up process manager (PM2)
- [ ] Configure logging
- [ ] Set up monitoring

### Docker Deployment
```bash
docker build -t safecom-backend .
docker run -p 3000:3000 safecom-backend
```

## Contributing

**Developer:** Pushkarjay Ajay  
**Email:** pushkarjay.ajay1@gmail.com  
**Organization:** SafeCom  

1. Fork the repository
2. Create a feature branch
3. Make changes and add tests
4. Run linting and tests
5. Submit a pull request

## License

MIT License - see LICENSE file for details

**Copyright (c) 2025 Pushkarjay Ajay - SafeCom**
