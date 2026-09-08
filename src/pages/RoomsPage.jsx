import { useEffect, useMemo, useState } from "react";
import { listRooms } from "../api/rooms";
import RoomCard from "../components/RoomCard";
import "./RoomsPage.css";

export default function RoomsPage() {
  const [rooms, setRooms] = useState([]);
  const [status, setStatus] = useState("loading");
  const [error, setError] = useState("");

  useEffect(() => {
    listRooms()
      .then((data) => {
        setRooms(data);
        setStatus("ready");
      })
      .catch((err) => {
        setError(err.message);
        setStatus("error");
      });
  }, []);

  // Guests book by room type, not a specific room — group listings so each
  // type shows once, with its lowest price and whether anything's free.
  const roomTypeCards = useMemo(() => {
    const byType = {};
    for (const room of rooms) {
      if (!byType[room.type]) {
        byType[room.type] = { ...room, availableCount: 0 };
      }
      if (room.status === "AVAILABLE") {
        byType[room.type].availableCount += 1;
        byType[room.type].status = "AVAILABLE";
      }
      byType[room.type].basePrice = Math.min(byType[room.type].basePrice, room.basePrice);
    }
    return Object.values(byType);
  }, [rooms]);

  return (
    <div>
      <section className="rooms-hero">
        <div className="container">
          <h1>A quiet place to put your bag down.</h1>
          <p>
            Three room types, real availability, and pricing that adjusts for weekends and the festive season —
            browse what's actually open right now.
          </p>
        </div>
      </section>

      <section className="rooms-section container">
        {status === "loading" && (
          <div className="rooms-grid" aria-busy="true" aria-label="Loading rooms">
            {[0, 1, 2].map((i) => (
              <div key={i} className="room-card">
                <div className="skeleton" style={{ aspectRatio: "4 / 3" }} />
                <div className="room-card-body">
                  <div className="skeleton" style={{ height: 20, width: "70%" }} />
                  <div className="skeleton" style={{ height: 14, width: "90%" }} />
                  <div className="skeleton" style={{ height: 36, marginTop: "auto" }} />
                </div>
              </div>
            ))}
          </div>
        )}
        {status === "error" && <div className="alert alert-danger">{error}</div>}

        {status === "ready" && roomTypeCards.length === 0 && (
          <div className="rooms-state">
            <h3>No rooms configured yet</h3>
            <p className="muted">Check back shortly, or ask the front desk to set up room inventory.</p>
          </div>
        )}

        {status === "ready" && roomTypeCards.length > 0 && (
          <div className="rooms-grid">
            {roomTypeCards.map((room) => (
              <RoomCard key={room.type} room={room} />
            ))}
          </div>
        )}
      </section>
    </div>
  );
}
