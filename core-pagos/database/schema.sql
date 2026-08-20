CREATE DATABASE IF NOT EXISTS pasarela_pagos;
USE pasarela_pagos;

-- Tabla de Comercios
CREATE TABLE merchants (
    merchant_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    razon_social VARCHAR(100) NOT NULL,
    nombre_fantasia VARCHAR(100),
    cuit VARCHAR(20) NOT NULL UNIQUE,
    codigo_categoria VARCHAR(10),
    direccion VARCHAR(255),
    codigo_postal VARCHAR(20),
    email VARCHAR(150) NOT NULL,
    telefono VARCHAR(50),
    api_key VARCHAR(100) UNIQUE,
    secret_key VARCHAR(100)
);

-- Tabla de Clientes
CREATE TABLE customers (
    customer_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_dni VARCHAR(20) NOT NULL UNIQUE,
    categoria_consumidor VARCHAR(50),
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE
);

-- Tabla de Tokens (Son las tarjetas tokenizadas)
CREATE TABLE tokens (
    token_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token_value VARCHAR(64) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    last_four_digits CHAR(4) NOT NULL,
    card_holder_name VARCHAR(100) NOT NULL,
    expiration_month TINYINT NOT NULL,
    expiration_year SMALLINT NOT NULL,
    brand VARCHAR(20) NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

-- Tabla de Transacciones
CREATE TABLE transactions (
    transaction_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    merchant_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    token_id BIGINT NOT NULL,
    amount DECIMAL(12, 2) NOT NULL, -- DECIMAL para dinero
    currency CHAR(3) NOT NULL DEFAULT 'ARS',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    status_reason VARCHAR(255),
    idempotency_key VARCHAR(64),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (merchant_id) REFERENCES merchants(merchant_id),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (token_id) REFERENCES tokens(token_id),
    
    -- Índice para buscar rápido y evitar doble cobro
    INDEX idx_idempotency (idempotency_key) 
);

-- Tabla de Claves de Idempotencia (Historial HTTP)
CREATE TABLE idempotency_keys (
    idempotency_key VARCHAR(64) PRIMARY KEY,
    merchant_id BIGINT NOT NULL,
    request_hash VARCHAR(64),
    response_code INT,
    response_body JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (merchant_id) REFERENCES merchants(merchant_id)
);

-- Inserts para realizar pruebas iniciales
INSERT INTO merchants (razon_social, nombre_fantasia, cuit, codigo_categoria, direccion, codigo_postal, email, telefono, api_key, secret_key) 
VALUES (
    'E-Commerce Desktop S.A.', 
    'Tienda Local', 
    '30-77778888-9', 
    'RETAIL', 
    'Av. Pellegrini 1234', 
    '2000', 
    'contacto@tiendalocal.com', 
    '341-555-0000', 
    'pk_test_12345', 
    'sk_test_98765'
);

INSERT INTO customers (numero_dni, categoria_consumidor, nombre, apellido, email) 
VALUES (
    '12345678', 
    'CONSUMIDOR_FINAL', 
    'Javier', 
    'González', 
    'javier.desktop@email.com'
);

INSERT INTO tokens (token_value, customer_id, last_four_digits, card_holder_name, expiration_month, expiration_year, brand, status) 
VALUES (
    'tok_live_desktop_998877665544', 
    1, 
    '4242', 
    'JAVIER GONZALEZ', 
    12, 
    2028, 
    'VISA', 
    'ACTIVE'
);