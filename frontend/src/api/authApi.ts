import axiosClient from './axiosClient';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  username: string;
  password: string;
  role?: string;
}

export interface AuthResponse {
  token: string;
  username?: string;
  email?: string;
}

export const authApi = {
  login: async (username: string, password: string): Promise<string> => {
    const response = await axiosClient.post<string | AuthResponse>('/auth/login', {
      username,
      password,
    });
    
    // Handle both string token and object response
    const data = response.data;
    if (typeof data === 'string') {
      return data;
    }
    return data.token;
  },

  register: async (email: string, username: string, password: string, role?: string): Promise<string> => {
    const requestBody: any = {
      email,
      username,
      password,
    };
    // Only include role if explicitly provided, otherwise backend will default to "USER"
    if (role) {
      requestBody.role = role;
    }
    const response = await axiosClient.post<string>('/auth/register', requestBody);
    return response.data;
  },

  forgotPassword: async (email: string): Promise<string> => {
    const response = await axiosClient.post<string>('/auth/forgot-password', null, {
      params: { email },
    });
    return response.data;
  },

  verifyOtp: async (email: string, otp: string): Promise<string> => {
    const response = await axiosClient.post<string>('/auth/verify-otp', {
      email,
      otp,
    });
    return response.data;
  },

  resetPassword: async (email: string, otp: string, newPassword: string): Promise<string> => {
    const response = await axiosClient.post<string>('/auth/reset-password', {
      email,
      otp,
      newPassword,
    });
    return response.data;
  },
};

