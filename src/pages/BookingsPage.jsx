import { useEffect, useState } from "react";
import { getBookingsByUser, cancelBooking } from "../api/bookings";
import { makePayment } from "../api/payments";
import { contentFor } from "../content/roomContent";
import { useAuth } from "../context/AuthContext";
import "./BookingsPage.css";

const nairaFormatter = new Intl.NumberFormat("en-NG", {
  style: "currency",
  currency: "NGN",
  maximumFractionDigits: 0,
});

const STATUS_BADGE = {
  CONFIRMED: "badge-success",
  CANCELLED: "badge-danger",
  COMPLETED: "badge-neutral",
};

export default function BookingsPage() {
  const { user } = useAuth();
  const [bookings, setBookings] = useState([]);
  const [status, setStatus] = useState("loading");
  const [error, setError] = useState("");
  const [payingId, setPayingId] = useState(null);
  const [payForm, setPayForm] = useState({ amount: "", paymentMethod: "CARD" });
  const [actionError, setActionError] = useState("");
  const [actionMessage, setActionMessage] = useState("");

  const load = () => {
    setStatus("loading");
    getBookingsByUser(user.id)
      .then((data) => {
        setBookings(data);
        setStatus("ready");
      })
      .catch((err) => {
        setError(err.message);
        setStatus("error");
      });
  };

  useEffect(load, [user.id]);

  const handleCancel = async (bookingId) => {
    setActionError("");
    setActionMessage("");
    try {
      await cancelBooking(bookingId);
      setActionMessage("Booking cancelled.");
      load();
    } catch (err) {
      setActionError(err.message);
    }
  };

  const openPayForm = (booking) => {
    setPayingId(booking.bookingId);
    setPayForm({ amount: booking.totalPrice, paymentMethod: "CARD" });
    setActionError("");
    setActionMessage("");
  };

  const submitPayment = async (bookingId) => {
    setActionError("");
    setActionMessage("");
    try {
      await makePayment({
        bookingId,
        amount: Number(payForm.amount),
        paymentMethod: payForm.paymentMethod,
      });
      setActionMessage("Payment recorded.");
      setPayingId(null);
      load();
    } catch (err) {
      setActionError(err.message);
    }
  };

  return (
    <div className="bookings-page container">
      <h1>My bookings</h1>

      {actionMessage && <div className="alert alert-success">{actionMessage}</div>}
      {actionError && <div className="alert alert-danger">{actionError}</div>}

      {status === "loading" && (
        <div className="booking-list" aria-busy="true" aria-label="Loading your bookings">
          {[0, 1].map((i) => (
            <div key={i} className="card booking-row">
              <div className="booking-row-main">
                <div className="skeleton" style={{ height: 18, width: 90, borderRadius: 999 }} />
                <div className="skeleton" style={{ height: 20, width: "50%", marginTop: 10 }} />
                <div className="skeleton" style={{ height: 14, width: "70%", marginTop: 8 }} />
              </div>
              <div className="skeleton" style={{ height: 22, width: 100 }} />
            </div>
          ))}
        </div>
      )}
      {status === "error" && <div className="alert alert-danger">{error}</div>}

      {status === "ready" && bookings.length === 0 && (
        <p className="muted">You haven't booked a room yet — head over to Rooms to find one.</p>
      )}

      <div className="booking-list">
        {bookings.map((booking) => {
          const content = contentFor(booking.roomType);
          return (
            <div key={booking.bookingId} className="card booking-row">
              <div className="booking-row-main">
                <span className={`badge ${STATUS_BADGE[booking.status] || "badge-neutral"}`}>{booking.status}</span>
                <h3 style={{ marginTop: 8 }}>
                  {content.label} · Room {booking.roomNumber || "—"}
                </h3>
                <div className="booking-row-meta">
                  <span>Check-in: {booking.checkInDate}</span>
                  <span>Check-out: {booking.checkOutDate}</span>
                </div>

                {payingId === booking.bookingId && (
                  <div className="pay-form">
                    <input
                      type="number"
                      min="0"
                      value={payForm.amount}
                      onChange={(e) => setPayForm({ ...payForm, amount: e.target.value })}
                    />
                    <select
                      value={payForm.paymentMethod}
                      onChange={(e) => setPayForm({ ...payForm, paymentMethod: e.target.value })}
                    >
                      <option value="CARD">Card</option>
                      <option value="CASH">Cash</option>
                      <option value="TRANSFER">Transfer</option>
                    </select>
                    <button className="btn btn-primary btn-sm" onClick={() => submitPayment(booking.bookingId)}>
                      Confirm payment
                    </button>
                    <button className="btn btn-ghost btn-sm" onClick={() => setPayingId(null)}>
                      Cancel
                    </button>
                  </div>
                )}
              </div>

              <div className="booking-row-actions">
                <span className="booking-price">{nairaFormatter.format(booking.totalPrice)}</span>
                {booking.status === "CONFIRMED" && payingId !== booking.bookingId && (
                  <>
                    <button className="btn btn-primary btn-sm" onClick={() => openPayForm(booking)}>
                      Pay
                    </button>
                    <button className="btn btn-danger btn-sm" onClick={() => handleCancel(booking.bookingId)}>
                      Cancel booking
                    </button>
                  </>
                )}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
