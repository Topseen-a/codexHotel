import { BOOKING_STATUS, ROOM_STATUS } from "../../utils/status";

const SOURCES = { booking: BOOKING_STATUS, room: ROOM_STATUS };

export default function StatusBadge({ kind = "booking", status }) {
  const meta = SOURCES[kind]?.[status] || { label: status, tone: "neutral" };
  return <span className={`badge badge-${meta.tone}`}>{meta.label}</span>;
}

export function PaymentBadge({ successful }) {
  return successful ? (
    <span className="badge badge-success">Successful</span>
  ) : (
    <span className="badge badge-warning">Pending</span>
  );
}
