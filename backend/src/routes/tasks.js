const express = require('express');
const { body, query } = require('express-validator');
const taskController = require('../controllers/taskController');
const auth = require('../middleware/auth');

const router = express.Router();

// Apply authentication middleware to all routes
router.use(auth);

// Validation rules
const createTaskValidation = [
  body('title')
    .isLength({ min: 1, max: 200 })
    .withMessage('Title is required and must not exceed 200 characters')
    .trim(),
  body('description')
    .isLength({ min: 1, max: 2000 })
    .withMessage('Description is required and must not exceed 2000 characters')
    .trim(),
  body('status')
    .optional()
    .isIn(['PENDING', 'IN_PROGRESS', 'COMPLETED', 'OVERDUE', 'CANCELLED'])
    .withMessage('Invalid status value'),
  body('priority')
    .optional()
    .isIn(['Low', 'Medium', 'High', 'Critical'])
    .withMessage('Invalid priority value'),
  body('assignedTo')
    .optional()
    .isMongoId()
    .withMessage('Invalid user ID for assignedTo'),
  body('dueDate')
    .optional()
    .isISO8601()
    .withMessage('Due date must be a valid date'),
  body('estimatedHours')
    .optional()
    .isFloat({ min: 0, max: 1000 })
    .withMessage('Estimated hours must be between 0 and 1000'),
  body('category')
    .optional()
    .isLength({ max: 50 })
    .withMessage('Category cannot exceed 50 characters')
    .trim(),
  body('tags')
    .optional()
    .isArray()
    .withMessage('Tags must be an array'),
  body('tags.*')
    .optional()
    .isLength({ max: 30 })
    .withMessage('Each tag cannot exceed 30 characters')
    .trim(),
  body('location')
    .optional()
    .isLength({ max: 200 })
    .withMessage('Location cannot exceed 200 characters')
    .trim(),
  body('reminderDate')
    .optional()
    .isISO8601()
    .withMessage('Reminder date must be a valid date'),
  body('isRecurring')
    .optional()
    .isBoolean()
    .withMessage('isRecurring must be a boolean'),
  body('recurringPattern')
    .optional()
    .isIn(['daily', 'weekly', 'monthly', 'yearly'])
    .withMessage('Invalid recurring pattern'),
  body('parentTask')
    .optional()
    .isMongoId()
    .withMessage('Invalid parent task ID')
];

const updateTaskValidation = [
  body('title')
    .optional()
    .isLength({ min: 1, max: 200 })
    .withMessage('Title must not exceed 200 characters')
    .trim(),
  body('description')
    .optional()
    .isLength({ min: 1, max: 2000 })
    .withMessage('Description must not exceed 2000 characters')
    .trim(),
  body('status')
    .optional()
    .isIn(['PENDING', 'IN_PROGRESS', 'COMPLETED', 'OVERDUE', 'CANCELLED'])
    .withMessage('Invalid status value'),
  body('priority')
    .optional()
    .isIn(['Low', 'Medium', 'High', 'Critical'])
    .withMessage('Invalid priority value'),
  body('assignedTo')
    .optional()
    .isMongoId()
    .withMessage('Invalid user ID for assignedTo'),
  body('dueDate')
    .optional()
    .isISO8601()
    .withMessage('Due date must be a valid date'),
  body('completedAt')
    .optional()
    .isISO8601()
    .withMessage('Completed date must be a valid date'),
  body('estimatedHours')
    .optional()
    .isFloat({ min: 0, max: 1000 })
    .withMessage('Estimated hours must be between 0 and 1000'),
  body('actualHours')
    .optional()
    .isFloat({ min: 0, max: 1000 })
    .withMessage('Actual hours must be between 0 and 1000'),
  body('category')
    .optional()
    .isLength({ max: 50 })
    .withMessage('Category cannot exceed 50 characters')
    .trim(),
  body('tags')
    .optional()
    .isArray()
    .withMessage('Tags must be an array'),
  body('location')
    .optional()
    .isLength({ max: 200 })
    .withMessage('Location cannot exceed 200 characters')
    .trim(),
  body('reminderDate')
    .optional()
    .isISO8601()
    .withMessage('Reminder date must be a valid date'),
  body('isRecurring')
    .optional()
    .isBoolean()
    .withMessage('isRecurring must be a boolean'),
  body('recurringPattern')
    .optional()
    .isIn(['daily', 'weekly', 'monthly', 'yearly'])
    .withMessage('Invalid recurring pattern')
];

const addCommentValidation = [
  body('content')
    .isLength({ min: 1, max: 1000 })
    .withMessage('Comment content is required and must not exceed 1000 characters')
    .trim(),
  body('attachments')
    .optional()
    .isArray()
    .withMessage('Attachments must be an array')
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
  query('status')
    .optional()
    .isIn(['PENDING', 'IN_PROGRESS', 'COMPLETED', 'OVERDUE', 'CANCELLED'])
    .withMessage('Invalid status value'),
  query('priority')
    .optional()
    .isIn(['Low', 'Medium', 'High', 'Critical'])
    .withMessage('Invalid priority value'),
  query('assignedTo')
    .optional()
    .isMongoId()
    .withMessage('Invalid user ID for assignedTo'),
  query('createdBy')
    .optional()
    .isMongoId()
    .withMessage('Invalid user ID for createdBy'),
  query('dueDate')
    .optional()
    .isISO8601()
    .withMessage('Due date must be a valid date'),
  query('sortBy')
    .optional()
    .isIn(['createdAt', 'updatedAt', 'dueDate', 'priority', 'status', 'title'])
    .withMessage('Invalid sort field'),
  query('sortOrder')
    .optional()
    .isIn(['asc', 'desc'])
    .withMessage('Sort order must be asc or desc')
];

// Routes
router.post('/', createTaskValidation, taskController.createTask);
router.get('/', queryValidation, taskController.getTasks);
router.get('/stats', taskController.getTaskStats);
router.get('/:id', taskController.getTaskById);
router.patch('/:id', updateTaskValidation, taskController.updateTask);
router.delete('/:id', taskController.deleteTask);
router.post('/:id/comments', addCommentValidation, taskController.addComment);

module.exports = router;
