# Authentication flow

The application supports two authentication methods:

- Email/password for administration and legacy accounts.
- Phone OTP for members and shop customers.

## OTP development flow

1. `POST /api/auth/otp/request` with an international phone number.
2. The API creates a 5-minute challenge.
3. Development mode logs the OTP and returns `debugCode` because `OTP_EXPOSE_DEV_CODE=true` by default locally.
4. `POST /api/auth/otp/verify` verifies the challenge. A missing account is created as `MEMBER` when first name and last name are provided.
5. The response contains a 15-minute access token and a 30-day refresh token.

Set `OTP_EXPOSE_DEV_CODE=false` in any shared or production environment and replace `LoggingSmsSender` with a real SMS provider.

## Session refresh

`POST /api/auth/refresh` rotates the opaque refresh token and returns a new access token plus a new refresh token.

## Account data

`/api/me` exposes the authenticated user profile. Delivery addresses are stored below `/api/me/addresses`.

Orders are linked to the authenticated user under `/api/orders`. The backend stores a snapshot of shipping details and product prices so historical orders are not changed when a profile or product is later edited.
