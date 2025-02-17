--liquibase formatted sql
--changeset Valeriya:24-10-2024-created-author-tbl runOnChange:false

INSERT INTO genre (id, name) VALUES (1, 'фэнтези');
INSERT INTO genre (id, name) VALUES (2, 'роман');
INSERT INTO genre (id, name) VALUES (3, 'сатира');
INSERT INTO genre (id, name) VALUES (4, 'комедия');
