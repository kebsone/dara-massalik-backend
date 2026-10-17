create table members (
    id bigserial primary key,
    first_name varchar(80) not null,
    last_name varchar(80) not null,
    email varchar(160),
    phone varchar(30),
    address varchar(255),
    joined_date date not null,
    active boolean not null default true
);

create table commissions (
    id bigserial primary key,
    name varchar(120) not null unique,
    description varchar(1000),
    active boolean not null default true
);

create table commission_members (
    id bigserial primary key,
    commission_id bigint not null references commissions(id),
    member_id bigint not null references members(id),
    role_label varchar(120),
    joined_date date not null,
    constraint uq_commission_member unique (commission_id, member_id)
);

create table activities (
    id bigserial primary key,
    name varchar(160) not null,
    description varchar(1500),
    activity_date date not null,
    location varchar(200),
    status varchar(30) not null,
    commission_id bigint references commissions(id)
);

create table shop_products (
    id bigserial primary key,
    name varchar(160) not null,
    reference varchar(80) not null unique,
    category varchar(80) not null,
    description varchar(1500),
    price numeric(10,2) not null,
    stock integer not null default 0,
    image_url varchar(500),
    active boolean not null default true,
    constraint ck_shop_product_price check (price >= 0),
    constraint ck_shop_product_stock check (stock >= 0)
);

insert into members(first_name,last_name,email,phone,address,joined_date,active) values
('Mamadou','Diop','mamadou.diop@daramassalik.local','0610000001',null,'2025-01-12',true),
('Fatou','Fall','fatou.fall@daramassalik.local','0610000002',null,'2025-02-05',true),
('Abdou','Ndiaye','abdou.ndiaye@daramassalik.local','0610000003',null,'2025-03-10',true);

insert into commissions(name,description,active) values
('Éducation','Pilotage de l’école coranique et des actions en faveur des enfants.',true),
('Boutique','Gestion des produits, du stock, des ventes et des approvisionnements.',true),
('Communication','Informations aux membres, annonces et communication événementielle.',true),
('Femmes','Coordination des activités et initiatives portées par les femmes.',true),
('Finances','Suivi des cotisations, dépenses et reporting financier.',true),
('Jeunesse','Activités jeunesse, accompagnement des enfants et événements dédiés.',true);

insert into commission_members(commission_id,member_id,role_label,joined_date) values
(1,1,'Coordinateur','2025-01-12'),
(1,2,'Membre','2025-02-05'),
(2,2,'Responsable','2025-02-05'),
(2,3,'Membre','2025-03-10'),
(3,1,'Responsable','2025-01-12'),
(3,3,'Membre','2025-03-10'),
(6,3,'Membre','2025-03-10');

insert into activities(name,description,activity_date,location,status,commission_id) values
('Cours du dimanche','Cours hebdomadaire de l’école coranique.','2026-10-04','Salle principale','PLANNED',1),
('Journée des enfants','Activités éducatives et animations pour les enfants.','2026-10-18','Dahira','PLANNED',3),
('Réunion de commission','Point de coordination des activités du Dahira.','2026-10-11','Salle de réunion','PLANNED',3);

insert into shop_products(name,reference,category,description,price,stock,image_url,active) values
('Tunique enfant','TUN-ENF-01','Clothing','Tunique légère et confortable pour les enfants, adaptée aux cours et aux activités du Dahira.',18.00,14,null,true),
('Livre Coran – édition enfant','COR-ENF-02','Books','Édition pensée pour accompagner l’apprentissage et la lecture du Coran chez les enfants.',12.00,22,null,true),
('T-shirt Dara Massalik','TSH-DM-04','Clothing','T-shirt aux couleurs de Dara Massalik, disponible pour enfants et adultes.',10.00,31,null,true),
('Chapelet','CHA-001','Accessories','Chapelet classique, compact et facile à emporter pour les activités quotidiennes.',5.00,18,null,true),
('Agenda du Dahira','AGE-DM-01','Books','Agenda annuel pour noter les cours, activités, réunions et temps forts du Dahira.',8.00,11,null,true),
('Sac enfant','SAC-ENF-01','Accessories','Sac pratique pour transporter les cahiers, livres et fournitures des enfants.',20.00,9,null,true);
