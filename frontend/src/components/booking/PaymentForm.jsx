import { useState } from "react";
import { makePayment } from "../../api/payments";
import { formatNaira } from "../../utils/format";
import { PAYMENT_METHODS } from "../../utils/status";
import Alert from "../ui/Alert";
import Dropdown from "../ui/Dropdown";

/** Records a payment against a booking, defaulting to the outstanding balance. */
export default function PaymentForm({ bookingId, outstanding, onPaid }) {
  const [amount, setAmount] = useState(String(outstanding));
  const [method, setMethod] = useState("CARD");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");

  const submit = async (event) => {
    event.preventDefault();
    setError("");
    const value = Math.round(Number(amount) * 100) / 100;
    if (!(value > 0)) {
      setError("Enter an amount greater than zero.");
      return;
    }
    if (value > outstanding) {
      setError(`You only owe ${formatNaira(outstanding)} on this booking.`);
      return;
    }

    setSubmitting(true);
    try {
      const payment = await makePayment({ bookingId, amount: value, paymentMethod: method });
      onPaid(payment);
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form className="payment-form" onSubmit={submit}>
      <div className="form-grid">
        <div className="field">
          <label htmlFor="pay-amount">Amount (₦)</label>
          <input
            id="pay-amount"
            type="number"
            inputMode="decimal"
            min="0.01"
            step="0.01"
            max={outstanding}
            required
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
          />
          <span className="field-hint">Outstanding: {formatNaira(outstanding)}</span>
        </div>
        <div className="field">
          <label htmlFor="pay-method">Payment method</label>
          <Dropdown
            id="pay-method"
            variant="field"
            label="Payment method"
            value={method}
            options={PAYMENT_METHODS}
            onChange={setMethod}
          />
        </div>
      </div>
      <Alert tone="danger">{error}</Alert>
      <button type="submit" className="btn btn-primary" disabled={submitting}>
        {submitting ? "Processing…" : `Pay ${formatNaira(Number(amount) || 0)}`}
      </button>
    </form>
  );
}
