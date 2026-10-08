# Codex Hotel

A full-stack hotel booking and management system for a boutique seaside
resort in Lagos. Guests can browse rooms, check live availability, book and
pay online. Staff run the hotel from a role-based dashboard covering rooms,
bookings, payments, guests, pricing and reports.

- **Frontend:** React (Vite) — deployed on Vercel
- **Backend:** Spring Boot REST API — deployed on Render (Docker)
- **Database:** MongoDB — Atlas in production, Docker locally
- **Live API:** https://codexhotel-wg6d.onrender.com

---

## Features

### For guests
- Browse room types with photos, amenities, capacity and live availability
- Search by dates and number of guests
- Live price quotes per night using seasonal pricing (weekday, weekend, festive)
- Book a room — the system assigns a free room of the chosen type
- Pay in full or in parts (card, transfer or cash) and track the balance
- View, manage and cancel bookings
- Account settings: update profile, change password, delete account
- Sign up, sign in, show/hide password, and forgot/reset password by email

### For staff (role-based dashboard)
| Area | What it does | Who can use it |
|---|---|---|
| Overview | Revenue, occupancy, active bookings, upcoming stays | Admin, Manager |
| Bookings | Filter by status, room or guest; book on a guest's behalf; cancel | All staff |
| Rooms | Update room status; add and remove rooms | All staff (add/remove: Admin, Manager) |
| Payments | Review payments, confirm pending ones, look up by reference | All staff (delete: Admin) |
| Guests | Look up a guest and their booking history | All staff |
| Staff & Users | Create accounts, change roles, edit and delete users | Admin |
| Pricing | Seasonal rate table and a price calculator | All staff |

### Roles
| Role | Access |
|---|---|
| `GUEST` | Own profile, bookings and payments |
| `RECEPTIONIST` | Front desk: bookings, payments, room status, guest lookup |
| `MANAGER` | Everything a receptionist can do, plus rooms and reports |
| `ADMIN` | Full access, including staff accounts and deleting payment records |

Permissions are enforced by the backend (Spring Security). The frontend
mirrors them so users only see actions they're allowed to take.

---

## Tech stack

| Layer | Technology |
|---|---|
| Frontend | React 19, Vite 8, React Router 7, Axios, plain CSS with design tokens, oxlint |
| Backend | Java 21, Spring Boot 3.3, Spring Security + JWT (jjwt), Bean Validation, Spring Data MongoDB, Spring Mail |
| Database | MongoDB 7 |
| Tooling | Maven, Docker / Docker Compose |
| Hosting | Vercel (frontend), Render (backend), MongoDB Atlas (database) |

---

## Repository structure

```
codexHotel/
├── backend/                 Spring Boot API
│   ├── src/main/java/com/codexhotel/
│   │   ├── config/          Security, seed data, mail, typed configuration
│   │   ├── controller/      REST endpoints
│   │   ├── dto/             Request and response objects (with validation)
│   │   ├── enums/           Roles, room types, statuses, seasons
│   │   ├── exception/       Exception hierarchy and global error handler
│   │   ├── mapper/          Entity ↔ DTO conversion
│   │   ├── model/           MongoDB documents
│   │   ├── notification/    Email / SMS notifications
│   │   ├── repository/      Spring Data repositories
│   │   ├── security/        JWT, access checks, 401/403 handlers
│   │   └── service/         Business logic (interfaces + impl/)
│   ├── compose.yaml         Local MongoDB (and optionally the API) in Docker
│   ├── Dockerfile           Production image used by Render
│   └── README.md            Backend details and full API reference
│
└── frontend/                React app
    ├── src/
    │   ├── api/             One module per backend resource
    │   ├── components/      Layout, UI kit, home sections, booking widgets
    │   ├── config/          Brand, contact details, navigation
    │   ├── content/         Marketing copy and photography
    │   ├── context/         Authentication state
    │   ├── hooks/           Data loading, price quotes, countdown
    │   ├── pages/           Public pages, guest area, auth, staff dashboard
    │   ├── styles/          Design tokens and base styles
    │   └── utils/           Formatting, roles, status labels
    ├── vercel.json          Single-page-app routing for Vercel
    └── README.md            Frontend details and page/endpoint map
```

---

## Running it locally

### Prerequisites
- **JDK 21** — newer JDKs (25, 26) can't compile Lombok yet
- **Node.js 20+**
- **Docker** — for the local MongoDB

### 1. Start MongoDB
```bash
cd backend
docker compose up -d
```
Data is kept in a Docker volume between restarts. `docker compose down -v`
wipes it.

