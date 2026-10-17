-- V6: Simplify permissions by functional module.
-- Only old WRITE permissions are converted to MANAGE to avoid granting
-- full management rights to roles/users that had read-only access.
-- Flyway manages the migration transaction; do not add BEGIN/COMMIT here.

-- 1. Remove the old permission constraints before converting values.
ALTER TABLE role_permissions
DROP CONSTRAINT ck_role_permissions_permission;

ALTER TABLE user_permission_overrides
DROP CONSTRAINT ck_user_permission_override_permission;

-- 2. Convert role permissions.
-- Only old WRITE permissions grant the new MANAGE permission.
INSERT INTO role_permissions (role, permission)
SELECT DISTINCT rp.role,
                CASE rp.permission
                    WHEN 'MEMBER_WRITE' THEN 'MEMBER_MANAGE'
                    WHEN 'ACTIVITY_WRITE' THEN 'ACTIVITY_MANAGE'
                    WHEN 'COMMISSION_WRITE' THEN 'COMMISSION_MANAGE'
                    WHEN 'STUDENT_WRITE' THEN 'STUDENT_MANAGE'
                    WHEN 'TEACHER_WRITE' THEN 'TEACHER_MANAGE'
                    WHEN 'GROUP_WRITE' THEN 'GROUP_MANAGE'
                    WHEN 'ATTENDANCE_WRITE' THEN 'ATTENDANCE_MANAGE'
                    WHEN 'PEDAGOGICAL_TRACKING_WRITE' THEN 'PEDAGOGICAL_TRACKING_MANAGE'
                    WHEN 'PRODUCT_WRITE' THEN 'PRODUCT_MANAGE'
                    WHEN 'PAYMENT_WRITE' THEN 'PAYMENT_MANAGE'
                    END
FROM role_permissions rp
WHERE rp.permission IN (
                        'MEMBER_WRITE', 'ACTIVITY_WRITE', 'COMMISSION_WRITE',
                        'STUDENT_WRITE', 'TEACHER_WRITE', 'GROUP_WRITE',
                        'ATTENDANCE_WRITE', 'PEDAGOGICAL_TRACKING_WRITE',
                        'PRODUCT_WRITE', 'PAYMENT_WRITE'
    )
    ON CONFLICT (role, permission) DO NOTHING;

-- 3. Convert user permission overrides.
-- Only explicit WRITE overrides are mapped to MANAGE.
-- An existing MANAGE override, if any, is updated from the old WRITE override.
INSERT INTO user_permission_overrides (user_id, permission, granted)
SELECT upo.user_id,
       CASE upo.permission
           WHEN 'MEMBER_WRITE' THEN 'MEMBER_MANAGE'
           WHEN 'ACTIVITY_WRITE' THEN 'ACTIVITY_MANAGE'
           WHEN 'COMMISSION_WRITE' THEN 'COMMISSION_MANAGE'
           WHEN 'STUDENT_WRITE' THEN 'STUDENT_MANAGE'
           WHEN 'TEACHER_WRITE' THEN 'TEACHER_MANAGE'
           WHEN 'GROUP_WRITE' THEN 'GROUP_MANAGE'
           WHEN 'ATTENDANCE_WRITE' THEN 'ATTENDANCE_MANAGE'
           WHEN 'PEDAGOGICAL_TRACKING_WRITE' THEN 'PEDAGOGICAL_TRACKING_MANAGE'
           WHEN 'PRODUCT_WRITE' THEN 'PRODUCT_MANAGE'
           WHEN 'PAYMENT_WRITE' THEN 'PAYMENT_MANAGE'
           END,
       upo.granted
FROM user_permission_overrides upo
WHERE upo.permission IN (
                         'MEMBER_WRITE', 'ACTIVITY_WRITE', 'COMMISSION_WRITE',
                         'STUDENT_WRITE', 'TEACHER_WRITE', 'GROUP_WRITE',
                         'ATTENDANCE_WRITE', 'PEDAGOGICAL_TRACKING_WRITE',
                         'PRODUCT_WRITE', 'PAYMENT_WRITE'
    )
    ON CONFLICT (user_id, permission)
DO UPDATE SET granted = EXCLUDED.granted;

