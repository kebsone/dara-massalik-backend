create table teachers (
    id bigserial primary key,
    first_name varchar(80) not null,
    last_name varchar(80) not null,
    email varchar(160)
);

create table school_groups (
    id bigserial primary key,
    name varchar(100) not null unique,
    teacher_id bigint references teachers(id)
);

create table students (
    id bigserial primary key,
    first_name varchar(80) not null,
    last_name varchar(80) not null,
    birth_date date,
    phone varchar(30),
    parent_name varchar(160),
    parent_phone varchar(30),
    active boolean not null default true,
    group_id bigint references school_groups(id)
);

create table app_users (
    id bigserial primary key,
    email varchar(160) not null unique,
    password_hash varchar(255) not null,
    role varchar(20) not null,
    active boolean not null default true
);

create table attendance (
    id bigserial primary key,
    student_id bigint not null references students(id),
    attendance_date date not null,
    status varchar(30) not null,
    comment varchar(500),
    teacher_id bigint references teachers(id),
    constraint uq_attendance_student_date unique (student_id, attendance_date)
);

create table pedagogical_tracking (
    id bigserial primary key,
    student_id bigint not null references students(id),
    teacher_id bigint references teachers(id),
    date date not null,
    surah varchar(120),
    completed_today varchar(1000),
    to_prepare varchar(1000),
    observations varchar(1500)
);

create table payments (
    id bigserial primary key,
    student_id bigint not null references students(id),
    month integer not null,
    year integer not null,
    amount numeric(10,2) not null,
    payment_date date not null,
    comment varchar(500),
    constraint uq_payment_student_month_year unique (student_id, month, year),
    constraint ck_payment_month check (month between 1 and 12)
);

insert into teachers(first_name,last_name,email) values ('Aminata','Fall','aminata@daramassalik.local');
insert into school_groups(name,teacher_id) values ('Groupe 1',1), ('Groupe 2',1), ('Groupe 3',1);
insert into students(first_name,last_name,birth_date,phone,parent_name,parent_phone,group_id) values
('Amadou','Diop','2017-05-12','0600000001','Mamadou Diop','0610000001',1),
('Moussa','Ndiaye','2016-10-03','0600000002','Abdou Ndiaye','0610000002',1),
('Aissatou','Fall','2018-01-22','0600000003','Fatou Fall','0610000003',2);