### 2. Start the backend
```bash
cd backend
cp .env.example .env      # then set a real JWT_SECRET
./mvnw spring-boot:run
```
The API runs on http://localhost:8080. You can also run
`CodexHotelApplication` from IntelliJ with the JDK 21 SDK.

On first start it seeds:
- an **admin account** using `ADMIN_EMAIL` / `ADMIN_PASSWORD` from `.env`
  (only if no admin exists yet — change the password after first sign-in)
- **seasonal pricing** for every room type
- **five sample rooms**

### 3. Start the frontend
```bash
cd frontend
cp .env.example .env
# Point the app at your local backend:
#   VITE_API_URL=http://localhost:8080
npm install
npm run dev
```
The site runs on http://localhost:5173.

> **Important:** if `VITE_API_URL` isn't set, the frontend talks to the
> **live** API. Always point it at `localhost:8080` while developing so test
> bookings and accounts don't end up in production.

### Alternative: run the backend in Docker too
```bash
cd backend
docker compose --profile app up -d --build
```

---

## Configuration

### Backend (`backend/.env` locally, environment variables on Render)
| Variable | Purpose |
|---|---|
| `MONGODB_URI` | MongoDB connection string |
| `JWT_SECRET` | Secret for signing login tokens (32+ characters — generate with `openssl rand -base64 48`) |
| `JWT_EXPIRATION_MS` | Session length, default 24 hours |
| `ADMIN_EMAIL`, `ADMIN_PASSWORD` | The bootstrap admin created on first start |
| `CORS_ALLOWED_ORIGINS` | Comma-separated allowed origins (defaults to `*`) |
| `FRONTEND_URL` | Frontend address, used in password-reset links |
| `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_FROM` | SMTP settings for emails. Without `MAIL_HOST`, emails are only written to the log |

### Frontend (`frontend/.env`)
| Variable | Purpose |
|---|---|
| `VITE_API_URL` | Backend address (defaults to the live Render API) |

`.env` files are git-ignored — never commit real secrets.

---

## How pricing works

Each night is priced separately: **room base price × season multiplier**.

| Season | When | Multiplier |
|---|---|---|
| Weekday | Monday – Friday | 1.0 |
| Weekend | Saturday and Sunday | 1.3 |
| Festive | All of December | 1.6 |

The total is fixed when the booking is made. Money is stored as exact decimals,
not floating point.

---

## Testing and checks

```bash
# Backend — needs the local MongoDB running; uses a separate codexhotel_test database
cd backend && ./mvnw test

# Frontend
cd frontend && npm run lint && npm run build
```

---

## Deployment

| Part | Platform | Notes |
|---|---|---|
| Database | MongoDB Atlas | Allow Render's outbound access (`0.0.0.0/0` on the free tier) |
| Backend | Render web service | Docker environment, root directory `backend/`. Set the backend variables above |
| Frontend | Vercel | Root directory `frontend/`. `vercel.json` rewrites all routes to the app |

Both deploy automatically on push to `main`. When a change touches the API
and the frontend together, **deploy the backend first**: push the backend
commit, wait until Render shows it as live, then push the frontend.

On startup the backend creates unique indexes (user email, room number,
pricing per room type and season). If the database already contains
duplicates, remove them first or the app won't start.

---

## Security

- Passwords are hashed with **BCrypt**.
- Sessions use signed **JWTs**, sent as `Authorization: Bearer <token>`.
- Every endpoint is protected by role checks in Spring Security, re-checked in
  the service layer, with ownership rules (for example, guests only see their
  own bookings).
- Changing or resetting a password **signs out all other sessions**.
- Password-reset links are **single-use, expire after 30 minutes**, and only a
  hash of each token is stored.
- "Forgot password" replies the same way whether or not an email is
  registered, so accounts can't be discovered.
- Input is validated on both the frontend and the backend.

---

## Known limitations and next steps

- **Photos and copy:** room, gallery and experience photos are stock images,
  and testimonials and experience descriptions are placeholders. Replace them
  with the property's own before launch.
- **Payments** are recorded, not processed — there is no card gateway yet
  (e.g. Paystack or Flutterwave).
- **Email** needs SMTP settings in production; SMS notifications are still
  simulated.
- **No rate limiting** on sign-in or forgot-password.
- **Bookings can be cancelled but not deleted** through the API.

---

## More documentation

- [Backend README](backend/README.md) — architecture, error format, full API reference, Render setup
- [Frontend README](frontend/README.md) — project structure, every page with the endpoints it uses

---

## Author

Built by **Temitope Abodunrin** — [github.com/Topseen-a](https://github.com/Topseen-a)
