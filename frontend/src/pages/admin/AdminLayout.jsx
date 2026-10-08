import { NavLink, Outlet } from "react-router-dom";
import Icon from "../../components/ui/Icon";
import { useAuth } from "../../context/useAuth";
import { ROLE_LABELS } from "../../utils/roles";
import "./Admin.css";

const ADMIN_SECTIONS = [
  { to: "/admin/overview", label: "Overview", icon: "chart", permission: "viewReports" },
  { to: "/admin/bookings", label: "Bookings", icon: "calendar", permission: "manageBookings" },
  { to: "/admin/rooms", label: "Rooms", icon: "bed", permission: "updateRoomStatus" },
  { to: "/admin/payments", label: "Payments", icon: "creditCard", permission: "managePayments" },
  { to: "/admin/guests", label: "Guests", icon: "users", permission: "lookupGuests" },
  { to: "/admin/users", label: "Staff & Users", icon: "key", permission: "manageUsers" },
  { to: "/admin/pricing", label: "Pricing", icon: "tag", permission: "viewDashboard" },
];

export default function AdminLayout() {
  const { user, can } = useAuth();
  const sections = ADMIN_SECTIONS.filter((section) => can(section.permission));

  return (
    <div className="admin">
      <aside className="admin-sidebar">
        <div className="admin-sidebar-head">
          <span className="eyebrow">Staff dashboard</span>
          <strong>{user.name}</strong>
          <span className="badge badge-info">{ROLE_LABELS[user.role]}</span>
        </div>
        <nav className="admin-nav" aria-label="Dashboard">
          {sections.map((section) => (
            <NavLink key={section.to} to={section.to} className={({ isActive }) => (isActive ? "active" : "")}>
              <Icon name={section.icon} size={18} />
              <span>{section.label}</span>
            </NavLink>
          ))}
        </nav>
      </aside>
      <div className="admin-main">
        <Outlet />
      </div>
    </div>
  );
}
