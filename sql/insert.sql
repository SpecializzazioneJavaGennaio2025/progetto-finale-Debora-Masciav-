insert into users (username, email, password, created_at) values ('admin', 'admin@aulab.it', '$2a$10$oMiUOq5ToRfUI/Zprg5nE.qt8nT9KKJZoDBu1SIWuj.UGx8aRHwxS', '2025-02-10');

insert into roles (name) values ('ROLE_ADMIN');
insert into roles (name) values ('ROLE_USER');
insert into roles (name) values ('ROLE_WRITER');
insert into roles (name) values ('ROLE_REVISOR');

insert INTO users_roles (user_id, role_id) VALUES (1, 1);

insert into categories (name) values ('politica'), ('economia'), ('food&drink'), ('sport'), ('intrattenimento'), ('tech');
