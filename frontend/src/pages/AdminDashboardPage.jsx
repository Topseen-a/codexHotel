import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import { listRooms, createRoom, updateRoomStatus, deleteRoom } from "../api/rooms";
import { getBookingsByStatus } from "../api/bookings";
import { getAllUsers, createStaffUser } from "../api/users";
import { getReport } from "../api/reports";
import "./AdminDashboardPage.css";

const nairaFormatter = new Intl.NumberFormat("en-NG", {
  style: "currency",
  currency: "NGN",
  maximumFractionDigits: 0,
});

const ROOM_STATUSES = ["AVAILABLE", "OCCUPIED", "MAINTENANCE"];
const ROOM_TYPES = ["STANDARD", "DELUXE", "SUITE"];
const STAFF_ROLES = ["RECEPTIONIST", "MANAGER", "ADMIN"];

export default function AdminDashboardPage() {
  const { user, isAdmin } = useAuth();
  const canManageRooms = ["ADMIN", "MANAGER"].includes(user.role);
  const canSeeReports = ["ADMIN", "MANAGER"].includes(user.role);

  const tabs = [
    canSeeReports && { key: "overview", label: "Overview" },
    { key: "rooms", label: "Rooms" },
    { key: "bookings", label: "Bookings" },
    isAdmin && { key: "users", label: "Staff & Users" },
  ].filter(Boolean);

  const [activeTab, setActiveTab] = useState(tabs[0]?.key || "rooms");

  return (
    <div className="admin-page container">
      <div className="admin-header">
        <h1>Staff dashboard</h1>
        <p className="muted">Signed in as {user.name} · {user.role}</p>
      </div>

      <div className="admin-tabs">
        {tabs.map((tab) => (
          <button key={tab.key} className={activeTab === tab.key ? "active" : ""} onClick={() => setActiveTab(tab.key)}>
            {tab.label}
          </button>
        ))}
      </div>

      {activeTab === "overview" && <OverviewTab />}
      {activeTab === "rooms" && <RoomsTab canManage={canManageRooms} />}
      {activeTab === "bookings" && <BookingsTab />}
      {activeTab === "users" && isAdmin && <UsersTab />}
    </div>
  );
}

function OverviewTab() {
  const [report, setReport] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    getReport().then(setReport).catch((err) => setError(err.message));
  }, []);

  if (error) return <div className="alert alert-danger">{error}</div>;

  if (!report) {
    return (
      <div className="stat-grid" aria-busy="true" aria-label="Loading report">
        {Array.from({ length: 6 }).map((_, i) => (
          <div key={i} className="card stat-card">
            <div className="skeleton" style={{ height: 28, width: "60%" }} />
            <div className="skeleton" style={{ height: 13, width: "80%", marginTop: 10 }} />
          </div>
        ))}
      </div>
    );
  }

  const stats = [
    { label: "Total rooms", value: report.totalRooms },
    { label: "Occupied rooms", value: report.occupiedRooms },
    { label: "Available rooms", value: report.availableRooms },
    { label: "Occupancy rate", value: `${report.occupancyRate.toFixed(0)}%` },
    { label: "Active bookings", value: report.activeBookings },
    { label: "Total revenue", value: nairaFormatter.format(report.totalRevenue) },
  ];

  return (
    <div className="stat-grid">
      {stats.map((s) => (
        <div key={s.label} className="card stat-card">
          <div className="stat-value">{s.value}</div>
          <div className="stat-label">{s.label}</div>
        </div>
      ))}
    </div>
  );
}

