const express = require('express');
const Task = require('../models/Task');
const { auth } = require('../middleware/auth');

// Mobile compatibility router (Option A)
// Provides simplified DTO shape expected by Android legacy REST layer without altering core /tasks.
const router = express.Router();
router.use(auth);

function toTaskDto(t){
  if(!t) return null;
  const obj = t.toObject({virtuals:true});
  return {
    id: obj._id.toString(),
    title: obj.title,
    description: obj.description,
    status: obj.status,
    priority: obj.priority,
    assignedTo: obj.assignedTo ? obj.assignedTo.toString() : null,
    assignedBy: obj.assignedBy ? obj.assignedBy.toString() : null,
    createdBy: obj.createdBy ? obj.createdBy.toString() : null,
    dueDate: obj.dueDate ? obj.dueDate.getTime() : null,
    createdAt: obj.createdAt ? obj.createdAt.getTime() : Date.now(),
    updatedAt: obj.updatedAt ? obj.updatedAt.getTime() : Date.now(),
    completedAt: obj.completedAt ? obj.completedAt.getTime() : null,
    tags: obj.tags || [],
    attachments: (obj.attachments || []).map(a=>a.url),
    estimatedHours: obj.estimatedHours || null,
    actualHours: obj.actualHours || null,
    category: obj.category || null,
    location: obj.location || null,
    reminderDate: obj.reminderDate ? obj.reminderDate.getTime() : null,
    isRecurring: obj.isRecurring || false,
    recurringPattern: obj.recurringPattern || null
  };
}

function roleQuery(user){
  if(user.role === 'Admin') return {}; // all tasks
  return { $or: [{ assignedTo: user._id }, { createdBy: user._id }] };
}

router.get('/tasks', async (req,res)=>{
  try { const tasks = await Task.find(roleQuery(req.user)).sort({updatedAt:-1}).limit(500); res.json(tasks.map(toTaskDto)); }
  catch(e){ res.status(500).json({message:'Server error'}); }
});

router.get('/tasks/status/:status', async (req,res)=>{
  try { const status = req.params.status.toUpperCase(); const tasks = await Task.find({ ...roleQuery(req.user), status }).limit(200); res.json(tasks.map(toTaskDto)); }
  catch(e){ res.status(500).json({message:'Server error'}); }
});

router.get('/tasks/assignee/:userId', async (req,res)=>{
  try { const tasks = await Task.find({ assignedTo: req.params.userId }).limit(200); res.json(tasks.map(toTaskDto)); }
  catch(e){ res.status(500).json({message:'Server error'}); }
});

router.get('/tasks/search', async (req,res)=>{
  try { const q = req.query.q; if(!q) return res.json([]); const tasks = await Task.find({ $text: { $search: q }, ...roleQuery(req.user) }).limit(100); res.json(tasks.map(toTaskDto)); }
  catch(e){ res.status(500).json({message:'Server error'}); }
});

router.get('/tasks/:id', async (req,res)=>{
  try { const task = await Task.findById(req.params.id); if(!task) return res.status(404).json({message:'Not found'}); res.json(toTaskDto(task)); }
  catch(e){ res.status(500).json({message:'Server error'}); }
});

router.post('/tasks', async (req,res)=>{
  try { const { title, description, priority, assignedTo, dueDate, estimatedHours, category } = req.body; const task = await Task.create({ title, description, priority: priority || 'Medium', assignedTo: assignedTo||null, assignedBy: assignedTo?req.user._id:null, createdBy: req.user._id, dueDate: dueDate?new Date(dueDate):null, estimatedHours: estimatedHours||null, category }); res.status(201).json(toTaskDto(task)); }
  catch(e){ res.status(400).json({message:e.message}); }
});

router.patch('/tasks/:id', async (req,res)=>{
  try { const update = req.body||{}; if(update.dueDate) update.dueDate = new Date(update.dueDate); const task = await Task.findByIdAndUpdate(req.params.id, update, { new: true }); if(!task) return res.status(404).json({message:'Not found'}); res.json(toTaskDto(task)); }
  catch(e){ res.status(400).json({message:e.message}); }
});

router.patch('/tasks/:id/status', async (req,res)=>{
  try { const { status } = req.body; if(!status) return res.status(400).json({message:'status required'}); const task = await Task.findById(req.params.id); if(!task) return res.status(404).json({message:'Not found'}); task.status = status.toUpperCase(); await task.save(); res.json(toTaskDto(task)); }
  catch(e){ res.status(400).json({message:e.message}); }
});

router.delete('/tasks/:id', async (req,res)=>{
  try { const task = await Task.findByIdAndDelete(req.params.id); if(!task) return res.status(404).json({message:'Not found'}); res.json({message:'Deleted'}); }
  catch(e){ res.status(500).json({message:'Server error'}); }
});

router.get('/tasks/count', async (req,res)=>{
  try { const c = await Task.countDocuments(roleQuery(req.user)); res.json(c); }
  catch(e){ res.status(500).json({message:'Server error'}); }
});

router.get('/tasks/count/status/:status', async (req,res)=>{
  try { const c = await Task.countDocuments({ ...roleQuery(req.user), status: req.params.status.toUpperCase() }); res.json(c); }
  catch(e){ res.status(500).json({message:'Server error'}); }
});

router.get('/activities/recent', async (req,res)=>{
  try { const limit = parseInt(req.query.limit||'10',10); const tasks = await Task.find(roleQuery(req.user)).sort({updatedAt:-1}).limit(limit); const activities = tasks.map(t=>({ id: t._id.toString(), type: t.status === 'COMPLETED' ? 'COMPLETED':'TASK', title: t.title, description: t.description, timestamp: t.updatedAt ? t.updatedAt.getTime():Date.now(), userId: t.assignedTo? t.assignedTo.toString() : (t.createdBy? t.createdBy.toString():''), userName:'', userAvatar:null, taskId: t._id.toString(), taskTitle: t.title, icon:null })); res.json(activities); }
  catch(e){ res.status(500).json({message:'Server error'}); }
});

module.exports = router;
