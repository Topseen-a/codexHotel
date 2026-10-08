import { useState } from "react";
import { Link, useSearchParams } from "react-router-dom";
import { cancelBooking, getBookingsByRoom, getBookingsByStatus, getBookingsByUser } from "../../api/bookings";
import { listRooms } from "../../api/rooms";
import { getUserByEmail } from "../../api/users";
import Alert from "../../components/ui/Alert";
import ConfirmDialog from "../../components/ui/ConfirmDialog";
import Dropdown from "../../components/ui/Dropdown";
import EmptyState from "../../components/ui/EmptyState";
import Icon from "../../components/ui/Icon";
import StatusBadge from "../../components/ui/StatusBadge";
import { roomContent } from "../../content/rooms";
import { useAsync } from "../../hooks/useAsync";
import { useDocumentTitle } from "../../hooks/useDocumentTitle";
import { formatDate, formatNaira, shortId } from "../../utils/format";
import { BOOKING_STATUS } from "../../utils/status";
import AdminHeader from "./AdminHeader";
import NewBookingModal from "./NewBookingModal";

const MODES = [
  { key: "status", label: "By status" },
  { key: "room", label: "By room" },
  { key: "guest", label: "By guest" },
];

export default function BookingsAdminPage() {
  useDocumentTitle("Bookings");
  const [params, setParams] = useSearchParams();
  const mode = params.get("roomId") ? "room" : params.get("guest") ? "guest" : params.get("mode") || "status";
  const status = params.get("status") || "CONFIRMED";
  const roomId = params.get("roomId") || "";
  const guestEmail = params.get("guest") || "";

  const [creating, setCreating] = useState(false);
  const [toCancel, setToCancel] = useState(null);
  const [message, setMessage] = useState("");

  const rooms = useAsync(listRooms, []);
  const bookings = useAsync(() => {
    if (mode === "room") return roomId ? getBookingsByRoom(roomId) : Promise.resolve(null);
    if (mode === "guest") {
      return guestEmail
        ? getUserByEmail(guestEmail).then((user) => getBookingsByUser(user.id).then((list) => list))
        : Promise.resolve(null);
    }
    return getBookingsByStatus(status);
  }, [mode, status, roomId, guestEmail]);

  const setQuery = (next) => {
    setMessage("");
    setParams(next, { replace: true });
  };

  const list = [...(bookings.data || [])].sort((a, b) => b.checkInDate.localeCompare(a.checkInDate));
  const sortedRooms = [...(rooms.data || [])].sort((a, b) => a.roomNumber - b.roomNumber);

  const doCancel = async () => {
    const updated = await cancelBooking(toCancel.bookingId);
    bookings.setData((items) =>
      items
        .map((b) => (b.bookingId === updated.bookingId ? updated : b))
        .filter((b) => mode !== "status" || b.status === status)
    );
    setMessage(`Booking ${shortId(updated.bookingId)} cancelled.`);
  };

  return (
    <>
      <AdminHeader
        title="Bookings"
        description="Find reservations by status, room or guest — and book on a guest's behalf."
        actions={
          <button type="button" className="btn btn-primary btn-sm" onClick={() => setCreating(true)}>
            <Icon name="plus" size={16} /> New booking
          </button>
        }
      />

      <div className="admin-filters card">
        <div className="segmented" role="tablist" aria-label="Find bookings">
          {MODES.map((m) => (
            <button
              key={m.key}
              type="button"
              role="tab"
              aria-selected={mode === m.key}
              className={mode === m.key ? "active" : ""}
              onClick={() => setQuery({ mode: m.key })}
            >
              {m.label}
            </button>
          ))}
        </div>

        {mode === "status" && (
          <div className="field">
            <label htmlFor="bk-status">Status</label>
            <Dropdown
              id="bk-status"
              variant="field"
              label="Status"
              value={status}
              options={Object.entries(BOOKING_STATUS).map(([value, meta]) => ({ value, label: meta.label }))}
              onChange={(value) => setQuery({ mode: "status", status: value })}
            />
          </div>
        )}

        {mode === "room" && (
          <div className="field">
            <label htmlFor="bk-room">Room</label>
            <Dropdown
              id="bk-room"
              variant="field"
              label="Room"
              value={roomId}
              placeholder="Select a room…"
              options={sortedRooms.map((room) => ({
                value: room.id,
                label: `#${room.roomNumber} — ${roomContent(room.type).name}`,
              }))}
              onChange={(value) => setQuery({ roomId: value })}
            />
          </div>
        )}

        {mode === "guest" && (
          <form
            className="field"
            onSubmit={(e) => {
              e.preventDefault();
              const value = new FormData(e.currentTarget).get("guest").trim();
              if (value) setQuery({ guest: value });
            }}
          >
            <label htmlFor="bk-guest">Guest email</label>
            <div className="input-with-button">
              <input
                key={guestEmail}
                id="bk-guest"
                name="guest"
                type="email"
                required
                placeholder="guest@example.com"
                defaultValue={guestEmail}
              />
              <button type="submit" className="btn btn-primary">
                Search
              </button>
            </div>
          </form>
        )}
      </div>

      <Alert tone="success">{message}</Alert>
      <Alert tone="danger">{bookings.error?.message}</Alert>

      {bookings.loading && <div className="skeleton" style={{ height: 240 }} />}

      {!bookings.loading && !bookings.error && bookings.data === null && (
        <EmptyState icon="search" title={mode === "room" ? "Choose a room" : "Search for a guest"}>
          {mode === "room" ? "Pick a room to see every booking made for it." : "Enter a guest's email to see their bookings."}
        </EmptyState>
      )}

      {!bookings.loading && bookings.data && (
        <div className="table-wrap">
          <table className="data-table">
            <thead>
              <tr>
                <th>Booking</th>
                <th>Room</th>
                <th>Stay</th>
                <th>Total</th>
                <th>Status</th>
                <th aria-label="Actions" />
              </tr>
            </thead>
            <tbody>
              {list.map((b) => (
                <tr key={b.bookingId}>
                  <td data-label="Booking" className="mono">
                    {shortId(b.bookingId)}
                  </td>
                  <td data-label="Room">
                    {roomContent(b.roomType).name} · #{b.roomNumber || "—"}
                  </td>
                  <td data-label="Stay">
                    {formatDate(b.checkInDate)} → {formatDate(b.checkOutDate)}
                  </td>
                  <td data-label="Total">{formatNaira(b.totalPrice)}</td>
                  <td data-label="Status">
                    <StatusBadge status={b.status} />
                  </td>
                  <td data-label="">
                    <div className="cell-actions">
                      <Link to={`/admin/guests?id=${b.userId}`} className="btn btn-ghost btn-sm">
                        Guest
                      </Link>
                      <Link to={`/bookings/${b.bookingId}`} className="btn btn-outline btn-sm">
                        Open
                      </Link>
                      {b.status === "CONFIRMED" && (
                        <button type="button" className="btn btn-danger-outline btn-sm" onClick={() => setToCancel(b)}>
                          Cancel
                        </button>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          {list.length === 0 && (
            <EmptyState icon="calendar" title="No bookings found">
              Nothing matches this filter yet.
            </EmptyState>
          )}
        </div>
      )}

      <NewBookingModal
        open={creating}
        onClose={() => setCreating(false)}
        onCreated={() => (mode === "status" && status === "CONFIRMED" ? bookings.reload() : undefined)}
      />

      <ConfirmDialog
        open={Boolean(toCancel)}
        title="Cancel this booking?"
        confirmLabel="Cancel booking"
        onConfirm={doCancel}
        onClose={() => setToCancel(null)}
      >
        {toCancel &&
          `${roomContent(toCancel.roomType).name}, ${formatDate(toCancel.checkInDate)} → ${formatDate(toCancel.checkOutDate)}. The guest will be notified.`}
      </ConfirmDialog>
    </>
  );
}
