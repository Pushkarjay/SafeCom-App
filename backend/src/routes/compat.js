const express = require('express');
const Task = require('../models/Task');
const User = require('../models/User');
const { auth } = require('../middleware/auth');

// This router provides a compatibility layer that maps the current web frontend expectations
// to the existing backend domain models without breaking existing mobile/API clients.
// It intentionally simplifies role logic and response shapes.

const router = express.Router();

// Helper: map frontend lowercase role to backend canonical role
function mapRole(frontRole) {
  if (!frontRole) return 'Employee';
  switch (frontRole.toLowerCase()) {
    case 'admin': return 'Admin';
    case 'manager': return 'Manager';
    case 'employee':
    case 'user':
    case 'customer': // treat customer as basic employee role
      return 'Employee';
    default: return 'Employee';
  }
}

// Helper: transform backend task to frontend expected casing
function toFrontendTask(task) {
  if (!task) return null;
  const plain = task.toObject({ virtuals: true });
  return {
    _id: plain._id,
    title: plain.title,
    description: plain.description,
    status: (plain.status || '').toLowerCase().replace('_', '-'),
    priority: (plain.priority || '').toLowerCase() === 'critical' ? 'urgent' : (plain.priority || '').toLowerCase(),
    dueDate: plain.dueDate,
    createdAt: plain.createdAt,
    updatedAt: plain.updatedAt,
    assignedTo: plain.assignedTo ? {
      id: plain.assignedTo._id,
      name: plain.assignedTo.fullName || plain.assignedTo.name
    } : null,
    assignedBy: plain.assignedBy ? (plain.assignedBy.fullName || plain.assignedBy.name) : null,
    estimatedTime: plain.estimatedHours ? `${plain.estimatedHours}h` : null,
    category: plain.category || null
  };
}

// ---- AUTH COMPAT (adjust request/response shapes) ----
// NOTE: We don't re-implement auth; suggest frontend update. Placeholder could be added here if needed.

// ---- ADMIN TASKS ----
router.get('/admin/tasks', auth, async (req, res) => {
  try {
    // Only allow if user role is Admin
    if (req.user.role !== 'Admin') {
      return res.status(403).json({ message: 'Forbidden' });
    }
    const tasks = await Task.find({}).limit(500).populate('assignedTo assignedBy', 'name fullName');
    res.json(tasks.map(toFrontendTask));
  } catch (e) {
    res.status(500).json({ message: 'Server error' });
  }
});

// ---- CUSTOMER TASKS (acts like tasks created by the user) ----
router.get('/customer/tasks', auth, async (req, res) => {
  try {
    const tasks = await Task.find({ createdBy: req.user._id }).populate('assignedTo assignedBy', 'name fullName');
    res.json(tasks.map(toFrontendTask));
  } catch (e) {
    res.status(500).json({ message: 'Server error' });
  }
});

router.post('/customer/tasks', auth, async (req, res) => {
  try {
    const { title, description, priority, dueDate, category } = req.body;
    const backendPriority = (priority || 'medium').toLowerCase();
    const priorityMap = { low: 'Low', medium: 'Medium', high: 'High', urgent: 'Critical' };
    const task = await Task.create({
      title,
      description,
      priority: priorityMap[backendPriority] || 'Medium',
      dueDate: dueDate ? new Date(dueDate) : null,
      category,
      createdBy: req.user._id
    });
    res.status(201).json(toFrontendTask(task));
  } catch (e) {
    res.status(400).json({ message: e.message });
  }
});

// ---- EMPLOYEE TASKS (assigned to user) ----
router.get('/employee/tasks', auth, async (req, res) => {
  try {
    // Optional date filter: ?date=YYYY-MM-DD
    const { date } = req.query;
    const query = { assignedTo: req.user._id };
    if (date) {
      const start = new Date(date);
      const end = new Date(date);
      end.setDate(end.getDate() + 1);
      query.dueDate = { $gte: start, $lt: end };
    }
    const tasks = await Task.find(query).populate('assignedTo assignedBy', 'name fullName');
    res.json(tasks.map(toFrontendTask));
  } catch (e) {
    res.status(500).json({ message: 'Server error' });
  }
});

