// Customer Dashboard JavaScript
document.addEventListener('DOMContentLoaded', function() {
    // Check authentication and role
    if (!checkAuth()) return;
    
    const user = JSON.parse(localStorage.getItem('safecom-user') || '{}');
    if (user.role !== 'customer') {
        alert('Access denied. Customer privileges required.');
        logout();
        return;
    }

    // Initialize dashboard
    initializeCustomerDashboard();
    loadCustomerNavigation();
    loadDashboardStats();
    loadRecentTasks();
    setupEventListeners();
});

function initializeCustomerDashboard() {
    const user = JSON.parse(localStorage.getItem('safecom-user') || '{}');
    
    // Update user profile
    const userNameElement = document.querySelector('.user-name');
    if (userNameElement) {
        userNameElement.textContent = user.name || 'Customer';
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

function loadCustomerNavigation() {
    const navElement = document.getElementById('customerNav');
    if (!navElement) return;
    
    const navigationItems = NAVIGATION_MENUS.customer || [];
    
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
    const response = await fetch(`${SafeComConfig.API_BASE}/api/customer/tasks`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        
        if (response.ok) {
            const raw = await response.json();
            const tasks = (typeof TaskUtils !== 'undefined' && TaskUtils.normalizeTasks) ? TaskUtils.normalizeTasks(raw) : raw;
            updateTaskStats(tasks);
            renderTaskChart(tasks);
        } else {
            setDemoStats();
            renderDemoChart();
        }

        // Customer analytics summary
        const analyticsResponse = await fetch(`${SafeComConfig.API_BASE}/api/customer/analytics/summary`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (analyticsResponse.ok) {
            const summary = await analyticsResponse.json();
            const el = document.getElementById('customerCompletionRate');
            if (el) el.textContent = summary.completionRate + '%';
        }
        
    } catch (error) {
        console.error('Error loading stats:', error);
        setDemoStats();
        renderDemoChart();
    }
}

function updateTaskStats(tasks) {
    const totalTasks = tasks.length;
    const completedTasks = tasks.filter(task => task.status === 'completed').length;
    const inProgressTasks = tasks.filter(task => task.status === 'in-progress').length;
    const overdueTasks = tasks.filter(task => 
        new Date(task.dueDate) < new Date() && task.status !== 'completed').length;
    
    document.getElementById('myTasks').textContent = totalTasks;
    document.getElementById('completedTasks').textContent = completedTasks;
    document.getElementById('inProgressTasks').textContent = inProgressTasks;
    document.getElementById('overdueTasks').textContent = overdueTasks;
}

function setDemoStats() {
    document.getElementById('myTasks').textContent = '24';
    document.getElementById('completedTasks').textContent = '18';
    document.getElementById('inProgressTasks').textContent = '4';
    document.getElementById('overdueTasks').textContent = '2';
}

async function loadRecentTasks() {
    try {
        const token = localStorage.getItem('safecom-token');
    const response = await fetch(`${SafeComConfig.API_BASE}/api/customer/tasks?limit=5`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        
        if (response.ok) {
            const raw = await response.json();
            const tasks = (typeof TaskUtils !== 'undefined' && TaskUtils.normalizeTasks) ? TaskUtils.normalizeTasks(raw) : raw;
            displayRecentTasks(tasks);
        } else {
            displayDemoTasks();
        }
    } catch (error) {
        console.error('Error loading tasks:', error);
        displayDemoTasks();
    }
}

function displayRecentTasks(tasks) {
    const tasksElement = document.getElementById('recentTasks');
    if (!tasksElement) return;
    
    if (!tasks || tasks.length === 0) {
        tasksElement.innerHTML = '<p class="no-data">No tasks found</p>';
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
                    <span class="task-date">Due: ${formatDate(task.dueDate)}</span>
                </div>
            </div>
            <div class="task-actions">
                <button class="btn-sm btn-primary" onclick="viewTask('${task._id}')">
                    <i class="fas fa-eye"></i>
                </button>
            </div>
        </div>
    `).join('');
}

function displayDemoTasks() {
    const demoTasks = [
        {
            _id: '1',
            title: 'Office Air Conditioning Repair',
            description: 'AC unit in conference room needs maintenance',
            status: 'in-progress',
            priority: 'high',
            dueDate: new Date(Date.now() + 2 * 24 * 60 * 60 * 1000)
        },
        {
            _id: '2',
            title: 'Security System Upgrade',
            description: 'Install new security cameras in lobby',
            status: 'pending',
            priority: 'medium',
            dueDate: new Date(Date.now() + 5 * 24 * 60 * 60 * 1000)
        },
        {
            _id: '3',
            title: 'Network Cable Installation',
            description: 'Run ethernet cables to new workstations',
            status: 'completed',
            priority: 'low',
            dueDate: new Date(Date.now() - 1 * 24 * 60 * 60 * 1000)
        }
    ];
    
    displayRecentTasks(demoTasks);
}

function renderTaskChart(tasks) {
    const canvas = document.getElementById('taskStatusChart');
    if (!canvas) return;
    
    const ctx = canvas.getContext('2d');
    const data = getTaskStatusData(tasks);
    
    // Simple pie chart implementation
    drawPieChart(ctx, data);
}

function renderDemoChart() {
    const demoData = [
        { label: 'Completed', value: 18, color: '#10B981' },
        { label: 'In Progress', value: 4, color: '#F59E0B' },
        { label: 'Pending', value: 2, color: '#6B7280' },
        { label: 'Overdue', value: 2, color: '#EF4444' }
    ];
    
    const canvas = document.getElementById('taskStatusChart');
    if (!canvas) return;
    
    const ctx = canvas.getContext('2d');
    drawPieChart(ctx, demoData);
}

function getTaskStatusData(tasks) {
    const statusCounts = {
        completed: 0,
        'in-progress': 0,
        pending: 0,
        overdue: 0
    };
    
    tasks.forEach(task => {
        if (new Date(task.dueDate) < new Date() && task.status !== 'completed') {
            statusCounts.overdue++;
        } else {
            statusCounts[task.status] = (statusCounts[task.status] || 0) + 1;
        }
    });
    
    return [
        { label: 'Completed', value: statusCounts.completed, color: '#10B981' },
        { label: 'In Progress', value: statusCounts['in-progress'], color: '#F59E0B' },
        { label: 'Pending', value: statusCounts.pending, color: '#6B7280' },
        { label: 'Overdue', value: statusCounts.overdue, color: '#EF4444' }
    ];
}

function drawPieChart(ctx, data) {
    const centerX = ctx.canvas.width / 2;
    const centerY = ctx.canvas.height / 2;
    const radius = Math.min(centerX, centerY) - 20;
    
    const total = data.reduce((sum, item) => sum + item.value, 0);
    let currentAngle = -Math.PI / 2;
    
    ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
    
    data.forEach(item => {
        if (item.value > 0) {
            const sliceAngle = (item.value / total) * 2 * Math.PI;
            
            ctx.beginPath();
            ctx.moveTo(centerX, centerY);
            ctx.arc(centerX, centerY, radius, currentAngle, currentAngle + sliceAngle);
            ctx.closePath();
            ctx.fillStyle = item.color;
            ctx.fill();
            
            currentAngle += sliceAngle;
        }
    });
}

function setupEventListeners() {
    // Create task form
    const createTaskForm = document.getElementById('createTaskForm');
    if (createTaskForm) {
        createTaskForm.addEventListener('submit', handleCreateTask);
    }
    
    // Set minimum date for due date
    const dueDateInput = document.getElementById('taskDueDate');
    if (dueDateInput) {
        const today = new Date().toISOString().split('T')[0];
        dueDateInput.min = today;
    }
}

async function handleCreateTask(e) {
    e.preventDefault();
    
    const taskData = {
        title: document.getElementById('taskTitle').value,
        description: document.getElementById('taskDescription').value,
        priority: document.getElementById('taskPriority').value,
        category: document.getElementById('taskCategory').value,
        dueDate: document.getElementById('taskDueDate').value
    };
    
    try {
        const token = localStorage.getItem('safecom-token');
    const response = await fetch(`${SafeComConfig.API_BASE}/api/customer/tasks`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(taskData)
        });
        
        const result = await response.json();
        
        if (response.ok) {
            alert('Task created successfully!');
            hideCreateTaskModal();
            e.target.reset();
            loadDashboardStats();
            loadRecentTasks();
        } else {
            alert(result.message || 'Failed to create task');
        }
    } catch (error) {
        console.error('Error creating task:', error);
        alert('Network error. Please try again.');
    }
}

// Modal functions
function showCreateTaskModal() {
    const modal = document.getElementById('createTaskModal');
    if (modal) {
        modal.style.display = 'block';
    }
}

function hideCreateTaskModal() {
    const modal = document.getElementById('createTaskModal');
    if (modal) {
        modal.style.display = 'none';
    }
}

function viewTask(taskId) {
    // Navigate to task details
    window.location.href = `task-details.html?id=${taskId}`;
}

// Utility functions
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
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
    const modal = document.getElementById('createTaskModal');
    if (e.target === modal) {
        hideCreateTaskModal();
    }
});
