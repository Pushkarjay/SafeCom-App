const Message = require('../models/Message');
const User = require('../models/User');
const { validationResult } = require('express-validator');
const admin = require('../firebase');

class MessageController {
  // Send a new message
  async sendMessage(req, res) {
    try {
      const errors = validationResult(req);
      if (!errors.isEmpty()) {
        return res.status(400).json({
          success: false,
          message: 'Validation failed',
          errors: errors.array()
        });
      }

      const { 
        content, 
        recipient, 
        conversationId, 
        conversationType, 
        messageType = 'TEXT',
        attachments = [],
        relatedTask = null,
        replyTo = null,
        priority = 'normal'
      } = req.body;

      // Validate recipient for direct messages
      if (conversationType === 'DIRECT' && recipient) {
        const recipientUser = await User.findById(recipient);
        if (!recipientUser) {
          return res.status(400).json({
            success: false,
            message: 'Recipient not found'
          });
        }
      }

      // Create message
      const message = new Message({
        content,
        sender: req.user.id,
        recipient: conversationType === 'DIRECT' ? recipient : null,
        conversationId,
        conversationType,
        messageType,
        attachments,
        relatedTask,
        replyTo,
        priority
      });

      await message.save();

      // Populate message data
      await message.populate([
        { path: 'sender', select: 'username fullName avatar' },
        { path: 'recipient', select: 'username fullName avatar' },
        { path: 'replyTo', select: 'content sender', populate: { path: 'sender', select: 'username fullName' } }
      ]);

      // Emit real-time message via Socket.IO
      req.io.to(conversationId).emit('new_message', {
        message,
        conversationId
      });

      // Send push notification
      if (conversationType === 'DIRECT' && recipient) {
        await this.sendMessageNotification(recipient, {
          title: `Message from ${message.sender.fullName}`,
          body: messageType === 'TEXT' ? content : `Sent ${messageType.toLowerCase()}`,
          conversationId,
          senderId: req.user.id
        });
      }

      res.status(201).json({
        success: true,
        message: 'Message sent successfully',
        data: { message }
      });

    } catch (error) {
      console.error('Send message error:', error);
      res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: process.env.NODE_ENV === 'development' ? error.message : undefined
      });
    }
  }

  // Get conversation messages
  async getConversationMessages(req, res) {
    try {
      const { conversationId } = req.params;
      const { page = 1, limit = 50 } = req.query;

      const messages = await Message.getConversationMessages(
        conversationId, 
        parseInt(page), 
        parseInt(limit)
      );

      // Mark messages as delivered for current user
      await Message.markAsDelivered(conversationId, req.user.id);

      const totalMessages = await Message.countDocuments({
        conversationId,
        isDeleted: false
      });

      res.json({
        success: true,
        data: {
          messages: messages.reverse(), // Reverse to show oldest first
          pagination: {
            current: parseInt(page),
            pages: Math.ceil(totalMessages / limit),
            total: totalMessages
          }
        }
      });

    } catch (error) {
      console.error('Get conversation messages error:', error);
      res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: process.env.NODE_ENV === 'development' ? error.message : undefined
      });
    }
  }

  // Get user conversations
  async getConversations(req, res) {
    try {
      const userId = req.user.id;
      const { page = 1, limit = 20 } = req.query;
      const skip = (page - 1) * limit;

      // Get latest message for each conversation
      const conversations = await Message.aggregate([
        {
          $match: {
            $or: [
              { sender: mongoose.Types.ObjectId(userId) },
              { recipient: mongoose.Types.ObjectId(userId) },
              { conversationType: 'GROUP' } // For group messages, additional filtering needed
            ],
            isDeleted: false
          }
        },
        {
          $sort: { createdAt: -1 }
        },
        {
          $group: {
            _id: '$conversationId',
            lastMessage: { $first: '$$ROOT' },
            unreadCount: {
              $sum: {
                $cond: [
                  {
                    $and: [
                      { $ne: ['$sender', mongoose.Types.ObjectId(userId)] },
                      { $not: { $in: [mongoose.Types.ObjectId(userId), '$readBy.user'] } }
                    ]
                  },
                  1,
                  0
                ]
              }
            }
          }
        },
        {
          $sort: { 'lastMessage.createdAt': -1 }
        },
        {
          $skip: skip
        },
        {
          $limit: parseInt(limit)
        }
      ]);

      // Populate user data
      await Message.populate(conversations, [
        { path: 'lastMessage.sender', select: 'username fullName avatar' },
        { path: 'lastMessage.recipient', select: 'username fullName avatar' }
      ]);

      res.json({
        success: true,
        data: { conversations }
      });

    } catch (error) {
      console.error('Get conversations error:', error);
      res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: process.env.NODE_ENV === 'development' ? error.message : undefined
      });
    }
  }

  // Mark messages as read
  async markAsRead(req, res) {
    try {
      const { conversationId } = req.params;

      await Message.markAsRead(conversationId, req.user.id);

      // Emit read receipt via Socket.IO
      req.io.to(conversationId).emit('messages_read', {
        conversationId,
        userId: req.user.id,
        readAt: new Date()
      });

      res.json({
        success: true,
        message: 'Messages marked as read'
      });

    } catch (error) {
      console.error('Mark as read error:', error);
      res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: process.env.NODE_ENV === 'development' ? error.message : undefined
      });
    }
  }

  // Edit message
  async editMessage(req, res) {
    try {
      const errors = validationResult(req);
      if (!errors.isEmpty()) {
        return res.status(400).json({
          success: false,
          message: 'Validation failed',
          errors: errors.array()
        });
      }

      const { messageId } = req.params;
      const { content } = req.body;

      const message = await Message.findById(messageId);
      if (!message) {
        return res.status(404).json({
          success: false,
          message: 'Message not found'
        });
      }

      // Only sender can edit message
      if (message.sender.toString() !== req.user.id) {
        return res.status(403).json({
          success: false,
          message: 'You can only edit your own messages'
        });
      }

      // Check if message is too old to edit (e.g., 24 hours)
      const editTimeLimit = 24 * 60 * 60 * 1000; // 24 hours
      if (Date.now() - message.createdAt > editTimeLimit) {
        return res.status(400).json({
          success: false,
          message: 'Message is too old to edit'
        });
      }

      await message.editContent(content);

      // Populate updated message
      await message.populate([
        { path: 'sender', select: 'username fullName avatar' },
        { path: 'recipient', select: 'username fullName avatar' }
      ]);

      // Emit message edit via Socket.IO
      req.io.to(message.conversationId).emit('message_edited', {
        message,
        conversationId: message.conversationId
      });

      res.json({
        success: true,
        message: 'Message edited successfully',
        data: { message }
      });

    } catch (error) {
      console.error('Edit message error:', error);
      res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: process.env.NODE_ENV === 'development' ? error.message : undefined
      });
    }
  }

  // Delete message
  async deleteMessage(req, res) {
    try {
      const { messageId } = req.params;

      const message = await Message.findById(messageId);
      if (!message) {
        return res.status(404).json({
          success: false,
          message: 'Message not found'
        });
      }

      // Only sender can delete message
      if (message.sender.toString() !== req.user.id) {
        return res.status(403).json({
          success: false,
          message: 'You can only delete your own messages'
        });
      }

      await message.softDelete(req.user.id);

      // Emit message deletion via Socket.IO
      req.io.to(message.conversationId).emit('message_deleted', {
        messageId,
        conversationId: message.conversationId,
        deletedBy: req.user.id
      });

      res.json({
        success: true,
        message: 'Message deleted successfully'
      });

    } catch (error) {
      console.error('Delete message error:', error);
      res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: process.env.NODE_ENV === 'development' ? error.message : undefined
      });
    }
  }

  // Add reaction to message
  async addReaction(req, res) {
    try {
      const { messageId } = req.params;
      const { emoji } = req.body;

      if (!emoji) {
        return res.status(400).json({
          success: false,
          message: 'Emoji is required'
        });
      }

      const message = await Message.findById(messageId);
      if (!message) {
        return res.status(404).json({
          success: false,
          message: 'Message not found'
        });
      }

      await message.addReaction(req.user.id, emoji);

      // Emit reaction via Socket.IO
      req.io.to(message.conversationId).emit('reaction_added', {
        messageId,
        userId: req.user.id,
        emoji,
        conversationId: message.conversationId
      });

      res.json({
        success: true,
        message: 'Reaction added successfully'
      });

    } catch (error) {
      console.error('Add reaction error:', error);
      res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: process.env.NODE_ENV === 'development' ? error.message : undefined
      });
    }
  }

  // Remove reaction from message
  async removeReaction(req, res) {
    try {
      const { messageId } = req.params;

      const message = await Message.findById(messageId);
      if (!message) {
        return res.status(404).json({
          success: false,
          message: 'Message not found'
        });
      }

      await message.removeReaction(req.user.id);

      // Emit reaction removal via Socket.IO
      req.io.to(message.conversationId).emit('reaction_removed', {
        messageId,
        userId: req.user.id,
        conversationId: message.conversationId
      });

      res.json({
        success: true,
        message: 'Reaction removed successfully'
      });

    } catch (error) {
      console.error('Remove reaction error:', error);
      res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: process.env.NODE_ENV === 'development' ? error.message : undefined
      });
    }
  }

  // Search messages
  async searchMessages(req, res) {
    try {
      const { query, limit = 20 } = req.query;

      if (!query || query.trim().length < 2) {
        return res.status(400).json({
          success: false,
          message: 'Search query must be at least 2 characters'
        });
      }

      const messages = await Message.searchMessages(query, req.user.id, parseInt(limit));

      res.json({
        success: true,
        data: { messages }
      });

    } catch (error) {
      console.error('Search messages error:', error);
      res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: process.env.NODE_ENV === 'development' ? error.message : undefined
      });
    }
  }

  // Get unread message count
  async getUnreadCount(req, res) {
    try {
      const { conversationId } = req.query;

      const count = await Message.getUnreadCount(req.user.id, conversationId);

      res.json({
        success: true,
        data: { unreadCount: count }
      });

    } catch (error) {
      console.error('Get unread count error:', error);
      res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: process.env.NODE_ENV === 'development' ? error.message : undefined
      });
    }
  }

  // Helper method to send message notifications
  async sendMessageNotification(userId, notification) {
    try {
      const user = await User.findById(userId);
      if (!user || user.fcmTokens.length === 0) return;

      const message = {
        notification: {
          title: notification.title,
          body: notification.body
        },
        data: {
          type: 'NEW_MESSAGE',
          conversationId: notification.conversationId,
          senderId: notification.senderId
        },
        tokens: user.fcmTokens
      };

      await admin.messaging().sendMulticast(message);
    } catch (error) {
      console.error('Send message notification error:', error);
    }
  }
}

module.exports = new MessageController();
