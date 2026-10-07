import { useEffect, useState } from "react";
import { NavLink, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import "./Navbar.css";

export default function Navbar() {
  const { user, isStaff, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [menuOpen, setMenuOpen] = useState(false);

  useEffect(() => {
    setMenuOpen(false);
  }, [location.pathname]);

  useEffect(() => {
    document.body.classList.toggle("nav-menu-open", menuOpen);
    return () => document.body.classList.remove("nav-menu-open");
  }, [menuOpen]);

  const handleLogout = () => {
    logout();
    setMenuOpen(false);
    navigate("/");
  };

  const firstName = user?.name ? user.name.split(" ")[0] : "Account";

  return (
    <header className="nav">
      <div className="container nav-inner">
        <NavLink to="/" className="nav-brand">
          codex<span>Hotel</span>
        </NavLink>

        <button
          type="button"
          className="nav-toggle"
          aria-label={menuOpen ? "Close menu" : "Open menu"}
          aria-expanded={menuOpen}
          onClick={() => setMenuOpen((open) => !open)}
        >
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round">
            {menuOpen ? <path d="M6 6l12 12M18 6L6 18" /> : <path d="M4 7h16M4 12h16M4 17h16" />}
          </svg>
        </button>

        <nav className={`nav-links ${menuOpen ? "is-open" : ""}`}>
          <NavLink to="/rooms" className={({ isActive }) => (isActive ? "active" : "")}>
            Rooms
          </NavLink>
          {user && (
            <NavLink to="/bookings" className={({ isActive }) => (isActive ? "active" : "")}>
              My Bookings
            </NavLink>
          )}
          {isStaff && (
            <NavLink to="/admin" className={({ isActive }) => (isActive ? "active" : "")}>
              Staff Dashboard
            </NavLink>
          )}

          <div className="nav-account nav-account-mobile">
            {user ? (
              <>
                <span className="nav-user">
                  {firstName}
                  <span className="nav-role">{user.role}</span>
                </span>
                <button className="btn btn-ghost btn-sm" onClick={handleLogout}>
                  Log out
                </button>
              </>
            ) : (
              <>
                <NavLink to="/login" className="btn btn-ghost btn-sm">
                  Log in
                </NavLink>
                <NavLink to="/register" className="btn btn-primary btn-sm">
                  Sign up
                </NavLink>
              </>
            )}
          </div>
        </nav>

        <div className="nav-account nav-account-desktop">
          {user ? (
            <>
              <span className="nav-user">
                {firstName}
                <span className="nav-role">{user.role}</span>
              </span>
              <button className="btn btn-ghost btn-sm" onClick={handleLogout}>
                Log out
              </button>
            </>
          ) : (
            <>
              <NavLink to="/login" className="btn btn-ghost btn-sm">
                Log in
              </NavLink>
              <NavLink to="/register" className="btn btn-primary btn-sm">
                Sign up
              </NavLink>
            </>
          )}
        </div>
      </div>
    </header>
  );
}
