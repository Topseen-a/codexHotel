import { useEffect, useRef, useState } from "react";
import { Link, NavLink, useLocation, useNavigate } from "react-router-dom";
import { NAV_LINKS } from "../../config/site";
import { useAuth } from "../../context/useAuth";
import { initials } from "../../utils/format";
import { ROLE_LABELS } from "../../utils/roles";
import Icon from "../ui/Icon";
import Brand from "./Brand";
import "./Navbar.css";

// Pages whose top section is a full-bleed photo the navbar can sit over.
const OVERLAY_PATHS = ["/", "/rooms", "/experiences", "/gallery", "/about", "/contact"];

function isLinkActive(to, location) {
  if (to.includes("#")) return location.pathname === "/" && location.hash === to.slice(1);
  if (to === "/") return location.pathname === "/" && !location.hash;
  return location.pathname.startsWith(to);
}

export default function Navbar() {
  const { user, isStaff, logout } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();
  // Menus record the location they were opened on, so navigating closes them.
  const [menuOpenAt, setMenuOpenAt] = useState(null);
  const [accountOpenAt, setAccountOpenAt] = useState(null);
  const menuOpen = menuOpenAt === location.key;
  const accountOpen = accountOpenAt === location.key;
  const setMenuOpen = (open) => setMenuOpenAt(open ? location.key : null);
  const setAccountOpen = (open) => setAccountOpenAt(open ? location.key : null);
  const [scrolled, setScrolled] = useState(false);
  const accountRef = useRef(null);

  const overlay = OVERLAY_PATHS.includes(location.pathname) && !scrolled && !menuOpen;

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 24);
    onScroll();
    window.addEventListener("scroll", onScroll, { passive: true });
    return () => window.removeEventListener("scroll", onScroll);
  }, []);

  useEffect(() => {
    document.body.classList.toggle("no-scroll", menuOpen);
    return () => document.body.classList.remove("no-scroll");
  }, [menuOpen]);

  useEffect(() => {
    if (!accountOpen) return undefined;
    const onClick = (event) => !accountRef.current?.contains(event.target) && setAccountOpenAt(null);
    const onKey = (event) => event.key === "Escape" && setAccountOpenAt(null);
    document.addEventListener("mousedown", onClick);
    document.addEventListener("keydown", onKey);
    return () => {
      document.removeEventListener("mousedown", onClick);
      document.removeEventListener("keydown", onKey);
    };
  }, [accountOpen]);

  const handleLogout = () => {
    logout();
    navigate("/");
  };

  const accountLinks = user
    ? [
        isStaff && { to: "/admin", label: "Staff dashboard", icon: "grid" },
        { to: "/bookings", label: "My bookings", icon: "calendar" },
        { to: "/account", label: "Account settings", icon: "user" },
      ].filter(Boolean)
    : [];

  return (
    <header
      className={`nav ${overlay ? "nav--overlay" : "nav--solid"} ${menuOpen ? "nav--open" : ""} ${
        location.pathname === "/" ? "nav--home" : ""
      }`}
    >
      <div className="nav-inner">
        <Brand variant="mark" />

        <nav className="nav-links" aria-label="Main">
          {NAV_LINKS.map((link) => (
            <NavLink
              key={link.to}
              to={link.to}
              className={() => (isLinkActive(link.to, location) ? "active" : "")}
              end={link.to === "/"}
            >
              {link.label}
            </NavLink>
          ))}
        </nav>

        <div className="nav-actions">
          {user ? (
            <div className="nav-account" ref={accountRef}>
              <button
                type="button"
                className="nav-account-trigger"
                aria-haspopup="menu"
                aria-expanded={accountOpen}
                onClick={() => setAccountOpen(!accountOpen)}
              >
                <span className="avatar avatar-sm">{initials(user.name)}</span>
                <span className="nav-account-name">{user.name.split(" ")[0]}</span>
                <Icon name="chevronDown" size={14} />
              </button>
              {accountOpen && (
                <div className="nav-menu" role="menu">
                  <div className="nav-menu-head">
                    <strong>{user.name}</strong>
                    <span>{ROLE_LABELS[user.role]}</span>
                  </div>
                  {accountLinks.map((link) => (
                    <Link key={link.to} to={link.to} role="menuitem">
                      <Icon name={link.icon} size={17} />
                      {link.label}
                    </Link>
                  ))}
                  <button type="button" role="menuitem" onClick={handleLogout}>
                    <Icon name="logOut" size={17} />
                    Log out
                  </button>
                </div>
              )}
            </div>
          ) : (
            <Link
              to="/login"
              state={location.pathname === "/" ? undefined : { from: location }}
              className="nav-login"
            >
              Sign in
            </Link>
          )}
          <Link to="/rooms" className="nav-cta">
            Book now
          </Link>
          <button
            type="button"
            className="nav-toggle"
            aria-label={menuOpen ? "Close menu" : "Open menu"}
            aria-expanded={menuOpen}
            aria-controls="mobile-menu"
            onClick={() => setMenuOpen(!menuOpen)}
          >
            <Icon name={menuOpen ? "close" : "menu"} size={22} />
          </button>
        </div>
      </div>

      <div id="mobile-menu" className="mobile-menu" hidden={!menuOpen}>
        <nav className="container mobile-menu-links" aria-label="Mobile">
          {NAV_LINKS.map((link) => (
            <Link key={link.to} to={link.to} className={isLinkActive(link.to, location) ? "active" : ""}>
              {link.label}
              <Icon name="chevronRight" size={18} />
            </Link>
          ))}
        </nav>
        <div className="container mobile-menu-account">
          {user ? (
            <>
              <div className="mobile-menu-user">
                <span className="avatar">{initials(user.name)}</span>
                <div>
                  <strong>{user.name}</strong>
                  <span>{ROLE_LABELS[user.role]}</span>
                </div>
              </div>
              {accountLinks.map((link) => (
                <Link key={link.to} to={link.to} className="mobile-menu-item">
                  <Icon name={link.icon} size={18} />
                  {link.label}
                </Link>
              ))}
              <button type="button" className="mobile-menu-item" onClick={handleLogout}>
                <Icon name="logOut" size={18} />
                Log out
              </button>
            </>
          ) : (
            <div className="mobile-menu-auth">
              <Link to="/login" className="btn btn-outline btn-block">
                Sign in
              </Link>
              <Link to="/register" className="btn btn-primary btn-block">
                Create account
              </Link>
            </div>
          )}
        </div>
      </div>
    </header>
  );
}
