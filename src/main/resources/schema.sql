-- ============================================================
-- Tablas principales (idempotentes con IF NOT EXISTS)
-- ============================================================

CREATE TABLE IF NOT EXISTS clientes (
    id                       BIGSERIAL PRIMARY KEY,
    nombre                   VARCHAR(100)   NOT NULL,
    tipo                     VARCHAR(20)    NOT NULL,
    -- Precios especiales por tipo de huevo
    precio_especial_extra    DECIMAL(12, 2),
    precio_especial_aa       DECIMAL(12, 2),
    precio_especial_a        DECIMAL(12, 2),
    precio_especial_b        DECIMAL(12, 2),
    -- Descuento por volumen
    descuento_desde_canastas INTEGER,
    descuento_precio_extra   DECIMAL(12, 2),
    descuento_precio_aa      DECIMAL(12, 2),
    descuento_precio_a       DECIMAL(12, 2),
    descuento_precio_b       DECIMAL(12, 2)
);

CREATE TABLE IF NOT EXISTS ventas (
    id              BIGSERIAL PRIMARY KEY,
    cliente_id      BIGINT         NOT NULL REFERENCES clientes (id),
    tipo_producto   VARCHAR(20)    NOT NULL,
    cantidad        INTEGER        NOT NULL,
    precio_unitario DECIMAL(12, 2) NOT NULL,
    tipo_pago       VARCHAR(20)    NOT NULL,
    fecha           TIMESTAMP      NOT NULL
);

CREATE TABLE IF NOT EXISTS inventario (
    id          BIGSERIAL PRIMARY KEY,
    stock_extra INTEGER NOT NULL DEFAULT 0,
    stock_aa    INTEGER NOT NULL DEFAULT 0,
    stock_a     INTEGER NOT NULL DEFAULT 0,
    stock_b     INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS caja (
    id                   BIGSERIAL PRIMARY KEY,
    fecha                DATE           NOT NULL UNIQUE,
    total_efectivo       DECIMAL(12, 2) NOT NULL DEFAULT 0,
    total_transferencia  DECIMAL(12, 2) NOT NULL DEFAULT 0,
    total_fiado          DECIMAL(12, 2) NOT NULL DEFAULT 0,
    total_abonos         DECIMAL(12, 2) NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS creditos (
    id           BIGSERIAL PRIMARY KEY,
    cliente_id   BIGINT         NOT NULL UNIQUE REFERENCES clientes (id),
    monto_total  DECIMAL(12, 2) NOT NULL DEFAULT 0,
    monto_pagado DECIMAL(12, 2) NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS precio_publico (
    id           BIGSERIAL PRIMARY KEY,
    precio_extra DECIMAL(12, 2) NOT NULL DEFAULT 0,
    precio_aa    DECIMAL(12, 2) NOT NULL DEFAULT 0,
    precio_a     DECIMAL(12, 2) NOT NULL DEFAULT 0,
    precio_b     DECIMAL(12, 2) NOT NULL DEFAULT 0
);

-- ============================================================
-- Migración idempotente: agregar columnas nuevas en BD existente
-- ============================================================

-- inventario: nuevos tipos de huevo
ALTER TABLE inventario ADD COLUMN IF NOT EXISTS stock_aa INTEGER NOT NULL DEFAULT 0;
ALTER TABLE inventario ADD COLUMN IF NOT EXISTS stock_a  INTEGER NOT NULL DEFAULT 0;
ALTER TABLE inventario ADD COLUMN IF NOT EXISTS stock_b  INTEGER NOT NULL DEFAULT 0;

-- precio_publico: nuevos tipos de huevo
ALTER TABLE precio_publico ADD COLUMN IF NOT EXISTS precio_aa DECIMAL(12, 2) NOT NULL DEFAULT 0;
ALTER TABLE precio_publico ADD COLUMN IF NOT EXISTS precio_a  DECIMAL(12, 2) NOT NULL DEFAULT 0;
ALTER TABLE precio_publico ADD COLUMN IF NOT EXISTS precio_b  DECIMAL(12, 2) NOT NULL DEFAULT 0;

-- clientes: nuevos precios especiales y descuentos
ALTER TABLE clientes ADD COLUMN IF NOT EXISTS precio_especial_aa    DECIMAL(12, 2);
ALTER TABLE clientes ADD COLUMN IF NOT EXISTS precio_especial_a     DECIMAL(12, 2);
ALTER TABLE clientes ADD COLUMN IF NOT EXISTS precio_especial_b     DECIMAL(12, 2);
ALTER TABLE clientes ADD COLUMN IF NOT EXISTS descuento_precio_aa   DECIMAL(12, 2);
ALTER TABLE clientes ADD COLUMN IF NOT EXISTS descuento_precio_a    DECIMAL(12, 2);
ALTER TABLE clientes ADD COLUMN IF NOT EXISTS descuento_precio_b    DECIMAL(12, 2);

-- ventas existentes con tipo_producto = 'NORMAL' se migran a 'A'
UPDATE ventas SET tipo_producto = 'A' WHERE tipo_producto = 'NORMAL';

-- ============================================================
-- Datos iniciales (idempotentes)
-- ============================================================

-- Fila única de inventario
INSERT INTO inventario (stock_extra, stock_aa, stock_a, stock_b)
SELECT 0, 0, 0, 0
WHERE NOT EXISTS (SELECT 1 FROM inventario);

-- Precio público inicial en 0 (el dueño lo configura antes de operar)
INSERT INTO precio_publico (precio_extra, precio_aa, precio_a, precio_b)
SELECT 0.00, 0.00, 0.00, 0.00
WHERE NOT EXISTS (SELECT 1 FROM precio_publico);

-- ============================================================
-- Limpieza de filas duplicadas en tablas singleton
-- Conserva solo la fila de menor id para garantizar
-- resultados deterministas en findFirstByOrderByIdAsc()
-- ============================================================
DELETE FROM inventario    WHERE id NOT IN (SELECT MIN(id) FROM inventario);
DELETE FROM precio_publico WHERE id NOT IN (SELECT MIN(id) FROM precio_publico);
