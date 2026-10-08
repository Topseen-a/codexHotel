import { useState } from "react";
import { Link, useLocation, useNavigate, useSearchParams } from "react-router-dom";
import { createBooking } from "../../api/bookings";
import { useAuth } from "../../context/useAuth";
import { groupNightsByPrice, useStayQuote } from "../../hooks/useStayQuote";
import { addDaysISO, formatDate, formatNaira, nightsBetween, pluralize, todayISO } from "../../utils/format";
import Alert from "../ui/Alert";
import DatePicker from "../ui/DatePicker";
import Dropdown from "../ui/Dropdown";
import Icon from "../ui/Icon";
import "./BookingPanel.css";

export default function BookingPanel({ roomType, room, fromPrice, bookable }) {
  const { user, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [searchParams, setSearchParams] = useSearchParams();
  const today = todayISO();

  const checkIn = searchParams.get("checkIn") || "";
  const checkOut = searchParams.get("checkOut") || "";
  const guests = Number(searchParams.get("guests")) || Math.min(2, room.guests);

  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");
  const [booking, setBooking] = useState(null);

  const nights = nightsBetween(checkIn, checkOut);
  const quote = useStayQuote({ roomType, basePrice: fromPrice, checkIn, checkOut });
  const tooManyGuests = guests > room.guests;

  const updateParam = (key, value) => {
    const next = new URLSearchParams(searchParams);
    if (value) next.set(key, value);
    else next.delete(key);
    if (key === "checkIn" && value && (!checkOut || checkOut <= value)) next.set("checkOut", addDaysISO(value, 1));
    setSearchParams(next, { replace: true });
    setError("");
  };

  const submit = async (event) => {
    event.preventDefault();
    setError("");

    if (!isAuthenticated) {
      navigate("/login", { state: { from: location } });
      return;
    }
    if (nights <= 0) {
      setError("Choose a check-out date after your check-in date.");
      return;
    }
    if (tooManyGuests) {
      setError(`The ${room.name} sleeps up to ${room.guests} guests.`);
      return;
    }

    setSubmitting(true);
    try {
      setBooking(await createBooking({ userId: user.id, roomType, checkInDate: checkIn, checkOutDate: checkOut }));
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  };

  if (booking) {
    return (
      <aside className="booking-panel card">
        <div className="booking-success">
          <span className="booking-success-icon">
            <Icon name="check" size={26} />
          </span>
          <h3>Booking confirmed</h3>
          <p className="muted">
            Room {booking.roomNumber} · {formatDate(booking.checkInDate)} – {formatDate(booking.checkOutDate)}
          </p>
          <div className="booking-success-total">
            <span>Total</span>
            <strong>{formatNaira(booking.totalPrice)}</strong>
          </div>
          <Link to={`/bookings/${booking.bookingId}`} className="btn btn-primary btn-block">
            View booking &amp; pay
          </Link>
          <Link to="/bookings" className="btn btn-ghost btn-block">
            All my bookings
          </Link>
        </div>
      </aside>
    );
  }

  return (
    <aside className="booking-panel card">
      <div className="booking-panel-price">
        {fromPrice != null ? (
          <>
            <span className="faint">From</span>
            <strong>{formatNaira(fromPrice)}</strong>
            <span className="faint">/ night</span>
          </>
        ) : (
          <span className="muted">Rates unavailable right now</span>
        )}
      </div>

      <form className="stack" onSubmit={submit}>
        <div className="booking-panel-dates">
          <div className="field">
            <label htmlFor="checkIn">Check in</label>
            <DatePicker
              id="checkIn"
              label="Check in"
              min={today}
              value={checkIn}
              placeholder="Add date"
              onChange={(value) => updateParam("checkIn", value)}
            />
          </div>
          <div className="field">
            <label htmlFor="checkOut">Check out</label>
            <DatePicker
              id="checkOut"
              label="Check out"
              min={checkIn ? addDaysISO(checkIn, 1) : addDaysISO(today, 1)}
              rangeStart={checkIn}
              value={checkOut}
              placeholder="Add date"
              onChange={(value) => updateParam("checkOut", value)}
            />
          </div>
        </div>

        <div className="field">
          <label htmlFor="guests">Guests</label>
          <Dropdown
            id="guests"
            variant="field"
            label="Guests"
            value={guests}
            options={Array.from({ length: room.guests }, (_, i) => ({ value: i + 1, label: pluralize(i + 1, "guest") }))}
            onChange={(value) => updateParam("guests", String(value))}
          />
          <span className="field-hint">Sleeps up to {room.guests}</span>
        </div>

        {nights > 0 && (
          <div className="booking-quote" aria-live="polite">
            {quote.status === "loading" && <div className="skeleton" style={{ height: 44 }} />}
            {quote.status === "ready" &&
              groupNightsByPrice(quote.nights).map((group) => (
                <div key={group.price} className="spread">
                  <span>
                    {formatNaira(group.price)} × {pluralize(group.count, "night")}
                  </span>
                  <span>{formatNaira(group.price * group.count)}</span>
                </div>
              ))}
            {quote.status === "too-long" && (
              <p className="faint">Long stay — seasonal pricing is applied to each night when you book.</p>
            )}
            {quote.status === "error" && <p className="faint">Couldn't load a live quote — pricing is applied when you book.</p>}
            {(quote.status === "ready" || quote.status === "too-long") && (
              <div className="spread booking-quote-total">
                <span>Estimated total</span>
                <strong>{formatNaira(quote.total)}</strong>
              </div>
            )}
            <p className="booking-quote-note">
              Weekend and December nights are priced at seasonal rates. Your total is confirmed on booking.
            </p>
          </div>
        )}

        <Alert tone="danger">{error}</Alert>
        {!bookable && <Alert tone="warning">No {room.name.toLowerCase()}s are open for booking right now.</Alert>}

        <button type="submit" className="btn btn-primary btn-block" disabled={submitting || !bookable}>
          {submitting ? "Booking…" : isAuthenticated ? "Reserve now" : "Log in to book"}
        </button>
        <p className="booking-panel-foot">
          <Icon name="shield" size={15} /> Free cancellation online until your stay begins
        </p>
      </form>
    </aside>
  );
}
