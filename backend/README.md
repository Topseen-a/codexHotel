# codexHotel — Hotel Management System (Spring Boot)

A REST API for hotel management: rooms, bookings, payments, seasonal pricing,
staff/guest accounts, and reporting — secured with JWT-based authentication
and role-based access control (RBAC).

## Stack

- Java 21, Spring Boot 3.3.4
- MongoDB (Spring Data MongoDB)
- Spring Security + JWT (`jjwt` 0.12.6), BCrypt password hashing
- Jakarta Bean Validation
- Maven, Docker (multi-stage build)

## Role model

| Role | Access |
|---|---|
| `GUEST` | Self-service only: own profile, own bookings, own payments |
| `RECEPTIONIST` | Manage bookings/payments for any guest, update room status |
| `MANAGER` | Everything RECEPTIONIST can, plus create/delete rooms, view reports |
| `ADMIN` | Full access: manage staff accounts, everything above, delete payment records |

Every endpoint reads the caller's identity from a validated JWT — never from
a client-supplied ID — so these roles are actually enforced server-side via
Spring Security (`@PreAuthorize`) rather than trusted from the request.

## Running it locally

Requires a local (or reachable) MongoDB instance.

```bash
cp .env.example .env
# edit .env with real values — at minimum a real JWT_SECRET
./mvnw spring-boot:run
```

`.env` is loaded automatically for local dev (via `spring-dotenv`) and is
git-ignored — never commit it. In production (Render, etc.) skip the file
entirely and set the same variables as real environment variables in the
host's dashboard instead.

The server starts on `http://localhost:8080`.

### First run — seed data

On first startup, `DataSeeder` automatically creates whatever's missing:

- A bootstrap **ADMIN** account (`ADMIN_EMAIL` / `ADMIN_PASSWORD` from your
  `.env`, defaulting to `admin@codexhotel.com` / `ChangeMe123!` if unset) —
  only if no ADMIN exists yet. This exists because otherwise nobody could
  ever become an admin: staff creation itself requires an existing admin to
  call it.
- Pricing for every `(RoomType, Season)` combination, so `PricingService`
  never 404s on a valid room type.
- Five sample rooms across all three room types.

Each item is checked individually before insert (not just "is the
collection non-empty"), so this self-heals if a previous run was
interrupted partway through and only seeded some of the expected data. Safe
to leave running on every startup, including in production.

**Log in as the seeded admin and change that password immediately** —
especially on a public deployment, since the default is documented right
here in this README.

## Project structure

```
src/main/java/com/codexhotel
├── CodexHotelApplication.java
├── config/            Security, seed data, typed @ConfigurationProperties
├── controller/        REST endpoints (thin: delegate to services)
├── dto/request/       Request bodies + Bean Validation constraints
├── dto/response/      Response bodies + ApiResponse envelope
├── enums/
├── exception/         AppException hierarchy + GlobalExceptionHandler
├── mapper/            Entity <-> DTO conversion
├── model/             MongoDB documents
├── notification/      Email/SMS notification channels
├── repository/        Spring Data repositories
├── security/          JWT, UserPrincipal, AccessGuard, 401/403 handlers
└── service/           Service interfaces; implementations in service/impl/
```

### Error handling

Services throw one of a small set of exceptions, each tied to an HTTP status,
and `GlobalExceptionHandler` turns them into the standard response envelope:

| Exception | Status |
|---|---|
| `BadRequestException` | 400 — business-rule violation |
| `ResourceNotFoundException` | 404 |
| `ConflictException` | 409 — duplicates, room unavailable |
| `ForbiddenException` | 403 — authenticated but not allowed |

Input validation lives on the request DTOs (`@NotBlank`, `@Email`, `@Pattern`,
…) and is enforced both on controller `@Valid` bodies and on service
interfaces via `@Validated`. Validation failures return 400 with
`data` holding a `{ field: message }` map.

## API reference

All responses are wrapped as `{ "success": bool, "message": string, "data": ... }`.
Send the JWT as `Authorization: Bearer <token>` on every request except
`/api/auth/register` and `/api/auth/login`.

### Auth
| Method | Path | Access |
|---|---|---|
| POST | `/api/auth/register` | public — always creates a GUEST account |
| POST | `/api/auth/login` | public |
| GET | `/api/auth/me` | authenticated |

### Users
| Method | Path | Access |
|---|---|---|
| POST | `/api/users` | ADMIN — creates staff/guest accounts with any role |
| GET | `/api/users/{id}` | self or staff |
| GET | `/api/users/email?email=` | ADMIN/MANAGER/RECEPTIONIST |
| GET | `/api/users` | ADMIN |
| PUT | `/api/users/{id}` | self or ADMIN |
| DELETE | `/api/users/{id}` | self or ADMIN |

### Rooms
| Method | Path | Access |
|---|---|---|
| POST | `/api/rooms` | ADMIN/MANAGER |
| GET | `/api/rooms`, `/api/rooms/{id}`, `/api/rooms/number/{n}`, `/api/rooms/status?status=` | public |
| PUT | `/api/rooms/status` | ADMIN/MANAGER/RECEPTIONIST |
| DELETE | `/api/rooms/{id}` | ADMIN/MANAGER |

### Bookings
| Method | Path | Access |
|---|---|---|
| POST | `/api/bookings` | authenticated (own bookings; staff can book for anyone) |
| PUT | `/api/bookings/cancel` | owner or staff |
| GET | `/api/bookings/{id}` | owner or staff |
| GET | `/api/bookings/user/{userId}` | self or staff |
| GET | `/api/bookings/room/{roomId}` | staff |
| GET | `/api/bookings/status?status=` | staff |

### Payments
| Method | Path | Access |
|---|---|---|
| POST | `/api/payments` | owner or staff |
| GET | `/api/payments/{id}`, `/api/payments/booking/{bookingId}` | owner or staff |
| GET | `/api/payments/status?successful=` | staff |
| PUT | `/api/payments/{id}/success` | staff |
| DELETE | `/api/payments/{id}` | ADMIN |

### Pricing & Reports
| Method | Path | Access |
|---|---|---|
| GET | `/api/pricing`, `/api/pricing/calculate` | public |
| GET | `/api/reports` | ADMIN/MANAGER |

## Deploying to Render

Render doesn't offer a managed MongoDB service (only Postgres and Redis/Key
Value), so the database lives on MongoDB Atlas's free tier instead.

