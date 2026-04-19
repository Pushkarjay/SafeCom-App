// Centralized configuration for SafeCom Frontend
// Step 1: Introduce dynamic API base URL resolution to remove hard-coded endpoints.
(function(global){
  const DEFAULTS = {
    production: 'https://safecom-backend-render-tempo.onrender.com',
    development: 'http://localhost:3000'
  };

  function detectEnv(){
    // If explicitly set via localStorage override
    const stored = localStorage.getItem('safecom-env');
    if (stored) return stored;
    // Heuristic: running on localhost? => development
    if (location.hostname === 'localhost' || location.hostname === '127.0.0.1') return 'development';
    return 'production';
  }

  const env = detectEnv();
  const overrideBase = localStorage.getItem('safecom-api-base');
  const API_BASE = (overrideBase && overrideBase.trim()) || DEFAULTS[env];

  global.SafeComConfig = {
    env,
    API_BASE,
    setApiBase(url){
      localStorage.setItem('safecom-api-base', url);
      this.API_BASE = url;
    },
    resetApiBase(){
      localStorage.removeItem('safecom-api-base');
      this.API_BASE = DEFAULTS[detectEnv()];
    },
    
    // Firebase Configuration
    firebase: {
      projectId: 'safecom-task-management',
      projectNumber: '344799045102',
      apiKey: 'AIzaSyCxoXVuxbQxiNMkJnXyF1PKpXmLoEo-cMU',
      authDomain: 'safecom-task-management.firebaseapp.com',
      storageBucket: 'safecom-task-management.appspot.com',
      messagingSenderId: '344799045102',
      appId: '1:344799045102:web:YOUR_WEB_APP_ID' // Add this when you create Web app in Firebase Console
    }
  };

  console.log('[SafeCom] Config initialized', { env, API_BASE });
})(window);
