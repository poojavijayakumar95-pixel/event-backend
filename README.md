# EventHub — Backend API

> Production-ready Event Management System REST API built with Spring Boot 3, Spring Security 6, and JWT authentication.

**Live API:** https://event-backend-production-06c4.up.railway.app/api  
**Swagger UI:** https://event-backend-production-06c4.up.railway.app/api/swagger-ui.html  
**Frontend:** https://event-managementfrontend.netlify.app

---

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.2.3 |
| Language | Java 21 |
| Security | Spring Security 6 + JWT (jjwt 0.11.5) |
| Database | MySQL 8 (Railway) |
| ORM | Spring Data JPA + Hibernate 6 |
| Email | JavaMailSender (Gmail SMTP) |
| API Docs | Springdoc OpenAPI / Swagger UI |
| Build | Maven 3.9 |
| Deployment | Railway (Docker) |
| Testing | JUnit 5 + Mockito + H2 (in-memory) |

---

## Project Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/eventmanagement/
│   │   │   ├── config/               # SecurityConfig, AppConfig
│   │   │   ├── controller/           # 5 REST controllers
│   │   │   ├── dto/
│   │   │   │   ├── request/          # AuthRequest, EventRequest, etc.
│   │   │   │   └── response/         # ApiResponses (all DTOs)
│   │   │   ├── entity/               # User, Event, Speaker, Registration
│   │   │   ├── enums/                # Role, EventCategory, RegistrationStatus
│   │   │   ├── exception/            # GlobalExceptionHandler, AppExceptions
│   │   │   ├── repository/           # 4 Spring Data JPA repositories
│   │   │   ├── security/             # JwtUtils, JwtAuthenticationFilter
│   │   │   ├── service/
│   │   │   │   └── impl/             # AuthService, EventService, etc.
│   │   │   └── util/                 # ReminderScheduler (cron)
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       ├── java/com/eventmanagement/
│       │   ├── controller/           # AuthControllerTest, UserWorkflowE2ETest
│       │   ├── repository/           # EventRepositoryTest
│       │   └── service/              # 5 service unit tests
│       └── resources/
│           └── application-test.properties
├── Dockerfile
├── pom.xml
└── .env.example
```

---

## Features

- **User Registration & Authentication** — sign up, login, JWT access + refresh tokens
- **Role-Based Access Control** — `ROLE_USER` and `ROLE_ADMIN` with method-level security
- **Event Management** — full CRUD for events (admin only for write operations)
- **Speaker Management** — create and link speakers to events
- **Event Registration** — users register/cancel; duplicate and capacity checks enforced
- **Re-registration after cancel** — cancelled users can re-register for the same event
- **Attendance Tracking** — admin marks attendees as REGISTERED / ATTENDED / NO_SHOW
- **Email Notifications** — async HTML emails for registration confirmation, cancellation, welcome, and daily reminders
- **Search & Filters** — search events by keyword, category, location, date range
- **Pagination** — all list endpoints are paginated
- **Scheduled Reminders** — cron job at 9 AM daily emails users about tomorrow's events
- **Soft Delete** — events are deactivated, not permanently deleted

---

## API Reference

### Base URL
```
https://event-backend-production-06c4.up.railway.app/api
```

### Authentication
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/auth/register` | No | Register new user |
| POST | `/auth/login` | No | Login, returns JWT tokens |
| POST | `/auth/refresh` | No | Refresh access token |
| GET | `/auth/me` | Yes | Get current user profile |

### Events
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/events` | No | List all active events (paginated) |
| GET | `/events/search` | No | Search & filter events |
| GET | `/events/{id}` | No | Get event by ID |
| POST | `/events` | Admin | Create event |
| PUT | `/events/{id}` | Admin | Update event |
| DELETE | `/events/{id}` | Admin | Soft-delete event |

### Registrations
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/registrations` | User | Register for an event |
| DELETE | `/registrations/events/{id}` | User | Cancel registration |
| GET | `/registrations/my` | User | My registrations (paginated) |

### Speakers
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/speakers` | No | List / search speakers |
| GET | `/speakers/{id}` | No | Get speaker by ID |
| POST | `/speakers` | Admin | Create speaker |
| PUT | `/speakers/{id}` | Admin | Update speaker |
| DELETE | `/speakers/{id}` | Admin | Delete speaker |

### Admin
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/admin/dashboard` | Admin | Dashboard statistics |
| GET | `/admin/users` | Admin | List all users |
| PATCH | `/admin/users/{id}/toggle-status` | Admin | Enable / disable user |
| PATCH | `/admin/users/{id}/promote` | Admin | Promote user to Admin |
| GET | `/admin/events/{id}/registrations` | Admin | Event attendee list |
| PATCH | `/admin/attendance` | Admin | Update attendance status |

---

## Authentication Flow

```
1. POST /auth/register  →  { accessToken, refreshToken, user }
2. Include header on protected requests:
       Authorization: Bearer <accessToken>
3. When accessToken expires (24h), call:
       POST /auth/refresh  { refreshToken }
   →  { accessToken, refreshToken }
```

---

## Data Models

### User
```json
{
  "id": 1,
  "firstName": "Jane",
  "lastName": "Doe",
  "email": "jane@example.com",
  "phone": "+91-9876543210",
  "role": "ROLE_USER",
  "createdAt": "2025-08-15T10:00:00"
}
```

