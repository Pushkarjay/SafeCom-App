// Role-based Authentication and Routing System for SafeCom
const ROLES = {
  ADMIN: 'admin',
  CUSTOMER: 'customer', 
  EMPLOYEE: 'employee'
};

const ROLE_PERMISSIONS = {
  [ROLES.ADMIN]: {
    canCreateTasks: true,
    canAssignTasks: true,
    canViewAllTasks: true,
    canManageUsers: true,
    canViewAnalytics: true,
    canAccessSettings: true,
    canDeleteTasks: true,
    canModifySystem: true,
    dashboard: 'admin-dashboard.html'
  },
  [ROLES.CUSTOMER]: {
    canCreateTasks: true,
    canAssignTasks: false,
    canViewAllTasks: false, // Only their own tasks
    canManageUsers: false,
    canViewAnalytics: false, // Only personal analytics
    canAccessSettings: false,
    canDeleteTasks: false, // Only their own tasks
    canModifySystem: false,
    dashboard: 'customer-dashboard.html'
  },
  [ROLES.EMPLOYEE]: {
    canCreateTasks: false,
    canAssignTasks: false,
    canViewAllTasks: false, // Only assigned tasks
    canManageUsers: false,
    canViewAnalytics: false, // Only personal analytics
    canAccessSettings: false,
    canDeleteTasks: false,
    canModifySystem: false,
    dashboard: 'employee-dashboard.html'
  }
};

// API Configuration with role-based endpoints
const API_CONFIG = {
  BASE_URL: 'https://safecom-backend-render-tempo.onrender.com',
  ENDPOINTS: {
    // Authentication
    LOGIN: '/api/auth/login',
    REGISTER: '/api/auth/register',
    LOGOUT: '/api/auth/logout',
    
    // Role-specific task endpoints
    ADMIN_TASKS: '/api/admin/tasks',
    CUSTOMER_TASKS: '/api/customer/tasks',
    EMPLOYEE_TASKS: '/api/employee/tasks',
    
    // User management (Admin only)
    USERS: '/api/admin/users',
    CREATE_USER: '/api/admin/users/create',
    
    // Analytics
    ADMIN_ANALYTICS: '/api/admin/analytics',
    CUSTOMER_ANALYTICS: '/api/customer/analytics',
    EMPLOYEE_ANALYTICS: '/api/employee/analytics',
    
    // Messages
    MESSAGES: '/api/messages',
    SEND_MESSAGE: '/api/messages/send'
  }
};

// Role-based navigation menus
const NAVIGATION_MENUS = {
  [ROLES.ADMIN]: [
    { name: 'Dashboard', url: 'admin-dashboard.html', icon: 'fas fa-tachometer-alt' },
    { name: 'All Tasks', url: 'admin-tasks.html', icon: 'fas fa-tasks' },
    { name: 'User Management', url: 'user-management.html', icon: 'fas fa-users-cog' },
    { name: 'Analytics', url: 'admin-analytics.html', icon: 'fas fa-chart-line' },
    { name: 'Messages', url: 'messages.html', icon: 'fas fa-envelope' },
    { name: 'System Settings', url: 'admin-settings.html', icon: 'fas fa-cog' },
    { name: 'Profile', url: 'profile.html', icon: 'fas fa-user' }
  ],
  [ROLES.CUSTOMER]: [
    { name: 'Dashboard', url: 'customer-dashboard.html', icon: 'fas fa-home' },
    { name: 'My Tasks', url: 'customer-tasks.html', icon: 'fas fa-clipboard-list' },
    { name: 'Create Task', url: 'create-task.html', icon: 'fas fa-plus-circle' },
    { name: 'Messages', url: 'messages.html', icon: 'fas fa-envelope' },
    { name: 'My Analytics', url: 'customer-analytics.html', icon: 'fas fa-chart-bar' },
    { name: 'Profile', url: 'profile.html', icon: 'fas fa-user' }
  ],
  [ROLES.EMPLOYEE]: [
    { name: 'Dashboard', url: 'employee-dashboard.html', icon: 'fas fa-home' },
    { name: 'Assigned Tasks', url: 'employee-tasks.html', icon: 'fas fa-clipboard-check' },
    { name: 'Task History', url: 'task-history.html', icon: 'fas fa-history' },
    { name: 'Messages', url: 'messages.html', icon: 'fas fa-envelope' },
    { name: 'My Stats', url: 'employee-stats.html', icon: 'fas fa-chart-pie' },
    { name: 'Profile', url: 'profile.html', icon: 'fas fa-user' }
  ]
};

// Helper functions
function getCurrentUserRole() {
  const user = JSON.parse(localStorage.getItem('safecom-user') || '{}');
  return user.role || null;
}

function hasPermission(action) {
  const role = getCurrentUserRole();
  return ROLE_PERMISSIONS[role]?.[action] || false;
}

function redirectToDashboard() {
  const role = getCurrentUserRole();
  const dashboard = ROLE_PERMISSIONS[role]?.dashboard || 'login.html';
  window.location.href = dashboard;
}

function checkRoleAccess(requiredRole) {
  const currentRole = getCurrentUserRole();
  if (currentRole !== requiredRole) {
    redirectToDashboard();
    return false;
  }
  return true;
}

// Export for use in other files
if (typeof module !== 'undefined' && module.exports) {
  module.exports = { ROLES, ROLE_PERMISSIONS, API_CONFIG, NAVIGATION_MENUS };
}
