/**
 * MongoDB to Firestore Migration Script
 * Converts existing MongoDB data to Firestore format
 * 
 * Usage: node migrate-to-firestore.js
 * 
 * Prerequisites:
 * - Set MONGODB_URI in .env
 * - Set FIREBASE_PROJECT_ID in .env
 * - Have Firebase Admin SDK credentials
 */

require('dotenv').config();
const mongoose = require('mongoose');
const admin = require('firebase-admin');
const path = require('path');

// Initialize Firebase Admin
const serviceAccount = require(path.join(__dirname, '../server/src/config/firebase-adminsdk.json'));
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  projectId: process.env.FIREBASE_PROJECT_ID
});

const db = admin.firestore();

// MongoDB Models
const User = require('../server/src/models/User');
const Task = require('../server/src/models/Task');
const Message = require('../server/src/models/Message');

async function migrateUsers() {
  console.log('🔄 Migrating Users...');
  try {
    const users = await User.find();
    let migrated = 0;
    
    for (const user of users) {
      try {
        // Create user in Firebase Auth if doesn't exist
        let firebaseUser;
        try {
          firebaseUser = await admin.auth().getUserByEmail(user.email);
        } catch (e) {
          if (e.code === 'auth/user-not-found') {
            firebaseUser = await admin.auth().createUser({
              email: user.email,
              password: user.password, // Note: will be hashed by Firebase
              displayName: user.name,
              photoURL: user.avatar
            });
          } else {
            throw e;
          }
        }
        
        // Migrate to Firestore
        await db.collection('users').doc(firebaseUser.uid).set({
          uid: firebaseUser.uid,
          email: user.email,
          name: user.name,
          role: user.role,
          avatar: user.avatar || '',
          phone: user.phone || '',
          department: user.department || '',
          createdAt: admin.firestore.Timestamp.fromDate(user.createdAt || new Date()),
          updatedAt: admin.firestore.Timestamp.fromDate(user.updatedAt || new Date()),
          isDemoMode: false,
          status: 'active'
        });
        
        migrated++;
        console.log(`✅ Migrated user: ${user.email}`);
      } catch (err) {
        console.error(`❌ Failed to migrate user ${user.email}:`, err.message);
      }
    }
    
    console.log(`✅ Users migration complete: ${migrated}/${users.length} migrated\n`);
    return migrated;
  } catch (err) {
    console.error('❌ Users migration failed:', err);
    throw err;
  }
}

async function migrateTasks() {
  console.log('🔄 Migrating Tasks...');
  try {
    const tasks = await Task.find();
    let migrated = 0;
    
    for (const task of tasks) {
      try {
        // Get Firebase UIDs for created/assigned users
        const createdByUser = task.createdBy ? await User.findById(task.createdBy) : null;
        const assignedToUser = task.assignedTo ? await User.findById(task.assignedTo) : null;
        
        const taskData = {
          id: task._id.toString(),
          title: task.title,
          description: task.description,
          status: task.status || 'pending',
          priority: task.priority || 'medium',
          category: task.category || '',
          createdBy: createdByUser?.email || 'system',
          assignedTo: assignedToUser?.email || null,
          dueDate: task.dueDate ? admin.firestore.Timestamp.fromDate(task.dueDate) : null,
          completedAt: task.completedAt ? admin.firestore.Timestamp.fromDate(task.completedAt) : null,
          attachments: task.attachments || [],
          createdAt: admin.firestore.Timestamp.fromDate(task.createdAt || new Date()),
          updatedAt: admin.firestore.Timestamp.fromDate(task.updatedAt || new Date())
        };
        
        await db.collection('tasks').doc(task._id.toString()).set(taskData);
        migrated++;
        console.log(`✅ Migrated task: ${task.title}`);
      } catch (err) {
        console.error(`❌ Failed to migrate task ${task._id}:`, err.message);
      }
    }
    
    console.log(`✅ Tasks migration complete: ${migrated}/${tasks.length} migrated\n`);
    return migrated;
  } catch (err) {
    console.error('❌ Tasks migration failed:', err);
    throw err;
  }
}

