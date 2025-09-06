// Admin Dashboard JavaScript
document.addEventListener('DOMContentLoaded', function() {
    // Check authentication and role
    if (!checkAuth()) return;
    
    const user = JSON.parse(localStorage.getItem('safecom-user') || '{}');
    if (user.role !== 'admin') {
        alert('Access denied. Admin privileges required.');
        logout();
        return;
    }

    // Initialize dashboard
    initializeAdminDashboard();
    loadAdminNavigation();
    loadDashboardStats();
    loadRecentActivity();
    setupEventListeners();
});

function initializeAdminDashboard() {
    const user = JSON.parse(localStorage.getItem('safecom-user') || '{}');
    
    // Update user profile
    const userNameElement = document.querySelector('.user-name');
    if (userNameElement) {
        userNameElement.textContent = user.name || 'Admin User';
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

function loadAdminNavigation() {
    const navElement = document.getElementById('adminNav');
    if (!navElement) return;
    
    const navigationItems = NAVIGATION_MENUS.admin || [];
    
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
        
        // Load total tasks
    const tasksResponse = await fetch(`${SafeComConfig.API_BASE}/api/admin/tasks`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        
        if (tasksResponse.ok) {
            const raw = await tasksResponse.json();
            const tasks = (typeof TaskUtils !== 'undefined' && TaskUtils.normalizeTasks) ? TaskUtils.normalizeTasks(raw) : raw;
            updateTaskStats(tasks);
        }
        
        // Load total users
    const usersResponse = await fetch(`${SafeComConfig.API_BASE}/api/admin/users`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        
        if (usersResponse.ok) {
            const users = await usersResponse.json();
            updateUserStats(users);
        }

        // Analytics summary (optional UI elements)
        const analyticsResponse = await fetch(`${SafeComConfig.API_BASE}/api/admin/analytics/summary`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (analyticsResponse.ok) {
            const summary = await analyticsResponse.json();
            // Populate if elements exist; otherwise just log for now
            const completionEl = document.getElementById('completionRate');
            if (completionEl) completionEl.textContent = summary.completionRate + '%';
            const overdueRateEl = document.getElementById('overdueRate');
            if (overdueRateEl) overdueRateEl.textContent = summary.overdueRate + '%';
            const criticalEl = document.getElementById('criticalTasks');
            if (criticalEl) criticalEl.textContent = summary.criticalTasks;
        }
        
    } catch (error) {
        console.error('Error loading stats:', error);
        // Set demo data if API is not available
        setDemoStats();
    }
}

function updateTaskStats(tasks) {
    const totalTasks = tasks.length;
    const pendingTasks = tasks.filter(task => task.status === 'pending').length;
    const overdueTasks = tasks.filter(task => task.status === 'overdue' || 
        (task.dueDate && new Date(task.dueDate) < new Date() && task.status !== 'completed')).length;
    document.getElementById('totalTasks').textContent = totalTasks;
    document.getElementById('pendingTasks').textContent = pendingTasks;
    document.getElementById('overdueTasks').textContent = overdueTasks;
}

function updateUserStats(users) {
    const totalUsers = users.length;
    document.getElementById('totalUsers').textContent = totalUsers;
}

function setDemoStats() {
    // Demo data for when API is not available
    document.getElementById('totalTasks').textContent = '156';
    document.getElementById('totalUsers').textContent = '42';
    document.getElementById('pendingTasks').textContent = '23';
    document.getElementById('overdueTasks').textContent = '7';
}

async function loadRecentActivity() {
    try {
        const token = localStorage.getItem('safecom-token');
    const response = await fetch(`${SafeComConfig.API_BASE}/api/admin/activity`, {
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
        <div class="activity-item">
            <div class="activity-icon ${activity.type}">
                <i class="fas ${getActivityIcon(activity.type)}"></i>
            </div>
            <div class="activity-content">
                <p>${activity.message}</p>
                <span class="activity-time">${formatTimeAgo(activity.timestamp)}</span>
            </div>
        </div>
    `).join('');
}

function displayDemoActivity() {
    const demoActivities = [
        { type: 'task', message: 'New task "System Maintenance" created by John Doe', timestamp: new Date(Date.now() - 10 * 60 * 1000) },
        { type: 'user', message: 'New employee Sarah Johnson added to the system', timestamp: new Date(Date.now() - 30 * 60 * 1000) },
        { type: 'task', message: 'Task "Security Update" completed by Mike Smith', timestamp: new Date(Date.now() - 45 * 60 * 1000) },
        { type: 'system', message: 'System backup completed successfully', timestamp: new Date(Date.now() - 60 * 60 * 1000) },
        { type: 'task', message: 'High priority task "Emergency Repair" assigned', timestamp: new Date(Date.now() - 90 * 60 * 1000) }
    ];
    
    displayRecentActivity(demoActivities);
}

function getActivityIcon(type) {
    const icons = {
        'task': 'fa-tasks',
        'user': 'fa-user',
        'system': 'fa-cog',
        'message': 'fa-envelope'
    };
    return icons[type] || 'fa-info-circle';
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

function setupEventListeners() {
    // Create user form
    const createUserForm = document.getElementById('createUserForm');
    if (createUserForm) {
        createUserForm.addEventListener('submit', handleCreateUser);
    }
}

async function handleCreateUser(e) {
    e.preventDefault();
    
    const formData = new FormData(e.target);
    const userData = {
        name: document.getElementById('userName').value,
        email: document.getElementById('userEmail').value,
        role: document.getElementById('userRole').value,
        password: document.getElementById('userPassword').value
    };
    
    try {
        const token = localStorage.getItem('safecom-token');
    const response = await fetch(`${SafeComConfig.API_BASE}/api/admin/users/create`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(userData)
        });
        
        const result = await response.json();
        
        if (response.ok) {
            alert('User created successfully!');
            hideCreateUserModal();
            e.target.reset();
            loadDashboardStats(); // Refresh stats
        } else {
            alert(result.message || 'Failed to create user');
        }
    } catch (error) {
        console.error('Error creating user:', error);
        alert('Network error. Please try again.');
    }
}

// Modal functions
function showCreateUserModal() {
    const modal = document.getElementById('createUserModal');
    if (modal) {
        modal.style.display = 'block';
    }
}

function hideCreateUserModal() {
    const modal = document.getElementById('createUserModal');
    if (modal) {
        modal.style.display = 'none';
    }
}

// Utility functions
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
    const modal = document.getElementById('createUserModal');
    if (e.target === modal) {
        hideCreateUserModal();
    }
});
