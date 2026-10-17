# V5 – Roles and permissions

This change is incremental. Keep existing migrations V1–V4 and let Flyway apply V5.

## Roles
ADMIN, TEACHER, TREASURER, MEMBER, STUDENT, CUSTOMER, PARENT

## Permission model
- `role_permissions`: default permissions per role
- `user_permission_overrides`: explicit grant/deny per user
- effective permissions = role permissions + grants - denies
- ADMIN always has all permissions

## Admin endpoints
- GET /api/admin/access/users
- GET /api/admin/access/roles
- GET /api/admin/access/permissions
- GET /api/admin/access/roles/{role}/permissions
- PUT /api/admin/access/roles/{role}/permissions
- GET /api/admin/access/users/{id}/roles
- PUT /api/admin/access/users/{id}/roles
- GET /api/admin/access/users/{id}/permissions
- PUT /api/admin/access/users/{id}/permissions
