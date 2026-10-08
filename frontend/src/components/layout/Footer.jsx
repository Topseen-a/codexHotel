import { Link } from "react-router-dom";
import { NAV_LINKS, SITE } from "../../config/site";
import Icon from "../ui/Icon";
import Brand from "./Brand";
import "./Footer.css";

export default function Footer() {
  return (
    <footer className="footer">
      <div className="container footer-top">
        <div className="footer-brand">
          <Brand className="brand--light" />
          <p>Seaside rooms and suites on the {SITE.location}. Book direct for the best rate.</p>
          <div className="footer-socials">
            {SITE.socials.map((social) => (
              <a key={social.label} href={social.href} target="_blank" rel="noreferrer" aria-label={social.label}>
                <Icon name={social.icon} size={18} />
              </a>
            ))}
          </div>
        </div>

        <div className="footer-col">
          <h4>Explore</h4>
          {NAV_LINKS.map((link) => (
            <Link key={link.to} to={link.to}>
              {link.label}
            </Link>
          ))}
        </div>

        <div className="footer-col">
          <h4>Your stay</h4>
          <Link to="/rooms">Book a room</Link>
          <Link to="/bookings">My bookings</Link>
          <Link to="/account">Account</Link>
          <span>
            Check-in from {SITE.checkInTime} · Check-out by {SITE.checkOutTime}
          </span>
        </div>

        <div className="footer-col">
          <h4>Contact</h4>
          <a href={`tel:${SITE.phone.replace(/\s/g, "")}`}>
            <Icon name="phone" size={16} /> {SITE.phone}
          </a>
          <a href={`mailto:${SITE.email}`}>
            <Icon name="mail" size={16} /> {SITE.email}
          </a>
          <span>
            <Icon name="mapPin" size={16} /> {SITE.address}
          </span>
        </div>
      </div>

      <div className="container footer-bottom">
        <p>
          © {new Date().getFullYear()} {SITE.name} {SITE.subtitle}. All rights reserved.
        </p>
      </div>
    </footer>
  );
}
