// Employee Dashboard JavaScript
let timeTracker = {
    isRunning: false,
    isPaused: false,
    startTime: null,
    pausedTime: 0,
    interval: null,
    currentTaskId: null
};

document.addEventListener('DOMContentLoaded', function() {
    // Check authentication and role
    if (!checkAuth()) return;
    
    const user = JSON.parse(localStorage.getItem('safecom-user') || '{}');
    if (user.role !== 'employee') {
        alert('Access denied. Employee privileges required.');
        logout();
        return;
    }

    // Initialize dashboard
    initializeEmployeeDashboard();
    loadEmployeeNavigation();
    loadDashboardStats();
    loadTodaysTasks();
    loadRecentActivity();
    loadWeeklyProgress();
    setupEventListeners();
});

function initializeEmployeeDashboard() {
    const user = JSON.parse(localStorage.getItem('safecom-user') || '{}');
    
    // Update user profile
    const userNameElement = document.querySelector('.user-name');
    if (userNameElement) {
        userNameElement.textContent = user.name || 'Employee';
    }
    
    // Show demo mode indicator if applicable
    if (user.isDemoMode) {
        showDemoModeIndicator();
    }
    
    // Theme handling
    const themeToggle = document.getElementById('themeToggle');
    if (themeToggle) {
        themeToggle.addEventListener('click', toggleTheme);
    }
    
    // Sidebar toggle
    const sidebarToggle = document.getElementById('sidebarToggle');
    if (sidebarToggle) {
        sidebarToggle.addEventListener('click', toggleSidebar);
    }
}

function loadEmployeeNavigation() {
    const navElement = document.getElementById('employeeNav');
    if (!navElement) return;
    
    const navigationItems = NAVIGATION_MENUS.employee || [];
    
    navElement.innerHTML = navigationItems.map(item => `
        <a href="${item.url}" class="nav-item ${window.location.pathname.includes(item.url.replace('.html', '')) ? 'active' : ''}">
            <i class="${item.icon}"></i>
            <span>${item.name}</span>
        </a>
    `).join('');
}

