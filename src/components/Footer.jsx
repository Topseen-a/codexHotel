import { Link } from "react-router-dom";
import "./Footer.css";

export default function Footer() {
  return (
    <footer className="site-footer">
      <div className="container site-footer-inner">
        <div className="site-footer-brand">
          <span className="nav-brand">
            codex<span>Hotel</span>
          </span>
          <p className="muted">A quiet place to put your bag down — book direct, no surprises.</p>
        </div>

        <div className="site-footer-links">
          <Link to="/rooms">Rooms</Link>
          <Link to="/login">Log in</Link>
          <Link to="/register">Create account</Link>
        </div>

        <p className="site-footer-copy">© {new Date().getFullYear()} codexHotel. All rights reserved.</p>
      </div>
    </footer>
  );
}