// Update task (PUT) -> map to patch semantics
router.put('/employee/tasks/:id', auth, async (req, res) => {
  try {
    const { id } = req.params;
    const { status, notes, timeSpent } = req.body;
    const task = await Task.findById(id).populate('assignedTo assignedBy', 'name fullName');
    if (!task) return res.status(404).json({ message: 'Not found' });
    if (task.assignedTo?.toString() !== req.user._id.toString() && task.createdBy.toString() !== req.user._id.toString()) {
      return res.status(403).json({ message: 'Forbidden' });
    }
    if (status) {
      const map = { 'pending': 'PENDING', 'in-progress': 'IN_PROGRESS', 'completed': 'COMPLETED', 'overdue': 'OVERDUE', 'cancelled': 'CANCELLED' };
      task.status = map[status.toLowerCase()] || task.status;
    }
    if (timeSpent) {
      task.timeLogs.push({ user: req.user._id, seconds: parseInt(timeSpent * 3600, 10) });
    }
    // notes could be appended as a comment
    if (notes) {
      task.comments.push({ content: notes, author: req.user._id });
    }
    await task.save();
    res.json(toFrontendTask(task));
  } catch (e) {
    res.status(400).json({ message: e.message });
  }
});

// Log time endpoint
router.post('/employee/tasks/:id/time', auth, async (req, res) => {
  try {
    const { id } = req.params; const { timeSpent } = req.body; // seconds
    const task = await Task.findById(id);
    if (!task) return res.status(404).json({ message: 'Not found' });
    if (task.assignedTo?.toString() !== req.user._id.toString()) return res.status(403).json({ message: 'Forbidden' });
    if (!timeSpent) return res.status(400).json({ message: 'timeSpent required' });
    task.timeLogs.push({ user: req.user._id, seconds: parseInt(timeSpent, 10) });
    await task.save();
    res.json({ message: 'Time logged' });
  } catch (e) { res.status(400).json({ message: e.message }); }
});

// Employee weekly-progress (basic placeholder)
router.get('/employee/weekly-progress', auth, async (req, res) => {
  try {
    const oneWeek = new Date(); oneWeek.setDate(oneWeek.getDate() - 7);
    const tasks = await Task.find({ assignedTo: req.user._id, createdAt: { $gte: oneWeek } });
    const completed = tasks.filter(t => t.status === 'COMPLETED').length;
    res.json({ completed, total: tasks.length, onTimeRate: 80 });
  } catch (e) { res.status(500).json({ message: 'Server error' }); }
});

// Employee activity placeholder
router.get('/employee/activity', auth, async (req, res) => {
  try {
    const recent = await Task.find({ assignedTo: req.user._id }).sort({ updatedAt: -1 }).limit(10);
    const activities = recent.map(t => ({ type: t.status === 'COMPLETED' ? 'completed' : 'updated', message: `${t.title} status is ${t.status}`, timestamp: t.updatedAt }));
    res.json(activities);
  } catch (e) { res.status(500).json({ message: 'Server error' }); }
});

// Admin users list
router.get('/admin/users', auth, async (req, res) => {
  try {
    if (req.user.role !== 'Admin') return res.status(403).json({ message: 'Forbidden' });
    const users = await User.find({}).select('name fullName email role createdAt');
    res.json(users.map(u => ({ id: u._id, name: u.fullName || u.name, email: u.email, role: u.role.toLowerCase(), createdAt: u.createdAt })));
  } catch (e) { res.status(500).json({ message: 'Server error' }); }
});

