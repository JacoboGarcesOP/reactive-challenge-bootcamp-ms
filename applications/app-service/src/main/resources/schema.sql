CREATE SCHEMA IF NOT EXISTS bootcamp_schema;

CREATE TABLE IF NOT EXISTS bootcamp_schema.bootcamp (
    bootcamp_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(90) NOT NULL,
    launch_date DATE NOT NULL,
    duration INTEGER NOT NULL CHECK (duration > 0),
);
