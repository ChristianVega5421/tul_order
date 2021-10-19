CREATE TABLE sale.product
(
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    sku uuid NOT NULL,
    stock integer NOT NULL,
    created_on timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

ALTER TABLE sale.product
    OWNER to postgres;