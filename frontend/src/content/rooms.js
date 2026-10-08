import { unsplash } from "./images";

// The backend stores only roomNumber / type / basePrice / status per room.
// Everything a guest needs to choose a room — copy, photos, capacity,
// amenities — lives here, keyed by RoomType.

export const ROOM_TYPES = ["STANDARD", "DELUXE", "SUITE"];

export const ROOM_CONTENT = {
  STANDARD: {
    name: "Standard Room",
    tagline: "Calm, bright and uncomplicated",
    description:
      "A light-filled room with a king bed, a writing desk and a rain shower — everything you need for an easy stay, and nothing you don't.",
    guests: 2,
    beds: "1 King Bed",
    size: "28 m²",
    view: "Garden view",
    amenities: ["Free Wi-Fi", "Air conditioning", "Smart TV", "Rain shower", "Work desk", "Daily housekeeping"],
    images: [
      unsplash("1631049307264-da0ec9d70304", 1400),
      unsplash("1595576508898-0ad5c879a061", 1400),
      unsplash("1512918728675-ed5a9ecdebfd", 1400),
      unsplash("1584622650111-993a426fbf0a", 1400),
    ],
  },
  DELUXE: {
    name: "Ocean View Deluxe",
    tagline: "More space, wider views",
    description:
      "A larger room with floor-to-ceiling windows over the water, a lounge corner and a deep soaking tub — for stays where a little extra comfort matters.",
    guests: 3,
    beds: "1 King Bed + Sofa",
    size: "38 m²",
    view: "Ocean view",
    amenities: [
      "Free Wi-Fi",
      "Air conditioning",
      "Smart TV",
      "Soaking tub",
      "Mini bar",
      "Lounge seating",
      "Daily housekeeping",
    ],
    images: [
      unsplash("1582719478250-c89cae4dc85b", 1400),
      unsplash("1568495248636-6432b97bd949", 1400),
      unsplash("1566665797739-1674de7a421a", 1400),
      unsplash("1631889993959-41b4e9c6e3c5", 1400),
    ],
  },
  SUITE: {
    name: "Signature Suite",
    tagline: "Our most generous stay",
    description:
      "A separate living room, a private terrace and a spa-style bathroom, with breakfast included and 24-hour in-suite dining — the full CodexHotel experience.",
    guests: 4,
    beds: "2 King Beds",
    size: "64 m²",
    view: "Panoramic ocean view",
    amenities: [
      "Free Wi-Fi",
      "Air conditioning",
      "Private terrace",
      "Spa bathroom",
      "Breakfast included",
      "24-hour in-suite dining",
      "Butler service",
      "Daily housekeeping",
    ],
    images: [
      unsplash("1578683010236-d716f9a3f461", 1400),
      unsplash("1560448204-e02f11c3d0e2", 1400),
      unsplash("1602002418082-a4443e081dd1", 1400),
      unsplash("1591088398332-8a7791972843", 1400),
    ],
  },
};

export const roomContent = (type) => ROOM_CONTENT[type] || ROOM_CONTENT.STANDARD;

/**
 * Collapses individual rooms into one summary per room type — guests book a
 * type, and the backend assigns a free room of that type for their dates.
 */
export function summarizeRoomTypes(rooms = []) {
  return ROOM_TYPES.map((type) => {
    const ofType = rooms.filter((room) => room.type === type);
    if (ofType.length === 0) return null;
    const bookable = ofType.filter((room) => room.status !== "MAINTENANCE");
    const priced = bookable.length ? bookable : ofType;
    return {
      type,
      ...roomContent(type),
      fromPrice: Math.min(...priced.map((room) => Number(room.basePrice))),
      totalRooms: ofType.length,
      availableNow: ofType.filter((room) => room.status === "AVAILABLE").length,
      bookableRooms: bookable.length,
    };
  }).filter(Boolean);
}