async function loadDashboardStats() {
    try {
        const token = localStorage.getItem('safecom-token');
    const response = await fetch(`${SafeComConfig.API_BASE}/api/employee/tasks`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        
        if (response.ok) {
            const raw = await response.json();
            const tasks = (typeof TaskUtils !== 'undefined' && TaskUtils.normalizeTasks) ? TaskUtils.normalizeTasks(raw) : raw;
            updateTaskStats(tasks);
        } else {
            setDemoStats();
        }

        // Load analytics summary
        const analyticsResponse = await fetch(`${SafeComConfig.API_BASE}/api/employee/analytics/summary`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (analyticsResponse.ok) {
            const summary = await analyticsResponse.json();
            const completionEl = document.getElementById('analyticsCompletionRate');
            if (completionEl) completionEl.textContent = summary.completionRate + '%';
        }
        
    } catch (error) {
        console.error('Error loading stats:', error);
        setDemoStats();
    }
}

function updateTaskStats(tasks) {
    const assignedTasks = tasks.length;
    const today = new Date().toDateString();
    const completedToday = tasks.filter(task => 
        task.status === 'completed' && 
        new Date(task.completedAt).toDateString() === today
    ).length;
    const pendingTasks = tasks.filter(task => task.status === 'in-progress').length;
    const urgentTasks = tasks.filter(task => 
        task.priority === 'urgent' && task.status !== 'completed'
    ).length;
    
    document.getElementById('assignedTasks').textContent = assignedTasks;
    document.getElementById('completedTasks').textContent = completedToday;
    document.getElementById('pendingTasks').textContent = pendingTasks;
    document.getElementById('urgentTasks').textContent = urgentTasks;
}

function setDemoStats() {
    document.getElementById('assignedTasks').textContent = '12';
    document.getElementById('completedTasks').textContent = '3';
    document.getElementById('pendingTasks').textContent = '6';
    document.getElementById('urgentTasks').textContent = '2';
}

async function loadTodaysTasks() {
    try {
        const token = localStorage.getItem('safecom-token');
        const today = new Date().toISOString().split('T')[0];
    const response = await fetch(`${SafeComConfig.API_BASE}/api/employee/tasks?date=${today}`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        
        if (response.ok) {
            const raw = await response.json();
            const tasks = (typeof TaskUtils !== 'undefined' && TaskUtils.normalizeTasks) ? TaskUtils.normalizeTasks(raw) : raw;
            displayTodaysTasks(tasks);
        } else {
            displayDemoTasks();
        }
    } catch (error) {
        console.error('Error loading tasks:', error);
        displayDemoTasks();
    }
}

function displayTodaysTasks(tasks) {
    const tasksElement = document.getElementById('todaysTasks');
    if (!tasksElement) return;
    
    if (!tasks || tasks.length === 0) {
        tasksElement.innerHTML = '<p class="no-data">No tasks scheduled for today</p>';
        return;
    }
    
    tasksElement.innerHTML = tasks.map(task => `
        <div class="task-item">
            <div class="task-status ${task.status}"></div>
            <div class="task-content">
                <h4>${task.title}</h4>
                <p>${task.description}</p>
                <div class="task-meta">
                    <span class="task-priority ${task.priority}">${task.priority}</span>
                    <span class="task-time">${formatTime(task.estimatedTime)} est.</span>
                    <span class="task-assignedBy">by ${task.assignedBy}</span>
                </div>
            </div>
            <div class="task-actions">
                <button class="btn-sm btn-primary" onclick="startTaskTimer('${task._id}', '${task.title}')">
                    <i class="fas fa-play"></i>
                </button>
                <button class="btn-sm btn-success" onclick="showTaskUpdateModal('${task._id}')">
                    <i class="fas fa-edit"></i>
                </button>
            </div>
        </div>
    `).join('');
}

function displayDemoTasks() {
    const demoTasks = [
        {
            _id: '1',
            title: 'Server Maintenance',
            description: 'Perform routine maintenance on production servers',
            status: 'pending',
            priority: 'high',
            estimatedTime: '2h 30m',
            assignedBy: 'John Manager'
        },
        {
            _id: '2',
            title: 'Database Backup',
            description: 'Create backup of customer database',
            status: 'in-progress',
            priority: 'medium',
            estimatedTime: '1h 15m',
            assignedBy: 'Sarah Admin'
        },
        {
            _id: '3',
            title: 'Security Patch',
            description: 'Apply latest security patches to workstations',
            status: 'pending',
            priority: 'urgent',
            estimatedTime: '45m',
            assignedBy: 'Mike Lead'
        }
    ];
    
    displayTodaysTasks(demoTasks);
}

async function loadRecentActivity() {
    try {
        const token = localStorage.getItem('safecom-token');
    const response = await fetch(`${SafeComConfig.API_BASE}/api/employee/activity`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        
        if (response.ok) {
            const activities = await response.json();
            displayRecentActivity(activities);
        } else {
            displayDemoActivity();
        }
    } catch (error) {
        console.error('Error loading activity:', error);
        displayDemoActivity();
    }
}

function displayRecentActivity(activities) {
    const activityElement = document.getElementById('recentActivity');
    if (!activityElement) return;
    
    if (!activities || activities.length === 0) {
        activityElement.innerHTML = '<p class="no-data">No recent activity</p>';
        return;
    }
    
    activityElement.innerHTML = activities.slice(0, 5).map(activity => `
        <div class="timeline-item">
            <div class="timeline-marker ${activity.type}"></div>
            <div class="timeline-content">
                <p>${activity.message}</p>
                <span class="timeline-time">${formatTimeAgo(activity.timestamp)}</span>
            </div>
        </div>
    `).join('');
}

function displayDemoActivity() {
    const demoActivities = [
        { type: 'completed', message: 'Completed task "Network Cable Installation"', timestamp: new Date(Date.now() - 30 * 60 * 1000) },
        { type: 'started', message: 'Started working on "Database Backup"', timestamp: new Date(Date.now() - 60 * 60 * 1000) },
        { type: 'assigned', message: 'New task "Security Patch" assigned', timestamp: new Date(Date.now() - 90 * 60 * 1000) },
        { type: 'completed', message: 'Completed task "System Update"', timestamp: new Date(Date.now() - 120 * 60 * 1000) }
    ];
    
    displayRecentActivity(demoActivities);
}

async function loadWeeklyProgress() {
    try {
        const token = localStorage.getItem('safecom-token');
    const response = await fetch(`${SafeComConfig.API_BASE}/api/employee/weekly-progress`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        
        if (response.ok) {
            const progress = await response.json();
            updateWeeklyProgress(progress);
        } else {
            setDemoProgress();
        }
    } catch (error) {
        console.error('Error loading progress:', error);
        setDemoProgress();
    }
}

function updateWeeklyProgress(progress) {
    const completedTasks = progress.completed || 0;
    const totalTasks = progress.total || 0;
    const onTimeRate = progress.onTimeRate || 0;
    
    document.getElementById('weeklyProgress').textContent = `${completedTasks}/${totalTasks}`;
    document.getElementById('onTimeRate').textContent = `${onTimeRate}%`;
    
    const progressPercentage = totalTasks > 0 ? (completedTasks / totalTasks) * 100 : 0;
    document.getElementById('weeklyProgressBar').style.width = `${progressPercentage}%`;
    document.getElementById('onTimeProgressBar').style.width = `${onTimeRate}%`;
}

function setDemoProgress() {
    document.getElementById('weeklyProgress').textContent = '8/12';
    document.getElementById('onTimeRate').textContent = '85%';
    document.getElementById('weeklyProgressBar').style.width = '67%';
    document.getElementById('onTimeProgressBar').style.width = '85%';
}

function setupEventListeners() {
    // Task update form
    const taskUpdateForm = document.getElementById('taskUpdateForm');
    if (taskUpdateForm) {
        taskUpdateForm.addEventListener('submit', handleTaskUpdate);
    }
}

// Time Tracker Functions
function startTaskTimer(taskId, taskTitle) {
    if (timeTracker.isRunning) {
        if (confirm('A timer is already running. Stop current timer and start new one?')) {
            stopTimer();
        } else {
            return;
        }
    }
    
    timeTracker.currentTaskId = taskId;
    timeTracker.isRunning = true;
    timeTracker.isPaused = false;
    timeTracker.startTime = Date.now();
    timeTracker.pausedTime = 0;
    
    // Update UI
    document.getElementById('currentTask').innerHTML = `
        <div class="task-info">
            <h4>${taskTitle}</h4>
            <p>Timer active</p>
        </div>
        <div class="time-display">
            <span id="timeDisplay">00:00:00</span>
        </div>
    `;
    
    document.getElementById('startBtn').style.display = 'none';
    document.getElementById('pauseBtn').style.display = 'inline-block';
    document.getElementById('stopBtn').style.display = 'inline-block';
    
    // Start timer
    timeTracker.interval = setInterval(updateTimeDisplay, 1000);
}

function pauseTimer() {
    if (!timeTracker.isRunning) return;
    
    if (timeTracker.isPaused) {
        // Resume
        timeTracker.isPaused = false;
        timeTracker.startTime = Date.now() - timeTracker.pausedTime;
        timeTracker.interval = setInterval(updateTimeDisplay, 1000);
        document.getElementById('pauseBtn').innerHTML = '<i class="fas fa-pause"></i> Pause';
    } else {
        // Pause
        timeTracker.isPaused = true;
        timeTracker.pausedTime = Date.now() - timeTracker.startTime;
        clearInterval(timeTracker.interval);
        document.getElementById('pauseBtn').innerHTML = '<i class="fas fa-play"></i> Resume';
    }
}

function stopTimer() {
    if (!timeTracker.isRunning) return;
    
    const totalTime = timeTracker.isPaused ? 
        timeTracker.pausedTime : 
        Date.now() - timeTracker.startTime;
    
    // Clear timer
    clearInterval(timeTracker.interval);
    timeTracker.isRunning = false;
    timeTracker.isPaused = false;
    timeTracker.startTime = null;
    timeTracker.pausedTime = 0;
    
    // Update UI
    document.getElementById('currentTask').innerHTML = `
        <div class="task-info">
            <h4>No active task</h4>
            <p>Select a task to start tracking time</p>
        </div>
        <div class="time-display">
            <span id="timeDisplay">00:00:00</span>
        </div>
    `;
    
    document.getElementById('startBtn').style.display = 'inline-block';
    document.getElementById('pauseBtn').style.display = 'none';
    document.getElementById('stopBtn').style.display = 'none';
    
    // Log time
    if (timeTracker.currentTaskId) {
        logTaskTime(timeTracker.currentTaskId, totalTime);
    }
    
    timeTracker.currentTaskId = null;
}

function updateTimeDisplay() {
    if (!timeTracker.isRunning || timeTracker.isPaused) return;
    
    const elapsed = Date.now() - timeTracker.startTime;
    const formatted = formatElapsedTime(elapsed);
    document.getElementById('timeDisplay').textContent = formatted;
}

function formatElapsedTime(milliseconds) {
    const seconds = Math.floor(milliseconds / 1000);
    const hours = Math.floor(seconds / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);
    const remainingSeconds = seconds % 60;
    
    return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`;
}

async function logTaskTime(taskId, timeSpent) {
    try {
        const token = localStorage.getItem('safecom-token');
    const response = await fetch(`${SafeComConfig.API_BASE}/api/employee/tasks/${taskId}/time`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({ timeSpent: Math.floor(timeSpent / 1000) })
        });
        
        if (response.ok) {
            console.log('Time logged successfully');
        }
    } catch (error) {
        console.error('Error logging time:', error);
    }
}

// Modal and form functions
function showTaskUpdateModal(taskId) {
    const modal = document.getElementById('taskUpdateModal');
    if (modal) {
        modal.dataset.taskId = taskId;
        modal.style.display = 'block';
    }
}

function hideTaskUpdateModal() {
    const modal = document.getElementById('taskUpdateModal');
    if (modal) {
        modal.style.display = 'none';
        modal.removeAttribute('data-task-id');
    }
}

async function handleTaskUpdate(e) {
    e.preventDefault();
    
    const modal = document.getElementById('taskUpdateModal');
    const taskId = modal.dataset.taskId;
    
    const updateData = {
        status: document.getElementById('taskStatus').value,
        notes: document.getElementById('progressNotes').value,
        timeSpent: parseFloat(document.getElementById('timeSpent').value) || 0
    };
    
    try {
        const token = localStorage.getItem('safecom-token');
    const response = await fetch(`${SafeComConfig.API_BASE}/api/employee/tasks/${taskId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(updateData)
        });
        
        const result = await response.json();
        
        if (response.ok) {
            alert('Task updated successfully!');
            hideTaskUpdateModal();
            e.target.reset();
            loadDashboardStats();
            loadTodaysTasks();
            loadRecentActivity();
        } else {
            alert(result.message || 'Failed to update task');
        }
    } catch (error) {
        console.error('Error updating task:', error);
        alert('Network error. Please try again.');
    }
}

function refreshTasks() {
    loadTodaysTasks();
    loadDashboardStats();
    
    // Add visual feedback
    const button = event.target;
    const originalText = button.innerHTML;
    button.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Refreshing...';
    button.disabled = true;
    
    setTimeout(() => {
        button.innerHTML = originalText;
        button.disabled = false;
    }, 1000);
}

// Utility functions
function formatTime(timeString) {
    return timeString || '0h 0m';
}

function formatTimeAgo(timestamp) {
    const now = new Date();
    const time = new Date(timestamp);
    const diffInSeconds = Math.floor((now - time) / 1000);
    
    if (diffInSeconds < 60) return 'Just now';
    if (diffInSeconds < 3600) return `${Math.floor(diffInSeconds / 60)} minutes ago`;
    if (diffInSeconds < 86400) return `${Math.floor(diffInSeconds / 3600)} hours ago`;
    return `${Math.floor(diffInSeconds / 86400)} days ago`;
}

function toggleTheme() {
    const body = document.body;
    const isDark = body.classList.contains('dark-theme');
    
    body.classList.toggle('dark-theme', !isDark);
    body.classList.toggle('light-theme', isDark);
    
    localStorage.setItem('safecom-theme', isDark ? 'light' : 'dark');
}

function toggleSidebar() {
    const sidebar = document.querySelector('.sidebar');
    if (sidebar) {
        sidebar.classList.toggle('collapsed');
    }
}

function showDemoModeIndicator() {
    // Create demo mode banner
    const demoBanner = document.createElement('div');
    demoBanner.className = 'demo-mode-banner';
    demoBanner.innerHTML = `
        <div class="demo-banner-content">
            <i class="fas fa-exclamation-triangle"></i>
            <span>Demo Mode - Backend service unavailable. Using sample data.</span>
            <button onclick="this.parentElement.parentElement.remove()" class="demo-banner-close">
                <i class="fas fa-times"></i>
            </button>
        </div>
    `;
    
    // Add banner to the top of the main content
    const mainContent = document.querySelector('.main-content');
    if (mainContent) {
        mainContent.insertBefore(demoBanner, mainContent.firstChild);
    }
}

// Close modal when clicking outside
window.addEventListener('click', function(e) {
    const modal = document.getElementById('taskUpdateModal');
    if (e.target === modal) {
        hideTaskUpdateModal();
    }
});
