import { useState } from "react";
import { Link } from "react-router-dom";
import { getBookingsByUser } from "../../api/bookings";
import BookingSummaryCard from "../../components/booking/BookingSummaryCard";
import Alert from "../../components/ui/Alert";
import EmptyState from "../../components/ui/EmptyState";
import { useAuth } from "../../context/useAuth";
import { useAsync } from "../../hooks/useAsync";
import { useDocumentTitle } from "../../hooks/useDocumentTitle";
import { todayISO } from "../../utils/format";
import "./Account.css";

const TABS = [
  { key: "upcoming", label: "Upcoming" },
  { key: "past", label: "Past" },
  { key: "cancelled", label: "Cancelled" },
];

function categorize(booking, today) {
  if (booking.status === "CANCELLED") return "cancelled";
  if (booking.status === "COMPLETED" || booking.checkOutDate <= today) return "past";
  return "upcoming";
}

export default function MyBookingsPage() {
  useDocumentTitle("My bookings");
  const { user } = useAuth();
  const { data, loading, error } = useAsync(() => getBookingsByUser(user.id), [user.id]);
  const [tab, setTab] = useState("upcoming");

  const today = todayISO();
  const bookings = (data || []).filter((booking) => categorize(booking, today) === tab);
  bookings.sort((a, b) =>
    tab === "upcoming" ? a.checkInDate.localeCompare(b.checkInDate) : b.checkInDate.localeCompare(a.checkInDate)
  );
  const counts = Object.fromEntries(
    TABS.map((t) => [t.key, (data || []).filter((b) => categorize(b, today) === t.key).length])
  );

  return (
    <div className="app-page container">
      <header className="app-page-header">
        <div>
          <span className="eyebrow">Your stays</span>
          <h1>My bookings</h1>
          <p>Review your reservations, pay outstanding balances or cancel a stay.</p>
        </div>
        <Link to="/rooms" className="btn btn-primary">
          Book a new stay
        </Link>
      </header>

      <div className="chip-tabs" role="tablist" aria-label="Booking filter">
        {TABS.map((t) => (
          <button
            key={t.key}
            type="button"
            role="tab"
            aria-selected={tab === t.key}
            className={tab === t.key ? "active" : ""}
            onClick={() => setTab(t.key)}
          >
            {t.label}
            {data && <span className="chip-count">{counts[t.key]}</span>}
          </button>
        ))}
      </div>

      {error && <Alert tone="danger">{error.message}</Alert>}

      <div className="booking-list">
        {loading &&
          [0, 1].map((i) => (
            <div key={i} className="booking-card card" aria-hidden="true">
              <div className="skeleton booking-card-image" />
              <div className="booking-card-body stack">
                <div className="skeleton" style={{ height: 20, width: "50%" }} />
                <div className="skeleton" style={{ height: 14, width: "70%" }} />
              </div>
            </div>
          ))}

        {!loading && !error && bookings.length === 0 && (
          <EmptyState
            icon="calendar"
            title={tab === "upcoming" ? "No upcoming stays" : `No ${tab} bookings`}
            action={
              tab === "upcoming" && (
                <Link to="/rooms" className="btn btn-primary">
                  Find a room
                </Link>
              )
            }
          >
            {tab === "upcoming" ? "When you book a room it'll show up here." : "Nothing to show in this list yet."}
          </EmptyState>
        )}

        {bookings.map((booking) => (
          <BookingSummaryCard key={booking.bookingId} booking={booking} />
        ))}
      </div>
    </div>
  );
}
