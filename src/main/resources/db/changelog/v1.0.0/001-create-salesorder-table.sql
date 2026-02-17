-- liquibase formatted sql

-- changeset gemini:1
CREATE TABLE salesorder (
    id UUID PRIMARY KEY,
    order_date TIMESTAMP WITH TIME ZONE NOT NULL,
    customer_name VARCHAR(255) NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- rollback DROP TABLE salesorder;