async function migrateMessages() {
  console.log('🔄 Migrating Messages...');
  try {
    const messages = await Message.find();
    let migrated = 0;
    
    for (const msg of messages) {
      try {
        const senderUser = await User.findById(msg.sender);
        const receiverUser = await User.findById(msg.receiver);
        
        const messageData = {
          id: msg._id.toString(),
          senderId: senderUser?.email || 'system',
          receiverId: receiverUser?.email || 'system',
          taskId: msg.taskId?.toString() || null,
          content: msg.content,
          type: msg.type || 'text',
          attachments: msg.attachments || [],
          isRead: msg.read || false,
          createdAt: admin.firestore.Timestamp.fromDate(msg.createdAt || new Date()),
          updatedAt: admin.firestore.Timestamp.fromDate(msg.updatedAt || new Date())
        };
        
        await db.collection('messages').doc(msg._id.toString()).set(messageData);
        migrated++;
        console.log(`✅ Migrated message: ${msg._id}`);
      } catch (err) {
        console.error(`❌ Failed to migrate message ${msg._id}:`, err.message);
      }
    }
    
    console.log(`✅ Messages migration complete: ${migrated}/${messages.length} migrated\n`);
    return migrated;
  } catch (err) {
    console.error('❌ Messages migration failed:', err);
    throw err;
  }
}

async function seedDemoUsers() {
  console.log('🌱 Seeding demo users...');
  try {
    const demoUsers = [
      { email: 'admin@safecom.test', password: 'Demo@1234', name: 'Admin User', role: 'admin' },
      { email: 'customer@safecom.test', password: 'Demo@1234', name: 'Customer User', role: 'customer' },
      { email: 'employee@safecom.test', password: 'Demo@1234', name: 'Employee User', role: 'employee' },
      { email: 'manager@safecom.test', password: 'Demo@1234', name: 'Manager User', role: 'customer' }
    ];
    
    for (const userData of demoUsers) {
      try {
        // Create Firebase Auth user
        const firebaseUser = await admin.auth().createUser({
          email: userData.email,
          password: userData.password,
          displayName: userData.name
        });
        
        // Set custom claims for role
        await admin.auth().setCustomUserClaims(firebaseUser.uid, { role: userData.role });
        
        // Create Firestore user doc
        await db.collection('users').doc(firebaseUser.uid).set({
          uid: firebaseUser.uid,
          email: userData.email,
          name: userData.name,
          role: userData.role,
          avatar: '',
          phone: '',
          department: '',
          createdAt: admin.firestore.FieldValue.serverTimestamp(),
          updatedAt: admin.firestore.FieldValue.serverTimestamp(),
          isDemoMode: true,
          status: 'active'
        });
        
        console.log(`✅ Created demo user: ${userData.email}`);
      } catch (err) {
        if (err.code === 'auth/email-already-exists') {
          console.log(`⚠️  Demo user already exists: ${userData.email}`);
        } else {
          console.error(`❌ Failed to create demo user ${userData.email}:`, err.message);
        }
      }
    }
  } catch (err) {
    console.error('❌ Demo user seeding failed:', err);
    throw err;
  }
}

async function runMigration() {
  try {
    console.log('═══════════════════════════════════════════════');
    console.log('  MongoDB → Firestore Migration Started');
    console.log('═══════════════════════════════════════════════\n');
    
    // Connect to MongoDB
    console.log('📡 Connecting to MongoDB...');
    await mongoose.connect(process.env.MONGODB_URI, {
      useNewUrlParser: true,
      useUnifiedTopology: true
    });
    console.log('✅ MongoDB connected\n');
    
    // Run migrations
    const userCount = await migrateUsers();
    const taskCount = await migrateTasks();
    const messageCount = await migrateMessages();
    
    // Seed demo users
    await seedDemoUsers();
    
    console.log('═══════════════════════════════════════════════');
    console.log('  ✅ Migration Complete!');
    console.log('═══════════════════════════════════════════════');
    console.log(`
    Users migrated:    ${userCount}
    Tasks migrated:    ${taskCount}
    Messages migrated: ${messageCount}
    Demo users added:  4
    
    Next steps:
    1. Verify data in Firestore console
    2. Deploy updated backend to Cloud Run
    3. Update React frontend with Firestore SDK
    4. Test all user roles
    `);
    
    // Cleanup
    await mongoose.connection.close();
    await admin.app().delete();
    process.exit(0);
  } catch (err) {
    console.error('❌ Migration failed:', err);
    await mongoose.connection.close();
    await admin.app().delete();
    process.exit(1);
  }
}

// Run migration
runMigration();
