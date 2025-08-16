const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const compression = require('compression');
const morgan = require('morgan');
const rateLimit = require('express-rate-limit');
const mongoose = require('mongoose');
const http = require('http');
const socketIo = require('socket.io');
require('dotenv').config();

// Import routes
const authRoutes = require('./routes/auth');
const taskRoutes = require('./routes/tasks');
const messageRoutes = require('./routes/messages');

// Import middleware
const { auth } = require('./middleware/auth');
const { errorHandler } = require('./middleware/errorHandler');

// Import Firebase admin
const admin = require('./config/firebase');

// Create Express app
const app = express();
const server = http.createServer(app);

// Socket.IO setup for real-time messaging
const io = socketIo(server, {
  cors: {
    origin: process.env.ALLOWED_ORIGINS?.split(',') || "*",
    methods: ["GET", "POST"]
  }
});

// Rate limiting
const limiter = rateLimit({
  windowMs: parseInt(process.env.RATE_LIMIT_WINDOW_MS) || 15 * 60 * 1000, // 15 minutes
  max: parseInt(process.env.RATE_LIMIT_MAX_REQUESTS) || 100, // limit each IP to 100 requests per windowMs
  message: 'Too many requests from this IP, please try again later.',
  standardHeaders: true,
  legacyHeaders: false,
});

// Middleware setup
app.use(helmet());
app.use(compression());
app.use(morgan('combined'));
app.use(limiter);

// CORS configuration
const corsOptions = {
  origin: function (origin, callback) {
    const allowedOrigins = process.env.ALLOWED_ORIGINS?.split(',') || ['http://localhost:3000'];
    if (!origin || allowedOrigins.indexOf(origin) !== -1) {
      callback(null, true);
    } else {
      callback(new Error('Not allowed by CORS'));
    }
  },
  credentials: true,
  methods: ['GET', 'POST', 'PUT', 'DELETE', 'PATCH', 'OPTIONS'],
  allowedHeaders: ['Content-Type', 'Authorization']
};

app.use(cors(corsOptions));
app.use(express.json({ limit: '10mb' }));
app.use(express.urlencoded({ extended: true, limit: '10mb' }));

// Database connection
mongoose.connect(process.env.MONGODB_URI, {
  useNewUrlParser: true,
  useUnifiedTopology: true,
  serverSelectionTimeoutMS: 30000,
  socketTimeoutMS: 45000,
})
.then(() => {
  console.log('✅ Connected to MongoDB');
  console.log(`Database: ${process.env.MONGODB_URI}`);
})
.catch(err => {
  console.error('❌ MongoDB connection error:', err.message);
  console.log('Server will continue without database. Some features may not work.');
});

// Health check endpoint
app.get('/health', (req, res) => {
  res.status(200).json({
    status: 'OK',
    timestamp: new Date().toISOString(),
    uptime: process.uptime(),
    environment: process.env.NODE_ENV
  });
});

// API routes
app.use('/api/auth', authRoutes);
app.use('/api/tasks', auth, taskRoutes);
app.use('/api/messages', auth, messageRoutes);

// Socket.IO connection handling
io.on('connection', (socket) => {
  console.log('User connected:', socket.id);

  // Join user to their room
  socket.on('join', (userId) => {
    socket.join(userId);
    console.log(`User ${userId} joined room`);
  });

  // Handle new message
  socket.on('new_message', async (data) => {
    try {
      // Save message to database (implement in message controller)
      // Emit to recipient
      socket.to(data.receiverId).emit('message_received', data);
      
      // Send push notification
      await sendPushNotification(data.receiverId, {
        title: `New message from ${data.senderName}`,
        body: data.content,
        data: {
          type: 'new_message',
          conversationId: data.conversationId
        }
      });
    } catch (error) {
      console.error('Error handling new message:', error);
    }
  });

  // Handle typing indicators
  socket.on('typing', (data) => {
    socket.to(data.conversationId).emit('user_typing', data);
  });

  socket.on('stop_typing', (data) => {
    socket.to(data.conversationId).emit('user_stop_typing', data);
  });

  socket.on('disconnect', () => {
    console.log('User disconnected:', socket.id);
  });
});

// Push notification helper
async function sendPushNotification(userId, notification) {
  try {
    // Check if Firebase is initialized
    if (!admin.isInitialized()) {
      console.warn('Firebase not initialized. Skipping push notification.');
      return;
    }

    // Get user's FCM token from database
    const User = require('./models/User');
    const user = await User.findById(userId);
    
    if (user && user.fcmToken) {
      const message = {
        notification: {
          title: notification.title,
          body: notification.body
        },
        data: notification.data,
        token: user.fcmToken
      };
      
      await admin.messaging().send(message);
      console.log('Push notification sent successfully');
    }
  } catch (error) {
    console.error('Error sending push notification:', error.message);
  }
}

// 404 handler
app.use('*', (req, res) => {
  res.status(404).json({
    success: false,
    message: 'Route not found'
  });
});

// Error handling middleware
app.use(errorHandler);

// Graceful shutdown
process.on('SIGTERM', () => {
  console.log('SIGTERM received. Shutting down gracefully...');
  server.close(() => {
    mongoose.connection.close();
    process.exit(0);
  });
});

const PORT = process.env.PORT || 3000;

server.listen(PORT, () => {
  console.log(`SafeCom Backend Server is running on port ${PORT}`);
  console.log(`Environment: ${process.env.NODE_ENV}`);
  console.log(`MongoDB: ${process.env.MONGODB_URI}`);
});

module.exports = { app, io };
