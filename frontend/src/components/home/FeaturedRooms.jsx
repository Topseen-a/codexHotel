import { Link } from "react-router-dom";
import { listRooms } from "../../api/rooms";
import { ROOM_TYPES, roomContent, summarizeRoomTypes } from "../../content/rooms";
import { useAsync } from "../../hooks/useAsync";
import RoomCard from "../rooms/RoomCard";
import Icon from "../ui/Icon";

export default function FeaturedRooms() {
  const { data: rooms, loading } = useAsync(listRooms, []);

  // Photos and copy are static, so render the cards straight away and let the
  // photos start downloading; only live prices wait for the API (which can be
  // slow to wake on a cold start). Once loaded, hide types with no rooms.
  const summaries = rooms?.length ? summarizeRoomTypes(rooms) : null;
  const cards = ROOM_TYPES.map((type) => {
    const summary = summaries?.find((s) => s.type === type);
    if (summaries && !summary) return null;
    return { type, ...roomContent(type), fromPrice: summary?.fromPrice ?? null, priceLoading: loading };
  }).filter(Boolean);

  return (
    <section className="section featured-rooms" id="rooms">
      <div className="container featured-rooms-layout">
        <div className="section-heading featured-rooms-intro">
          <span className="eyebrow">Featured stays</span>
          <h2>Exquisite Rooms &amp; Suites</h2>
          <p>Thoughtfully designed for your comfort — our rooms blend modern luxury with natural beauty.</p>
          <Link to="/rooms" className="btn btn-outline">
            View All Rooms <Icon name="arrowRight" size={16} />
          </Link>
        </div>

        <div className="featured-rooms-track">
          {cards.map((room, index) => (
            <RoomCard key={room.type} room={room} priority={index === 0} />
          ))}
        </div>
      </div>
    </section>
  );
}
