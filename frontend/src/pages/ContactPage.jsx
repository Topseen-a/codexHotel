import { Link } from "react-router-dom";
import Icon from "../components/ui/Icon";
import { SITE } from "../config/site";
import { backgroundFor, IMAGES } from "../content/images";
import { useDocumentTitle } from "../hooks/useDocumentTitle";
import "./InfoPages.css";

export default function ContactPage() {
  useDocumentTitle("Contact");

  const channels = [
    { icon: "phone", label: "Call reservations", value: SITE.phone, href: `tel:${SITE.phone.replace(/\s/g, "")}` },
    { icon: "mail", label: "Email us", value: SITE.email, href: `mailto:${SITE.email}` },
    { icon: "mapPin", label: "Visit", value: SITE.address },
    { icon: "clock", label: "Front desk", value: "Open 24 hours, every day" },
  ];

  return (
    <>
      <section className="page-hero" style={{ backgroundImage: `url(${backgroundFor(IMAGES.contact)})` }}>
        <div className="container">
          <span className="eyebrow">Contact</span>
          <h1>We&apos;re Here to Help</h1>
          <p>Questions about a booking, special requests or group stays — reach the team any time.</p>
        </div>
      </section>

      <section className="container section contact">
        <div className="info-grid">
          {channels.map((channel) => {
            const body = (
              <>
                <span className="info-tile-icon">
                  <Icon name={channel.icon} size={22} />
                </span>
                <h3>{channel.label}</h3>
                <p className="muted">{channel.value}</p>
              </>
            );
            return channel.href ? (
              <a key={channel.label} href={channel.href} className="info-tile card info-tile-link">
                {body}
              </a>
            ) : (
              <div key={channel.label} className="info-tile card">
                {body}
              </div>
            );
          })}
        </div>

        <div className="contact-help card card-pad">
          <div>
            <h2>Already booked?</h2>
            <p className="muted">
              View your reservation, pay an outstanding balance or cancel your stay online — no call needed.
            </p>
          </div>
          <Link to="/bookings" className="btn btn-primary">
            Manage my bookings <Icon name="arrowRight" size={16} />
          </Link>
        </div>
      </section>
    </>
  );
}
