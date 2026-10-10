import { Link } from "react-router-dom";
import { formatNaira } from "../../utils/format";
import Icon from "../ui/Icon";
import Photo from "../ui/Photo";
import "./RoomCard.css";

/** Compact room-type card (home page carousel and similar grids). */
export default function RoomCard({ room, search = "", priority = false }) {
  const href = `/rooms/${room.type}${search}`;

  return (
    <article className="room-card">
      <Link to={href} className="room-card-image" tabIndex={-1} aria-hidden="true">
        <Photo
          src={room.images[0]}
          sizes="(max-width: 760px) min(78vw, 320px), (max-width: 960px) 33vw, 280px"
          priority={priority}
        />
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
            {room.priceLoading ? (
              <span className="skeleton room-card-price-skeleton" aria-label="Loading price" />
            ) : room.fromPrice != null ? (
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
