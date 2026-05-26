-- ============================================================
-- LIMPIEZA COMPLETA DE DATOS DE PRUEBA
-- ⚠️  EJECUTAR UNA SOLA VEZ antes de entrar en producción.
-- ⚠️  Esta operación NO se puede deshacer.
--
-- Conserva : configuracion_factura (razón social, NIT, resolución DIAN)
-- Elimina  : clientes, ventas, facturas, abonos, créditos, caja
-- Resetea  : inventario → stock 0  |  precios → $ 0
-- ============================================================

BEGIN;

-- ── 1. Datos transaccionales (respeta orden de FK) ───────────────────────────
TRUNCATE TABLE abonos    RESTART IDENTITY CASCADE;
TRUNCATE TABLE creditos  RESTART IDENTITY CASCADE;
TRUNCATE TABLE facturas  RESTART IDENTITY CASCADE;
TRUNCATE TABLE ventas    RESTART IDENTITY CASCADE;
TRUNCATE TABLE clientes  RESTART IDENTITY CASCADE;
TRUNCATE TABLE caja      RESTART IDENTITY CASCADE;

-- ── 2. Resetear stock e inventario a cero ────────────────────────────────────
UPDATE inventario
   SET stock_extra = 0,
       stock_aa    = 0,
       stock_a     = 0,
       stock_b     = 0;

-- ── 3. Resetear precios a cero (para que el dueño los configure de nuevo) ────
UPDATE precio_publico
   SET precio_extra = 0,
       precio_aa    = 0,
       precio_a     = 0,
       precio_b     = 0;

UPDATE precio_costo
   SET costo_extra = 0,
       costo_aa    = 0,
       costo_a     = 0,
       costo_b     = 0;

-- ── 4. Reiniciar consecutivo de facturas a 1 ─────────────────────────────────
--  (La resolución, NIT y demás config se conservan intactos)
UPDATE configuracion_factura SET consecutivo_actual = 1;

COMMIT;

-- ── Verificación (ejecutar después del COMMIT para confirmar) ─────────────────
-- SELECT 'clientes'  AS tabla, COUNT(*) AS filas FROM clientes
-- UNION ALL
-- SELECT 'ventas',   COUNT(*) FROM ventas
-- UNION ALL
-- SELECT 'facturas', COUNT(*) FROM facturas
-- UNION ALL
-- SELECT 'abonos',   COUNT(*) FROM abonos
-- UNION ALL
-- SELECT 'creditos', COUNT(*) FROM creditos
-- UNION ALL
-- SELECT 'caja',     COUNT(*) FROM caja;
-- → Todas deben devolver 0.
