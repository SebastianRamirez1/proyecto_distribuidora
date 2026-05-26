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

CREATE TABLE IF NOT EXISTS abonos (
    id          BIGSERIAL PRIMARY KEY,
    cliente_id  BIGINT         NOT NULL REFERENCES clientes (id),
    monto       DECIMAL(12, 2) NOT NULL,
    medio_pago  VARCHAR(20)    NOT NULL,
    fecha       TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS precio_publico (
    id           BIGSERIAL PRIMARY KEY,
    precio_extra DECIMAL(12, 2) NOT NULL DEFAULT 0,
    precio_aa    DECIMAL(12, 2) NOT NULL DEFAULT 0,
    precio_a     DECIMAL(12, 2) NOT NULL DEFAULT 0,
    precio_b     DECIMAL(12, 2) NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS precio_costo (
    id           BIGSERIAL PRIMARY KEY,
    costo_extra  DECIMAL(12, 2) NOT NULL DEFAULT 0,
    costo_aa     DECIMAL(12, 2) NOT NULL DEFAULT 0,
    costo_a      DECIMAL(12, 2) NOT NULL DEFAULT 0,
    costo_b      DECIMAL(12, 2) NOT NULL DEFAULT 0
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

-- anulación de ventas (soft delete)
ALTER TABLE ventas ADD COLUMN IF NOT EXISTS anulada         BOOLEAN   NOT NULL DEFAULT FALSE;
ALTER TABLE ventas ADD COLUMN IF NOT EXISTS fecha_anulacion TIMESTAMP;

-- costo de liquidación por venta (nullable para compatibilidad con registros anteriores)
ALTER TABLE ventas ADD COLUMN IF NOT EXISTS costo_unitario DECIMAL(12, 2);

CREATE TABLE IF NOT EXISTS configuracion_factura (
    id                 BIGSERIAL    PRIMARY KEY,
    razon_social       VARCHAR(200) NOT NULL DEFAULT '',
    nit                VARCHAR(20)  NOT NULL DEFAULT '',
    direccion          VARCHAR(200) NOT NULL DEFAULT '',
    ciudad             VARCHAR(100) NOT NULL DEFAULT '',
    telefono           VARCHAR(50)  NOT NULL DEFAULT '',
    regimen            VARCHAR(150) NOT NULL DEFAULT 'No responsable de IVA',
    resolucion_numero  VARCHAR(50)  NOT NULL DEFAULT '',
    resolucion_fecha   DATE,
    resolucion_prefijo VARCHAR(10)  NOT NULL DEFAULT 'FAC',
    resolucion_desde   INTEGER      NOT NULL DEFAULT 1,
    resolucion_hasta   INTEGER      NOT NULL DEFAULT 9999,
    consecutivo_actual INTEGER      NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS facturas (
    id              BIGSERIAL     PRIMARY KEY,
    numero          VARCHAR(20)   NOT NULL UNIQUE,
    venta_id        BIGINT        REFERENCES ventas(id),
    cliente_id      BIGINT        NOT NULL REFERENCES clientes(id),
    fecha_emision   TIMESTAMP     NOT NULL DEFAULT NOW(),
    tipo            VARCHAR(20)   NOT NULL DEFAULT 'MANUAL',
    estado          VARCHAR(20)   NOT NULL DEFAULT 'EMITIDA',
    nombre_cliente  VARCHAR(200)  NOT NULL,
    nit_cliente     VARCHAR(30)   NOT NULL DEFAULT 'Sin NIT',
    tipo_producto   VARCHAR(20)   NOT NULL,
    cantidad        INTEGER       NOT NULL,
    precio_unitario DECIMAL(12,2) NOT NULL,
    total           DECIMAL(12,2) NOT NULL,
    tipo_pago       VARCHAR(20)   NOT NULL
);

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

-- Precio de liquidación/costo inicial en 0 (configurable)
INSERT INTO precio_costo (costo_extra, costo_aa, costo_a, costo_b)
SELECT 0.00, 0.00, 0.00, 0.00
WHERE NOT EXISTS (SELECT 1 FROM precio_costo);

-- ============================================================
-- Limpieza de filas duplicadas en tablas singleton
-- Conserva solo la fila de menor id para garantizar
-- resultados deterministas en findFirstByOrderByIdAsc()
-- ============================================================
DELETE FROM inventario    WHERE id NOT IN (SELECT MIN(id) FROM inventario);
DELETE FROM precio_publico WHERE id NOT IN (SELECT MIN(id) FROM precio_publico);
DELETE FROM precio_costo   WHERE id NOT IN (SELECT MIN(id) FROM precio_costo);

-- Fila única de configuracion_factura
INSERT INTO configuracion_factura (razon_social, nit, direccion, ciudad, telefono, regimen,
    resolucion_numero, resolucion_prefijo, resolucion_desde, resolucion_hasta, consecutivo_actual)
SELECT '', '', '', '', '', 'No responsable de IVA', '', 'FAC', 1, 9999, 1
WHERE NOT EXISTS (SELECT 1 FROM configuracion_factura);

DELETE FROM configuracion_factura WHERE id NOT IN (SELECT MIN(id) FROM configuracion_factura);
