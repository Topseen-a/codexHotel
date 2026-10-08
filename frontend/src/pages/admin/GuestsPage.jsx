import { useState } from "react";
import { Link, useSearchParams } from "react-router-dom";
import { getBookingsByUser } from "../../api/bookings";
import { getUserByEmail, getUserById } from "../../api/users";
import Alert from "../../components/ui/Alert";
import EmptyState from "../../components/ui/EmptyState";
import Icon from "../../components/ui/Icon";
import StatusBadge from "../../components/ui/StatusBadge";
import { roomContent } from "../../content/rooms";
import { useAsync } from "../../hooks/useAsync";
import { useDocumentTitle } from "../../hooks/useDocumentTitle";
import { formatDate, formatNaira, initials } from "../../utils/format";
import { ROLE_LABELS } from "../../utils/roles";
import AdminHeader from "./AdminHeader";
import NewBookingModal from "./NewBookingModal";

export default function GuestsPage() {
  useDocumentTitle("Guests");
  const [params, setParams] = useSearchParams();
  const id = params.get("id");
  const email = params.get("email");
  const [booking, setBooking] = useState(false);

  const guest = useAsync(id ? () => getUserById(id) : email ? () => getUserByEmail(email) : null, [id, email]);
  const bookings = useAsync(guest.data ? () => getBookingsByUser(guest.data.id) : null, [guest.data?.id]);

  const list = [...(bookings.data || [])].sort((a, b) => b.checkInDate.localeCompare(a.checkInDate));
  const totalSpend = list.filter((b) => b.status !== "CANCELLED").reduce((sum, b) => sum + Number(b.totalPrice), 0);

  return (
    <>
      <AdminHeader title="Guests" description="Look up a guest to see their details and booking history." />

      <form
        className="admin-filters card"
        onSubmit={(e) => {
          e.preventDefault();
          const value = new FormData(e.currentTarget).get("email").trim();
          if (value) setParams({ email: value });
        }}
      >
        <div className="field" style={{ flex: 1 }}>
          <label htmlFor="guest-search">Guest email</label>
          <div className="input-with-button">
            <input
              key={email}
              id="guest-search"
              name="email"
              type="email"
              required
              placeholder="guest@example.com"
              defaultValue={email || ""}
            />
            <button type="submit" className="btn btn-primary">
              <Icon name="search" size={16} /> Find
            </button>
          </div>
        </div>
      </form>

      {!id && !email && (
        <EmptyState icon="users" title="Find a guest">
          Search by email, or open a guest from any booking.
        </EmptyState>
      )}

      {guest.loading && <div className="skeleton" style={{ height: 160 }} />}
      {guest.error && (
        <Alert tone="danger">{guest.error.status === 404 ? "No account found for that guest." : guest.error.message}</Alert>
      )}

      {guest.data && (
        <div className="guest-layout">
          <section className="card card-pad guest-profile">
            <span className="avatar avatar-lg">{initials(guest.data.name)}</span>
            <h2>{guest.data.name}</h2>
            <span className="badge badge-info">{ROLE_LABELS[guest.data.role]}</span>
            <dl className="guest-facts">
              <div>
                <dt>Email</dt>
                <dd>
                  <a href={`mailto:${guest.data.email}`}>{guest.data.email}</a>
                </dd>
              </div>
              <div>
                <dt>Phone</dt>
                <dd>
                  <a href={`tel:${guest.data.phoneNumber}`}>{guest.data.phoneNumber}</a>
                </dd>
              </div>
              <div>
                <dt>Bookings</dt>
                <dd>{list.length}</dd>
              </div>
              <div>
                <dt>Booked value</dt>
                <dd>{formatNaira(totalSpend)}</dd>
              </div>
            </dl>
            <button type="button" className="btn btn-primary btn-block" onClick={() => setBooking(true)}>
              <Icon name="plus" size={16} /> Book for this guest
            </button>
          </section>

          <section className="stack">
            <h2 className="admin-card-title">Booking history</h2>
            {bookings.error && <Alert tone="danger">{bookings.error.message}</Alert>}
            {bookings.loading && <div className="skeleton" style={{ height: 160 }} />}
            {!bookings.loading && list.length === 0 && (
              <EmptyState icon="calendar" title="No bookings yet" />
            )}
            {list.length > 0 && (
              <div className="table-wrap">
                <table className="data-table">
                  <thead>
                    <tr>
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
                            <Link to={`/bookings/${b.bookingId}`} className="btn btn-outline btn-sm">
                              Open
                            </Link>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </section>
        </div>
      )}

      {guest.data && (
        <NewBookingModal
          key={guest.data.id}
          open={booking}
          initialGuest={guest.data}
          onClose={() => setBooking(false)}
          onCreated={() => bookings.reload()}
        />
      )}
    </>
  );
}
