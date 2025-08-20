const mongoose = require('mongoose');

const taskSchema = new mongoose.Schema({
  title: {
    type: String,
    required: [true, 'Task title is required'],
    trim: true,
    maxlength: [200, 'Title cannot exceed 200 characters']
  },
  description: {
    type: String,
    required: [true, 'Task description is required'],
    trim: true,
    maxlength: [2000, 'Description cannot exceed 2000 characters']
  },
  status: {
    type: String,
    enum: ['PENDING', 'IN_PROGRESS', 'COMPLETED', 'OVERDUE', 'CANCELLED'],
    default: 'PENDING'
  },
  priority: {
    type: String,
    enum: ['Low', 'Medium', 'High', 'Critical'],
    default: 'Medium'
  },
  assignedTo: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    default: null
  },
  assignedBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    default: null
  },
  createdBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  dueDate: {
    type: Date,
    default: null
  },
  completedAt: {
    type: Date,
    default: null
  },
  estimatedHours: {
    type: Number,
    min: [0, 'Estimated hours cannot be negative'],
    max: [1000, 'Estimated hours cannot exceed 1000']
  },
  actualHours: {
    type: Number,
    min: [0, 'Actual hours cannot be negative'],
    max: [1000, 'Actual hours cannot exceed 1000']
  },
  category: {
    type: String,
    trim: true,
    maxlength: [50, 'Category cannot exceed 50 characters']
  },
  tags: [{
    type: String,
    trim: true,
    maxlength: [30, 'Tag cannot exceed 30 characters']
  }],
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
    uploadedAt: {
      type: Date,
      default: Date.now
    },
    uploadedBy: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User',
      required: true
    }
  }],
  comments: [{
    content: {
      type: String,
      required: true,
      trim: true,
      maxlength: [1000, 'Comment cannot exceed 1000 characters']
    },
    author: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User',
      required: true
    },
    createdAt: {
      type: Date,
      default: Date.now
    },
    updatedAt: {
      type: Date,
      default: Date.now
    },
    attachments: [{
      filename: String,
      url: String,
      mimeType: String,
      size: Number
    }]
  }],
  location: {
    type: String,
    trim: true,
    maxlength: [200, 'Location cannot exceed 200 characters']
  },
  reminderDate: {
    type: Date,
    default: null
  },
  isRecurring: {
    type: Boolean,
    default: false
  },
  recurringPattern: {
    type: String,
    enum: ['daily', 'weekly', 'monthly', 'yearly'],
    default: null
  },
  parentTask: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Task',
    default: null
  },
  subtasks: [{
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Task'
  }],
  watchers: [{
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  }],
  customFields: [{
    name: String,
    value: String,
    type: {
      type: String,
      enum: ['text', 'number', 'date', 'boolean', 'select']
    }
  }],
  // Time tracking logs (compat for web frontend employee timer)
  timeLogs: [{
    user: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
    seconds: { type: Number, required: true, min: 1 },
    loggedAt: { type: Date, default: Date.now }
  }]
}, {
  timestamps: true,
  toJSON: { virtuals: true },
  toObject: { virtuals: true }
});

// Indexes for better performance
taskSchema.index({ status: 1 });
taskSchema.index({ priority: 1 });
taskSchema.index({ assignedTo: 1 });
taskSchema.index({ createdBy: 1 });
taskSchema.index({ dueDate: 1 });
taskSchema.index({ createdAt: -1 });
taskSchema.index({ tags: 1 });
taskSchema.index({ category: 1 });

// Compound indexes
taskSchema.index({ assignedTo: 1, status: 1 });
taskSchema.index({ createdBy: 1, status: 1 });
taskSchema.index({ dueDate: 1, status: 1 });

// Text index for search functionality
taskSchema.index({
  title: 'text',
  description: 'text',
  category: 'text',
  tags: 'text'
});

// Virtual for overdue status
taskSchema.virtual('isOverdue').get(function() {
  if (!this.dueDate || this.status === 'COMPLETED') {
    return false;
  }
  return new Date() > this.dueDate;
});

// Virtual for days until due
taskSchema.virtual('daysUntilDue').get(function() {
  if (!this.dueDate) return null;
  
  const now = new Date();
  const due = new Date(this.dueDate);
  const diffTime = due - now;
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  
  return diffDays;
});

// Virtual for completion percentage (for tasks with subtasks)
taskSchema.virtual('completionPercentage').get(function() {
  if (this.subtasks.length === 0) {
    return this.status === 'COMPLETED' ? 100 : 0;
  }
  
  // This would need to be populated with actual subtask data
  // For now, return a basic calculation
  return this.status === 'COMPLETED' ? 100 : 0;
});

// Pre-save middleware to update status based on due date
taskSchema.pre('save', function(next) {
  // Auto-update to overdue if past due date and not completed
  if (this.dueDate && 
      new Date() > this.dueDate && 
      this.status !== 'COMPLETED' && 
      this.status !== 'CANCELLED') {
    this.status = 'OVERDUE';
  }
  
  // Set completedAt when status changes to completed
  if (this.status === 'COMPLETED' && !this.completedAt) {
    this.completedAt = new Date();
  }
  
  // Clear completedAt if status changes from completed
  if (this.status !== 'COMPLETED' && this.completedAt) {
    this.completedAt = null;
  }
  
  next();
});

// Static method to get tasks by status
taskSchema.statics.findByStatus = function(status) {
  return this.find({ status });
};

// Static method to get overdue tasks
taskSchema.statics.findOverdue = function() {
  return this.find({
    dueDate: { $lt: new Date() },
    status: { $nin: ['COMPLETED', 'CANCELLED'] }
  });
};

// Static method to get tasks due soon
taskSchema.statics.findDueSoon = function(days = 3) {
  const futureDate = new Date();
  futureDate.setDate(futureDate.getDate() + days);
  
  return this.find({
    dueDate: { 
      $gte: new Date(),
      $lte: futureDate 
    },
    status: { $nin: ['COMPLETED', 'CANCELLED'] }
  });
};

// Instance method to add comment
taskSchema.methods.addComment = function(commentData) {
  this.comments.push(commentData);
  return this.save();
};

// Instance method to add watcher
taskSchema.methods.addWatcher = function(userId) {
  if (!this.watchers.includes(userId)) {
    this.watchers.push(userId);
    return this.save();
  }
  return Promise.resolve(this);
};

// Instance method to remove watcher
taskSchema.methods.removeWatcher = function(userId) {
  this.watchers = this.watchers.filter(watcher => 
    watcher.toString() !== userId.toString()
  );
  return this.save();
};

module.exports = mongoose.model('Task', taskSchema);
