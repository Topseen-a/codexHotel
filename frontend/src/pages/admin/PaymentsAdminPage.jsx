import { useState } from "react";
import { Link } from "react-router-dom";
import { deletePayment, getPaymentById, getPaymentsByStatus, markPaymentSuccessful } from "../../api/payments";
import Alert from "../../components/ui/Alert";
import ConfirmDialog from "../../components/ui/ConfirmDialog";
import EmptyState from "../../components/ui/EmptyState";
import Icon from "../../components/ui/Icon";
import { PaymentBadge } from "../../components/ui/StatusBadge";
import { useAuth } from "../../context/useAuth";
import { useAsync } from "../../hooks/useAsync";
import { useDocumentTitle } from "../../hooks/useDocumentTitle";
import { formatDate, formatNaira, shortId } from "../../utils/format";
import AdminHeader from "./AdminHeader";

export default function PaymentsAdminPage() {
  useDocumentTitle("Payments");
  const { can } = useAuth();
  const canDelete = can("deletePayments");

  const [successful, setSuccessful] = useState(true);
  const [query, setQuery] = useState("");
  const [lookupId, setLookupId] = useState(null);
  const payments = useAsync(
    () => (lookupId ? getPaymentById(lookupId).then((p) => [p]) : getPaymentsByStatus(successful)),
    [successful, lookupId]
  );

  const [message, setMessage] = useState("");
  const [actionError, setActionError] = useState("");
  const [toDelete, setToDelete] = useState(null);

  const list = [...(payments.data || [])].sort((a, b) => (b.paymentDate || "").localeCompare(a.paymentDate || ""));
  const total = list.reduce((sum, p) => sum + Number(p.amount), 0);

  const markPaid = async (payment) => {
    setActionError("");
    try {
      const updated = await markPaymentSuccessful(payment.paymentId);
      payments.setData((items) =>
        items.map((p) => (p.paymentId === updated.paymentId ? updated : p)).filter((p) => lookupId || p.successful === successful)
      );
      setMessage(`Payment ${shortId(updated.paymentId)} marked as successful.`);
    } catch (err) {
      setActionError(err.message);
    }
  };

  const remove = async () => {
    await deletePayment(toDelete.paymentId);
    payments.setData((items) => items.filter((p) => p.paymentId !== toDelete.paymentId));
    setMessage(`Payment ${shortId(toDelete.paymentId)} deleted.`);
  };

  const clearLookup = () => {
    setQuery("");
    setLookupId(null);
  };

  return (
    <>
      <AdminHeader title="Payments" description="Review payments, confirm pending ones and look up a payment by reference." />

      <div className="admin-toolbar">
        <div className="segmented" role="tablist" aria-label="Payment status">
          {[
            { value: true, label: "Successful" },
            { value: false, label: "Pending" },
          ].map((option) => (
            <button
              key={option.label}
              type="button"
              role="tab"
              aria-selected={!lookupId && successful === option.value}
              className={!lookupId && successful === option.value ? "active" : ""}
              onClick={() => {
                clearLookup();
                setSuccessful(option.value);
              }}
            >
              {option.label}
            </button>
          ))}
        </div>
        <form
          className="admin-search"
          role="search"
          onSubmit={(e) => {
            e.preventDefault();
            setMessage("");
            setLookupId(query.trim() || null);
          }}
        >
          <Icon name="search" size={16} />
          <input placeholder="Payment reference (full ID)" aria-label="Find payment by ID" value={query} onChange={(e) => setQuery(e.target.value)} />
          {lookupId && (
            <button type="button" className="btn btn-ghost btn-sm" onClick={clearLookup}>
              Clear
            </button>
          )}
        </form>
      </div>

      <Alert tone="success">{message}</Alert>
      <Alert tone="danger">{actionError || payments.error?.message}</Alert>

      {payments.loading && <div className="skeleton" style={{ height: 240 }} />}

      {!payments.loading && !payments.error && (
        <>
          {list.length > 0 && (
            <p className="muted admin-summary">
              {list.length} payment{list.length === 1 ? "" : "s"} · <strong>{formatNaira(total)}</strong>
            </p>
          )}
          <div className="table-wrap">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Reference</th>
                  <th>Booking</th>
                  <th>Date</th>
                  <th>Amount</th>
                  <th>Status</th>
                  <th aria-label="Actions" />
                </tr>
              </thead>
              <tbody>
                {list.map((p) => (
                  <tr key={p.paymentId}>
                    <td data-label="Reference" className="mono" title={p.paymentId}>
                      {shortId(p.paymentId)}
                    </td>
                    <td data-label="Booking">
                      <Link to={`/bookings/${p.bookingId}`} className="link mono">
                        {shortId(p.bookingId)}
                      </Link>
                    </td>
                    <td data-label="Date">{formatDate(p.paymentDate)}</td>
                    <td data-label="Amount">
                      <strong>{formatNaira(p.amount)}</strong>
                    </td>
                    <td data-label="Status">
                      <PaymentBadge successful={p.successful} />
                    </td>
                    <td data-label="">
                      <div className="cell-actions">
                        {!p.successful && (
                          <button type="button" className="btn btn-outline btn-sm" onClick={() => markPaid(p)}>
                            <Icon name="check" size={14} /> Mark paid
                          </button>
                        )}
                        {canDelete && (
                          <button
                            type="button"
                            className="btn btn-ghost btn-icon btn-sm"
                            aria-label={`Delete payment ${shortId(p.paymentId)}`}
                            onClick={() => setToDelete(p)}
                          >
                            <Icon name="trash" size={16} />
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            {list.length === 0 && (
              <EmptyState icon="creditCard" title={successful ? "No payments yet" : "No pending payments"}>
                {successful ? "Payments will appear here as guests pay." : "Every recorded payment has been confirmed."}
              </EmptyState>
            )}
          </div>
        </>
      )}

      <ConfirmDialog
        open={Boolean(toDelete)}
        title="Delete this payment record?"
        confirmLabel="Delete payment"
        onConfirm={remove}
        onClose={() => setToDelete(null)}
      >
        {toDelete && `${formatNaira(toDelete.amount)} on ${formatDate(toDelete.paymentDate)}. `}
        Deleting a financial record can&apos;t be undone and will change the booking&apos;s balance.
      </ConfirmDialog>
    </>
  );
}