// Admin create user
router.post('/admin/users/create', auth, async (req, res) => {
  try {
    if (req.user.role !== 'Admin') return res.status(403).json({ message: 'Forbidden' });
    const { name, email, password, role } = req.body;
    const newUser = await User.create({ name, email, password, role: mapRole(role) });
    res.status(201).json({ id: newUser._id, name: newUser.name, email: newUser.email, role: newUser.role.toLowerCase() });
  } catch (e) { res.status(400).json({ message: e.message }); }
});

// Admin activity placeholder
router.get('/admin/activity', auth, async (req, res) => {
  try {
    if (req.user.role !== 'Admin') return res.status(403).json({ message: 'Forbidden' });
    const recent = await Task.find({}).sort({ updatedAt: -1 }).limit(15);
    const activities = recent.map(t => ({ type: 'task', message: `Task ${t.title} is ${t.status}`, timestamp: t.updatedAt }));
    res.json(activities);
  } catch (e) { res.status(500).json({ message: 'Server error' }); }
});

// ---- ANALYTICS (basic summaries for Step 4) ----
router.get('/admin/analytics/summary', auth, async (req, res) => {
  try {
    if (req.user.role !== 'Admin') return res.status(403).json({ message: 'Forbidden' });
    const [totalTasks, completedTasks, pendingTasks, inProgressTasks, overdueTasks, criticalTasks] = await Promise.all([
      Task.countDocuments({}),
      Task.countDocuments({ status: 'COMPLETED' }),
      Task.countDocuments({ status: 'PENDING' }),
      Task.countDocuments({ status: 'IN_PROGRESS' }),
      Task.countDocuments({ status: 'OVERDUE' }),
      Task.countDocuments({ priority: 'Critical' })
    ]);
    const completionRate = totalTasks ? Math.round((completedTasks / totalTasks) * 100) : 0;
    const overdueRate = totalTasks ? Math.round((overdueTasks / totalTasks) * 100) : 0;
    res.json({
      totalTasks,
      completedTasks,
      pendingTasks,
      inProgressTasks,
      overdueTasks,
      criticalTasks,
      completionRate,
      overdueRate,
      timestamp: new Date()
    });
  } catch (e) { res.status(500).json({ message: 'Server error' }); }
});

router.get('/employee/analytics/summary', auth, async (req, res) => {
  try {
    const userId = req.user._id;
    const [totalAssigned, completed, inProgress, pending, overdue] = await Promise.all([
      Task.countDocuments({ assignedTo: userId }),
      Task.countDocuments({ assignedTo: userId, status: 'COMPLETED' }),
      Task.countDocuments({ assignedTo: userId, status: 'IN_PROGRESS' }),
      Task.countDocuments({ assignedTo: userId, status: 'PENDING' }),
      Task.countDocuments({ assignedTo: userId, status: 'OVERDUE' })
    ]);
    const rate = totalAssigned ? Math.round((completed / totalAssigned) * 100) : 0;
    res.json({ totalAssigned, completed, inProgress, pending, overdue, completionRate: rate, timestamp: new Date() });
  } catch (e) { res.status(500).json({ message: 'Server error' }); }
});

router.get('/customer/analytics/summary', auth, async (req, res) => {
  try {
    const userId = req.user._id;
    const [totalCreated, completed, pending, inProgress, overdue] = await Promise.all([
      Task.countDocuments({ createdBy: userId }),
      Task.countDocuments({ createdBy: userId, status: 'COMPLETED' }),
      Task.countDocuments({ createdBy: userId, status: 'PENDING' }),
      Task.countDocuments({ createdBy: userId, status: 'IN_PROGRESS' }),
      Task.countDocuments({ createdBy: userId, status: 'OVERDUE' })
    ]);
    const completionRate = totalCreated ? Math.round((completed / totalCreated) * 100) : 0;
    res.json({ totalCreated, completed, pending, inProgress, overdue, completionRate, timestamp: new Date() });
  } catch (e) { res.status(500).json({ message: 'Server error' }); }
});

module.exports = router;
