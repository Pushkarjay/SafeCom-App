const express = require('express');
const { body, query } = require('express-validator');
const messageController = require('../controllers/messageController');
const auth = require('../middleware/auth');

const router = express.Router();

// Apply authentication middleware to all routes
router.use(auth);

// Validation rules
const sendMessageValidation = [
  body('content')
    .isLength({ min: 1, max: 2000 })
    .withMessage('Message content is required and must not exceed 2000 characters')
    .trim(),
  body('recipient')
    .optional()
    .isMongoId()
    .withMessage('Invalid recipient user ID'),
  body('conversationId')
    .notEmpty()
    .withMessage('Conversation ID is required')
    .trim(),
  body('conversationType')
    .isIn(['DIRECT', 'GROUP', 'TASK_CHAT'])
    .withMessage('Invalid conversation type'),
  body('messageType')
    .optional()
    .isIn(['TEXT', 'IMAGE', 'FILE', 'AUDIO', 'SYSTEM', 'TASK_UPDATE'])
    .withMessage('Invalid message type'),
  body('attachments')
    .optional()
    .isArray()
    .withMessage('Attachments must be an array'),
  body('relatedTask')
    .optional()
    .isMongoId()
    .withMessage('Invalid task ID'),
  body('replyTo')
    .optional()
    .isMongoId()
    .withMessage('Invalid message ID for reply'),
  body('priority')
    .optional()
    .isIn(['normal', 'high', 'urgent'])
    .withMessage('Invalid priority value')
];

const editMessageValidation = [
  body('content')
    .isLength({ min: 1, max: 2000 })
    .withMessage('Message content is required and must not exceed 2000 characters')
    .trim()
];

const reactionValidation = [
  body('emoji')
    .notEmpty()
    .withMessage('Emoji is required')
    .isLength({ min: 1, max: 10 })
    .withMessage('Emoji must be between 1 and 10 characters')
];

const queryValidation = [
  query('page')
    .optional()
    .isInt({ min: 1 })
    .withMessage('Page must be a positive integer'),
  query('limit')
    .optional()
    .isInt({ min: 1, max: 100 })
    .withMessage('Limit must be between 1 and 100'),
  query('query')
    .optional()
    .isLength({ min: 2, max: 100 })
    .withMessage('Search query must be between 2 and 100 characters')
    .trim()
];

// Routes

// Send a new message
router.post('/', sendMessageValidation, messageController.sendMessage);

// Get conversations for current user
router.get('/conversations', queryValidation, messageController.getConversations);

// Get messages in a specific conversation
router.get('/conversations/:conversationId', queryValidation, messageController.getConversationMessages);

// Mark messages as read in a conversation
router.patch('/conversations/:conversationId/read', messageController.markAsRead);

// Get unread message count
router.get('/unread-count', messageController.getUnreadCount);

// Search messages
router.get('/search', queryValidation, messageController.searchMessages);

// Edit a message
router.patch('/:messageId', editMessageValidation, messageController.editMessage);

// Delete a message
router.delete('/:messageId', messageController.deleteMessage);

// Add reaction to a message
router.post('/:messageId/reactions', reactionValidation, messageController.addReaction);

// Remove reaction from a message
router.delete('/:messageId/reactions', messageController.removeReaction);

module.exports = router;
