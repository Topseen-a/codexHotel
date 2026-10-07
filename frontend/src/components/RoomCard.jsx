import { Link } from "react-router-dom";
import { contentFor } from "../content/roomContent";
import "./RoomCard.css";

const nairaFormatter = new Intl.NumberFormat("en-NG", {
  style: "currency",
  currency: "NGN",
  maximumFractionDigits: 0,
});

export default function RoomCard({ room }) {
  const content = contentFor(room.type);
  const isAvailable = room.status === "AVAILABLE";

  return (
    <article className="room-card">
      <div className="room-card-image">
        <img src={content.images[0]} alt={content.label} loading="lazy" />
        {!isAvailable && <span className="room-card-unavailable">Currently {room.status.toLowerCase()}</span>}
      </div>
      <div className="room-card-body">
        <div className="spread">
          <h3>{content.label}</h3>
          <span className="room-card-price">
            {nairaFormatter.format(room.basePrice)}
            <small>/ night</small>
          </span>
        </div>
        <p className="muted room-card-tagline">{content.tagline}</p>
        <ul className="room-card-amenities">
          {content.amenities.slice(0, 3).map((a) => (
            <li key={a}>{a}</li>
          ))}
        </ul>
        <Link to={`/rooms/${room.type}`} className="btn btn-dark btn-block">
          View room
        </Link>
      </div>
    </article>
  );
}
