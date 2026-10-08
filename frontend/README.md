# CodexHotel — Frontend

React (Vite) frontend for the CodexHotel backend: a resort-style marketing
site with live room availability and booking, a guest area for managing and
paying for stays, and a role-aware staff dashboard.

## Stack

- React 19 + Vite, React Router 7
- Axios API client (JWT auth, response unwrapping, typed `ApiError`)
- Plain CSS with design tokens — no UI framework (`src/styles/tokens.css`)
- Font: Jost (Google Fonts, free) — uppercase for headlines, sentence case for
  everything else
- Theme: warm dark (espresso surfaces, bronze accent) — all colours are tokens
  in `src/styles/tokens.css`

## Running it

```bash
npm install
cp .env.example .env          # set VITE_API_URL=http://localhost:8080 for a local backend
npm run dev                   # http://localhost:5173
```

```bash
npm run build    # production build to dist/
npm run preview  # serve the production build locally
npm run lint     # oxlint
```

## Project structure

```
src/
├── api/            One module per backend resource + the shared axios client
├── components/
│   ├── layout/     Navbar, Footer, Layout, route guards, scroll handling
│   ├── ui/         Icon, Alert, Modal, ConfirmDialog, badges, empty states
│   ├── home/       Landing page sections (hero, search, rooms, experiences…)
│   ├── rooms/      Room cards
│   └── booking/    Booking panel (live quote), payment form, booking cards
├── config/site.js  Brand, contact details and navigation
├── content/        Marketing copy and photography (rooms, experiences, gallery)
├── context/        Auth state (session restore, login/register, 401 handling)
├── hooks/          useAsync, useStayQuote, useCountdown, useDocumentTitle
├── pages/          Public pages, auth/, account/ (guest area), admin/ (dashboard)
├── styles/         Design tokens + base styles
└── utils/          Formatting, roles/permissions, enum display metadata
```

## Pages and access

| Route | Access | Backend endpoints used |
|---|---|---|
| `/` | public | `GET /api/rooms` |
| `/rooms` | public | `GET /api/rooms`, `GET /api/pricing` |
| `/rooms/:roomType` | public (booking needs login) | `GET /api/rooms`, `GET /api/pricing/calculate`, `POST /api/bookings` |
| `/gallery`, `/about`, `/contact` | public | — |
| `/login`, `/register` | signed-out only | `POST /api/auth/login`, `POST /api/auth/register`, `GET /api/auth/me` |
| `/bookings` | signed in | `GET /api/bookings/user/{id}` |
| `/bookings/:id` | owner or staff | `GET /api/bookings/{id}`, `GET /api/payments/booking/{id}`, `POST /api/payments`, `PUT /api/bookings/cancel` |
| `/account` | signed in | `PUT /api/users/{id}`, `DELETE /api/users/{id}` |
| `/admin/overview` | ADMIN, MANAGER | `GET /api/reports`, `GET /api/bookings/status` |
| `/admin/bookings` | staff | `GET /api/bookings/status`, `/room/{id}`, `/user/{id}`, `GET /api/users/email`, `POST /api/bookings`, `PUT /api/bookings/cancel` |
| `/admin/rooms` | staff (create/delete: ADMIN, MANAGER) | `GET /api/rooms`, `/status`, `/number/{n}`, `POST /api/rooms`, `PUT /api/rooms/status`, `DELETE /api/rooms/{id}` |
| `/admin/payments` | staff (delete: ADMIN) | `GET /api/payments/status`, `GET /api/payments/{id}`, `PUT /api/payments/{id}/success`, `DELETE /api/payments/{id}` |
| `/admin/guests` | staff | `GET /api/users/email`, `GET /api/users/{id}`, `GET /api/bookings/user/{id}` |
| `/admin/users` | ADMIN | `GET /api/users`, `POST /api/users`, `PUT /api/users/{id}`, `DELETE /api/users/{id}` |
| `/admin/pricing` | staff | `GET /api/pricing`, `GET /api/pricing/calculate` |

Permissions live in `src/utils/roles.js` and mirror the backend's
`@PreAuthorize` rules; the backend stays the source of truth.

## Notes

- **Booking is by room type.** The backend assigns a free room of that type
  for the chosen dates, so the site lists room types, not individual rooms.
- **Room content** (photos, copy, capacity, amenities) lives in
  `src/content/rooms.js`, keyed by `RoomType` — the API only stores number,
  type, base price and status.
- **Photography** is Unsplash stock; replace with the property's own photos.
  **Testimonials** in `src/content/testimonials.js` are placeholders — swap in
  real guest reviews before launch. Contact details are in `src/config/site.js`.
- **Sessions** are a JWT in `localStorage`. Any 401 on an authenticated
  request signs the user out with a "session expired" notice. Changing your
  email signs you out, because the token is tied to the old address.
- **Festive countdown** on the home page counts down to December, when the
  backend's FESTIVE pricing applies.
