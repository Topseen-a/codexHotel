import { useState } from "react";
import { Link } from "react-router-dom";
import { createRoom, deleteRoom, getRoomByNumber, listRooms, listRoomsByStatus, updateRoomStatus } from "../../api/rooms";
import Alert from "../../components/ui/Alert";
import ConfirmDialog from "../../components/ui/ConfirmDialog";
import Dropdown from "../../components/ui/Dropdown";
import EmptyState from "../../components/ui/EmptyState";
import Icon from "../../components/ui/Icon";
import Modal from "../../components/ui/Modal";
import { ROOM_TYPES, roomContent } from "../../content/rooms";
import { useAuth } from "../../context/useAuth";
import { useAsync } from "../../hooks/useAsync";
import { useDocumentTitle } from "../../hooks/useDocumentTitle";
import { formatNaira } from "../../utils/format";
import { ROOM_STATUS } from "../../utils/status";
import AdminHeader from "./AdminHeader";

const FILTERS = ["ALL", ...Object.keys(ROOM_STATUS)];

export default function RoomsAdminPage() {
  useDocumentTitle("Rooms");
  const { can } = useAuth();
  const canManage = can("manageRooms");

  const [filter, setFilter] = useState("ALL");
  const [numberQuery, setNumberQuery] = useState("");
  const [searched, setSearched] = useState(null);
  const rooms = useAsync(
    () => (searched ? getRoomByNumber(searched).then((room) => [room]) : filter === "ALL" ? listRooms() : listRoomsByStatus(filter)),
    [filter, searched]
  );

  const [message, setMessage] = useState("");
  const [actionError, setActionError] = useState("");
  const [creating, setCreating] = useState(false);
  const [toDelete, setToDelete] = useState(null);

  const sorted = [...(rooms.data || [])].sort((a, b) => a.roomNumber - b.roomNumber);

  const search = (event) => {
    event.preventDefault();
    setSearched(numberQuery.trim() || null);
  };

  const clearSearch = () => {
    setNumberQuery("");
    setSearched(null);
  };

  const changeStatus = async (room, status) => {
    setActionError("");
    setMessage("");
    try {
      const updated = await updateRoomStatus(room.id, status);
      // Drop the row if it no longer matches the active status filter.
      rooms.setData((list) =>
        list
          .map((r) => (r.id === updated.id ? updated : r))
          .filter((r) => filter === "ALL" || searched || r.status === filter)
      );
      setMessage(`Room ${updated.roomNumber} is now ${ROOM_STATUS[updated.status].label.toLowerCase()}.`);
    } catch (err) {
      setActionError(err.message);
    }
  };

  const remove = async () => {
    await deleteRoom(toDelete.id);
    rooms.setData((list) => list.filter((r) => r.id !== toDelete.id));
    setMessage(`Room ${toDelete.roomNumber} deleted.`);
  };

  return (
    <>
      <AdminHeader
        title="Rooms"
        description={canManage ? "Add rooms, update their status and remove rooms from inventory." : "Update room status as guests check in and out."}
        actions={
          canManage && (
            <button type="button" className="btn btn-primary btn-sm" onClick={() => setCreating(true)}>
              <Icon name="plus" size={16} /> Add room
            </button>
          )
        }
      />

      <div className="admin-toolbar">
        <div className="chip-group" role="tablist" aria-label="Filter by status">
          {FILTERS.map((key) => (
            <button
              key={key}
              type="button"
              role="tab"
              aria-selected={filter === key && !searched}
              className={`chip ${filter === key && !searched ? "active" : ""}`}
              onClick={() => {
                clearSearch();
                setFilter(key);
              }}
            >
              {key === "ALL" ? "All rooms" : ROOM_STATUS[key].label}
            </button>
          ))}
        </div>
        <form className="admin-search" onSubmit={search} role="search">
          <Icon name="search" size={16} />
          <input
            type="number"
            min="1"
            placeholder="Find room number"
            aria-label="Find room by number"
            value={numberQuery}
            onChange={(e) => setNumberQuery(e.target.value)}
          />
          {searched && (
            <button type="button" className="btn btn-ghost btn-sm" onClick={clearSearch}>
              Clear
            </button>
          )}
        </form>
      </div>

      <Alert tone="success">{message}</Alert>
      <Alert tone="danger">{actionError || rooms.error?.message}</Alert>

      {rooms.loading ? (
        <div className="skeleton" style={{ height: 240 }} />
      ) : (
        !rooms.error && (
          <div className="table-wrap">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Room</th>
                  <th>Type</th>
                  <th>Base price</th>
                  <th>Status</th>
                  <th aria-label="Actions" />
                </tr>
              </thead>
              <tbody>
                {sorted.map((room) => (
                  <tr key={room.id}>
                    <td data-label="Room">
                      <strong>#{room.roomNumber}</strong>
                    </td>
                    <td data-label="Type">{roomContent(room.type).name}</td>
                    <td data-label="Base price">{formatNaira(room.basePrice)}</td>
                    <td data-label="Status">
                      <Dropdown
                        variant="compact"
                        label={`Status of room ${room.roomNumber}`}
                        value={room.status}
                        options={Object.entries(ROOM_STATUS).map(([value, meta]) => ({ value, label: meta.label }))}
                        onChange={(status) => changeStatus(room, status)}
                      />
                    </td>
                    <td data-label="">
                      <div className="cell-actions">
                        <Link to={`/admin/bookings?roomId=${room.id}`} className="btn btn-ghost btn-sm">
                          Bookings
                        </Link>
                        {canManage && (
                          <button
                            type="button"
                            className="btn btn-ghost btn-icon btn-sm"
                            aria-label={`Delete room ${room.roomNumber}`}
                            onClick={() => setToDelete(room)}
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
            {sorted.length === 0 && (
              <EmptyState icon="bed" title="No rooms here">
                {filter === "ALL" ? "Add your first room to start taking bookings." : "No rooms currently have this status."}
              </EmptyState>
            )}
          </div>
        )
      )}

      <CreateRoomModal
        open={creating}
        onClose={() => setCreating(false)}
        onCreated={(room) => {
          setMessage(`Room ${room.roomNumber} added.`);
          clearSearch();
          setFilter("ALL");
          rooms.reload();
        }}
      />

      <ConfirmDialog
        open={Boolean(toDelete)}
        title={`Delete room ${toDelete?.roomNumber}?`}
        confirmLabel="Delete room"
        onConfirm={remove}
        onClose={() => setToDelete(null)}
      >
        The room will be removed from inventory. Existing bookings keep their records but will no longer show a room
        number.
      </ConfirmDialog>
    </>
  );
}

function CreateRoomModal({ open, onClose, onCreated }) {
  const empty = { roomNumber: "", roomType: "STANDARD", basePrice: "", roomStatus: "AVAILABLE" };
  const [form, setForm] = useState(empty);
  const [error, setError] = useState("");
  const [saving, setSaving] = useState(false);

  const close = () => {
    setForm(empty);
    setError("");
    onClose();
  };

  const submit = async (event) => {
    event.preventDefault();
    setError("");
    setSaving(true);
    try {
      const room = await createRoom({
        roomNumber: Number(form.roomNumber),
        roomType: form.roomType,
        basePrice: Number(form.basePrice),
        roomStatus: form.roomStatus,
      });
      onCreated(room);
      close();
    } catch (err) {
      setError(err.message);
    } finally {
      setSaving(false);
    }
  };

  return (
    <Modal
      open={open}
      title="Add a room"
      onClose={close}
      footer={
        <>
          <button type="button" className="btn btn-outline" onClick={close}>
            Cancel
          </button>
          <button type="submit" form="create-room" className="btn btn-primary" disabled={saving}>
            {saving ? "Adding…" : "Add room"}
          </button>
        </>
      }
    >
      <form id="create-room" className="stack" onSubmit={submit}>
        <Alert tone="danger">{error}</Alert>
        <div className="form-grid">
          <div className="field">
            <label htmlFor="room-number">Room number</label>
            <input
              id="room-number"
              type="number"
              min="1"
              required
              value={form.roomNumber}
              onChange={(e) => setForm({ ...form, roomNumber: e.target.value })}
            />
          </div>
          <div className="field">
            <label htmlFor="room-type">Room type</label>
            <Dropdown
              id="room-type"
              variant="field"
              label="Room type"
              value={form.roomType}
              options={ROOM_TYPES.map((type) => ({ value: type, label: roomContent(type).name }))}
              onChange={(roomType) => setForm({ ...form, roomType })}
            />
          </div>
          <div className="field">
            <label htmlFor="room-price">Base price per night (₦)</label>
            <input
              id="room-price"
              type="number"
              min="0"
              step="0.01"
              required
              value={form.basePrice}
              onChange={(e) => setForm({ ...form, basePrice: e.target.value })}
            />
            <span className="field-hint">Seasonal multipliers are applied on top.</span>
          </div>
          <div className="field">
            <label htmlFor="room-status">Initial status</label>
            <Dropdown
              id="room-status"
              variant="field"
              label="Initial status"
              value={form.roomStatus}
              options={Object.entries(ROOM_STATUS).map(([value, meta]) => ({ value, label: meta.label }))}
              onChange={(roomStatus) => setForm({ ...form, roomStatus })}
            />
          </div>
        </div>
      </form>
    </Modal>
  );
}

