import { useAuth } from "../auth/useAuth";
import { Navigate } from "react-router-dom";

export default function Dashboard() {

  const { user } = useAuth();

  if (!user) return <Navigate to="/login" />;

  return (
    <div>
      <h1>Welcome, {user.name} 👋</h1>
      <p>Your store is connected successfully!</p>
    </div>
  );
}
