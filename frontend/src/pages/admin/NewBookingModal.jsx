import { useState } from "react";
import { Link } from "react-router-dom";
import { createBooking } from "../../api/bookings";
import { getUserByEmail } from "../../api/users";
import Alert from "../../components/ui/Alert";
import DatePicker from "../../components/ui/DatePicker";
import Dropdown from "../../components/ui/Dropdown";
import Modal from "../../components/ui/Modal";
import { ROOM_TYPES, roomContent } from "../../content/rooms";
import { addDaysISO, formatNaira, todayISO } from "../../utils/format";

/** Front-desk booking on behalf of a guest: find the guest by email, then reserve a room type. */
export default function NewBookingModal({ open, onClose, onCreated, initialGuest = null }) {
  const today = todayISO();
  const [email, setEmail] = useState("");
  const [guest, setGuest] = useState(initialGuest);
  const [form, setForm] = useState({ roomType: "STANDARD", checkInDate: today, checkOutDate: addDaysISO(today, 1) });
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);
  const [created, setCreated] = useState(null);

  const close = () => {
    setEmail("");
    setGuest(initialGuest);
    setError("");
    setCreated(null);
    onClose();
  };

  const findGuest = async () => {
    setError("");
    if (!email.trim()) return;
    setBusy(true);
    try {
      setGuest(await getUserByEmail(email.trim()));
    } catch (err) {
      setError(err.status === 404 ? "No account uses that email. Ask the guest to register first." : err.message);
    } finally {
      setBusy(false);
    }
  };

  const submit = async (event) => {
    event.preventDefault();
    setError("");
    if (!guest) {
      await findGuest();
      return;
    }
    if (form.checkOutDate <= form.checkInDate) {
      setError("Check-out must be after check-in.");
      return;
    }
    setBusy(true);
    try {
      const booking = await createBooking({ userId: guest.id, ...form });
      setCreated(booking);
      onCreated?.(booking);
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(false);
    }
  };

  return (
    <Modal
      open={open}
      title={created ? "Booking created" : "New booking"}
      onClose={close}
      footer={
        created ? (
          <>
            <button type="button" className="btn btn-outline" onClick={close}>
              Done
            </button>
            <Link to={`/bookings/${created.bookingId}`} className="btn btn-primary">
              Open booking
            </Link>
          </>
        ) : (
          <>
            <button type="button" className="btn btn-outline" onClick={close}>
              Cancel
            </button>
            <button type="submit" form="new-booking" className="btn btn-primary" disabled={busy}>
              {busy ? "Working…" : guest ? "Create booking" : "Find guest"}
            </button>
          </>
        )
      }
    >
      {created ? (
        <Alert tone="success">
          Room {created.roomNumber} booked for {guest.name} — total {formatNaira(created.totalPrice)}.
        </Alert>
      ) : (
        <form id="new-booking" className="stack" onSubmit={submit}>
          <Alert tone="danger">{error}</Alert>

          {guest ? (
            <div className="guest-chip">
              <div>
                <strong>{guest.name}</strong>
                <span className="faint">
                  {guest.email} · {guest.phoneNumber}
                </span>
              </div>
              {!initialGuest && (
                <button type="button" className="btn btn-ghost btn-sm" onClick={() => setGuest(null)}>
                  Change
                </button>
              )}
            </div>
          ) : (
            <div className="field">
              <label htmlFor="guest-email">Guest email</label>
              <input
                id="guest-email"
                type="email"
                required
                placeholder="guest@example.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              />
              <span className="field-hint">The guest needs a CodexHotel account.</span>
            </div>
          )}

          {guest && (
            <div className="form-grid">
              <div className="field">
                <label htmlFor="nb-type">Room type</label>
                <Dropdown
                  id="nb-type"
                  variant="field"
                  label="Room type"
                  value={form.roomType}
                  options={ROOM_TYPES.map((type) => ({ value: type, label: roomContent(type).name }))}
                  onChange={(roomType) => setForm({ ...form, roomType })}
                />
              </div>
              <div className="field">
                <label htmlFor="nb-in">Check in</label>
                <DatePicker
                  id="nb-in"
                  label="Check in"
                  min={today}
                  value={form.checkInDate}
                  onChange={(checkInDate) =>
                    setForm((prev) => ({
                      ...prev,
                      checkInDate,
                      checkOutDate: prev.checkOutDate <= checkInDate ? addDaysISO(checkInDate, 1) : prev.checkOutDate,
                    }))
                  }
                />
              </div>
              <div className="field">
                <label htmlFor="nb-out">Check out</label>
                <DatePicker
                  id="nb-out"
                  label="Check out"
                  min={addDaysISO(form.checkInDate, 1)}
                  rangeStart={form.checkInDate}
                  value={form.checkOutDate}
                  onChange={(checkOutDate) => setForm({ ...form, checkOutDate })}
                />
              </div>
            </div>
          )}
        </form>
      )}
    </Modal>
  );
}
