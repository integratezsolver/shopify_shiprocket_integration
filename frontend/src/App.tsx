import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import LandingPage from './components/LandingPage';
import LoginPage from './components/LoginPage';
import SignupPage from './components/SignupPage';
import OnboardingPage from './components/OnboardingPage';
import ShopifyEntry from './components/ShopifyEntry';
import DashboardLayout from './components/DashboardLayout';
import Dashboard from './components/Dashboard';
import IntegrationsPage from './components/IntegrationsPage';
import ProductsPage from './components/ProductsPage';
import OrdersPage from './components/OrdersPage';
import ShipmentsPage from './components/ShipmentsPage';
import PlansPage from './components/PlansPage';
import { Toaster } from './components/ui/sonner';

function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { user } = useAuth();
  
  if (!user) {
    return <Navigate to="/" replace />;
  }
  
  return <>{children}</>;
}

function PublicRoute({ children }: { children: React.ReactNode }) {
  const { user, onboardingComplete } = useAuth();
  
  if (user && !onboardingComplete) {
    return <Navigate to="/onboarding" replace />;
  }
  
  if (user && onboardingComplete) {
    return <Navigate to="/dashboard" replace />;
  }
  
  return <>{children}</>;
}

function OnboardingRoute({ children }: { children: React.ReactNode }) {
  const { user, onboardingComplete } = useAuth();
  
  if (!user) {
    return <Navigate to="/" replace />;
  }
  
  if (onboardingComplete) {
    return <Navigate to="/dashboard" replace />;
  }
  
  return <>{children}</>;
}

export default function App() {
  return (
    <Router>
      <AuthProvider>
        <Routes>
          <Route path="/" element={
            <PublicRoute>
              <LandingPage />
            </PublicRoute>
          } />
          <Route path="/login" element={
            <PublicRoute>
              <LoginPage />
            </PublicRoute>
          } />
          <Route path="/signup" element={
            <PublicRoute>
              <SignupPage />
            </PublicRoute>
          } />
          <Route path="/onboarding" element={
            <OnboardingRoute>
              <OnboardingPage />
            </OnboardingRoute>
          } />
          <Route path="/app" element={<ShopifyEntry />} />
          <Route path="/" element={
            <ProtectedRoute>
              <DashboardLayout />
            </ProtectedRoute>
          }>
            <Route path="dashboard" element={<Dashboard />} />
            <Route path="integrations" element={<IntegrationsPage />} />
            <Route path="products" element={<ProductsPage />} />
            <Route path="orders" element={<OrdersPage />} />
            <Route path="shipments" element={<ShipmentsPage />} />
            <Route path="plans" element={<PlansPage />} />
          </Route>
          {/* Catch-all route for unmatched paths */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
        <Toaster />
      </AuthProvider>
    </Router>
  );
}