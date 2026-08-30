-- Tabela singleton de conteúdo do site (id fixo = 1)
-- Armazena o conteúdo editável do site como JSON serializado
-- A coluna version é usada pelo Hibernate para controle de concorrência (lock otimista)

CREATE TABLE site_content (
    id      BIGINT  PRIMARY KEY,
    data    TEXT    NOT NULL,
    version BIGINT  NOT NULL DEFAULT 0,

    CONSTRAINT ck_site_content_singleton
        CHECK (id = 1),

    CONSTRAINT ck_site_content_data_not_empty
        CHECK (data <> '')
);
