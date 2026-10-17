-- User roles are now many-to-many in concept: one account can have several roles.
-- Keep the legacy app_users.role column for backwards compatibility during the transition.
alter table app_users
    alter column role drop not null;

create table user_roles (
    user_id bigint not null references app_users(id) on delete cascade,
    role varchar(30) not null,
    constraint pk_user_roles primary key (user_id, role),
    constraint ck_user_roles_role check (
        role in ('ADMIN', 'TEACHER', 'TREASURER', 'MEMBER', 'STUDENT', 'CUSTOMER', 'PARENT')
    )
);

insert into user_roles(user_id, role)
select id, role
from app_users
where role is not null
on conflict (user_id, role) do nothing;

create index ix_user_roles_role on user_roles(role);

alter table students
    add column user_id bigint references app_users(id);

create unique index uq_students_user_id
    on students(user_id)
    where user_id is not null;

alter table teachers
    add column user_id bigint references app_users(id);

create unique index uq_teachers_user_id
    on teachers(user_id)
    where user_id is not null;

alter table members
    add column user_id bigint references app_users(id);

create unique index uq_members_user_id
    on members(user_id)
    where user_id is not null;
