/**
 * AuthContext.tsx - Firebase Authentication Context
 * Manages user authentication state and operations
 */

import React, { createContext, useContext, useEffect, useState } from 'react';
import {
  auth,
  signInWithEmail,
  signUpWithEmail,
  signOut,
  onAuthStateChanged,
} from '../services/firebase';

interface AuthContextType {
  user: any | null;
  userRole: string | null;
  isDemoMode: boolean;
  loading: boolean;
  login: (email: string, password: string) => Promise<void>;
  signup: (email: string, password: string, name: string) => Promise<void>;
  logout: () => Promise<void>;
  enterDemoMode: (role: string) => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<any | null>(null);
  const [userRole, setUserRole] = useState<string | null>(null);
  const [isDemoMode, setIsDemoMode] = useState(false);
  const [loading, setLoading] = useState(true);

  // Listen to Firebase auth state changes
  useEffect(() => {
    const unsubscribe = onAuthStateChanged(auth, async (currentUser) => {
      if (currentUser) {
        setUser(currentUser);
        
        // Get user role from custom claims
        const idTokenResult = await currentUser.getIdTokenResult();
        setUserRole(idTokenResult.claims.role || 'employee');
        
        // Store in localStorage for demo mode
        localStorage.setItem('safecom-token', await currentUser.getIdToken());
        localStorage.setItem('safecom-user', JSON.stringify({
          id: currentUser.uid,
          email: currentUser.email,
          name: currentUser.displayName,
          role: idTokenResult.claims.role || 'employee'
        }));
      } else {
        setUser(null);
        setUserRole(null);
        localStorage.removeItem('safecom-token');
        localStorage.removeItem('safecom-user');
      }
      setLoading(false);
    });

    return () => unsubscribe();
  }, []);

  const login = async (email: string, password: string) => {
    try {
      // Check if demo credentials
      const demoCreds: any = {
        'admin@safecom.test': { password: 'Demo@1234', role: 'admin' },
        'customer@safecom.test': { password: 'Demo@1234', role: 'customer' },
        'employee@safecom.test': { password: 'Demo@1234', role: 'employee' },
        'manager@safecom.test': { password: 'Demo@1234', role: 'customer' }
      };

      if (demoCreds[email] && demoCreds[email].password === password) {
        // Enter demo mode
        enterDemoMode(demoCreds[email].role);
        return;
      }

      // Sign in with Firebase
      await signInWithEmail(email, password);
    } catch (error: any) {
      // Fallback to demo mode if backend unavailable
      console.warn('Backend unavailable, entering demo mode...');
      enterDemoMode('employee');
    }
  };

  const signup = async (email: string, password: string, name: string) => {
    await signUpWithEmail(email, password, name);
  };

  const logout = async () => {
    await signOut();
    setIsDemoMode(false);
  };

  const enterDemoMode = (role: string) => {
    const demoUser = {
      id: `demo-user-${Date.now()}`,
      email: `${role}@demo.test`,
      name: `${role.charAt(0).toUpperCase() + role.slice(1)} Demo User`,
      role: role,
      isDemoMode: true
    };

    setUser(demoUser);
    setUserRole(role);
    setIsDemoMode(true);

    localStorage.setItem('safecom-token', `demo-token-${Date.now()}`);
    localStorage.setItem('safecom-user', JSON.stringify(demoUser));
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        userRole,
        isDemoMode,
        loading,
        login,
        signup,
        logout,
        enterDemoMode,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
};
