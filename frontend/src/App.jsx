import { useState } from 'react'
import reactLogo from './assets/react.svg'


import viteLogo from '/vite.svg'

import './App.css'
import { BrowserRouter, Routes, Route } from "react-router-dom";
import AuthProvider from "./auth/AuthProvider.jsx";;

import Login from "./pages/Login.jsx";
import Dashboard from "./pages/Dashboard.jsx";
import ShopifyEntry from "./pages/ShopifyEntry.jsx";

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/app" element={<ShopifyEntry />} />
          <Route path="/dashboard" element={<Dashboard />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}
