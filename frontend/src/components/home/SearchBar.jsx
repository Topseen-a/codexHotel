import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { ROOM_TYPES, roomContent } from "../../content/rooms";
import { addDaysISO, todayISO } from "../../utils/format";
import DatePicker from "../ui/DatePicker";
import Dropdown from "../ui/Dropdown";
import Icon from "../ui/Icon";
import "./SearchBar.css";

const ROOM_OPTIONS = [
  { value: "", label: "Any room type", hint: "Show every room" },
  ...ROOM_TYPES.map((type) => {
    const room = roomContent(type);
    return { value: type, label: room.name, hint: `Up to ${room.guests} guests · ${room.beds}` };
  }),
];

const GUEST_OPTIONS = [1, 2, 3, 4].map((n) => ({
  value: n,
  label: `${n} ${n === 1 ? "Adult" : "Adults"}`,
}));

/** Hero availability search — hands dates and guests to the rooms pages via the URL. */
export default function SearchBar() {
  const navigate = useNavigate();
  const today = todayISO();
  const [form, setForm] = useState({
    roomType: "",
    checkIn: today,
    checkOut: addDaysISO(today, 1),
    guests: 2,
  });

  const setField = (field, value) =>
    setForm((prev) => {
      const next = { ...prev, [field]: value };
      if (field === "checkIn" && next.checkOut <= value) next.checkOut = addDaysISO(value, 1);
      return next;
    });

  const submit = (event) => {
    event.preventDefault();
    const params = new URLSearchParams({ checkIn: form.checkIn, checkOut: form.checkOut, guests: form.guests });
    navigate(form.roomType ? `/rooms/${form.roomType}?${params}` : `/rooms?${params}`);
  };

  return (
    <form className="search-bar" onSubmit={submit} aria-label="Search rooms">
      <div className="search-field">
        <Icon name="bed" size={20} />
        <Dropdown
          label="Room"
          value={form.roomType}
          options={ROOM_OPTIONS}
          onChange={(value) => setField("roomType", value)}
        />
      </div>

      <div className="search-field">
        <Icon name="calendar" size={20} />
        <DatePicker
          variant="inline"
          label="Check in"
          min={today}
          value={form.checkIn}
          onChange={(value) => setField("checkIn", value)}
        />
      </div>

      <div className="search-field">
        <Icon name="calendar" size={20} />
        <DatePicker
          variant="inline"
          label="Check out"
          min={addDaysISO(form.checkIn, 1)}
          rangeStart={form.checkIn}
          value={form.checkOut}
          onChange={(value) => setField("checkOut", value)}
        />
      </div>

      <div className="search-field">
        <Icon name="users" size={20} />
        <Dropdown
          label="Guests"
          value={Number(form.guests)}
          options={GUEST_OPTIONS}
          onChange={(value) => setField("guests", value)}
        />
      </div>

      <button type="submit" className="btn btn-primary search-submit">
        Search Rooms <Icon name="arrowRight" size={16} />
      </button>
    </form>
  );
}
