create table goods
(
    id          serial primary key,
    name        varchar(255)   not null,
    description text,
    price       numeric(10, 2) not null,
    created_at  timestamp default current_timestamp
);