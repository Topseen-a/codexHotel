import { Link } from "react-router-dom";
import { getBookingsByStatus } from "../../api/bookings";
import { getReport } from "../../api/reports";
import Alert from "../../components/ui/Alert";
import Icon from "../../components/ui/Icon";
import { roomContent } from "../../content/rooms";
import { useAsync } from "../../hooks/useAsync";
import { useDocumentTitle } from "../../hooks/useDocumentTitle";
import { formatNaira, formatShortDate, todayISO } from "../../utils/format";
import AdminHeader from "./AdminHeader";

export default function OverviewPage() {
  useDocumentTitle("Overview");
  const report = useAsync(getReport, []);
  const confirmed = useAsync(() => getBookingsByStatus("CONFIRMED"), []);

  const today = todayISO();
  const upcoming = (confirmed.data || [])
    .filter((b) => b.checkOutDate > today)
    .sort((a, b) => a.checkInDate.localeCompare(b.checkInDate))
    .slice(0, 6);

  const r = report.data;
  const maintenance = r ? Math.max(0, r.totalRooms - r.occupiedRooms - r.availableRooms) : 0;

  const stats = r
    ? [
        { label: "Total revenue", value: formatNaira(r.totalRevenue), icon: "creditCard", hint: "Successful payments" },
        { label: "Active bookings", value: r.activeBookings, icon: "calendar", hint: "Confirmed reservations" },
        { label: "Occupancy", value: `${Number(r.occupancyRate).toFixed(0)}%`, icon: "chart", hint: `${r.occupiedRooms} of ${r.totalRooms} rooms` },
        { label: "Available rooms", value: r.availableRooms, icon: "bed", hint: `${maintenance} in maintenance` },
      ]
    : [];

  return (
    <>
      <AdminHeader
        title="Overview"
        description="Live figures across rooms, bookings and revenue."
        actions={
          <button type="button" className="btn btn-outline btn-sm" onClick={() => { report.reload(); confirmed.reload(); }}>
            <Icon name="refresh" size={15} /> Refresh
          </button>
        }
      />

      {report.error && <Alert tone="danger">{report.error.message}</Alert>}

      <div className="stat-grid">
        {report.loading &&
          [0, 1, 2, 3].map((i) => (
            <div key={i} className="stat-card card">
              <div className="skeleton" style={{ height: 14, width: "50%" }} />
              <div className="skeleton" style={{ height: 30, width: "70%", marginTop: 12 }} />
            </div>
          ))}
        {stats.map((stat) => (
          <div key={stat.label} className="stat-card card">
            <div className="spread">
              <span className="stat-label">{stat.label}</span>
              <span className="stat-icon">
                <Icon name={stat.icon} size={18} />
              </span>
            </div>
            <strong className="stat-value">{stat.value}</strong>
            <span className="faint stat-hint">{stat.hint}</span>
          </div>
        ))}
      </div>

      <div className="overview-grid">
        {r && (
          <section className="card card-pad stack">
            <h2 className="admin-card-title">Room status</h2>
            <div className="status-bar" aria-hidden="true">
              {[
                { key: "occupied", value: r.occupiedRooms },
                { key: "available", value: r.availableRooms },
                { key: "maintenance", value: maintenance },
              ].map((part) =>
                part.value > 0 ? (
                  <span key={part.key} className={`status-bar-${part.key}`} style={{ flexGrow: part.value }} />
                ) : null
              )}
            </div>
            <ul className="status-legend">
              <li>
                <span className="dot dot-occupied" /> Occupied <strong>{r.occupiedRooms}</strong>
              </li>
              <li>
                <span className="dot dot-available" /> Available <strong>{r.availableRooms}</strong>
              </li>
              <li>
                <span className="dot dot-maintenance" /> Maintenance <strong>{maintenance}</strong>
              </li>
            </ul>
            <Link to="/admin/rooms" className="btn btn-outline btn-sm" style={{ justifySelf: "start" }}>
              Manage rooms
            </Link>
          </section>
        )}

        <section className="card card-pad stack">
          <div className="spread">
            <h2 className="admin-card-title">Upcoming stays</h2>
            <Link to="/admin/bookings" className="link">
              All bookings
            </Link>
          </div>
          {confirmed.error && <Alert tone="danger">{confirmed.error.message}</Alert>}
          {confirmed.loading && <div className="skeleton" style={{ height: 120 }} />}
          {!confirmed.loading && upcoming.length === 0 && <p className="muted">No upcoming confirmed stays.</p>}
          <ul className="upcoming-list">
            {upcoming.map((b) => (
              <li key={b.bookingId}>
                <Link to={`/bookings/${b.bookingId}`}>
                  <span className="upcoming-date">
                    <strong>{formatShortDate(b.checkInDate)}</strong>
                    <span>{b.checkInDate <= today ? "In house" : "Arrives"}</span>
                  </span>
                  <span className="upcoming-room">
                    {roomContent(b.roomType).name}
                    <span className="faint"> · Room {b.roomNumber || "—"}</span>
                  </span>
                  <span>{formatNaira(b.totalPrice)}</span>
                </Link>
              </li>
            ))}
          </ul>
        </section>
      </div>
    </>
  );
}
