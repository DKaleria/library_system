--liquibase formatted sql
--changeset Valeriya:24-10-2024-created-author-tbl runOnChange:false

create table if not exists genre
(
    id   bigserial
    primary key,
    name varchar not null
    unique
);

comment on column genre.id is 'ID жанра';

comment on column genre.name is 'Название жанра';