function RoomsTab({ canManage }) {
  const [rooms, setRooms] = useState([]);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");
  const [form, setForm] = useState({ roomNumber: "", roomType: "STANDARD", basePrice: "", roomStatus: "AVAILABLE" });

  const load = () => listRooms().then(setRooms).catch((err) => setError(err.message));

  useEffect(load, []);

  const handleCreate = async (e) => {
    e.preventDefault();
    setError("");
    setMessage("");
    try {
      await createRoom({
        roomNumber: Number(form.roomNumber),
        roomType: form.roomType,
        basePrice: Number(form.basePrice),
        roomStatus: form.roomStatus,
      });
      setMessage("Room created.");
      setForm({ roomNumber: "", roomType: "STANDARD", basePrice: "", roomStatus: "AVAILABLE" });
      load();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleStatusChange = async (room, roomStatus) => {
    setError("");
    setMessage("");
    try {
      await updateRoomStatus({ roomId: room.id, roomStatus });
      load();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleDelete = async (room) => {
    setError("");
    setMessage("");
    try {
      await deleteRoom(room.id);
      setMessage("Room deleted.");
      load();
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div>
      {error && <div className="alert alert-danger">{error}</div>}
      {message && <div className="alert alert-success">{message}</div>}

      {canManage && (
        <form className="admin-form-inline" onSubmit={handleCreate}>
          <div className="field">
            <label>Room number</label>
            <input
              type="number"
              required
              value={form.roomNumber}
              onChange={(e) => setForm({ ...form, roomNumber: e.target.value })}
            />
          </div>
          <div className="field">
            <label>Type</label>
            <select value={form.roomType} onChange={(e) => setForm({ ...form, roomType: e.target.value })}>
              {ROOM_TYPES.map((t) => (
                <option key={t} value={t}>
                  {t}
                </option>
              ))}
            </select>
          </div>
          <div className="field">
            <label>Base price (₦/night)</label>
            <input
              type="number"
              required
              value={form.basePrice}
              onChange={(e) => setForm({ ...form, basePrice: e.target.value })}
            />
          </div>
          <button type="submit" className="btn btn-primary">
            Add room
          </button>
        </form>
      )}

      <div className="admin-table-scroll">
      <table className="admin-table">
        <thead>
          <tr>
            <th>Room</th>
            <th>Type</th>
            <th>Base price</th>
            <th>Status</th>
            {canManage && <th></th>}
          </tr>
        </thead>
        <tbody>
          {rooms.map((room) => (
            <tr key={room.id}>
              <td>{room.roomNumber}</td>
              <td>{room.type}</td>
              <td>{nairaFormatter.format(room.basePrice)}</td>
              <td>
                <select value={room.status} onChange={(e) => handleStatusChange(room, e.target.value)}>
                  {ROOM_STATUSES.map((s) => (
                    <option key={s} value={s}>
                      {s}
                    </option>
                  ))}
                </select>
              </td>
              {canManage && (
                <td>
                  <button className="btn btn-danger btn-sm" onClick={() => handleDelete(room)}>
                    Delete
                  </button>
                </td>
              )}
            </tr>
          ))}
        </tbody>
      </table>
      </div>
    </div>
  );
}

function BookingsTab() {
  const [statusFilter, setStatusFilter] = useState("CONFIRMED");
  const [bookings, setBookings] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    getBookingsByStatus(statusFilter)
      .then(setBookings)
      .catch((err) => setError(err.message));
  }, [statusFilter]);

  return (
    <div>
      <div className="admin-toolbar">
        <div className="field" style={{ marginBottom: 0, minWidth: 200 }}>
          <label>Filter by status</label>
          <select value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
            <option value="CONFIRMED">Confirmed</option>
            <option value="CANCELLED">Cancelled</option>
            <option value="COMPLETED">Completed</option>
          </select>
        </div>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      <div className="admin-table-scroll">
      <table className="admin-table">
        <thead>
          <tr>
            <th>Booking ID</th>
            <th>Room</th>
            <th>Check-in</th>
            <th>Check-out</th>
            <th>Total</th>
          </tr>
        </thead>
        <tbody>
          {bookings.map((b) => (
            <tr key={b.bookingId}>
              <td>{b.bookingId}</td>
              <td>
                {b.roomType} · #{b.roomNumber || "—"}
              </td>
              <td>{b.checkInDate}</td>
              <td>{b.checkOutDate}</td>
              <td>{nairaFormatter.format(b.totalPrice)}</td>
            </tr>
          ))}
          {bookings.length === 0 && (
            <tr>
              <td colSpan={5} className="muted">
                No {statusFilter.toLowerCase()} bookings.
              </td>
            </tr>
          )}
        </tbody>
      </table>
      </div>
    </div>
  );
}

function UsersTab() {
  const [users, setUsers] = useState([]);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");
  const [form, setForm] = useState({ name: "", email: "", phoneNumber: "", password: "", role: "RECEPTIONIST" });

  const load = () => getAllUsers().then(setUsers).catch((err) => setError(err.message));

  useEffect(load, []);

  const handleCreate = async (e) => {
    e.preventDefault();
    setError("");
    setMessage("");
    try {
      await createStaffUser(form);
      setMessage("Staff account created.");
      setForm({ name: "", email: "", phoneNumber: "", password: "", role: "RECEPTIONIST" });
      load();
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div>
      {error && <div className="alert alert-danger">{error}</div>}
      {message && <div className="alert alert-success">{message}</div>}

      <form className="admin-form-inline" onSubmit={handleCreate}>
        <div className="field">
          <label>Name</label>
          <input required value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
        </div>
        <div className="field">
          <label>Email</label>
          <input
            type="email"
            required
            value={form.email}
            onChange={(e) => setForm({ ...form, email: e.target.value })}
          />
        </div>
        <div className="field">
          <label>Phone</label>
          <input
            required
            placeholder="080XXXXXXXX"
            value={form.phoneNumber}
            onChange={(e) => setForm({ ...form, phoneNumber: e.target.value })}
          />
        </div>
        <div className="field">
          <label>Password</label>
          <input
            type="password"
            required
            minLength={8}
            value={form.password}
            onChange={(e) => setForm({ ...form, password: e.target.value })}
          />
        </div>
        <div className="field">
          <label>Role</label>
          <select value={form.role} onChange={(e) => setForm({ ...form, role: e.target.value })}>
            {STAFF_ROLES.map((r) => (
              <option key={r} value={r}>
                {r}
              </option>
            ))}
          </select>
        </div>
        <button type="submit" className="btn btn-primary">
          Create account
        </button>
      </form>

      <div className="admin-table-scroll">
      <table className="admin-table">
        <thead>
          <tr>
            <th>Name</th>
            <th>Email</th>
            <th>Phone</th>
            <th>Role</th>
          </tr>
        </thead>
        <tbody>
          {users.map((u) => (
            <tr key={u.id}>
              <td>{u.name}</td>
              <td>{u.email}</td>
              <td>{u.phoneNumber}</td>
              <td>
                <span className="badge badge-brass">{u.role}</span>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      </div>
    </div>
  );
}
