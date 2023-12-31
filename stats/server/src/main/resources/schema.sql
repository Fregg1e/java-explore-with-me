CREATE TABLE IF NOT EXISTS endpoint_hit
(
    endpoint_hit_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    app VARCHAR(50) NOT NULL,
    uri VARCHAR(50) NOT NULL,
    ip VARCHAR(50) NOT NULL,
    endpoint_hit_timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_endpoint_hit PRIMARY KEY (endpoint_hit_id)
);