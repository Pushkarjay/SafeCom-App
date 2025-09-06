// Task utilities: normalization & helpers (Step 3)
(function(global){
  const STATUS_BACKEND = ['PENDING','IN_PROGRESS','COMPLETED','OVERDUE','CANCELLED'];
  const PRIORITY_BACKEND = ['Low','Medium','High','Critical'];

  const statusMapToFrontend = {
    'PENDING':'pending',
    'IN_PROGRESS':'in-progress',
    'COMPLETED':'completed',
    'OVERDUE':'overdue',
    'CANCELLED':'cancelled'
  };
  const statusMapToBackend = Object.fromEntries(Object.entries(statusMapToFrontend).map(([k,v])=>[v,k]));

  const priorityMapToFrontend = {
    'Low':'low',
    'Medium':'medium',
    'High':'high',
    'Critical':'urgent'
  };
  const priorityMapToBackend = {
    'low':'Low',
    'medium':'Medium',
    'high':'High',
    'urgent':'Critical'
  };

  function normalizeTask(t){
    if(!t) return t;
    // If already normalized (from compat API) just ensure values exist
    const backendStatus = t.status && statusMapToBackend[t.status] ? statusMapToBackend[t.status] : null;
    return {
      ...t,
      status: t.status || (statusMapToFrontend[t.status] ? statusMapToFrontend[t.status] : t.status),
      priority: t.priority && priorityMapToBackend[t.priority] ? t.priority : (priorityMapToFrontend[t.priority] || t.priority),
      _backendStatus: backendStatus,
      _backendPriority: t.priority && priorityMapToBackend[t.priority] ? priorityMapToBackend[t.priority] : null
    };
  }

  function normalizeTasks(list){
    return Array.isArray(list) ? list.map(normalizeTask) : [];
  }

  function backendStatus(value){ return statusMapToBackend[value] || 'PENDING'; }
  function backendPriority(value){ return priorityMapToBackend[value] || 'Medium'; }

  function summarizeStatus(tasks){
    const counts = { pending:0, 'in-progress':0, completed:0, overdue:0, cancelled:0 };
    tasks.forEach(t=>{ if(counts[t.status] !== undefined) counts[t.status]++; });
    return counts;
  }

  global.TaskUtils = { normalizeTask, normalizeTasks, backendStatus, backendPriority, summarizeStatus };
})(window);
