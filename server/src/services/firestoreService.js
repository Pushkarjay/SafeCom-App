/**
 * Firestore Service - Replaces MongoDB models
 * Handles all Firestore database operations
 */

const admin = require('firebase-admin');
const db = admin.firestore();

// ==================== USER SERVICE ====================
class UserService {
  async getUserById(uid) {
    const doc = await db.collection('users').doc(uid).get();
    return doc.exists ? { id: doc.id, ...doc.data() } : null;
  }

  async getUserByEmail(email) {
    const query = await db.collection('users').where('email', '==', email).limit(1).get();
    if (query.empty) return null;
    const doc = query.docs[0];
    return { id: doc.id, ...doc.data() };
  }

  async getAllUsers() {
    const query = await db.collection('users').get();
    return query.docs.map(doc => ({ id: doc.id, ...doc.data() }));
  }

  async getUsersByRole(role) {
    const query = await db.collection('users').where('role', '==', role).get();
    return query.docs.map(doc => ({ id: doc.id, ...doc.data() }));
  }

  async createUser(userData) {
    const docRef = await db.collection('users').add({
      ...userData,
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
      updatedAt: admin.firestore.FieldValue.serverTimestamp(),
      status: 'active'
    });
    return { id: docRef.id, ...userData };
  }

  async updateUser(uid, updates) {
    await db.collection('users').doc(uid).update({
      ...updates,
      updatedAt: admin.firestore.FieldValue.serverTimestamp()
    });
    return this.getUserById(uid);
  }

  async deleteUser(uid) {
    await db.collection('users').doc(uid).delete();
    return true;
  }
}

// ==================== TASK SERVICE ====================
class TaskService {
  async getTaskById(taskId) {
    const doc = await db.collection('tasks').doc(taskId).get();
    return doc.exists ? { id: doc.id, ...doc.data() } : null;
  }

  async getAllTasks(filters = {}) {
    let query = db.collection('tasks');

    if (filters.status) {
      query = query.where('status', '==', filters.status);
    }
    if (filters.priority) {
      query = query.where('priority', '==', filters.priority);
    }
    if (filters.createdBy) {
      query = query.where('createdBy', '==', filters.createdBy);
    }
    if (filters.assignedTo) {
      query = query.where('assignedTo', '==', filters.assignedTo);
    }

    const snapshot = await query.orderBy('createdAt', 'desc').get();
    return snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
  }

  async getTasksByUser(userId, status = null) {
    let query = db.collection('tasks')
      .where('assignedTo', '==', userId);

    if (status) {
      query = query.where('status', '==', status);
    }

    const snapshot = await query.orderBy('dueDate', 'asc').get();
    return snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
  }

  async createTask(taskData) {
    const docRef = await db.collection('tasks').add({
      ...taskData,
      status: taskData.status || 'pending',
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
      updatedAt: admin.firestore.FieldValue.serverTimestamp()
    });
    return { id: docRef.id, ...taskData };
  }

  async updateTask(taskId, updates) {
    await db.collection('tasks').doc(taskId).update({
      ...updates,
      updatedAt: admin.firestore.FieldValue.serverTimestamp()
    });
    return this.getTaskById(taskId);
  }

  async deleteTask(taskId) {
    await db.collection('tasks').doc(taskId).delete();
    return true;
  }

  async getTaskStats(userId = null) {
    let query = db.collection('tasks');
    if (userId) {
      query = query.where('assignedTo', '==', userId);
    }

    const snapshot = await query.get();
    const tasks = snapshot.docs.map(doc => doc.data());

    return {
      total: tasks.length,
      pending: tasks.filter(t => t.status === 'pending').length,
      inProgress: tasks.filter(t => t.status === 'in_progress').length,
      completed: tasks.filter(t => t.status === 'completed').length,
      overdue: tasks.filter(t => t.dueDate < new Date() && t.status !== 'completed').length,
      highPriority: tasks.filter(t => t.priority === 'urgent' || t.priority === 'high').length
    };
  }
}

// ==================== MESSAGE SERVICE ====================
class MessageService {
  async getMessages(userId1, userId2) {
    const query = await db.collection('messages')
      .where('senderId', '==', userId1)
      .where('receiverId', '==', userId2)
      .orderBy('createdAt', 'asc')
      .get();

    const reverse = await db.collection('messages')
      .where('senderId', '==', userId2)
      .where('receiverId', '==', userId1)
      .orderBy('createdAt', 'asc')
      .get();

    const messages = [
      ...query.docs.map(doc => ({ id: doc.id, ...doc.data() })),
      ...reverse.docs.map(doc => ({ id: doc.id, ...doc.data() }))
    ];

    return messages.sort((a, b) => a.createdAt - b.createdAt);
  }

  async getConversations(userId) {
    const sent = await db.collection('messages')
      .where('senderId', '==', userId)
      .get();
    
    const received = await db.collection('messages')
      .where('receiverId', '==', userId)
      .get();

    const conversations = new Map();
    
    sent.docs.forEach(doc => {
      const data = doc.data();
      const key = data.receiverId;
      conversations.set(key, data);
    });

    received.docs.forEach(doc => {
      const data = doc.data();
      const key = data.senderId;
      if (!conversations.has(key)) {
        conversations.set(key, data);
      }
    });

    return Array.from(conversations.values());
  }

  async sendMessage(messageData) {
    const docRef = await db.collection('messages').add({
      ...messageData,
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
      updatedAt: admin.firestore.FieldValue.serverTimestamp(),
      isRead: false
    });
    return { id: docRef.id, ...messageData };
  }

  async markAsRead(messageId) {
    await db.collection('messages').doc(messageId).update({
      isRead: true
    });
  }
}

// ==================== NOTIFICATION SERVICE ====================
class NotificationService {
  async getUserNotifications(userId) {
    const query = await db.collection('notifications')
      .where('userId', '==', userId)
      .orderBy('createdAt', 'desc')
      .limit(50)
      .get();

    return query.docs.map(doc => ({ id: doc.id, ...doc.data() }));
  }

  async createNotification(notificationData) {
    const docRef = await db.collection('notifications').add({
      ...notificationData,
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
      isRead: false
    });
    return { id: docRef.id, ...notificationData };
  }

  async markNotificationAsRead(notificationId) {
    await db.collection('notifications').doc(notificationId).update({
      isRead: true
    });
  }

  async markAllAsRead(userId) {
    const batch = db.batch();
    const query = await db.collection('notifications')
      .where('userId', '==', userId)
      .where('isRead', '==', false)
      .get();

    query.docs.forEach(doc => {
      batch.update(doc.ref, { isRead: true });
    });

    await batch.commit();
  }
}

module.exports = {
  UserService: new UserService(),
  TaskService: new TaskService(),
  MessageService: new MessageService(),
  NotificationService: new NotificationService(),
  db
};
