import React, { createContext, useContext, useState, useEffect } from 'react';
import { authApi } from '../api/authApi';
import { jwtDecode } from 'jwt-decode';

interface User {
  id: string;
  email: string;
  username: string;
}

interface AuthContextType {
  user: User | null;
  onboardingComplete: boolean;
  login: (username: string, password: string) => Promise<void>;
  signup: (email: string, username: string, password: string) => Promise<void>;
  logout: () => void;
  completeOnboarding: () => void;
  setToken: (token: string) => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

interface JwtPayload {
  sub: string;
  email?: string;
  roles?: string[];
  exp?: number;
}

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [onboardingComplete, setOnboardingComplete] = useState(false);

  useEffect(() => {
    // Check for stored token and decode user info
    const token = localStorage.getItem('AUTH_TOKEN');
    const storedOnboarding = localStorage.getItem('onboardingComplete');
    
    if (token) {
      try {
        const decoded = jwtDecode<JwtPayload>(token);
        const userFromToken: User = {
          id: decoded.sub || '1',
          email: decoded.email || decoded.sub,
          username: decoded.sub,
        };
        setUser(userFromToken);
        localStorage.setItem('user', JSON.stringify(userFromToken));
      } catch (error) {
        console.error('Failed to decode token:', error);
        localStorage.removeItem('AUTH_TOKEN');
      }
    }
    
    if (storedOnboarding === 'true') {
      setOnboardingComplete(true);
    }
  }, []);

  const login = async (username: string, password: string) => {
    try {
      const token = await authApi.login(username, password);
      localStorage.setItem('AUTH_TOKEN', token);
      
      // Decode token to get user info
      const decoded = jwtDecode<JwtPayload>(token);
      const userFromToken: User = {
        id: decoded.sub || '1',
        email: decoded.email || decoded.sub,
        username: decoded.sub,
      };
      
      setUser(userFromToken);
      localStorage.setItem('user', JSON.stringify(userFromToken));
      
      // Check if onboarding was completed
      const storedOnboarding = localStorage.getItem('onboardingComplete');
      if (storedOnboarding === 'true') {
        setOnboardingComplete(true);
      }
    } catch (error: any) {
      console.error('Login failed:', error);
      throw new Error(error.response?.data || 'Login failed. Please check your credentials.');
    }
  };

  const signup = async (email: string, username: string, password: string) => {
    try {
      await authApi.register(email, username, password);
      // After successful signup, automatically log in
      await login(username, password);
      setOnboardingComplete(false);
      localStorage.setItem('onboardingComplete', 'false');
    } catch (error: any) {
      console.error('Signup failed:', error);
      throw new Error(error.response?.data || 'Signup failed. Please try again.');
    }
  };

  const setToken = (token: string) => {
    localStorage.setItem('AUTH_TOKEN', token);
    try {
      const decoded = jwtDecode<JwtPayload>(token);
      const userFromToken: User = {
        id: decoded.sub || '1',
        email: decoded.email || decoded.sub,
        username: decoded.sub,
      };
      setUser(userFromToken);
      localStorage.setItem('user', JSON.stringify(userFromToken));
    } catch (error) {
      console.error('Failed to decode token:', error);
    }
  };

  const logout = () => {
    setUser(null);
    setOnboardingComplete(false);
    localStorage.removeItem('AUTH_TOKEN');
    localStorage.removeItem('user');
    localStorage.removeItem('onboardingComplete');
  };

  const completeOnboarding = () => {
    setOnboardingComplete(true);
    localStorage.setItem('onboardingComplete', 'true');
  };

  return (
    <AuthContext.Provider value={{ user, onboardingComplete, login, signup, logout, completeOnboarding, setToken }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
