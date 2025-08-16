const Task = require('../models/Task');
const User = require('../models/User');
const { validationResult } = require('express-validator');
const admin = require('../config/firebase');

class TaskController {
  // Create new task
  async createTask(req, res) {
    try {
      const errors = validationResult(req);
      if (!errors.isEmpty()) {
        return res.status(400).json({
          success: false,
          message: 'Validation failed',
          errors: errors.array()
        });
      }

      const taskData = {
        ...req.body,
        createdBy: req.user.id
      };

      // If assignedTo is provided, validate the user exists
      if (taskData.assignedTo) {
        const assignedUser = await User.findById(taskData.assignedTo);
        if (!assignedUser) {
          return res.status(400).json({
            success: false,
            message: 'Assigned user not found'
          });
        }
        taskData.assignedBy = req.user.id;
      }

      const task = new Task(taskData);
      await task.save();

      // Populate task data
      await task.populate([
        { path: 'createdBy', select: 'username fullName avatar' },
        { path: 'assignedTo', select: 'username fullName avatar' },
        { path: 'assignedBy', select: 'username fullName avatar' }
      ]);

      // Send push notification if task is assigned
      if (taskData.assignedTo && taskData.assignedTo !== req.user.id) {
        await this.sendTaskNotification(taskData.assignedTo, {
          type: 'TASK_ASSIGNED',
          title: 'New Task Assigned',
          body: `You have been assigned: ${task.title}`,
          taskId: task._id
        });
      }

      res.status(201).json({
        success: true,
        message: 'Task created successfully',
        data: { task }
      });

    } catch (error) {
      console.error('Create task error:', error);
      res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: process.env.NODE_ENV === 'development' ? error.message : undefined
      });
    }
  }

  // Get all tasks with filters
  async getTasks(req, res) {
    try {
      const {
        page = 1,
        limit = 20,
        status,
        priority,
        assignedTo,
        createdBy,
        dueDate,
        search,
        sortBy = 'createdAt',
        sortOrder = 'desc'
      } = req.query;

      const skip = (page - 1) * limit;

      // Build query
      const query = {};

      if (status) query.status = status;
      if (priority) query.priority = priority;
      if (assignedTo) query.assignedTo = assignedTo;
      if (createdBy) query.createdBy = createdBy;

      // Date filters
      if (dueDate) {
        const date = new Date(dueDate);
        const nextDay = new Date(date);
        nextDay.setDate(date.getDate() + 1);
        
        query.dueDate = {
          $gte: date,
          $lt: nextDay
        };
      }

      // Search functionality
      if (search) {
        query.$text = { $search: search };
      }

      // User-specific filtering (users can only see their tasks or public tasks)
      query.$or = [
        { createdBy: req.user.id },
        { assignedTo: req.user.id },
        { watchers: req.user.id }
      ];

      // Sort options
      const sortOptions = {};
      sortOptions[sortBy] = sortOrder === 'desc' ? -1 : 1;

      const tasks = await Task.find(query)
        .populate('createdBy', 'username fullName avatar')
        .populate('assignedTo', 'username fullName avatar')
        .populate('assignedBy', 'username fullName avatar')
        .populate('watchers', 'username fullName avatar')
        .sort(sortOptions)
        .skip(skip)
        .limit(parseInt(limit));

      const totalTasks = await Task.countDocuments(query);

      res.json({
        success: true,
        data: {
          tasks,
          pagination: {
            current: parseInt(page),
            pages: Math.ceil(totalTasks / limit),
            total: totalTasks
          }
        }
      });

    } catch (error) {
      console.error('Get tasks error:', error);
      res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: process.env.NODE_ENV === 'development' ? error.message : undefined
      });
    }
  }

  // Get single task by ID
  async getTaskById(req, res) {
    try {
      const { id } = req.params;

      const task = await Task.findById(id)
        .populate('createdBy', 'username fullName avatar')
        .populate('assignedTo', 'username fullName avatar')
        .populate('assignedBy', 'username fullName avatar')
        .populate('watchers', 'username fullName avatar')
        .populate('subtasks')
        .populate('parentTask', 'title status')
        .populate('comments.author', 'username fullName avatar');

      if (!task) {
        return res.status(404).json({
          success: false,
          message: 'Task not found'
        });
      }

      // Check if user has permission to view this task
      const canView = task.createdBy._id.toString() === req.user.id ||
                     task.assignedTo?._id.toString() === req.user.id ||
                     task.watchers.some(watcher => watcher._id.toString() === req.user.id);

      if (!canView) {
        return res.status(403).json({
          success: false,
          message: 'Access denied'
        });
      }

      res.json({
        success: true,
        data: { task }
      });

    } catch (error) {
      console.error('Get task error:', error);
      res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: process.env.NODE_ENV === 'development' ? error.message : undefined
      });
    }
  }

  // Update task
  async updateTask(req, res) {
    try {
      const errors = validationResult(req);
      if (!errors.isEmpty()) {
        return res.status(400).json({
          success: false,
          message: 'Validation failed',
          errors: errors.array()
        });
      }

      const { id } = req.params;
      const updates = req.body;

      const task = await Task.findById(id);
      if (!task) {
        return res.status(404).json({
          success: false,
          message: 'Task not found'
        });
      }

      // Check permissions
      const canEdit = task.createdBy.toString() === req.user.id ||
                     task.assignedTo?.toString() === req.user.id;

      if (!canEdit) {
        return res.status(403).json({
          success: false,
          message: 'Access denied'
        });
      }

      // Track status changes for notifications
      const oldStatus = task.status;
      const oldAssignee = task.assignedTo?.toString();

      // Update task
      Object.keys(updates).forEach(key => {
        if (key !== '_id' && key !== 'createdBy') {
          task[key] = updates[key];
        }
      });

      await task.save();

      // Populate updated task
      await task.populate([
        { path: 'createdBy', select: 'username fullName avatar' },
        { path: 'assignedTo', select: 'username fullName avatar' },
        { path: 'assignedBy', select: 'username fullName avatar' }
      ]);

      // Send notifications for status changes
      if (oldStatus !== task.status) {
        await this.sendTaskStatusNotification(task, oldStatus);
      }

      // Send notification for new assignment
      if (updates.assignedTo && updates.assignedTo !== oldAssignee) {
        await this.sendTaskNotification(updates.assignedTo, {
          type: 'TASK_ASSIGNED',
          title: 'Task Assigned',
          body: `You have been assigned: ${task.title}`,
          taskId: task._id
        });
      }

      res.json({
        success: true,
        message: 'Task updated successfully',
        data: { task }
      });

    } catch (error) {
      console.error('Update task error:', error);
      res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: process.env.NODE_ENV === 'development' ? error.message : undefined
      });
    }
  }

  // Delete task
  async deleteTask(req, res) {
    try {
      const { id } = req.params;

      const task = await Task.findById(id);
      if (!task) {
        return res.status(404).json({
          success: false,
          message: 'Task not found'
        });
      }

      // Only creator can delete task
      if (task.createdBy.toString() !== req.user.id) {
        return res.status(403).json({
          success: false,
          message: 'Only task creator can delete this task'
        });
      }

      await Task.findByIdAndDelete(id);

      res.json({
        success: true,
        message: 'Task deleted successfully'
      });

    } catch (error) {
      console.error('Delete task error:', error);
      res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: process.env.NODE_ENV === 'development' ? error.message : undefined
      });
    }
  }

  // Add comment to task
  async addComment(req, res) {
    try {
      const errors = validationResult(req);
      if (!errors.isEmpty()) {
        return res.status(400).json({
          success: false,
          message: 'Validation failed',
          errors: errors.array()
        });
      }

      const { id } = req.params;
      const { content, attachments } = req.body;

      const task = await Task.findById(id);
      if (!task) {
        return res.status(404).json({
          success: false,
          message: 'Task not found'
        });
      }

      const comment = {
        content,
        author: req.user.id,
        attachments: attachments || []
      };

      task.comments.push(comment);
      await task.save();

      // Populate the new comment
      await task.populate('comments.author', 'username fullName avatar');

      // Get the newly added comment
      const newComment = task.comments[task.comments.length - 1];

      // Notify relevant users
      const notificationUsers = [task.createdBy];
      if (task.assignedTo) notificationUsers.push(task.assignedTo);
      task.watchers.forEach(watcher => notificationUsers.push(watcher));

      // Remove duplicates and current user
      const uniqueUsers = [...new Set(notificationUsers.map(u => u.toString()))]
        .filter(userId => userId !== req.user.id);

      for (const userId of uniqueUsers) {
        await this.sendTaskNotification(userId, {
          type: 'TASK_COMMENT',
          title: 'New Comment',
          body: `${req.user.fullName} commented on: ${task.title}`,
          taskId: task._id
        });
      }

      res.status(201).json({
        success: true,
        message: 'Comment added successfully',
        data: { comment: newComment }
      });

    } catch (error) {
      console.error('Add comment error:', error);
      res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: process.env.NODE_ENV === 'development' ? error.message : undefined
      });
    }
  }

  // Get task statistics
  async getTaskStats(req, res) {
    try {
      const userId = req.user.id;

      const stats = await Task.aggregate([
        {
          $match: {
            $or: [
              { createdBy: mongoose.Types.ObjectId(userId) },
              { assignedTo: mongoose.Types.ObjectId(userId) }
            ]
          }
        },
        {
          $group: {
            _id: '$status',
            count: { $sum: 1 }
          }
        }
      ]);

      // Get overdue tasks count
      const overdueCount = await Task.countDocuments({
        $or: [
          { createdBy: userId },
          { assignedTo: userId }
        ],
        dueDate: { $lt: new Date() },
        status: { $nin: ['COMPLETED', 'CANCELLED'] }
      });

      // Get tasks due soon (next 3 days)
      const dueSoonCount = await Task.countDocuments({
        $or: [
          { createdBy: userId },
          { assignedTo: userId }
        ],
        dueDate: {
          $gte: new Date(),
          $lte: new Date(Date.now() + 3 * 24 * 60 * 60 * 1000)
        },
        status: { $nin: ['COMPLETED', 'CANCELLED'] }
      });

      const formattedStats = {
        byStatus: stats.reduce((acc, stat) => {
          acc[stat._id] = stat.count;
          return acc;
        }, {}),
        overdue: overdueCount,
        dueSoon: dueSoonCount
      };

      res.json({
        success: true,
        data: formattedStats
      });

    } catch (error) {
      console.error('Get task stats error:', error);
      res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: process.env.NODE_ENV === 'development' ? error.message : undefined
      });
    }
  }

  // Helper method to send task notifications
  async sendTaskNotification(userId, notification) {
    try {
      const user = await User.findById(userId);
      if (!user || user.fcmTokens.length === 0) return;

      const message = {
        notification: {
          title: notification.title,
          body: notification.body
        },
        data: {
          type: notification.type,
          taskId: notification.taskId.toString()
        },
        tokens: user.fcmTokens
      };

      await admin.messaging().sendMulticast(message);
    } catch (error) {
      console.error('Send notification error:', error);
    }
  }

  // Helper method to send task status change notifications
  async sendTaskStatusNotification(task, oldStatus) {
    try {
      const notificationUsers = [task.createdBy];
      if (task.assignedTo) notificationUsers.push(task.assignedTo);
      task.watchers.forEach(watcher => notificationUsers.push(watcher));

      const uniqueUsers = [...new Set(notificationUsers.map(u => u.toString()))];

      for (const userId of uniqueUsers) {
        await this.sendTaskNotification(userId, {
          type: 'TASK_STATUS_CHANGED',
          title: 'Task Status Updated',
          body: `Task "${task.title}" status changed from ${oldStatus} to ${task.status}`,
          taskId: task._id
        });
      }
    } catch (error) {
      console.error('Send status notification error:', error);
    }
  }
}

module.exports = new TaskController();
