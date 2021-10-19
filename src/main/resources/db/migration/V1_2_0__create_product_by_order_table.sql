CREATE TABLE sale.product_by_order
(
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    sku uuid NOT NULL,
    order_id uuid NOT NULL,
    name character varying(150) NOT NULL,
    quantity smallint NOT NULL,
    price numeric NOT NULL,
    created_on timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

ALTER TABLE sale.product_by_order
    OWNER to postgres;