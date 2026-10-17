# Dara Massalik Backend

Backend REST for the general Dara Massalik Dahira management application.

## Stack

- Java 21
- Spring Boot 4.1.1
- Spring MVC
- Spring Security + JWT
- Spring Data JPA / Hibernate
- PostgreSQL 17
- Flyway
- springdoc-openapi / Swagger UI

## Domains

### Authentication and account
- Email/password login for administrative accounts
- Phone OTP login for members and shop customers
- 15-minute access token + 30-day rotating refresh token
- Current user profile
- Delivery addresses

### Dahira
- Members
- Commissions
- Activities

### Quranic school
- Students
- Teachers
- Groups
- Attendance
- Pedagogical tracking

### Shop
- Public product catalog
- Product details
- Product administration
- Orders linked to authenticated users (checkout/payment flow intentionally deferred)

### Finance
- Student contributions/payments

## Database

The schema is managed by Flyway:

- `V1__init.sql` - school/auth base schema
- `V2__dahira_general_and_shop.sql` - general Dahira and shop catalog
- `V3__authentication_profiles_orders.sql` - OTP/session/profile/address/order data

Start PostgreSQL:

```bash
docker compose up -d
```

Do not recreate the volume when upgrading an existing development database: Flyway will apply the next migration automatically.

For a disposable database only:

```bash
docker compose down -v
docker compose up -d
```

## Run

Open the project in IntelliJ with JDK 21 and run `DaraMassalikApplication`.

The API runs on `http://localhost:8080`.

Swagger UI:

`http://localhost:8080/swagger-ui.html`

OpenAPI JSON:

`http://localhost:8080/v3/api-docs`

## Authentication

### Password

```text
POST /api/auth/login
```

Demo account:

```text
admin@daramassalik.local
ChangeMe123!
```

### OTP (local development)

```text
POST /api/auth/otp/request
POST /api/auth/otp/verify
```

Example request:

```json
{
  "phoneNumber": "+33600000000"
}
```

In local development the generated code is logged by `LoggingSmsSender` and is also returned in `debugCode` because `OTP_EXPOSE_DEV_CODE=true` by default. Set `OTP_EXPOSE_DEV_CODE=false` before sharing or deploying the application and replace `LoggingSmsSender` with a real SMS provider.

For a new phone number, the verify request must include `firstName` and `lastName`; the account is created automatically with role `MEMBER`.

### Session

```text
POST /api/auth/refresh
POST /api/auth/logout
```

The refresh token is rotated on every successful refresh and stored hashed in PostgreSQL.

## Account and orders

```text
GET/PATCH /api/me
GET/POST/PUT/DELETE /api/me/addresses
GET /api/orders
GET /api/orders/{id}
POST /api/orders
```

Order creation validates current product prices and stock and stores a historical snapshot of product and shipping information. Payment is intentionally not implemented yet.

## User roles

A single `app_users` account can have multiple roles. Roles are stored in `user_roles` and currently include `ADMIN`, `TEACHER`, `TREASURER`, `MEMBER`, `STUDENT`, `CUSTOMER`, and `PARENT`.

The legacy `app_users.role` column is retained temporarily for compatibility; new authorization uses `user_roles`.

Admin role management:

```text
GET /api/users/{id}/roles
PUT /api/users/{id}/roles
```

Example:

```json
{
  "roles": ["MEMBER", "CUSTOMER"]
}
```
