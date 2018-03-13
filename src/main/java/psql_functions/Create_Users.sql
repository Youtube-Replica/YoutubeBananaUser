CREATE TABLE public.app_user
(
    id                BIGSERIAL     PRIMARY KEY NOT NULL,
    user_name         VARCHAR(255)               NOT NULL,
    email             VARCHAR(255)               NOT NULL,
    password          VARCHAR(255)               NOT NULL,
    UNIQUE(email)
);