-- 4. Remove obsolete READ and WRITE permission values.
DELETE FROM role_permissions
WHERE permission IN (
                     'MEMBER_READ', 'MEMBER_WRITE',
                     'ACTIVITY_READ', 'ACTIVITY_WRITE',
                     'COMMISSION_READ', 'COMMISSION_WRITE',
                     'STUDENT_READ', 'STUDENT_WRITE',
                     'TEACHER_READ', 'TEACHER_WRITE',
                     'GROUP_READ', 'GROUP_WRITE',
                     'ATTENDANCE_READ', 'ATTENDANCE_WRITE',
                     'PEDAGOGICAL_TRACKING_READ', 'PEDAGOGICAL_TRACKING_WRITE',
                     'PRODUCT_READ', 'PRODUCT_WRITE',
                     'PAYMENT_READ', 'PAYMENT_WRITE'
    );

DELETE FROM user_permission_overrides
WHERE permission IN (
                     'MEMBER_READ', 'MEMBER_WRITE',
                     'ACTIVITY_READ', 'ACTIVITY_WRITE',
                     'COMMISSION_READ', 'COMMISSION_WRITE',
                     'STUDENT_READ', 'STUDENT_WRITE',
                     'TEACHER_READ', 'TEACHER_WRITE',
                     'GROUP_READ', 'GROUP_WRITE',
                     'ATTENDANCE_READ', 'ATTENDANCE_WRITE',
                     'PEDAGOGICAL_TRACKING_READ', 'PEDAGOGICAL_TRACKING_WRITE',
                     'PRODUCT_READ', 'PRODUCT_WRITE',
                     'PAYMENT_READ', 'PAYMENT_WRITE'
    );

-- 5. Allow only the new permission names.
ALTER TABLE role_permissions
    ADD CONSTRAINT ck_role_permissions_permission
        CHECK (permission IN (
                              'DASHBOARD_VIEW',
                              'MEMBER_MANAGE', 'ACTIVITY_MANAGE', 'COMMISSION_MANAGE',
                              'STUDENT_MANAGE', 'TEACHER_MANAGE', 'GROUP_MANAGE',
                              'ATTENDANCE_MANAGE', 'PEDAGOGICAL_TRACKING_MANAGE',
                              'PRODUCT_MANAGE', 'STOCK_WRITE', 'ORDER_READ', 'ORDER_MANAGE',
                              'PAYMENT_MANAGE', 'USER_MANAGE', 'ROLE_MANAGE', 'PERMISSION_MANAGE'
            ));

ALTER TABLE user_permission_overrides
    ADD CONSTRAINT ck_user_permission_override_permission
        CHECK (permission IN (
                              'DASHBOARD_VIEW',
                              'MEMBER_MANAGE', 'ACTIVITY_MANAGE', 'COMMISSION_MANAGE',
                              'STUDENT_MANAGE', 'TEACHER_MANAGE', 'GROUP_MANAGE',
                              'ATTENDANCE_MANAGE', 'PEDAGOGICAL_TRACKING_MANAGE',
                              'PRODUCT_MANAGE', 'STOCK_WRITE', 'ORDER_READ', 'ORDER_MANAGE',
                              'PAYMENT_MANAGE', 'USER_MANAGE', 'ROLE_MANAGE', 'PERMISSION_MANAGE'
            ));

-- 6. Ensure ADMIN has every permission.
INSERT INTO role_permissions (role, permission)
SELECT 'ADMIN', permissions.permission
FROM unnest(ARRAY[
                'DASHBOARD_VIEW',
            'MEMBER_MANAGE', 'ACTIVITY_MANAGE', 'COMMISSION_MANAGE',
            'STUDENT_MANAGE', 'TEACHER_MANAGE', 'GROUP_MANAGE',
            'ATTENDANCE_MANAGE', 'PEDAGOGICAL_TRACKING_MANAGE',
            'PRODUCT_MANAGE', 'STOCK_WRITE', 'ORDER_READ', 'ORDER_MANAGE',
            'PAYMENT_MANAGE', 'USER_MANAGE', 'ROLE_MANAGE', 'PERMISSION_MANAGE'
                ]::varchar[]) AS permissions(permission)
    ON CONFLICT (role, permission) DO NOTHING;

-- 7. Ensure TEACHER can fully manage pedagogical modules.
INSERT INTO role_permissions (role, permission)
VALUES
    ('TEACHER', 'DASHBOARD_VIEW'),
    ('TEACHER', 'STUDENT_MANAGE'),
    ('TEACHER', 'TEACHER_MANAGE'),
    ('TEACHER', 'GROUP_MANAGE'),
    ('TEACHER', 'ATTENDANCE_MANAGE'),
    ('TEACHER', 'PEDAGOGICAL_TRACKING_MANAGE'),
    ('TEACHER', 'ACTIVITY_MANAGE')
    ON CONFLICT (role, permission) DO NOTHING;
