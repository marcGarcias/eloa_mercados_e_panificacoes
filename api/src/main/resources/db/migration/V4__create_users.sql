CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       name VARCHAR(150) NOT NULL,
                       user_code VARCHAR(20) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(30) NOT NULL,
                       status VARCHAR(30) NOT NULL,
                       last_login_at TIMESTAMP,
                       created_at TIMESTAMP NOT NULL,
                       updated_at TIMESTAMP NOT NULL,

                       CONSTRAINT uk_users_user_code
                           UNIQUE(user_code),

                       CONSTRAINT ck_users_user_code_numeric
                           CHECK(user_code ~ '^[0-9]+$'),

                       CONSTRAINT ck_users_role
                           CHECK(role IN ('SUPER_ADMIN', 'ADMIN', 'EDITOR')),

                       CONSTRAINT ck_users_status
                           CHECK(status IN ('ACTIVE', 'INACTIVE'))
);


CREATE EXTENSION IF NOT EXISTS pg_trgm;


-- Busca textual de usuário pelo nome
CREATE INDEX idx_users_name_search
    ON users
        USING gin(name gin_trgm_ops);