import { Link } from "react-router-dom";
import { formatNaira } from "../../utils/format";
import Icon from "../ui/Icon";
import "./RoomCard.css";

/** Compact room-type card (home page carousel and similar grids). */
export default function RoomCard({ room, search = "" }) {
  const href = `/rooms/${room.type}${search}`;

  return (
    <article className="room-card">
      <Link to={href} className="room-card-image" tabIndex={-1} aria-hidden="true">
        <img src={room.images[0]} alt="" loading="lazy" />
      </Link>
      <div className="room-card-body">
        <h3>
          <Link to={href}>{room.name}</Link>
        </h3>
        <div className="room-card-meta">
          <span>
            <Icon name="users" size={15} /> {room.guests} Guests
          </span>
          <span>
            <Icon name="bed" size={15} /> {room.beds}
          </span>
        </div>
        <div className="room-card-footer">
          <p className="room-card-price">
            {room.fromPrice != null ? (
              <>
                <strong>{formatNaira(room.fromPrice)}</strong> <span>/ night</span>
              </>
            ) : (
              <span>View rates</span>
            )}
          </p>
          <Link to={href} className="btn btn-outline btn-icon btn-sm" aria-label={`View ${room.name}`}>
            <Icon name="arrowRight" size={16} />
          </Link>
        </div>
      </div>
    </article>
  );
}

export function RoomCardSkeleton() {
  return (
    <div className="room-card" aria-hidden="true">
      <div className="skeleton room-card-image" />
      <div className="room-card-body">
        <div className="skeleton" style={{ height: 18, width: "70%" }} />
        <div className="skeleton" style={{ height: 13, width: "85%" }} />
        <div className="skeleton" style={{ height: 30, marginTop: 8 }} />
      </div>
    </div>
  );
}
