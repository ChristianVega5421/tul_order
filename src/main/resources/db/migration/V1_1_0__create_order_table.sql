CREATE TABLE sale."order"
(
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    user_id uuid NOT NULL,
    created_on timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_on timestamp with time zone,
    status character varying(100) NOT NULL,
    PRIMARY KEY (id)
);

ALTER TABLE sale."order"
    OWNER to postgres;