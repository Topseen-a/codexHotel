# codexHotel — Frontend

A React (Vite) frontend for the codexHotel backend — guest room browsing and
booking, plus a role-aware staff dashboard for managing rooms, bookings, and
staff accounts.

## Stack

- React 18 + Vite
- React Router (client-side routing, protected routes by role)
- Axios (API client with JWT auth interceptor)
- Plain CSS with design tokens (no UI framework) — see `src/styles/tokens.css`

## Design direction

Deep forest green + brass, serif display type (Fraunces) for anything that
reads as "the hotel speaking" (room names, prices) paired with a plain sans
(Inter) for functional UI — forms, tables, the staff dashboard. Deliberately
not the cream-background/terracotta-accent look, to avoid reading as a
generic template.

## Running it

```bash
npm install
cp .env.example .env
# edit .env if you're pointing at a different backend (e.g. local dev)
npm run dev
```

Opens at `http://localhost:5173`. By default it points at the deployed
Render backend (`https://codexhotel-wg6d.onrender.com`) — override with
`VITE_API_URL` in `.env` to point at `http://localhost:8080` for local
backend development instead.

```bash
npm run build    # production build to dist/
npm run preview  # serve the production build locally
npm run lint     # oxlint
```

## How it maps to the backend

The backend only stores `roomNumber`, `type`, `basePrice`, and `status` per
room — no photography, descriptions, or amenity lists. Those live in
`src/content/roomContent.js`, keyed by `RoomType` (`STANDARD` / `DELUXE` /
`SUITE`). Swap the placeholder images (currently seeded Picsum URLs, chosen
so they're stable rather than random on every load) for real photography
whenever it exists, and adjust the copy to match your actual amenities.

Booking is by **room type**, not a specific room — the backend picks an
available room of the requested type for the given dates
(`RoomNotAvailableException` if none are free). That's why the Rooms page
groups listings by type instead of listing every individual room.

## Pages

| Route | Access | Notes |
|---|---|---|
| `/rooms` | public | Browse by room type |
| `/rooms/:roomType` | public | Gallery, amenities, booking panel (requires login to actually book) |
| `/login`, `/register` | public | Register always creates a GUEST account |
| `/bookings` | authenticated | Own bookings — cancel, pay |
| `/admin` | ADMIN / MANAGER / RECEPTIONIST | Tabs adjust by role: Overview & reports (ADMIN/MANAGER), Rooms (create/delete: ADMIN/MANAGER; status updates: any staff), Bookings (any staff), Staff & Users (ADMIN only) |

## Known gaps / next steps

- **CORS**: the backend currently allows all origins (`allowedOriginPatterns("*")`).
  Fine for now; narrow it to this frontend's deployed origin once that exists.
- **Payments** are simplified to a single amount + method entry per booking —
  there's no card processor integration, matching the backend's simulated
  payment model.
- **No image upload** — room photography is placeholder-only until real
  photos and a storage solution (e.g. S3/Cloudinary) are wired in.
- Token is stored in `localStorage` for simplicity. Consider an `httpOnly`
  cookie via a backend-for-frontend layer before this handles real user data.
