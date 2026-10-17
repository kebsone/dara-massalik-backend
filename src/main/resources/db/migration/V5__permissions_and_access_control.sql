-- Fine-grained permissions. Roles provide defaults; user overrides may grant or deny a permission.
create table role_permissions (
    id bigserial primary key,
    role varchar(30) not null,
    permission varchar(50) not null,
    constraint uq_role_permissions unique (role, permission),
    constraint ck_role_permissions_role check (role in ('ADMIN', 'TEACHER', 'TREASURER', 'MEMBER', 'STUDENT', 'CUSTOMER', 'PARENT')),
    constraint ck_role_permissions_permission check (permission in (
        'DASHBOARD_VIEW','MEMBER_READ','MEMBER_WRITE','ACTIVITY_READ','ACTIVITY_WRITE',
        'COMMISSION_READ','COMMISSION_WRITE','STUDENT_READ','STUDENT_WRITE','TEACHER_READ','TEACHER_WRITE',
        'GROUP_READ','GROUP_WRITE','ATTENDANCE_READ','ATTENDANCE_WRITE',
        'PEDAGOGICAL_TRACKING_READ','PEDAGOGICAL_TRACKING_WRITE','PRODUCT_READ','PRODUCT_WRITE','STOCK_WRITE',
        'ORDER_READ','ORDER_MANAGE','PAYMENT_READ','PAYMENT_WRITE','USER_MANAGE','ROLE_MANAGE','PERMISSION_MANAGE'
    ))
);

create table user_permission_overrides (
    id bigserial primary key,
    user_id bigint not null references app_users(id) on delete cascade,
    permission varchar(50) not null,
    granted boolean not null,
    constraint uq_user_permission_override unique (user_id, permission),
    constraint ck_user_permission_override_permission check (permission in (
        'DASHBOARD_VIEW','MEMBER_READ','MEMBER_WRITE','ACTIVITY_READ','ACTIVITY_WRITE',
        'COMMISSION_READ','COMMISSION_WRITE','STUDENT_READ','STUDENT_WRITE','TEACHER_READ','TEACHER_WRITE',
        'GROUP_READ','GROUP_WRITE','ATTENDANCE_READ','ATTENDANCE_WRITE',
        'PEDAGOGICAL_TRACKING_READ','PEDAGOGICAL_TRACKING_WRITE','PRODUCT_READ','PRODUCT_WRITE','STOCK_WRITE',
        'ORDER_READ','ORDER_MANAGE','PAYMENT_READ','PAYMENT_WRITE','USER_MANAGE','ROLE_MANAGE','PERMISSION_MANAGE'
    ))
);

create index ix_role_permissions_role on role_permissions(role);
create index ix_user_permission_overrides_user on user_permission_overrides(user_id);

-- Default permissions by role.
insert into role_permissions(role, permission) values
('ADMIN', 'DASHBOARD_VIEW'), ('ADMIN', 'MEMBER_READ'), ('ADMIN', 'MEMBER_WRITE'), ('ADMIN', 'ACTIVITY_READ'), ('ADMIN', 'ACTIVITY_WRITE'),
('ADMIN', 'COMMISSION_READ'), ('ADMIN', 'COMMISSION_WRITE'), ('ADMIN', 'STUDENT_READ'), ('ADMIN', 'STUDENT_WRITE'),
('ADMIN', 'TEACHER_READ'), ('ADMIN', 'TEACHER_WRITE'), ('ADMIN', 'GROUP_READ'), ('ADMIN', 'GROUP_WRITE'),
('ADMIN', 'ATTENDANCE_READ'), ('ADMIN', 'ATTENDANCE_WRITE'), ('ADMIN', 'PEDAGOGICAL_TRACKING_READ'), ('ADMIN', 'PEDAGOGICAL_TRACKING_WRITE'),
('ADMIN', 'PRODUCT_READ'), ('ADMIN', 'PRODUCT_WRITE'), ('ADMIN', 'STOCK_WRITE'), ('ADMIN', 'ORDER_READ'), ('ADMIN', 'ORDER_MANAGE'),
('ADMIN', 'PAYMENT_READ'), ('ADMIN', 'PAYMENT_WRITE'), ('ADMIN', 'USER_MANAGE'), ('ADMIN', 'ROLE_MANAGE'), ('ADMIN', 'PERMISSION_MANAGE'),
('TEACHER', 'DASHBOARD_VIEW'), ('TEACHER', 'STUDENT_READ'), ('TEACHER', 'TEACHER_READ'), ('TEACHER', 'GROUP_READ'),
('TEACHER', 'ATTENDANCE_READ'), ('TEACHER', 'ATTENDANCE_WRITE'), ('TEACHER', 'PEDAGOGICAL_TRACKING_READ'), ('TEACHER', 'PEDAGOGICAL_TRACKING_WRITE'),
('TEACHER', 'ACTIVITY_READ'),
('TREASURER', 'DASHBOARD_VIEW'), ('TREASURER', 'PAYMENT_READ'), ('TREASURER', 'PAYMENT_WRITE'),
('MEMBER', 'DASHBOARD_VIEW'), ('MEMBER', 'MEMBER_READ'), ('MEMBER', 'ACTIVITY_READ'), ('MEMBER', 'COMMISSION_READ'),
('STUDENT', 'DASHBOARD_VIEW'), ('STUDENT', 'STUDENT_READ'), ('STUDENT', 'ATTENDANCE_READ'), ('STUDENT', 'PEDAGOGICAL_TRACKING_READ'),
('CUSTOMER', 'DASHBOARD_VIEW'), ('CUSTOMER', 'PRODUCT_READ'), ('CUSTOMER', 'ORDER_READ'),
('PARENT', 'DASHBOARD_VIEW'), ('PARENT', 'STUDENT_READ'), ('PARENT', 'ATTENDANCE_READ'), ('PARENT', 'PEDAGOGICAL_TRACKING_READ')
ON CONFLICT DO NOTHING;
