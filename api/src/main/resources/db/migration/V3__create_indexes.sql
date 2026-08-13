CREATE EXTENSION IF NOT EXISTS pg_trgm;



-- Busca de catálogo:
-- categoria + status + ordenação

CREATE INDEX idx_products_catalog

    ON products(

                category_id,

                status,

                position

        );



-- Busca textual:
-- nome contendo termo

CREATE INDEX idx_products_name_search

    ON products

        USING gin(name gin_trgm_ops);



-- Busca textual de categoria

CREATE INDEX idx_categories_name_search

    ON categories

        USING gin(name gin_trgm_ops);