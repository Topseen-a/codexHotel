import { useState } from "react";
import { calculatePrice, getPriceList } from "../../api/pricing";
import Alert from "../../components/ui/Alert";
import DatePicker from "../../components/ui/DatePicker";
import Dropdown from "../../components/ui/Dropdown";
import { ROOM_TYPES, roomContent } from "../../content/rooms";
import { useAsync } from "../../hooks/useAsync";
import { useDocumentTitle } from "../../hooks/useDocumentTitle";
import { formatDate, formatNaira, todayISO } from "../../utils/format";
import { SEASONS } from "../../utils/status";
import AdminHeader from "./AdminHeader";

export default function PricingPage() {
  useDocumentTitle("Pricing");
  const prices = useAsync(getPriceList, []);
  const [form, setForm] = useState({ roomType: "STANDARD", basePrice: "5000", date: todayISO() });
  const [result, setResult] = useState(null);
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);

  const seasons = Object.keys(SEASONS);
  const priceFor = (type, season) => prices.data?.find((p) => p.roomType === type && p.season === season)?.price;

  const calculate = async (event) => {
    event.preventDefault();
    setError("");
    setResult(null);
    setBusy(true);
    try {
      const price = await calculatePrice(form);
      setResult({ ...form, price });
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(false);
    }
  };

  return (
    <>
      <AdminHeader
        title="Pricing"
        description="Nightly rates are the room's base price multiplied by the season: weekday, weekend (Sat–Sun) or festive (December)."
      />

      <div className="pricing-layout">
        <section className="card card-pad stack">
          <h2 className="admin-card-title">Published rates</h2>
          <p className="muted">Reference nightly rates at each room type&apos;s standard base price.</p>
          <Alert tone="danger">{prices.error?.message}</Alert>
          {prices.loading && <div className="skeleton" style={{ height: 160 }} />}
          {prices.data && (
            <div className="table-wrap">
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Room type</th>
                    {seasons.map((season) => (
                      <th key={season}>{SEASONS[season].label}</th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {ROOM_TYPES.map((type) => (
                    <tr key={type}>
                      <td data-label="Room type">
                        <strong>{roomContent(type).name}</strong>
                      </td>
                      {seasons.map((season) => (
                        <td key={season} data-label={SEASONS[season].label}>
                          {priceFor(type, season) != null ? formatNaira(priceFor(type, season)) : "—"}
                        </td>
                      ))}
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>

        <section className="card card-pad stack">
          <h2 className="admin-card-title">Price calculator</h2>
          <p className="muted">Quote a single night for any room type, base price and date.</p>
          <form className="stack" onSubmit={calculate}>
            <div className="field">
              <label htmlFor="calc-type">Room type</label>
              <Dropdown
                id="calc-type"
                variant="field"
                label="Room type"
                value={form.roomType}
                options={ROOM_TYPES.map((type) => ({ value: type, label: roomContent(type).name }))}
                onChange={(roomType) => setForm({ ...form, roomType })}
              />
            </div>
            <div className="field">
              <label htmlFor="calc-base">Base price (₦)</label>
              <input
                id="calc-base"
                type="number"
                min="0"
                step="0.01"
                required
                value={form.basePrice}
                onChange={(e) => setForm({ ...form, basePrice: e.target.value })}
              />
            </div>
            <div className="field">
              <label htmlFor="calc-date">Night of</label>
              <DatePicker id="calc-date" label="Night of" value={form.date} onChange={(date) => setForm({ ...form, date })} />
            </div>
            <button type="submit" className="btn btn-primary" disabled={busy}>
              {busy ? "Calculating…" : "Calculate"}
            </button>
          </form>
          <Alert tone="danger">{error}</Alert>
          {result && (
            <div className="calc-result">
              <span className="faint">
                {roomContent(result.roomType).name} · {formatDate(result.date)}
              </span>
              <strong>{formatNaira(result.price)}</strong>
              <span className="faint">per night</span>
            </div>
          )}
        </section>
      </div>
    </>
  );
}
