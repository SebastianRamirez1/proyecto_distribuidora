CREATE TABLE IF NOT EXISTS clientes (
    id                       BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre                   VARCHAR(100)   NOT NULL,
    tipo                     VARCHAR(20)    NOT NULL,
    precio_especial_extra    DECIMAL(12, 2),
    precio_especial_normal   DECIMAL(12, 2),
    descuento_desde_canastas INTEGER,
    descuento_precio_extra   DECIMAL(12, 2),
    descuento_precio_normal  DECIMAL(12, 2)
);

CREATE TABLE IF NOT EXISTS ventas (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id      BIGINT         NOT NULL REFERENCES clientes (id),
    tipo_producto   VARCHAR(20)    NOT NULL,
    cantidad        INTEGER        NOT NULL,
    precio_unitario DECIMAL(12, 2) NOT NULL,
    tipo_pago       VARCHAR(20)    NOT NULL,
    fecha           TIMESTAMP      NOT NULL
);

CREATE TABLE IF NOT EXISTS inventario (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    stock_extra  INTEGER NOT NULL DEFAULT 0,
    stock_normal INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS caja (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha               DATE           NOT NULL UNIQUE,
    total_efectivo      DECIMAL(12, 2) NOT NULL DEFAULT 0,
    total_transferencia DECIMAL(12, 2) NOT NULL DEFAULT 0,
    total_fiado         DECIMAL(12, 2) NOT NULL DEFAULT 0,
    total_abonos        DECIMAL(12, 2) NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS creditos (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id   BIGINT         NOT NULL UNIQUE REFERENCES clientes (id),
    monto_total  DECIMAL(12, 2) NOT NULL DEFAULT 0,
    monto_pagado DECIMAL(12, 2) NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS precio_publico (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    precio_extra  DECIMAL(12, 2) NOT NULL DEFAULT 0,
    precio_normal DECIMAL(12, 2) NOT NULL DEFAULT 0
);

-- Fila única de inventario (idempotente)
INSERT INTO inventario (stock_extra, stock_normal)
    SELECT 0, 0 WHERE NOT EXISTS (SELECT 1 FROM inventario);

-- Precio público inicial en 0 (el dueño lo actualiza antes de operar)
INSERT INTO precio_publico (precio_extra, precio_normal)
    SELECT 0.00, 0.00 WHERE NOT EXISTS (SELECT 1 FROM precio_publico);
