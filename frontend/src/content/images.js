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

const STEPS = [320, 480, 640, 800, 1080, 1400, 1800, 2400];

/** Rewrites an Unsplash URL to another width, scaling a fixed height (crop) proportionally. */
function resizeUnsplash(url, width) {
  const params = new URL(url).searchParams;
  const sourceWidth = Number(params.get("w"));
  const sourceHeight = Number(params.get("h"));
  let next = url.replace(/([?&])w=\d+/, `$1w=${width}`);
  if (sourceWidth && sourceHeight) {
    next = next.replace(/([?&])h=\d+/, `$1h=${Math.round((sourceHeight * width) / sourceWidth)}`);
  }
  return next;
}

/** `srcset` for an Unsplash photo: the same image at several widths, up to its original size. */
export function srcSetFor(url) {
  if (!url?.includes("images.unsplash.com")) return undefined;
  const maxWidth = Number(new URL(url).searchParams.get("w")) || 1600;
  return STEPS.filter((w) => w < maxWidth)
    .concat(maxWidth)
    .map((w) => `${resizeUnsplash(url, w)} ${w}w`)
    .join(", ");
}

/**
 * For CSS background photos (which can't use srcset): pick a width that covers
 * the current screen, so phones don't download desktop-sized images.
 */
export function backgroundFor(url) {
  if (!url?.includes("images.unsplash.com") || typeof window === "undefined") return url;
  const maxWidth = Number(new URL(url).searchParams.get("w")) || 2000;
  const needed = window.innerWidth * Math.min(window.devicePixelRatio || 1, 2);
  const width = STEPS.find((w) => w >= needed) || STEPS[STEPS.length - 1];
  return resizeUnsplash(url, Math.min(width, maxWidth));
}
