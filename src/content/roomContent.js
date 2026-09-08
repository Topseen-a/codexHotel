// The backend only stores roomNumber/type/basePrice/status — no marketing
// copy or photography. This maps each RoomType to the display content a
// booking site actually needs. Images are curated real hotel-room
// photography (Unsplash) — swap for actual property photos whenever
// they exist.

export const ROOM_CONTENT = {
  STANDARD: {
    label: "Standard Room",
    tagline: "Calm and uncomplicated",
    description:
      "A well-lit room with a queen bed, a work desk, and everything you need for a straightforward stay — nothing you don't.",
    amenities: ["Free Wi-Fi", "Air conditioning", "Daily housekeeping", "Work desk", "Free cancellation"],
    images: [
      "https://images.unsplash.com/photo-1595576508898-0ad5c879a061?auto=format&fit=crop&w=1200&h=800&q=80",
      "https://images.unsplash.com/photo-1512918728675-ed5a9ecdebfd?auto=format&fit=crop&w=1200&h=800&q=80",
      "https://images.unsplash.com/photo-1560185893-a55cbc8c57e8?auto=format&fit=crop&w=1200&h=800&q=80",
      "https://images.unsplash.com/photo-1611892440504-42a792e24d32?auto=format&fit=crop&w=1200&h=800&q=80",
    ],
  },
  DELUXE: {
    label: "Deluxe Room",
    tagline: "More room to settle in",
    description:
      "A larger room with a separate seating area and better light — for stays where a little more comfort is worth it.",
    amenities: ["Free Wi-Fi", "Air conditioning", "Mini bar", "Lounge seating", "City view", "Free cancellation"],
    images: [
      "https://images.unsplash.com/photo-1631049307264-da0ec9d70304?auto=format&fit=crop&w=1200&h=800&q=80",
      "https://images.unsplash.com/photo-1566665797739-1674de7a421a?auto=format&fit=crop&w=1200&h=800&q=80",
      "https://images.unsplash.com/photo-1590490360182-c33d57733427?auto=format&fit=crop&w=1200&h=800&q=80",
      "https://images.unsplash.com/photo-1631889993959-41b4e9c6e3c5?auto=format&fit=crop&w=1200&h=800&q=80",
    ],
  },
  SUITE: {
    label: "Executive Suite",
    tagline: "Our most attentive stay",
    description:
      "A separate living area, premium furnishings, and 24-hour room service — the full range of what the hotel offers.",
    amenities: [
      "Free Wi-Fi",
      "Air conditioning",
      "Private lounge",
      "Soaking tub",
      "24-hour room service",
      "Complimentary breakfast",
      "Free cancellation",
    ],
    images: [
      "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?auto=format&fit=crop&w=1200&h=800&q=80",
      "https://images.unsplash.com/photo-1591088398332-8a7791972843?auto=format&fit=crop&w=1200&h=800&q=80",
      "https://images.unsplash.com/photo-1616594039964-ae9021a400a0?auto=format&fit=crop&w=1200&h=800&q=80",
      "https://images.unsplash.com/photo-1568495248636-6432b97bd949?auto=format&fit=crop&w=1200&h=800&q=80",
    ],
  },
};

export function contentFor(roomType) {
  return ROOM_CONTENT[roomType] || ROOM_CONTENT.STANDARD;
}
