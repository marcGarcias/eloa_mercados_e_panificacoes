CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX idx_products_catalog

    ON products(

                category_id,

                status,

                position

        );

CREATE INDEX idx_products_name_search

    ON products

        USING gin(name gin_trgm_ops);

CREATE INDEX idx_categories_name_search

    ON categories

        USING gin(name gin_trgm_ops);
