import { createContext, useEffect, useState } from "react";
import axiosClient from "../api/axiosClient";

export const AuthContext = createContext();

export default function AuthProvider({ children }) {

  const [user, setUser] = useState(null);

  const login = (token) => {
    localStorage.setItem("AUTH_TOKEN", token);
    fetchUser();
  };

  const logout = () => {
    localStorage.removeItem("AUTH_TOKEN");
    setUser(null);
  };

  const fetchUser = async () => {
    try {
      const res = await axiosClient.get("/auth/me");
      setUser(res.data);
    } catch (err) {
      setUser(null);
    }
  };

  useEffect(() => {
    const token = localStorage.getItem("AUTH_TOKEN");
    if (token) fetchUser();
  }, []);

  return (
    <AuthContext.Provider value={{ user, login, logout, fetchUser }}>
      {children}
    </AuthContext.Provider>
  );
}
