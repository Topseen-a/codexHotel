import { Link, useSearchParams } from "react-router-dom";
import { listRooms } from "../api/rooms";
import { getPriceList } from "../api/pricing";
import Alert from "../components/ui/Alert";
import DatePicker from "../components/ui/DatePicker";
import Dropdown from "../components/ui/Dropdown";
import EmptyState from "../components/ui/EmptyState";
import Icon from "../components/ui/Icon";
import Photo from "../components/ui/Photo";
import { backgroundFor, IMAGES } from "../content/images";
import { ROOM_TYPES, roomContent, summarizeRoomTypes } from "../content/rooms";
import { useAsync } from "../hooks/useAsync";
import { useDocumentTitle } from "../hooks/useDocumentTitle";
import { addDaysISO, formatNaira, pluralize, todayISO } from "../utils/format";
import { SEASONS } from "../utils/status";
import "./RoomsPage.css";

export default function RoomsPage() {
  useDocumentTitle("Rooms & Suites");
  const [searchParams, setSearchParams] = useSearchParams();
  const rooms = useAsync(listRooms, []);
  const prices = useAsync(getPriceList, []);

  const today = todayISO();
  const checkIn = searchParams.get("checkIn") || "";
  const checkOut = searchParams.get("checkOut") || "";
  const guests = Number(searchParams.get("guests")) || 1;

  const setParam = (key, value) => {
    const next = new URLSearchParams(searchParams);
    if (value) next.set(key, value);
    else next.delete(key);
    if (key === "checkIn" && value && (!checkOut || checkOut <= value)) next.set("checkOut", addDaysISO(value, 1));
    setSearchParams(next, { replace: true });
  };

  const summaries = summarizeRoomTypes(rooms.data || []);
  const suitable = summaries.filter((room) => room.guests >= guests);
  const hiddenCount = summaries.length - suitable.length;
  const query = searchParams.toString() ? `?${searchParams}` : "";

  return (
    <>
      <section className="page-hero" style={{ backgroundImage: `url(${backgroundFor(IMAGES.rooms)})` }}>
        <div className="container">
          <span className="eyebrow">Rooms &amp; suites</span>
          <h1>Find Your Room</h1>
          <p>Live availability and pricing that adjusts for weekends and the festive season.</p>
        </div>
      </section>

      <div className="container rooms-page">
        <form className="rooms-filter card" onSubmit={(e) => e.preventDefault()} aria-label="Filter rooms">
          <div className="field">
            <label htmlFor="f-checkin">Check in</label>
            <DatePicker
              id="f-checkin"
              label="Check in"
              min={today}
              value={checkIn}
              placeholder="Any date"
              clearable
              onChange={(value) => setParam("checkIn", value)}
            />
          </div>
          <div className="field">
            <label htmlFor="f-checkout">Check out</label>
            <DatePicker
              id="f-checkout"
              label="Check out"
              min={checkIn ? addDaysISO(checkIn, 1) : addDaysISO(today, 1)}
              rangeStart={checkIn}
              value={checkOut}
              placeholder="Any date"
              clearable
              onChange={(value) => setParam("checkOut", value)}
            />
          </div>
          <div className="field">
            <label htmlFor="f-guests">Guests</label>
            <Dropdown
              id="f-guests"
              variant="field"
              label="Guests"
              value={guests}
              options={[1, 2, 3, 4].map((n) => ({ value: n, label: pluralize(n, "guest") }))}
              onChange={(value) => setParam("guests", String(value))}
            />
          </div>
          {searchParams.toString() && (
            <button type="button" className="btn btn-ghost" onClick={() => setSearchParams({}, { replace: true })}>
              Clear
            </button>
          )}
        </form>

        {rooms.error && <Alert tone="danger">{rooms.error.message}</Alert>}

        <div className="room-list">
          {rooms.loading &&
            ROOM_TYPES.map((type) => (
              <div key={type} className="room-row card" aria-hidden="true">
                <div className="skeleton room-row-image" />
                <div className="room-row-body stack">
                  <div className="skeleton" style={{ height: 26, width: "45%" }} />
                  <div className="skeleton" style={{ height: 14, width: "90%" }} />
                  <div className="skeleton" style={{ height: 14, width: "70%" }} />
                </div>
              </div>
            ))}

          {!rooms.loading && !rooms.error && summaries.length === 0 && (
            <EmptyState icon="bed" title="No rooms configured yet">
              Check back shortly — our team is setting up room inventory.
            </EmptyState>
          )}

          {suitable.map((room) => (
            <article key={room.type} className="room-row card">
              <Link to={`/rooms/${room.type}${query}`} className="room-row-image" tabIndex={-1} aria-hidden="true">
                <Photo src={room.images[0]} sizes="(max-width: 860px) 100vw, 42vw" />
              </Link>
              <div className="room-row-body">
                <div className="room-row-head">
                  <div>
                    <h2>
                      <Link to={`/rooms/${room.type}${query}`}>{room.name}</Link>
                    </h2>
                    <p className="muted">{room.tagline}</p>
                  </div>
                  {room.availableNow > 0 ? (
                    <span className="badge badge-success">{pluralize(room.availableNow, "room")} free tonight</span>
                  ) : room.bookableRooms > 0 ? (
                    <span className="badge badge-info">Book for future dates</span>
                  ) : (
                    <span className="badge badge-warning">Temporarily closed</span>
                  )}
                </div>

                <ul className="room-row-meta">
                  <li>
                    <Icon name="users" size={16} /> Up to {room.guests} guests
                  </li>
                  <li>
                    <Icon name="bed" size={16} /> {room.beds}
                  </li>
                  <li>
                    <Icon name="maximize" size={16} /> {room.size}
                  </li>
                  <li>
                    <Icon name="eye" size={16} /> {room.view}
                  </li>
                </ul>

                <p className="room-row-description">{room.description}</p>

                <ul className="room-row-amenities">
                  {room.amenities.slice(0, 5).map((amenity) => (
                    <li key={amenity}>{amenity}</li>
                  ))}
                </ul>

                <div className="room-row-footer">
                  <p>
                    <span className="faint">From</span> <strong>{formatNaira(room.fromPrice)}</strong>{" "}
                    <span className="faint">/ night</span>
                  </p>
                  <Link to={`/rooms/${room.type}${query}`} className="btn btn-primary">
                    View &amp; Book <Icon name="arrowRight" size={16} />
                  </Link>
                </div>
              </div>
            </article>
          ))}

          {hiddenCount > 0 && (
            <p className="muted rooms-hidden-note">
              {pluralize(hiddenCount, "room type")} hidden because {hiddenCount === 1 ? "it sleeps" : "they sleep"} fewer
              than {guests} guests.
            </p>
          )}
        </div>

        <section className="rates card card-pad" aria-labelledby="rates-title">
          <div className="section-heading">
            <span className="eyebrow">Seasonal rates</span>
            <h2 id="rates-title">How our pricing works</h2>
            <p>Each night is priced by its season. Here are our published nightly rates by room type.</p>
          </div>
          {prices.error && <Alert tone="danger">{prices.error.message}</Alert>}
          {prices.data && <RatesTable prices={prices.data} />}
          {prices.loading && <div className="skeleton" style={{ height: 160 }} />}
        </section>
      </div>
    </>
  );
}

function RatesTable({ prices }) {
  const seasons = Object.keys(SEASONS);
  const priceFor = (type, season) => prices.find((p) => p.roomType === type && p.season === season)?.price;

  return (
    <div className="table-wrap">
      <table className="data-table">
        <thead>
          <tr>
            <th>Room</th>
            {seasons.map((season) => (
              <th key={season}>
                {SEASONS[season].label} <span className="rates-hint">{SEASONS[season].hint}</span>
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {ROOM_TYPES.map((type) => (
            <tr key={type}>
              <td data-label="Room">
                <strong>{roomContent(type).name}</strong>
              </td>
              {seasons.map((season) => {
                const price = priceFor(type, season);
                return (
                  <td key={season} data-label={SEASONS[season].label}>
                    {price != null ? formatNaira(price) : "—"}
                  </td>
                );
              })}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
