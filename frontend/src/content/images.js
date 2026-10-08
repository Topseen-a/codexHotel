// Stock photography (Unsplash licence) — swap for the property's own photos
// whenever they exist.

const unsplash = (id, width = 1600, height) =>
  `https://images.unsplash.com/photo-${id}?auto=format&fit=crop&w=${width}${height ? `&h=${height}` : ""}&q=75`;

export const IMAGES = {
  hero: unsplash("1540541338287-41700207dee6", 2000),
  heroMobile: unsplash("1540541338287-41700207dee6", 900, 1300),
  // Warm, wood-and-lamplight shots that sit in the espresso/bronze palette.
  heroTall: unsplash("1611892440504-42a792e24d32", 1100, 1500),
  heroThumbs: [
    unsplash("1600011689032-8b628b8a8747", 240, 300),
    unsplash("1566073771259-6a8506099945", 240, 300),
    unsplash("1590381105924-c72589b9ef3f", 240, 300),
  ],
  beachResort: unsplash("1600011689032-8b628b8a8747", 1400),
  advantage: unsplash("1584132967334-10e028bd69f7", 1200),
  promo: unsplash("1445019980597-93fa8acb246c", 2000),
  auth: unsplash("1600011689032-8b628b8a8747", 1400),
  rooms: unsplash("1582719478250-c89cae4dc85b", 2000),
  gallery: unsplash("1573843981267-be1999ff37cd", 2000),
  about: unsplash("1566073771259-6a8506099945", 2000),
  aboutStory: unsplash("1564501049412-61c2a3083791", 1200),
  contact: unsplash("1519046904884-53103b34b206", 2000),
  experiences: unsplash("1600011689032-8b628b8a8747", 2000),
  account: unsplash("1520250497591-112f2f40a3f4", 2000),
};

export { unsplash };
