// Central API helper (Step 2) for SafeCom frontend
// Provides standardized request handling, auth persistence, and error normalization.
(function(global){
  const STORAGE_KEYS = {
    TOKEN: 'safecom-token',
    USER: 'safecom-user'
  };

  function getToken(){ return localStorage.getItem(STORAGE_KEYS.TOKEN); }
  function setAuth(token, user){
    if(token) localStorage.setItem(STORAGE_KEYS.TOKEN, token);
    if(user) localStorage.setItem(STORAGE_KEYS.USER, JSON.stringify(user));
  }
  function clearAuth(){
    localStorage.removeItem(STORAGE_KEYS.TOKEN);
    localStorage.removeItem(STORAGE_KEYS.USER);
  }

  async function request(path, { method='GET', body, headers={} } = {}){
    const token = getToken();
    const fullUrl = `${SafeComConfig.API_BASE}${path}`;
    const opts = { method, headers: { 'Accept': 'application/json', ...headers } };
    if(body !== undefined){
      if(!(body instanceof FormData)){
        opts.headers['Content-Type'] = 'application/json';
        opts.body = JSON.stringify(body);
      } else {
        opts.body = body; // browser sets multipart headers
      }
    }
    if(token) opts.headers['Authorization'] = `Bearer ${token}`;

    let res;
    try { res = await fetch(fullUrl, opts); } catch (e){
      return { ok:false, status:0, error:'NETWORK_ERROR', message:e.message };
    }

    let data = null;
    try { data = await res.json(); } catch(_) {}

    if(!res.ok){
      return { ok:false, status:res.status, error:data?.error||'REQUEST_FAILED', message:data?.message||'Request failed', data };
    }
    return { ok:true, status:res.status, data };
  }

  // Role mapping: backend -> frontend canonical
  function mapBackendRole(role){
    if(!role) return 'employee';
    const r = role.toLowerCase();
    if(r === 'admin') return 'admin';
    if(r === 'manager') return 'admin'; // treat manager like admin for current UI until dedicated pages added
    if(r === 'employee') return 'employee';
    return 'employee';
  }

  // AUTH --------------------------------------------------
  async function login({ email, password, userType }){
    // Send minimal payload; userType optional
    const result = await request('/api/auth/login', { method:'POST', body: { email, password, userType } });
    if(!result.ok) return result;
    const raw = result.data;
    const token = raw.token || raw.data?.token;
    const user = raw.user || raw.data?.user || {};
    const frontendRole = mapBackendRole(user.role || userType);
    const userNormalized = {
      id: user._id || user.id,
      name: user.name || user.fullName || 'User',
      email: user.email,
      role: frontendRole,
      backendRole: user.role,
      isDemoMode: false
    };
    setAuth(token, userNormalized);
    return { ok:true, status:result.status, data:{ token, user: userNormalized } };
  }

  async function register({ name, email, password, userType }){
    const result = await request('/api/auth/register', { method:'POST', body: { name, email, password, userType } });
    if(!result.ok) return result;
    const raw = result.data;
    const token = raw.token || raw.data?.token;
    const user = raw.user || raw.data?.user || {};
    const frontendRole = mapBackendRole(user.role || userType);
    const userNormalized = {
      id: user._id || user.id,
      name: user.name || user.fullName || name,
      email: user.email,
      role: frontendRole,
      backendRole: user.role,
      isDemoMode: false
    };
    setAuth(token, userNormalized);
    return { ok:true, status:result.status, data:{ token, user: userNormalized } };
  }

  function logout(){ clearAuth(); }

  global.SafeComAPI = { request, login, register, logout, getToken, clearAuth };
})(window);
