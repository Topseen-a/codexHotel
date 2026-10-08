import { Navigate, useLocation } from "react-router-dom";
import { useAuth } from "../../context/useAuth";
import PageLoader from "./PageLoader";

/** Requires a signed-in user; otherwise sends them to log in and back again. */
export function RequireAuth({ children }) {
  const { isAuthenticated, loading } = useAuth();
  const location = useLocation();

  if (loading) return <PageLoader />;
  if (!isAuthenticated) return <Navigate to="/login" state={{ from: location }} replace />;
  return children;
}

/** Requires a signed-in user holding `permission` (see utils/roles). */
export function RequirePermission({ permission, children, fallback = "/" }) {
  const { isAuthenticated, loading, can } = useAuth();
  const location = useLocation();

  if (loading) return <PageLoader />;
  if (!isAuthenticated) return <Navigate to="/login" state={{ from: location }} replace />;
  if (!can(permission)) return <Navigate to={fallback} replace />;
  return children;
}

/** Keeps signed-in users away from the login/register screens. */
export function GuestOnly({ children }) {
  const { isAuthenticated, isStaff, loading } = useAuth();
  const location = useLocation();

  if (loading) return <PageLoader />;
  if (isAuthenticated) {
    // Keep the query string too (e.g. a room page's chosen dates).
    const from = location.state?.from;
    const destination = from ? `${from.pathname}${from.search || ""}` : isStaff ? "/admin" : "/";
    return <Navigate to={destination} replace />;
  }
  return children;
}
