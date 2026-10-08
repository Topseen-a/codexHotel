import { unsplash } from "./images";

export const GALLERY_CATEGORIES = ["All", "Resort", "Rooms", "Dining & Spa", "Surroundings"];

export const GALLERY = [
  { id: "1540541338287-41700207dee6", category: "Resort", alt: "Cliffside infinity pool over the ocean", wide: true },
  { id: "1578683010236-d716f9a3f461", category: "Rooms", alt: "Signature Suite bedroom and glass bathroom" },
  { id: "1544161515-4ab6ce6db874", category: "Dining & Spa", alt: "Massage treatment at the spa" },
  { id: "1573843981267-be1999ff37cd", category: "Surroundings", alt: "Overwater villas on a turquoise lagoon", tall: true },
  { id: "1566073771259-6a8506099945", category: "Resort", alt: "Wooden pool deck with loungers" },
  { id: "1582719478250-c89cae4dc85b", category: "Rooms", alt: "Ocean View Deluxe room with garden outlook" },
  { id: "1414235077428-338989a2e8c0", category: "Dining & Spa", alt: "Plated dinner at the restaurant" },
  { id: "1519046904884-53103b34b206", category: "Surroundings", alt: "Palm-lined white sand beach", wide: true },
  { id: "1600011689032-8b628b8a8747", category: "Resort", alt: "Pool at sunset framed by palm trees" },
  { id: "1631049307264-da0ec9d70304", category: "Rooms", alt: "Bright Standard Room with king bed" },
  { id: "1584132967334-10e028bd69f7", category: "Resort", alt: "Infinity pool deck overlooking the sea", tall: true },
  { id: "1507525428034-b723cf961d3e", category: "Surroundings", alt: "Sunrise over a calm beach" },
  { id: "1602002418082-a4443e081dd1", category: "Rooms", alt: "Suite with panoramic lagoon windows" },
  { id: "1571896349842-33c89424de2d", category: "Resort", alt: "Villa and pool lit up at dusk" },
  { id: "1499793983690-e29da59ef1c2", category: "Surroundings", alt: "Beach cabana on the water's edge" },
  { id: "1631889993959-41b4e9c6e3c5", category: "Rooms", alt: "Marble bathroom with soaking tub" },
].map((photo) => ({ ...photo, src: unsplash(photo.id, photo.wide ? 1400 : 900) }));
