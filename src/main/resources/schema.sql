# Deshabilita las comprobaciones de claves foráneas para evitar errores al crear tablas
set foreign_key_checks = 0;

create table if not exists users
(
    id                      bigint auto_increment,
    name                    varchar(255)                             not null,
    last_name               varchar(255)                             not null,
    phone_number            VARCHAR(255)                             not null,
    email                   varchar(255)                             not null,
    username                varchar(255)                             not null,
    password                varchar(255)                             not null,
    enabled                 boolean                                  not null default true,
    account_non_expired     boolean                                  not null default true,
    account_non_locked      boolean                                  not null default true,
    credentials_non_expired boolean                                  not null default true,
    affiliation_status      enum ('ACTIVE', 'INACTIVE', 'SUSPENDED') not null default 'ACTIVE',
    primary key (id),
    unique key uk_persons_phone_number (phone_number),
    unique key uk_persons_email (email),
    unique key uk_users_username (username)
);

create table if not exists club_administrators
(
    id      bigint not null,
    club_id bigint,
    primary key (id),
    constraint fk_club_administrators_users_id
        foreign key (id) references users (id),
    constraint fk_club_administrators_club_id
        foreign key (club_id) references clubs (id)
);

create table if not exists coaches
(
    id      bigint not null,
    club_id bigint,
    primary key (id),
    constraint fk_coaches_users_id
        foreign key (id) references users (id),
    constraint fk_coaches_club_id
        foreign key (club_id) references clubs (id)
);

create table if not exists players
(
    id      bigint not null,
    club_id bigint,
    primary key (id),
    constraint fk_players_users_id
        foreign key (id) references users (id),
    constraint fk_players_club_id
        foreign key (club_id) references clubs (id)
);

create table if not exists clubs
(
    id                    bigint auto_increment,
    club_administrator_id bigint,
    coach_id              bigint,
    player_id             bigint,
    name                  varchar(255) not null,
    address               varchar(255) not null,
    phone_number          varchar(255) not null,
    enabled               boolean      not null default true,
    creation_date         datetime     not null default now(),
    primary key (id),
    constraint fk_clubs_club_administrators_id
        foreign key (club_administrator_id) references club_administrators (id),
    constraint fk_clubs_coaches_id
        foreign key (coach_id) references coaches (id),
    constraint fk_clubs_players_id foreign key (player_id) references players (id),
    unique key uk_clubs_name (name),
    unique key uk_clubs_phone_number (phone_number)

);

create table if not exists roles
(
    id   bigint auto_increment,
    name varchar(255) not null,
    primary key (id),
    unique key uk_roles_name (name)
);

create table if not exists authorities
(
    id   bigint auto_increment,
    name varchar(255) not null,
    primary key (id),
    unique key uk_authorities_name (name)
);

create table if not exists users_roles
(
    user_id bigint not null,
    role_id bigint not null,
    primary key (user_id, role_id),
    constraint fk_users_roles_user_id foreign key (user_id) references users (id),
    constraint fk_users_roles_role_id foreign key (role_id) references roles (id)
);

create table if not exists roles_authorities
(
    role_id      bigint not null,
    authority_id bigint not null,
    primary key (role_id, authority_id),
    constraint fk_roles_authorities_role_id foreign key (role_id) references roles (id),
    constraint fk_roles_authorities_authority_id foreign key (authority_id) references authorities (id)
);

# Habilita las comprobaciones de claves foráneas de nuevo para evitar errores al crear tablas
set foreign_key_checks = 1;