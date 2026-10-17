# Incremental role model change

This patch does **not** regenerate the backend. It evolves the existing V3 project.

## Changes

- `AppUser` now owns a set of roles via `user_roles`.
- Added `STUDENT`, `CUSTOMER`, and `PARENT` roles.
- JWT contains both `roles` and legacy `role` claims.
- Spring Security authorizes from all assigned roles.
- Existing `app_users.role` is kept temporarily for compatibility and is migrated into `user_roles`.
- `students`, `teachers`, and `members` now have an optional `user_id` link.
- Admin-only APIs allow reading/replacing a user's roles:
  - `GET /api/users/{id}/roles`
  - `PUT /api/users/{id}/roles`
- Existing login/OTP APIs keep the same URLs.
- Existing `role` field in auth responses is kept as the primary role for frontend compatibility; a new `roles` array is added.

## Migration

Flyway migration added:

`V4__user_roles_and_profile_links.sql`

Do not run `docker compose down -v` against the existing development database. Start the backend normally so Flyway can apply V4.

## Example role update

```json
{
  "roles": ["TEACHER", "MEMBER"]
}
```
