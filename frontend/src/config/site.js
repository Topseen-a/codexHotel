// Brand and contact details shown across the site. Replace with the
// property's real details before launch.

export const SITE = {
  name: "CodexHotel",
  subtitle: "Hotels & Resorts",
  tagline: "Hotels & Resorts",
  location: "Lekki Peninsula, Lagos",
  city: "Lagos",
  address: "12 Admiralty Way, Lekki Phase 1, Lagos, Nigeria",
  phone: "+234 800 000 0000",
  email: "reservations@codexhotel.com",
  checkInTime: "2:00 PM",
  checkOutTime: "12:00 PM",
  socials: [
    { label: "Instagram", icon: "instagram", href: "https://instagram.com" },
    { label: "Facebook", icon: "facebook", href: "https://facebook.com" },
  ],
};

/** The name as shown in the logo and headlines: "CodexHotel" -> "Codex Hotel". */
export const BRAND_WORDMARK = SITE.name.replace(/([a-z])([A-Z])/, "$1 $2");

export const NAV_LINKS = [
  { label: "Home", to: "/" },
  { label: "Rooms", to: "/rooms" },
  { label: "Experiences", to: "/experiences" },
  { label: "Gallery", to: "/gallery" },
  { label: "About", to: "/about" },
  { label: "Contact", to: "/contact" },
];
