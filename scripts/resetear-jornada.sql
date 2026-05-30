-- ============================================================
-- RESETEAR JORNADA A HOY
-- Usar cuando se quiere volver a operar desde la fecha actual
-- después de haber avanzado hojas en pruebas.
--
-- ⚠️  Elimina todas las jornadas existentes y crea una nueva
--     ABIERTA con la fecha del día de hoy.
-- ⚠️  Las ventas, abonos y caja existentes se borran también
--     porque quedarían "huérfanos" sin una jornada coherente.
-- ⚠️  Esta operación NO se puede deshacer.
-- ============================================================

BEGIN;

-- 1. Limpiar datos de las hojas de prueba (respeta FK)
TRUNCATE TABLE abonos   RESTART IDENTITY CASCADE;
TRUNCATE TABLE facturas RESTART IDENTITY CASCADE;
TRUNCATE TABLE ventas   RESTART IDENTITY CASCADE;
TRUNCATE TABLE caja     RESTART IDENTITY CASCADE;

-- 2. Borrar todas las jornadas y crear solo la de hoy
TRUNCATE TABLE jornadas RESTART IDENTITY;

INSERT INTO jornadas (fecha, estado, abierta_en)
VALUES (CURRENT_DATE, 'ABIERTA', NOW());

COMMIT;

-- ── Verificación ──────────────────────────────────────────────────────────────
-- SELECT id, fecha, estado FROM jornadas;
-- → Debe mostrar una sola fila con la fecha de hoy y estado ABIERTA.
