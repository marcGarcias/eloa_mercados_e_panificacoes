CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    weight NUMERIC(10,3),
    position BIGINT NOT NULL,
    photo VARCHAR(500) NOT NULL,
    category_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL,

    CONSTRAINT fk_products_category
        FOREIGN KEY(category_id)
            REFERENCES categories(id)
            ON DELETE RESTRICT
);