### Event
```json
{
  "id": 1,
  "title": "React Summit 2025",
  "description": "...",
  "startDateTime": "2025-09-10T09:00:00",
  "endDateTime": "2025-09-10T18:00:00",
  "location": "Bangalore, India",
  "venue": "NIMHANS Convention Centre",
  "category": "CONFERENCE",
  "maxCapacity": 500,
  "registeredCount": 123,
  "availableSlots": 377,
  "isActive": true,
  "imageUrl": "https://...",
  "speakers": [ { "id": 1, "firstName": "John", "lastName": "Doe", ... } ]
}
```

### Registration Status Values
`REGISTERED` · `CANCELLED` · `ATTENDED` · `NO_SHOW`

### Event Category Values
`CONFERENCE` · `WORKSHOP` · `SEMINAR` · `WEBINAR` · `NETWORKING` · `CONCERT` · `SPORTS` · `EXHIBITION` · `OTHER`

---

## Running Locally

### Prerequisites
- Java 21+
- Maven 3.9+
- MySQL 8+ running locally

### 1. Clone the repo
```bash
git clone https://github.com/your-username/event-management-backend.git
cd event-management-backend
```

### 2. Create your `.env` file
```env
DB_URL=jdbc:mysql://localhost:3306/event_management?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=your_mysql_password

JWT_SECRET=your_minimum_32_character_secret_key_here
JWT_EXPIRATION_MS=86400000
JWT_REFRESH_EXPIRATION_MS=604800000

MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your_gmail@gmail.com
MAIL_PASSWORD=your_gmail_app_password
MAIL_FROM=your_gmail@gmail.com

FRONTEND_URL=http://localhost:5173
```

> **Gmail App Password:** Google Account → Security → 2-Step Verification → App Passwords → Generate

### 3. Run
```bash
# Load env vars and start
export $(cat .env | xargs) && mvn spring-boot:run
```

**Windows (PowerShell):**
```powershell
Get-Content .env | Where-Object { $_ -notmatch '^#' -and $_ -match '=' } | ForEach-Object {
    $key, $val = $_ -split '=', 2
    [System.Environment]::SetEnvironmentVariable($key.Trim(), $val.Trim())
}
mvn spring-boot:run
```

API available at: `http://localhost:8080/api`  
Swagger UI at: `http://localhost:8080/api/swagger-ui.html`

---

## Running Tests

```bash
mvn test
```

Tests use H2 in-memory database — no MySQL required.

### Test Coverage

| Test Class | Type | Tests |
|---|---|---|
| `AuthServiceTest` | Unit (Mockito) | 4 |
| `EventServiceTest` | Unit (Mockito) | 6 |
| `RegistrationServiceTest` | Unit (Mockito) | 4 |
| `SpeakerServiceTest` | Unit (Mockito) | 7 |
| `AdminServiceTest` | Unit (Mockito) | 6 |
| `AuthControllerTest` | Web Slice (MockMvc) | 3 |
| `EventRepositoryTest` | JPA Slice (H2) | 5 |
| `UserWorkflowE2ETest` | Integration (Full context) | 15 |
| **Total** | | **51** |

---

## Docker

```bash
# Build image
docker build -t eventhub-backend .

# Run with environment variables
docker run -p 8080:8080 \
  -e DB_URL=jdbc:mysql://host.docker.internal:3306/event_management \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=your_password \
  -e JWT_SECRET=your_secret \
  -e MAIL_USERNAME=your@gmail.com \
  -e MAIL_PASSWORD=your_app_password \
  -e MAIL_FROM=your@gmail.com \
  -e FRONTEND_URL=http://localhost:5173 \
  eventhub-backend
```

---

## Deployment (Railway)

This project is deployed on **Railway** using Docker.

1. Push to GitHub
2. New project on Railway → Deploy from GitHub repo
3. Set all environment variables in Railway dashboard
4. Railway auto-detects `Dockerfile` and builds/deploys on every push

**Live Backend:** https://event-backend-production-06c4.up.railway.app

---

## Environment Variables Reference

| Variable | Required | Description |
|---|---|---|
| `DB_URL` | ✅ | Full JDBC connection string |
| `DB_USERNAME` | ✅ | Database username |
| `DB_PASSWORD` | ✅ | Database password |
| `JWT_SECRET` | ✅ | Min 32-char random string |
| `JWT_EXPIRATION_MS` | ✅ | Access token TTL (ms). Default: `86400000` (24h) |
| `JWT_REFRESH_EXPIRATION_MS` | ✅ | Refresh token TTL (ms). Default: `604800000` (7d) |
| `MAIL_HOST` | ✅ | SMTP host. e.g. `smtp.gmail.com` |
| `MAIL_PORT` | ✅ | SMTP port. e.g. `587` |
| `MAIL_USERNAME` | ✅ | SMTP username / Gmail address |
| `MAIL_PASSWORD` | ✅ | Gmail App Password (not your real password) |
| `MAIL_FROM` | ✅ | From address shown in emails |
| `FRONTEND_URL` | ✅ | Frontend origin for CORS. e.g. `https://your-app.netlify.app` |
| `PORT` | Auto | Set by Railway automatically |

---

## Security Notes

- Passwords hashed with **BCrypt (strength 12)**
- JWT signed with **HS256**
- CORS restricted to `FRONTEND_URL` only
- All admin endpoints require `ROLE_ADMIN`
- Events are **soft-deleted** (never permanently removed)
- Refresh token rotation on every use

---

## First Admin Setup

After deploying, register a normal user then promote via direct DB query:

```sql
UPDATE users SET role = 'ROLE_ADMIN' WHERE email = 'your@email.com';
```

After that, admins can promote other users from the Admin Panel UI.

---
