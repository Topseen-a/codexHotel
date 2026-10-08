import { unsplash } from "./images";

// Experience copy is illustrative — align it with the services the property
// actually offers before launch.
export const EXPERIENCES = [
  {
    slug: "spa",
    title: "Spa & Wellness",
    description: "Relax, rejuvenate",
    long: "Massage suites, a steam room and an oceanfront treatment deck — book at the spa desk or ask reception.",
    intro:
      "Slow the pace right down. Our spa pairs ocean air with time-honoured treatments, from deep-tissue massage to shea-butter body rituals inspired by West African traditions.",
    highlights: ["Private massage suites for one or two", "Steam room and cold plunge", "Oceanfront treatment deck at sunset"],
    image: unsplash("1544161515-4ab6ce6db874", 800),
    imageLarge: unsplash("1544161515-4ab6ce6db874", 1400),
  },
  {
    slug: "dining",
    title: "Culinary Journey",
    description: "Taste the world",
    long: "Three restaurants and a beach bar serving coastal Nigerian dishes alongside a seasonal international menu.",
    intro:
      "From suya by the fire pit to a slow-cooked seafood feast, our kitchens celebrate coastal Nigerian flavours next to a seasonal international menu — with a beach bar for the golden hour.",
    highlights: ["Three restaurants and a beach bar", "Breakfast served on the terrace", "Private dining on the sand by arrangement"],
    image: unsplash("1414235077428-338989a2e8c0", 800),
    imageLarge: unsplash("1414235077428-338989a2e8c0", 1400),
  },
  {
    slug: "adventure",
    title: "Adventure Tours",
    description: "Explore more",
    long: "Boat trips, kayaking and guided island hops arranged by our concierge team.",
    intro:
      "The lagoon and the Atlantic are right on the doorstep. Our concierge team arranges boat trips, kayaking and guided island hops — at your pace, with local guides who know every cove.",
    highlights: ["Boat trips and island hopping", "Kayaking on the lagoon", "Guided half- and full-day excursions"],
    image: unsplash("1506929562872-bb421503ef21", 800),
    imageLarge: unsplash("1506929562872-bb421503ef21", 1400),
  },
  {
    slug: "culture",
    title: "Local Culture",
    description: "Find the culture",
    long: "Curated visits to art markets, galleries and heritage sites around Lagos.",
    intro:
      "Lagos is one of Africa's great creative capitals. Discover it through curated visits to art markets, contemporary galleries and heritage sites, with insider routes planned by our team.",
    highlights: ["Art markets and contemporary galleries", "Heritage and history tours", "Live music and nightlife recommendations"],
    image: unsplash("1537996194471-e657df975ab4", 800),
    imageLarge: unsplash("1537996194471-e657df975ab4", 1400),
  },
];

export const HIGHLIGHTS = [
  { icon: "bed", title: "Luxury Rooms & Suites", text: "Elegant spaces for every traveller" },
  { icon: "spa", title: "World-Class Amenities", text: "Pool, spa, fitness & more" },
  { icon: "dining", title: "Fine Dining", text: "Coastal flavours, local ingredients" },
  { icon: "mapPin", title: "Prime Location", text: "Beachfront, minutes from the city" },
  { icon: "shield", title: "Trusted & Safe", text: "Your comfort, our priority" },
];

export const ADVANTAGES = [
  { icon: "star", title: "5-Star Service", text: "Always at your service" },
  { icon: "tag", title: "Best Price, Direct", text: "Book here, never pay more" },
  { icon: "calendarCheck", title: "Flexible Booking", text: "Cancel online any time before check-in" },
  { icon: "headphones", title: "24/7 Support", text: "We're here for you" },
];
