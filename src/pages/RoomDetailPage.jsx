import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { listRooms } from "../api/rooms";
import { createBooking } from "../api/bookings";
import { contentFor } from "../content/roomContent";
import { useAuth } from "../context/AuthContext";
import "./RoomDetailPage.css";

const nairaFormatter = new Intl.NumberFormat("en-NG", {
  style: "currency",
  currency: "NGN",
  maximumFractionDigits: 0,
});

function nightsBetween(checkIn, checkOut) {
  if (!checkIn || !checkOut) return 0;
  const ms = new Date(checkOut) - new Date(checkIn);
  return Math.max(0, Math.round(ms / (1000 * 60 * 60 * 24)));
}

export default function RoomDetailPage() {
  const { roomType } = useParams();
  const { user, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const content = contentFor(roomType);

  const [rooms, setRooms] = useState([]);
  const [activeImage, setActiveImage] = useState(0);
  const [checkInDate, setCheckInDate] = useState("");
  const [checkOutDate, setCheckOutDate] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");
  const [confirmation, setConfirmation] = useState(null);

  useEffect(() => {
    listRooms().then(setRooms).catch(() => setRooms([]));
  }, []);

  const matchingRooms = rooms.filter((r) => r.type === roomType);
  const availableCount = matchingRooms.filter((r) => r.status === "AVAILABLE").length;
  const basePrice = matchingRooms.length
    ? Math.min(...matchingRooms.map((r) => r.basePrice))
    : 0;
  const nights = nightsBetween(checkInDate, checkOutDate);
  const estimatedTotal = nights * basePrice;

  const handleBook = async (e) => {
    e.preventDefault();
    setError("");
    setConfirmation(null);

    if (!isAuthenticated) {
      navigate("/login", { state: { from: { pathname: `/rooms/${roomType}` } } });
      return;
    }

    if (nights <= 0) {
      setError("Check-out must be after check-in.");
      return;
    }

    setSubmitting(true);
    try {
      const booking = await createBooking({
        userId: user.id,
        roomType,
        checkInDate,
        checkOutDate,
      });
      setConfirmation(booking);
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  };

  const today = new Date().toISOString().split("T")[0];

  return (
    <div className="detail-page container">
      <Link to="/rooms" className="detail-back">
        ← Back to all rooms
      </Link>

      <div className="detail-header">
        <h1>{content.label}</h1>
        <p className="muted">{content.tagline}</p>
      </div>

      <div className="detail-gallery">
        <div className="detail-gallery-main">
          <img src={content.images[activeImage]} alt={content.label} />
        </div>
        <div className="detail-gallery-side">
          {content.images.slice(1, 4).map((src, i) => (
            <div key={src} role="button" tabIndex={0} onClick={() => setActiveImage(i + 1)}>
              <img src={src} alt={`${content.label} view ${i + 2}`} />
            </div>
          ))}
        </div>
      </div>

      <div className="detail-layout">
        <div>
          <div className="detail-section">
            <h2>About this room</h2>
            <p>{content.description}</p>
          </div>

          <div className="detail-section">
            <h2>Popular amenities</h2>
            <ul className="detail-amenities">
              {content.amenities.map((a) => (
                <li key={a}>{a}</li>
              ))}
            </ul>
          </div>

          <div className="detail-section">
            <h2>Availability</h2>
            <p className="muted">
              {matchingRooms.length === 0
                ? "No rooms of this type are configured yet."
                : availableCount > 0
                ? `${availableCount} of ${matchingRooms.length} rooms currently available.`
                : "Fully booked right now — try different dates."}
            </p>
          </div>
        </div>

        <div className="card booking-panel">
          <div className="price">
            {nairaFormatter.format(basePrice)}
            <small> / night, before seasonal pricing</small>
          </div>

          {confirmation ? (
            <div className="alert alert-success">
              Booking confirmed — total {nairaFormatter.format(confirmation.totalPrice)} for room{" "}
              {confirmation.roomNumber}.{" "}
              <Link to="/bookings">View your bookings</Link>.
            </div>
          ) : (
            <form onSubmit={handleBook}>
              {error && <div className="alert alert-danger">{error}</div>}

              <div className="booking-dates">
                <div className="field">
                  <label htmlFor="checkin">Check-in</label>
                  <input
                    id="checkin"
                    type="date"
                    required
                    min={today}
                    value={checkInDate}
                    onChange={(e) => setCheckInDate(e.target.value)}
                  />
                </div>
                <div className="field">
                  <label htmlFor="checkout">Check-out</label>
                  <input
                    id="checkout"
                    type="date"
                    required
                    min={checkInDate || today}
                    value={checkOutDate}
                    onChange={(e) => setCheckOutDate(e.target.value)}
                  />
                </div>
              </div>

              {nights > 0 && (
                <div className="booking-summary">
                  <div className="spread">
                    <span>
                      {nairaFormatter.format(basePrice)} × {nights} night{nights > 1 ? "s" : ""}
                    </span>
                    <span>{nairaFormatter.format(estimatedTotal)}</span>
                  </div>
                  <div className="spread muted" style={{ fontSize: "0.8rem" }}>
                    <span>Weekend / festive dates are priced higher automatically</span>
                  </div>
                  <div className="spread total">
                    <span>Estimated total</span>
                    <span>{nairaFormatter.format(estimatedTotal)}</span>
                  </div>
                </div>
              )}

              <button type="submit" className="btn btn-primary btn-block" disabled={submitting}>
                {submitting ? "Booking…" : isAuthenticated ? "Book this room" : "Log in to book"}
              </button>
            </form>
          )}
        </div>
      </div>
    </div>
  );
}