### 1. Database — MongoDB Atlas (already set up)

Cluster, database user, and network access (`0.0.0.0/0`, since Render's
free-tier outbound IPs aren't static) are configured. Connection string
format:

### 2. Backend — Render Web Service

1. Push this project to GitHub (Render deploys from a repo, not an upload).
2. On [dashboard.render.com](https://dashboard.render.com) → **New +** → **Web Service**.
3. Connect the repo. Choose **Docker** as the environment — the `Dockerfile`
   at the repo root handles the build (JDK 21 + Lombok, matching `pom.xml`).
4. Add environment variables:
   | Key | Value |
   |---|---|
   | `MONGODB_URI` | the Atlas connection string above |
   | `JWT_SECRET` | a long random string (32+ chars) — generate with `openssl rand -base64 48` |
   | `JWT_EXPIRATION_MS` | optional, defaults to `86400000` (24h) |
   | `ADMIN_EMAIL` / `ADMIN_PASSWORD` | optional — override the seeded bootstrap admin's credentials |
   | `CORS_ALLOWED_ORIGINS` | optional — comma-separated allowed origins, defaults to `*` |

   Don't set `PORT` — Render injects it automatically; `application.properties`
   already binds to `${PORT:8080}`.
5. **Create Web Service.** First build takes a few minutes.
6. Live at `https://<service-name>.onrender.com`.

### Notes

- **Free-tier cold starts**: Render's free web services spin down after
  ~15 minutes idle; the first request after that takes a few seconds to
  wake back up. Fine for a demo; worth a loading state on the frontend.
- **CORS defaults to wide open** (`*`) so any frontend origin can call it
  during development. Set `CORS_ALLOWED_ORIGINS` (comma-separated) to your
  deployed frontend's origin before treating this as production-ready.
- **Unique indexes** (user email, room number, room type + season pricing)
  are created on startup (`spring.data.mongodb.auto-index-creation=true`).
  If an existing database already contains duplicates, index creation fails
  and the app won't start — remove the duplicates first.
- **Money** is stored as `Decimal128` and handled as `BigDecimal` (2 decimal
  places). Documents written earlier as plain doubles are still read correctly.
- **Seed data runs automatically** on first startup against whatever
  database it's pointed at, including Atlas — no manual seeding needed.
  Just log in as the seeded admin and change that password once live.

## Notes for whoever builds the frontend

- Store the JWT in memory (or `httpOnly` cookie via a BFF layer) rather than
  `localStorage` where practical.
- The `role` from `/api/auth/me` or the login response should drive which
  UI is shown — guest booking flow vs. staff dashboard vs. admin panel.
- Email/SMS notifications are simulated by logging in `notification/`.
  Swap in a real provider there when ready.