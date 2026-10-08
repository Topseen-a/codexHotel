import { Link } from "react-router-dom";
import { listRooms } from "../../api/rooms";
import { ROOM_TYPES, roomContent, summarizeRoomTypes } from "../../content/rooms";
import { useAsync } from "../../hooks/useAsync";
import RoomCard, { RoomCardSkeleton } from "../rooms/RoomCard";
import Icon from "../ui/Icon";

export default function FeaturedRooms() {
  const { data: rooms, loading, error } = useAsync(listRooms, []);

  // If the API is unreachable (e.g. a cold start), still show the room types — just without live prices.
  const cards = rooms?.length
    ? summarizeRoomTypes(rooms)
    : ROOM_TYPES.map((type) => ({ type, ...roomContent(type), fromPrice: null }));

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
          {loading && !error
            ? ROOM_TYPES.map((type) => <RoomCardSkeleton key={type} />)
            : cards.map((room) => <RoomCard key={room.type} room={room} />)}
        </div>
      </div>
    </section>
  );
}
