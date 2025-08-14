const mongoose = require('mongoose');

const messageSchema = new mongoose.Schema({
  content: {
    type: String,
    required: [true, 'Message content is required'],
    trim: true,
    maxlength: [2000, 'Message cannot exceed 2000 characters']
  },
  sender: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: [true, 'Sender is required']
  },
  recipient: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    default: null // null for group messages
  },
  messageType: {
    type: String,
    enum: ['TEXT', 'IMAGE', 'FILE', 'AUDIO', 'SYSTEM', 'TASK_UPDATE'],
    default: 'TEXT'
  },
  conversationType: {
    type: String,
    enum: ['DIRECT', 'GROUP', 'TASK_CHAT'],
    required: true
  },
  conversationId: {
    type: String,
    required: true,
    index: true
  },
  relatedTask: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Task',
    default: null
  },
  attachments: [{
    filename: {
      type: String,
      required: true
    },
    originalName: {
      type: String,
      required: true
    },
    url: {
      type: String,
      required: true
    },
    mimeType: {
      type: String,
      required: true
    },
    size: {
      type: Number,
      required: true
    },
    thumbnailUrl: {
      type: String,
      default: null
    }
  }],
  readBy: [{
    user: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User',
      required: true
    },
    readAt: {
      type: Date,
      default: Date.now
    }
  }],
  deliveredTo: [{
    user: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User',
      required: true
    },
    deliveredAt: {
      type: Date,
      default: Date.now
    }
  }],
  isEdited: {
    type: Boolean,
    default: false
  },
  editedAt: {
    type: Date,
    default: null
  },
  originalContent: {
    type: String,
    default: null
  },
  isDeleted: {
    type: Boolean,
    default: false
  },
  deletedAt: {
    type: Date,
    default: null
  },
  deletedBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    default: null
  },
  replyTo: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Message',
    default: null
  },
  mentions: [{
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  }],
  reactions: [{
    user: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User',
      required: true
    },
    emoji: {
      type: String,
      required: true
    },
    reactedAt: {
      type: Date,
      default: Date.now
    }
  }],
  priority: {
    type: String,
    enum: ['normal', 'high', 'urgent'],
    default: 'normal'
  },
  systemData: {
    action: String,
    oldValue: mongoose.Schema.Types.Mixed,
    newValue: mongoose.Schema.Types.Mixed,
    affectedUser: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User'
    }
  }
}, {
  timestamps: true,
  toJSON: { virtuals: true },
  toObject: { virtuals: true }
});

// Indexes for better performance
messageSchema.index({ conversationId: 1, createdAt: -1 });
messageSchema.index({ sender: 1, createdAt: -1 });
messageSchema.index({ recipient: 1, createdAt: -1 });
messageSchema.index({ relatedTask: 1 });
messageSchema.index({ messageType: 1 });
messageSchema.index({ conversationType: 1 });
messageSchema.index({ isDeleted: 1 });

// Compound indexes
messageSchema.index({ conversationId: 1, isDeleted: 1, createdAt: -1 });
messageSchema.index({ sender: 1, conversationType: 1, createdAt: -1 });

// Text search index
messageSchema.index({
  content: 'text'
});

// Virtual for read status
messageSchema.virtual('isRead').get(function() {
  return this.readBy && this.readBy.length > 0;
});

// Virtual for delivered status
messageSchema.virtual('isDelivered').get(function() {
  return this.deliveredTo && this.deliveredTo.length > 0;
});

// Virtual for reply preview
messageSchema.virtual('replyPreview').get(function() {
  if (!this.replyTo) return null;
  // This would be populated in the query
  return this.replyTo;
});

// Pre-save middleware
messageSchema.pre('save', function(next) {
  // Extract mentions from content
  if (this.content && this.messageType === 'TEXT') {
    const mentionPattern = /@(\w+)/g;
    const mentions = [];
    let match;
    
    while ((match = mentionPattern.exec(this.content)) !== null) {
      mentions.push(match[1]);
    }
    
    // This would need to be enhanced to resolve usernames to user IDs
    // For now, we'll leave mentions as is
  }
  
  next();
});

