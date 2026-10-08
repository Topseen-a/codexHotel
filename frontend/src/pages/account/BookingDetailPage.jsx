import { useState } from "react";
import { Link, useParams } from "react-router-dom";
import { cancelBooking, getBookingById } from "../../api/bookings";
import { getPaymentsByBooking } from "../../api/payments";
import PaymentForm from "../../components/booking/PaymentForm";
import Alert from "../../components/ui/Alert";
import ConfirmDialog from "../../components/ui/ConfirmDialog";
import EmptyState from "../../components/ui/EmptyState";
import Icon from "../../components/ui/Icon";
import PageLoader from "../../components/layout/PageLoader";
import StatusBadge, { PaymentBadge } from "../../components/ui/StatusBadge";
import { SITE } from "../../config/site";
import { roomContent } from "../../content/rooms";
import { useAuth } from "../../context/useAuth";
import { useAsync } from "../../hooks/useAsync";
import { useDocumentTitle } from "../../hooks/useDocumentTitle";
import { formatDate, formatNaira, nightsBetween, pluralize, shortId } from "../../utils/format";
import "./Account.css";

export default function BookingDetailPage() {
  const { bookingId } = useParams();
  useDocumentTitle("Booking details");
  const { user, isStaff } = useAuth();

  const booking = useAsync(() => getBookingById(bookingId), [bookingId]);
  const payments = useAsync(() => getPaymentsByBooking(bookingId), [bookingId]);
  const [confirmCancel, setConfirmCancel] = useState(false);
  const [notice, setNotice] = useState("");

  if (booking.loading) return <PageLoader />;

  if (booking.error) {
    return (
      <div className="app-page container">
        <EmptyState
          icon="alertCircle"
          title="Booking unavailable"
          action={
            <Link to="/bookings" className="btn btn-primary">
              Back to my bookings
            </Link>
          }
        >
          {booking.error.message}
        </EmptyState>
      </div>
    );
  }

  const b = booking.data;
  const room = roomContent(b.roomType);
  const nights = nightsBetween(b.checkInDate, b.checkOutDate);
  const paid = (payments.data || []).filter((p) => p.successful).reduce((sum, p) => sum + Number(p.amount), 0);
  const total = Number(b.totalPrice);
  const outstanding = Math.max(0, Math.round((total - paid) * 100) / 100);
  const paidPercent = total > 0 ? Math.min(100, (paid / total) * 100) : 0;
  const isOwnBooking = b.userId === user.id;
  const backTo = isStaff && !isOwnBooking ? "/admin/bookings" : "/bookings";

  const onPaid = (payment) => {
    payments.setData((list) => [...(list || []), payment]);
    setNotice(`Payment of ${formatNaira(payment.amount)} received. Thank you!`);
  };

  const onCancel = async () => {
    const updated = await cancelBooking(b.bookingId);
    booking.setData(updated);
    setNotice("Your booking has been cancelled.");
  };

  return (
    <div className="app-page container">
      <Link to={backTo} className="back-link">
        <Icon name="arrowLeft" size={16} /> {backTo === "/bookings" ? "My bookings" : "Bookings dashboard"}
      </Link>

      <header className="app-page-header">
        <div>
          <div className="cluster">
            <h1>{room.name}</h1>
            <StatusBadge status={b.status} />
          </div>
          <p>
            Booking <span className="mono">{shortId(b.bookingId)}</span> · Room {b.roomNumber || "—"}
          </p>
        </div>
        {b.status === "CONFIRMED" && (
          <button type="button" className="btn btn-danger-outline" onClick={() => setConfirmCancel(true)}>
            Cancel booking
          </button>
        )}
      </header>

      <Alert tone="success" className="detail-notice">
        {notice}
      </Alert>

      <div className="booking-detail">
        <div className="stack" style={{ "--stack-gap": "24px" }}>
          <section className="card booking-stay">
            <img src={room.images[0]} alt="" />
            <dl className="booking-stay-facts">
              <div>
                <dt>Check in</dt>
                <dd>{formatDate(b.checkInDate)}</dd>
                <dd className="faint">from {SITE.checkInTime}</dd>
              </div>
              <div>
                <dt>Check out</dt>
                <dd>{formatDate(b.checkOutDate)}</dd>
                <dd className="faint">by {SITE.checkOutTime}</dd>
              </div>
              <div>
                <dt>Length of stay</dt>
                <dd>{pluralize(nights, "night")}</dd>
              </div>
              <div>
                <dt>Room</dt>
                <dd>
                  {room.name} · #{b.roomNumber || "—"}
                </dd>
              </div>
            </dl>
          </section>

          <section className="card card-pad stack">
            <h2 className="card-title">Payment history</h2>
            {payments.error && <Alert tone="danger">{payments.error.message}</Alert>}
            {payments.loading && <div className="skeleton" style={{ height: 60 }} />}
            {payments.data?.length === 0 && <p className="muted">No payments yet.</p>}
            {payments.data?.length > 0 && (
              <div className="table-wrap">
                <table className="data-table">
                  <thead>
                    <tr>
                      <th>Date</th>
                      <th>Reference</th>
                      <th>Amount</th>
                      <th>Status</th>
                    </tr>
                  </thead>
                  <tbody>
                    {payments.data.map((payment) => (
                      <tr key={payment.paymentId}>
                        <td data-label="Date">{formatDate(payment.paymentDate)}</td>
                        <td data-label="Reference" className="mono">
                          {shortId(payment.paymentId)}
                        </td>
                        <td data-label="Amount">{formatNaira(payment.amount)}</td>
                        <td data-label="Status">
                          <PaymentBadge successful={payment.successful} />
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </section>
        </div>

        <aside className="card card-pad booking-balance">
          <h2 className="card-title">Summary</h2>
          <div className="spread">
            <span className="muted">Total</span>
            <strong>{formatNaira(total)}</strong>
          </div>
          <div className="spread">
            <span className="muted">Paid</span>
            <span>{formatNaira(paid)}</span>
          </div>
          <div className="progress" role="progressbar" aria-valuenow={Math.round(paidPercent)} aria-valuemin={0} aria-valuemax={100}>
            <span style={{ width: `${paidPercent}%` }} />
          </div>
          <div className="spread booking-balance-due">
            <span>Balance due</span>
            <strong>{formatNaira(outstanding)}</strong>
          </div>

          {b.status === "CONFIRMED" && outstanding > 0 && !payments.loading && (
            <PaymentForm key={outstanding} bookingId={b.bookingId} outstanding={outstanding} onPaid={onPaid} />
          )}
          {b.status === "CONFIRMED" && outstanding === 0 && !payments.loading && (
            <Alert tone="success">Paid in full — we look forward to welcoming you.</Alert>
          )}
          {b.status === "CANCELLED" && <Alert tone="info">This booking was cancelled.</Alert>}
        </aside>
      </div>

      <ConfirmDialog
        open={confirmCancel}
        title="Cancel this booking?"
        confirmLabel="Yes, cancel booking"
        onConfirm={onCancel}
        onClose={() => setConfirmCancel(false)}
      >
        Your stay from {formatDate(b.checkInDate)} to {formatDate(b.checkOutDate)} will be cancelled. This can&apos;t be
        undone.
      </ConfirmDialog>
    </div>
  );
}
