import { Navigate, useLocation } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export function RequireAuth({ children }) {
  const { isAuthenticated, loading } = useAuth();
  const location = useLocation();

  if (loading) return null;
  if (!isAuthenticated) return <Navigate to="/login" state={{ from: location }} replace />;
  return children;
}

export function RequireStaff({ children }) {
  const { isStaff, loading } = useAuth();
  const location = useLocation();

  if (loading) return null;
  if (!isStaff) return <Navigate to="/login" state={{ from: location }} replace />;
  return children;
}

export function RequireAdmin({ children }) {
  const { isAdmin, loading } = useAuth();
  const location = useLocation();

  if (loading) return null;
  if (!isAdmin) return <Navigate to="/admin" state={{ from: location }} replace />;
  return children;
}