// Static method to get conversation messages
messageSchema.statics.getConversationMessages = function(conversationId, page = 1, limit = 50) {
  const skip = (page - 1) * limit;
  
  return this.find({
    conversationId,
    isDeleted: false
  })
  .populate('sender', 'username fullName avatar')
  .populate('recipient', 'username fullName avatar')
  .populate('replyTo', 'content sender')
  .populate('readBy.user', 'username fullName')
  .populate('deliveredTo.user', 'username fullName')
  .sort({ createdAt: -1 })
  .skip(skip)
  .limit(limit);
};

// Static method to mark messages as read
messageSchema.statics.markAsRead = function(conversationId, userId) {
  return this.updateMany(
    {
      conversationId,
      'readBy.user': { $ne: userId },
      sender: { $ne: userId },
      isDeleted: false
    },
    {
      $push: {
        readBy: {
          user: userId,
          readAt: new Date()
        }
      }
    }
  );
};

// Static method to mark messages as delivered
messageSchema.statics.markAsDelivered = function(conversationId, userId) {
  return this.updateMany(
    {
      conversationId,
      'deliveredTo.user': { $ne: userId },
      sender: { $ne: userId },
      isDeleted: false
    },
    {
      $push: {
        deliveredTo: {
          user: userId,
          deliveredAt: new Date()
        }
      }
    }
  );
};

// Static method to get unread message count
messageSchema.statics.getUnreadCount = function(userId, conversationId = null) {
  const query = {
    $or: [
      { recipient: userId },
      { 
        conversationType: 'GROUP',
        'readBy.user': { $ne: userId }
      }
    ],
    sender: { $ne: userId },
    'readBy.user': { $ne: userId },
    isDeleted: false
  };
  
  if (conversationId) {
    query.conversationId = conversationId;
  }
  
  return this.countDocuments(query);
};

// Static method to search messages
messageSchema.statics.searchMessages = function(query, userId, limit = 20) {
  return this.find({
    $text: { $search: query },
    $or: [
      { sender: userId },
      { recipient: userId },
      { conversationType: 'GROUP' }
    ],
    isDeleted: false
  })
  .populate('sender', 'username fullName avatar')
  .populate('recipient', 'username fullName avatar')
  .sort({ score: { $meta: 'textScore' } })
  .limit(limit);
};

// Instance method to add reaction
messageSchema.methods.addReaction = function(userId, emoji) {
  // Remove existing reaction from this user
  this.reactions = this.reactions.filter(
    reaction => reaction.user.toString() !== userId.toString()
  );
  
  // Add new reaction
  this.reactions.push({
    user: userId,
    emoji,
    reactedAt: new Date()
  });
  
  return this.save();
};

// Instance method to remove reaction
messageSchema.methods.removeReaction = function(userId) {
  this.reactions = this.reactions.filter(
    reaction => reaction.user.toString() !== userId.toString()
  );
  
  return this.save();
};

// Instance method to edit message
messageSchema.methods.editContent = function(newContent) {
  this.originalContent = this.content;
  this.content = newContent;
  this.isEdited = true;
  this.editedAt = new Date();
  
  return this.save();
};

// Instance method to soft delete message
messageSchema.methods.softDelete = function(deletedBy) {
  this.isDeleted = true;
  this.deletedAt = new Date();
  this.deletedBy = deletedBy;
  
  return this.save();
};

// Instance method to check if user can see message
messageSchema.methods.canUserSee = function(userId) {
  if (this.isDeleted) return false;
  
  // Direct message
  if (this.conversationType === 'DIRECT') {
    return this.sender.toString() === userId.toString() || 
           this.recipient.toString() === userId.toString();
  }
  
  // Group or task chat - would need additional logic to check membership
  return true;
};

module.exports = mongoose.model('Message', messageSchema);
