CREATE TABLE site_content (
    id      BIGINT  PRIMARY KEY,
    data    TEXT    NOT NULL,
    version BIGINT  NOT NULL DEFAULT 0,

    CONSTRAINT ck_site_content_singleton
        CHECK (id = 1),

    CONSTRAINT ck_site_content_data_not_empty
        CHECK (data <> '')
);

