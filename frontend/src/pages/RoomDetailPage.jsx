import { useState } from "react";
import { Link, Navigate, useParams } from "react-router-dom";
import { listRooms } from "../api/rooms";
import BookingPanel from "../components/booking/BookingPanel";
import Alert from "../components/ui/Alert";
import Icon from "../components/ui/Icon";
import Photo from "../components/ui/Photo";
import { SITE } from "../config/site";
import { ROOM_CONTENT, summarizeRoomTypes } from "../content/rooms";
import { useAsync } from "../hooks/useAsync";
import { useDocumentTitle } from "../hooks/useDocumentTitle";
import "./RoomDetailPage.css";

export default function RoomDetailPage() {
  const { roomType } = useParams();
  const content = ROOM_CONTENT[roomType];
  useDocumentTitle(content?.name);
  const rooms = useAsync(listRooms, []);
  const [activeImage, setActiveImage] = useState(0);

  if (!content) return <Navigate to="/rooms" replace />;

  const summary = summarizeRoomTypes(rooms.data || []).find((s) => s.type === roomType);
  const fromPrice = summary?.fromPrice ?? null;
  const bookable = rooms.loading || Boolean(summary?.bookableRooms);

  return (
    <div className="room-detail container">
      <Link to="/rooms" className="back-link">
        <Icon name="arrowLeft" size={16} /> All rooms
      </Link>

      <header className="room-detail-header">
        <div>
          <span className="eyebrow">{content.view}</span>
          <h1>{content.name}</h1>
          <p className="muted">{content.tagline}</p>
        </div>
        {summary && (
          <span className={`badge ${summary.availableNow ? "badge-success" : "badge-info"}`}>
            {summary.availableNow
              ? `${summary.availableNow} of ${summary.totalRooms} free tonight`
              : "Check your dates for availability"}
          </span>
        )}
      </header>

      <div className="room-gallery">
        <div className="room-gallery-main">
          <Photo
            src={content.images[activeImage]}
            alt={`${content.name} — photo ${activeImage + 1}`}
            sizes="(max-width: 960px) 100vw, 1000px"
            priority
          />
        </div>
        <div className="room-gallery-thumbs" role="tablist" aria-label="Room photos">
          {content.images.map((src, i) => (
            <button
              key={src}
              type="button"
              role="tab"
              aria-selected={i === activeImage}
              aria-label={`Show photo ${i + 1}`}
              className={i === activeImage ? "active" : ""}
              onClick={() => setActiveImage(i)}
            >
              <Photo src={src} sizes="(max-width: 960px) 25vw, 130px" />
            </button>
          ))}
        </div>
      </div>

      <div className="room-detail-layout">
        <div className="room-detail-info">
          <ul className="room-facts">
            <li>
              <Icon name="users" size={22} />
              <span>
                <strong>Up to {content.guests}</strong> guests
              </span>
            </li>
            <li>
              <Icon name="bed" size={22} />
              <span>
                <strong>{content.beds}</strong>
              </span>
            </li>
            <li>
              <Icon name="maximize" size={22} />
              <span>
                <strong>{content.size}</strong> room size
              </span>
            </li>
            <li>
              <Icon name="eye" size={22} />
              <span>
                <strong>{content.view}</strong>
              </span>
            </li>
          </ul>

          <section>
            <h2>About this room</h2>
            <p>{content.description}</p>
          </section>

          <section>
            <h2>Amenities</h2>
            <ul className="room-amenities">
              {content.amenities.map((amenity) => (
                <li key={amenity}>
                  <Icon name="check" size={16} /> {amenity}
                </li>
              ))}
            </ul>
          </section>

          <section>
            <h2>Good to know</h2>
            <ul className="room-policies">
              <li>
                <Icon name="clock" size={18} />
                <span>
                  Check-in from <strong>{SITE.checkInTime}</strong>, check-out by <strong>{SITE.checkOutTime}</strong>
                </span>
              </li>
              <li>
                <Icon name="calendarCheck" size={18} />
                <span>Cancel online from My Bookings any time before your stay begins.</span>
              </li>
              <li>
                <Icon name="creditCard" size={18} />
                <span>Pay online after booking — in full or in parts — by card, transfer or cash at the desk.</span>
              </li>
              <li>
                <Icon name="tag" size={18} />
                <span>Weekend nights and all December dates are priced at seasonal rates.</span>
              </li>
            </ul>
          </section>

          {rooms.error && <Alert tone="danger">{rooms.error.message}</Alert>}
        </div>

        <BookingPanel roomType={roomType} room={content} fromPrice={fromPrice} bookable={bookable} />
      </div>
    </div>
  );
}
