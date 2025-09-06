document.addEventListener("DOMContentLoaded", () => {
  // Demo credentials
  window.fillDemoCredentials = (userType) => {
    const userTypeSelect = document.getElementById("userType");
    const emailInput = document.getElementById("email");
    const passwordInput = document.getElementById("password");
    
    if (userTypeSelect) userTypeSelect.value = userType;
    
    const credentials = {
      admin: { email: 'admin@safecom.com', password: 'admin@123' },
      customer: { email: 'customer@safecom.com', password: 'customer@123' },
      employee: { email: 'employee@safecom.com', password: 'employee@123' }
    };
    
    const cred = credentials[userType];
    if (cred && emailInput && passwordInput) {
      emailInput.value = cred.email;
      passwordInput.value = cred.password;
    }
  };

  // Theme toggle for auth pages
  const themeToggle = document.getElementById("theme-toggle")
  if (themeToggle) {
    const body = document.body
    const icon = themeToggle.querySelector("i")
    const text = themeToggle.querySelector("span")

    // Check for saved theme preference
    const savedTheme = localStorage.getItem("safecom-theme")
    if (savedTheme === "dark") {
      body.classList.remove("light-theme")
      body.classList.add("dark-theme")
      if (icon && text) {
        icon.classList.remove("fa-moon")
        icon.classList.add("fa-sun")
        text.textContent = "Light Mode"
      }
    }

    // Toggle theme
    themeToggle.addEventListener("click", () => {
      if (body.classList.contains("light-theme")) {
        body.classList.remove("light-theme")
        body.classList.add("dark-theme")
        if (icon && text) {
          icon.classList.remove("fa-moon")
          icon.classList.add("fa-sun")
          text.textContent = "Light Mode"
        }
        localStorage.setItem("safecom-theme", "dark")
      } else {
        body.classList.remove("dark-theme")
        body.classList.add("light-theme")
        if (icon && text) {
          icon.classList.remove("fa-sun")
          icon.classList.add("fa-moon")
          text.textContent = "Dark Mode"
        }
        localStorage.setItem("safecom-theme", "light")
      }
    })
  }

  // Login form handling
  const loginForm = document.getElementById("login-form")
  if (loginForm) {
    loginForm.addEventListener("submit", async (e) => {
      e.preventDefault()

      const userType = document.getElementById("userType").value
      const email = document.getElementById("email").value
      const password = document.getElementById("password").value
      const submitBtn = loginForm.querySelector('button[type="submit"]')
      
      if (!userType) {
        alert('Please select a user type');
        return;
      }
      
      // Show loading state
      const originalText = submitBtn.textContent
      submitBtn.textContent = 'Logging in...'
      submitBtn.disabled = true

      try {
        const result = await SafeComAPI.login({ email, password, userType });
        if(result.ok){
          const dashboards = { admin:'admin-dashboard.html', customer:'customer-dashboard.html', employee:'employee-dashboard.html' };
          window.location.href = dashboards[result.data.user.role] || 'dashboard.html';
        } else {
          alert(result.message || 'Login failed. Please try again.');
        }
      } catch (error) {
        console.error('Login error:', error)
        
        // Check if it's a network connectivity issue
        if (error.name === 'TypeError' || error.message.includes('fetch')) {
          // Backend is likely down - offer demo mode
          const useDemo = confirm('Backend service is currently unavailable. Would you like to continue in demo mode?')
          
          if (useDemo) {
            // Create demo user data
            const demoUserData = {
              name: userType.charAt(0).toUpperCase() + userType.slice(1) + ' User',
              email: email,
              role: userType,
              id: 'demo-' + userType,
              isDemoMode: true
            };
            
            // Store demo data
            localStorage.setItem("safecom-token", "demo-token-" + Date.now())
            localStorage.setItem("safecom-user", JSON.stringify(demoUserData))
            
            // Redirect to appropriate dashboard
            const dashboards = {
              admin: 'admin-dashboard.html',
              customer: 'customer-dashboard.html', 
              employee: 'employee-dashboard.html'
            };
            
            window.location.href = dashboards[userType] || 'dashboard.html';
            return;
          }
        }
        
        alert('Network error. Please check your connection and try again.')
      } finally {
        // Restore button state
        submitBtn.textContent = originalText
        submitBtn.disabled = false
      }
    })
  }

  // Signup form handling
  const signupForm = document.getElementById("signup-form")
  if (signupForm) {
    signupForm.addEventListener("submit", async (e) => {
      e.preventDefault()

      const name = document.getElementById("name").value
      const email = document.getElementById("email").value
      const password = document.getElementById("password").value
      const confirmPassword = document.getElementById("confirm-password").value
      const userType = document.getElementById("userType")?.value || 'customer'
      const submitBtn = signupForm.querySelector('button[type="submit"]')

      // Validate passwords match
      if (password !== confirmPassword) {
        alert("Passwords do not match")
        return
      }

      // Show loading state
      const originalText = submitBtn.textContent
      submitBtn.textContent = 'Creating account...'
      submitBtn.disabled = true

      try {
        const result = await SafeComAPI.register({ name, email, password, userType });
        if(result.ok){
          const dashboards = { admin:'admin-dashboard.html', customer:'customer-dashboard.html', employee:'employee-dashboard.html' };
          window.location.href = dashboards[result.data.user.role] || 'dashboard.html';
        } else {
          alert(result.message || 'Registration failed. Please try again.');
        }
      } catch (error) {
        console.error('Signup error:', error)
        
        // Check if it's a network connectivity issue
        if (error.name === 'TypeError' || error.message.includes('fetch')) {
          // Backend is likely down - offer demo mode
          const useDemo = confirm('Backend service is currently unavailable. Would you like to continue in demo mode?')
          
          if (useDemo) {
            // Create demo user data
            const demoUserData = {
              name: name,
              email: email,
              role: userType,
              id: 'demo-' + userType + '-' + Date.now(),
              isDemoMode: true
            };
            
            // Store demo data
            localStorage.setItem("safecom-token", "demo-token-" + Date.now())
            localStorage.setItem("safecom-user", JSON.stringify(demoUserData))
            
            // Redirect to appropriate dashboard
            const dashboards = {
              admin: 'admin-dashboard.html',
              customer: 'customer-dashboard.html', 
              employee: 'employee-dashboard.html'
            };
            
            window.location.href = dashboards[userType] || 'dashboard.html';
            return;
          }
        }
        
        alert('Network error. Please check your connection and try again.')
      } finally {
        // Restore button state
        submitBtn.textContent = originalText
        submitBtn.disabled = false
      }
    })
  }
})

// Authentication helpers
function checkAuth() {
  const token = localStorage.getItem('safecom-token');
  const user = JSON.parse(localStorage.getItem('safecom-user') || '{}');
  
  if (!token || !user.role) {
    window.location.href = 'login.html';
    return false;
  }
  
  return true;
}

function logout() {
  localStorage.removeItem('safecom-token');
  localStorage.removeItem('safecom-user');
  window.location.href = 'login.html';
}

