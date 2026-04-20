/**
 * Login Component - React version
 * Replaces login.html with proper routing and state management
 */

import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Login.css';

const Login: React.FC = () => {
  const navigate = useNavigate();
  const { login, enterDemoMode } = useAuth();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [userType, setUserType] = useState('employee');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const demoAccounts = [
    { type: 'Admin', email: 'admin@safecom.test', role: 'admin', icon: 'fas fa-crown' },
    { type: 'Customer', email: 'customer@safecom.test', role: 'customer', icon: 'fas fa-user-tie' },
    { type: 'Employee', email: 'employee@safecom.test', role: 'employee', icon: 'fas fa-user-hard-hat' },
  ];

  const fillDemoCredentials = (role: string, email: string) => {
    setEmail(email);
    setPassword('Demo@1234');
    setUserType(role);
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await login(email, password);
      // Auth context handles navigation or demo mode
      navigate('/dashboard');
    } catch (err: any) {
      setError(err.message || 'Login failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleDemoMode = (role: string) => {
    enterDemoMode(role);
    navigate('/dashboard');
  };

  return (
    <div className="login-container">
      <header className="login-header">
        <div className="logo-section">
          <img src="/logo.svg" alt="SafeCom" className="logo" />
          <h1>SafeCom</h1>
        </div>
        <nav className="nav-links">
          <a href="/">Home</a>
          <a href="/signup">Sign Up</a>
        </nav>
      </header>

      <main className="login-main">
        <div className="login-form-container">
          <div className="form-header">
            <h2>Welcome Back</h2>
            <p>Log in to your SafeCom account</p>
          </div>

          {error && <div className="error-message">{error}</div>}

          <form onSubmit={handleSubmit} className="login-form">
            <div className="form-group">
              <label htmlFor="userType">Login As</label>
              <select
                id="userType"
                value={userType}
                onChange={(e) => setUserType(e.target.value)}
                required
              >
                <option value="">Select User Type</option>
                <option value="admin">Administrator</option>
                <option value="customer">Customer</option>
                <option value="employee">Employee</option>
              </select>
            </div>

            <div className="form-group">
              <label htmlFor="email">Email</label>
              <input
                type="email"
                id="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="your@email.com"
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="password">Password</label>
              <input
                type="password"
                id="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Enter your password"
                required
              />
            </div>

            <div className="form-options">
              <label className="remember-me">
                <input type="checkbox" />
                Remember me
              </label>
              <a href="#" className="forgot-password">
                Forgot password?
              </a>
            </div>

            <button type="submit" className="btn-primary" disabled={loading}>
              {loading ? 'Logging in...' : 'Log In'}
            </button>
          </form>

          <div className="demo-section">
            <h4>Demo Accounts:</h4>
            <div className="demo-items">
              {demoAccounts.map((account) => (
                <div
                  key={account.role}
                  className="demo-item"
                  onClick={() => fillDemoCredentials(account.role, account.email)}
                  style={{ cursor: 'pointer' }}
                >
                  <i className={account.icon}></i>
                  <div>
                    <strong>{account.type}</strong>
                    <small>{account.email}</small>
                  </div>
                </div>
              ))}
            </div>
            <button
              type="button"
              className="btn-demo"
              onClick={() => handleDemoMode('employee')}
            >
              🎮 Enter Demo Mode (Quick Demo)
            </button>
          </div>

          <div className="auth-footer">
            Don't have an account? <a href="/signup">Sign up</a>
          </div>
        </div>
      </main>
    </div>
  );
};

export default Login;
