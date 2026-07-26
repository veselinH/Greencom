# Greencom

Greencom is a telecom self-service web portal. Customers browse and sign mobile (voice/data), internet, and television plans, manage their contracts and profile, and earn loyalty points through a dedicated rewards microservice.

## Architecture

The system consists of two independent Spring Boot applications:

| Application | Port | Database | Role |
|---|---|---|---|
| **GreencomWebApp** (this repository root) | 8080 | `greencom_db` (MySQL) | Thymeleaf MVC web application |
| **loyalty-service** (`loyalty-service/`) | 8081 | `greencom_loyalty_db` (MySQL) | REST microservice for the loyalty program |

The web app calls the loyalty service over HTTP using a Feign client (`LoyaltyClient`) wrapped in `LoyaltyFacade`, which degrades gracefully: if the loyalty service is down, signing/unsigning plans and the profile page keep working — only the loyalty data is skipped.

## Tech Stack

- Java 17, Spring Boot 3.4.1, Maven
- Spring Web MVC + Thymeleaf (with Spring Security extras)
- Spring Data JPA + MySQL (H2 in-memory for tests)
- Spring Security with form login and OAuth2/OIDC social login (Google, Facebook, GitHub)
- Spring Cloud OpenFeign (web app → loyalty service)
- Spring Cache + Spring Scheduling (loyalty service)
- ModelMapper (layer mapping), Flying Saucer + OpenPDF (contract PDF generation)
- JUnit 5, Mockito, Spring Security Test, JaCoCo (70% line coverage gate in both apps)

## Features

- **Authentication** — registration with auto-login, form login, and social login via Google, Facebook, or GitHub (a shadow account is provisioned automatically).
- **Roles** — `USER`, `MODERATOR`, `ADMIN`. Admins manage other users' roles from the users page; moderators manage plans.
- **Plans** — browse, add, and edit voice, data, internet, and television plans (add/edit are moderator-only). Television plans support selectable add-on packages.
- **Contracts** — sign a plan with a hand-drawn canvas signature (stored as an image), download the contract as a generated PDF (owner or admin only), and unsign with an early-termination penalty calculation.
- **Profile** — view signed plans and monthly debt, edit personal details, redeem loyalty points.
- **Error handling** — global exception handlers in both apps (custom error pages in the web app, JSON `ApiError` responses in the loyalty service).

## Loyalty Program (loyalty-service)

Signing a contract earns points (1 point per € of monthly price); unsigning revokes them. Points can be redeemed for a discount on the monthly bill (100 points = 1.00 €). Accounts have tiers based on total points earned — BRONZE, SILVER (≥ 500), GOLD (≥ 1500) — and a monthly cron job awards a tier-based bonus (10/50/100 points). Account reads are cached; a fixed-delay job periodically evicts the cache.

REST API (base path `/api/loyalty`):

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/{username}` | Balance, total earned, and tier |
| POST | `/{username}/earn` | Award points |
| PUT | `/{username}/redeem` | Spend points, returns the € discount |
| DELETE | `/{username}/points?amount=N` | Revoke points (floored at 0) |

## Getting Started

Prerequisites: JDK 17, Maven (or the included `mvnw`), MySQL on `localhost:3306` (databases are created automatically on first run; default credentials `root`/`NewPassword` in each `application.yaml`).

Environment variables used by the web app:

| Variable | Purpose |
|---|---|
| `GOOGLE_OAUTH_CLIENT_ID` / `GOOGLE_OAUTH_CLIENT_SECRET` | Google OAuth2 login |
| `FACEBOOK_OAUTH_CLIENT_ID` / `FACEBOOK_OAUTH_CLIENT_SECRET` | Facebook OAuth2 login |
| `GITHUB_OAUTH_CLIENT_ID` / `GITHUB_OAUTH_CLIENT_SECRET` | GitHub OAuth2 login |
| `GREENCOM_ADMIN_USERNAME` / `GREENCOM_ADMIN_PASSWORD` | Seeded admin account credentials |

Run each application from its own directory:

```bash
# Web application (repository root)
mvn spring-boot:run

# Loyalty microservice
mvn -f loyalty-service/pom.xml spring-boot:run
```

Reference data (plan types, extras, roles, admin user) is seeded automatically on startup.

## Testing

Both applications include unit, integration, and API (MockMvc) tests running against an in-memory H2 database — no MySQL or environment variables needed:

```bash
# Web application
mvn clean verify

# Loyalty microservice
mvn -f loyalty-service/pom.xml clean verify
```

JaCoCo enforces a minimum of 70% line coverage in each application; reports are written to `target/site/jacoco/index.html`.
