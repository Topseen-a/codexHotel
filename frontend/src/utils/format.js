const nairaFormatter = new Intl.NumberFormat("en-NG", {
  style: "currency",
  currency: "NGN",
  minimumFractionDigits: 0,
  maximumFractionDigits: 2,
});

export const formatNaira = (amount) => nairaFormatter.format(Number(amount) || 0);

const dateFormatter = new Intl.DateTimeFormat("en-GB", { day: "numeric", month: "short", year: "numeric" });
const shortDateFormatter = new Intl.DateTimeFormat("en-GB", { day: "numeric", month: "short" });

/** Parses a backend yyyy-mm-dd date as a local calendar date (no timezone shift). */
export function parseISODate(iso) {
  if (!iso) return null;
  const [year, month, day] = iso.split("-").map(Number);
  return new Date(year, month - 1, day);
}

export function toISODate(date) {
  const y = date.getFullYear();
  const m = String(date.getMonth() + 1).padStart(2, "0");
  const d = String(date.getDate()).padStart(2, "0");
  return `${y}-${m}-${d}`;
}

export const todayISO = () => toISODate(new Date());

export function addDaysISO(iso, days) {
  const date = parseISODate(iso);
  date.setDate(date.getDate() + days);
  return toISODate(date);
}

export const formatDate = (iso) => (iso ? dateFormatter.format(parseISODate(iso)) : "—");
export const formatShortDate = (iso) => (iso ? shortDateFormatter.format(parseISODate(iso)) : "—");

export function nightsBetween(checkIn, checkOut) {
  if (!checkIn || !checkOut) return 0;
  const ms = parseISODate(checkOut) - parseISODate(checkIn);
  return Math.max(0, Math.round(ms / 86_400_000));
}

/** Every night of a stay as ISO dates: check-in inclusive, check-out exclusive. */
export function stayNights(checkIn, checkOut) {
  const nights = [];
  for (let i = 0; i < nightsBetween(checkIn, checkOut); i += 1) {
    nights.push(addDaysISO(checkIn, i));
  }
  return nights;
}

export const pluralize = (count, word) => `${count} ${word}${count === 1 ? "" : "s"}`;

export const titleCase = (value = "") => value.charAt(0) + value.slice(1).toLowerCase();

export function initials(name = "") {
  return (
    name
      .split(" ")
      .filter(Boolean)
      .slice(0, 2)
      .map((part) => part[0].toUpperCase())
      .join("") || "?"
  );
}

export const shortId = (id = "") => (id.length > 8 ? `…${id.slice(-8)}` : id);
