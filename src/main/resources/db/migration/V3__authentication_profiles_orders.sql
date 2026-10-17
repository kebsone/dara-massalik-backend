alter table app_users
    alter column email drop not null,
    alter column password_hash drop not null;

alter table app_users
    add column phone varchar(30),
    add column first_name varchar(80),
    add column last_name varchar(80),
    add column created_at timestamptz not null default current_timestamp,
    add column updated_at timestamptz not null default current_timestamp;

create unique index uq_app_users_phone on app_users(phone) where phone is not null;

create table otp_challenges (
    id uuid primary key,
    phone varchar(30) not null,
    user_id bigint references app_users(id),
    code_hash varchar(255) not null,
    expires_at timestamptz not null,
    attempts integer not null default 0,
    consumed_at timestamptz,
    created_at timestamptz not null default current_timestamp
);

create index ix_otp_challenges_phone_created on otp_challenges(phone, created_at desc);

create table refresh_tokens (
    id uuid primary key,
    user_id bigint not null references app_users(id) on delete cascade,
    token_hash varchar(64) not null unique,
    expires_at timestamptz not null,
    created_at timestamptz not null default current_timestamp,
    revoked_at timestamptz
);

create index ix_refresh_tokens_user_id on refresh_tokens(user_id);

create table customer_addresses (
    id bigserial primary key,
    user_id bigint not null references app_users(id) on delete cascade,
    label varchar(80),
    first_name varchar(80) not null,
    last_name varchar(80) not null,
    address_line1 varchar(255) not null,
    address_line2 varchar(255),
    postal_code varchar(20) not null,
    city varchar(120) not null,
    country varchar(120) not null,
    is_default boolean not null default false,
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp
);

create index ix_customer_addresses_user_id on customer_addresses(user_id);

create table orders (
    id bigserial primary key,
    order_number varchar(40) not null unique,
    user_id bigint not null references app_users(id),
    status varchar(30) not null,
    total_amount numeric(10,2) not null,
    currency varchar(3) not null default 'EUR',
    shipping_address_id bigint references customer_addresses(id),
    shipping_first_name varchar(80) not null,
    shipping_last_name varchar(80) not null,
    shipping_address_line1 varchar(255) not null,
    shipping_address_line2 varchar(255),
    shipping_postal_code varchar(20) not null,
    shipping_city varchar(120) not null,
    shipping_country varchar(120) not null,
    created_at timestamptz not null default current_timestamp,
    updated_at timestamptz not null default current_timestamp
);

create index ix_orders_user_id_created_at on orders(user_id, created_at desc);

create table order_items (
    id bigserial primary key,
    order_id bigint not null references orders(id) on delete cascade,
    product_id bigint references shop_products(id),
    product_name varchar(160) not null,
    unit_price numeric(10,2) not null,
    quantity integer not null,
    line_total numeric(10,2) not null,
    constraint ck_order_item_quantity check (quantity > 0)
);

create index ix_order_items_order_id on order_items(order_id);

update app_users
set updated_at = current_timestamp
where updated_at is null;
