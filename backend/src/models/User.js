const mongoose = require('mongoose');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');

const userSchema = new mongoose.Schema({
  // Basic identity
  name: {
    type: String,
    required: [true, 'Name is required'],
    trim: true,
    maxlength: [100, 'Name cannot exceed 100 characters']
  },
  // Optional username (frontend may reference username). Keep unique if provided.
  username: {
    type: String,
    trim: true,
    lowercase: true,
    unique: false, // don't enforce uniqueness until we are certain all data populated
    maxlength: [50, 'Username cannot exceed 50 characters']
  },
  email: {
    type: String,
    required: [true, 'Email is required'],
    unique: true,
    lowercase: true,
    trim: true,
    match: [/^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/, 'Please enter a valid email']
  },
  password: {
    type: String,
    required: [true, 'Password is required'],
    minlength: [8, 'Password must be at least 8 characters long'],
    select: false // Don't include password in queries by default
  },
  role: {
    type: String,
    enum: ['Admin', 'Manager', 'Employee'],
    default: 'Employee'
  },
  department: {
    type: String,
    trim: true,
    maxlength: [50, 'Department cannot exceed 50 characters']
  },
  phoneNumber: {
    type: String,
    trim: true,
    match: [/^\+?[\d\s-()]+$/, 'Please enter a valid phone number']
  },
  // Avatar / profile image (frontend may refer to avatar)
  profileImageUrl: {
    type: String,
    default: null
  },
  avatar: { // keep separate field for clarity / backward compat
    type: String,
    default: null
  },
  // Single legacy token
  fcmToken: {
    type: String,
    default: null
  },
  // New plural tokens array (as controllers expect fcmTokens)
  fcmTokens: [{ type: String }],
  isActive: {
    type: Boolean,
    default: true
  },
  lastLoginAt: {
    type: Date,
    default: null
  },
  emailVerified: {
    type: Boolean,
    default: false
  },
  emailVerificationToken: {
    type: String,
    default: null
  },
  passwordResetToken: {
    type: String,
    default: null
  },
  passwordResetExpires: {
    type: Date,
    default: null
  },
  // User interface / notification settings (existing)
  settings: {
    isDarkModeEnabled: {
      type: Boolean,
      default: false
    },
    isNotificationEnabled: {
      type: Boolean,
      default: true
    },
    isPushNotificationEnabled: {
      type: Boolean,
      default: true
    },
    isEmailNotificationEnabled: {
      type: Boolean,
      default: true
    },
    language: {
      type: String,
      default: 'en',
      enum: ['en', 'es', 'fr', 'de', 'pt', 'zh']
    },
    timezone: {
      type: String,
      default: 'UTC'
    },
    workingHoursStart: {
      type: String,
      default: '09:00',
      match: [/^([01]?[0-9]|2[0-3]):[0-5][0-9]$/, 'Invalid time format']
    },
    workingHoursEnd: {
      type: String,
      default: '17:00',
      match: [/^([01]?[0-9]|2[0-3]):[0-5][0-9]$/, 'Invalid time format']
    }
  },
  // Optional bio / profile info used by updateProfile
  bio: {
    type: String,
    trim: true,
    maxlength: [500, 'Bio cannot exceed 500 characters']
  },
  // Preferences object (separate from settings for future flexibility)
  preferences: {
    type: Object,
    default: {}
  },
  refreshTokens: [{
    token: String,
    createdAt: {
      type: Date,
      default: Date.now,
      expires: 604800 // 7 days
    }
  }]
}, {
  timestamps: true,
  toJSON: { virtuals: true },
  toObject: { virtuals: true }
});

// Indexes for better performance
userSchema.index({ email: 1 });
userSchema.index({ role: 1 });
userSchema.index({ department: 1 });
userSchema.index({ isActive: 1 });

// Virtual for user's full name
userSchema.virtual('fullName').get(function() {
  return this.name;
});

// Provide backward compatible virtual for avatar if only profileImageUrl is set
userSchema.virtual('resolvedAvatar').get(function() {
  return this.avatar || this.profileImageUrl || null;
});

// Virtuals for tasks (controllers attempt to populate createdTasks / assignedTasks)
userSchema.virtual('createdTasks', {
  ref: 'Task',
  localField: '_id',
  foreignField: 'createdBy'
});
userSchema.virtual('assignedTasks', {
  ref: 'Task',
  localField: '_id',
  foreignField: 'assignedTo'
});

// Virtual to get task statistics
userSchema.virtual('taskStats', {
  ref: 'Task',
  localField: '_id',
  foreignField: 'assignedTo',
  justOne: false
});

// Pre-save middleware to hash password
userSchema.pre('save', async function(next) {
  // Only hash the password if it has been modified (or is new)
  if (!this.isModified('password')) return next();

  try {
    // Hash password with cost of 12
    const salt = await bcrypt.genSalt(12);
    this.password = await bcrypt.hash(this.password, salt);
    next();
  } catch (error) {
    next(error);
  }
});

// Instance method to check password
userSchema.methods.comparePassword = async function(candidatePassword) {
  if (!this.password) return false;
  return await bcrypt.compare(candidatePassword, this.password);
};

// Instance method to generate JWT auth token (controllers expect generateAuthToken)
userSchema.methods.generateAuthToken = function() {
  if (!process.env.JWT_SECRET) {
    throw new Error('JWT_SECRET not configured');
  }
  return jwt.sign({ id: this._id }, process.env.JWT_SECRET, {
    expiresIn: process.env.JWT_EXPIRES_IN || '7d'
  });
};

// Instance method to generate password reset token
userSchema.methods.createPasswordResetToken = function() {
  const resetToken = require('crypto').randomBytes(32).toString('hex');
  
  this.passwordResetToken = require('crypto')
    .createHash('sha256')
    .update(resetToken)
    .digest('hex');
  
  this.passwordResetExpires = Date.now() + 10 * 60 * 1000; // 10 minutes
  
  return resetToken;
};

// Instance method to add refresh token
userSchema.methods.addRefreshToken = function(token) {
  this.refreshTokens.push({ token });
  
  // Keep only last 5 refresh tokens
  if (this.refreshTokens.length > 5) {
    this.refreshTokens = this.refreshTokens.slice(-5);
  }
  
  return this.save();
};

// Instance method to remove refresh token
userSchema.methods.removeRefreshToken = function(token) {
  this.refreshTokens = this.refreshTokens.filter(t => t.token !== token);
  return this.save();
};

// Static method to find user by email
userSchema.statics.findByEmail = function(email) {
  return this.findOne({ email: email.toLowerCase() });
};

// Static method to get active users
userSchema.statics.findActiveUsers = function() {
  return this.find({ isActive: true });
};

module.exports = mongoose.model('User', userSchema);
