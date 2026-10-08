import { Navigate } from "react-router-dom";
import { useAuth } from "../../context/useAuth";

/** Lands managers on the reports overview and front-desk staff on bookings. */
export default function AdminIndex() {
  const { can } = useAuth();
  return <Navigate to={can("viewReports") ? "/admin/overview" : "/admin/bookings"} replace />;
}
