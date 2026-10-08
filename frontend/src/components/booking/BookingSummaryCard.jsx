import { Link } from "react-router-dom";
import { roomContent } from "../../content/rooms";
import { formatDate, formatNaira, nightsBetween, pluralize } from "../../utils/format";
import Icon from "../ui/Icon";
import StatusBadge from "../ui/StatusBadge";

/** One booking in a list: photo, room, dates and total, linking to its detail page. */
export default function BookingSummaryCard({ booking }) {
  const room = roomContent(booking.roomType);
  const nights = nightsBetween(booking.checkInDate, booking.checkOutDate);

  return (
    <article className="booking-card card">
      <div className="booking-card-image">
        <img src={room.images[0]} alt="" loading="lazy" />
      </div>
      <div className="booking-card-body">
        <div className="spread" style={{ alignItems: "flex-start" }}>
          <div>
            <h3>{room.name}</h3>
            <p className="faint">Room {booking.roomNumber || "—"}</p>
          </div>
          <StatusBadge status={booking.status} />
        </div>
        <div className="booking-card-dates">
          <span>
            <Icon name="calendar" size={15} /> {formatDate(booking.checkInDate)} → {formatDate(booking.checkOutDate)}
          </span>
          <span>
            <Icon name="clock" size={15} /> {pluralize(nights, "night")}
          </span>
        </div>
        <div className="booking-card-footer">
          <strong>{formatNaira(booking.totalPrice)}</strong>
          <Link to={`/bookings/${booking.bookingId}`} className="btn btn-outline btn-sm">
            View details <Icon name="arrowRight" size={14} />
          </Link>
        </div>
      </div>
    </article>
  );
}
