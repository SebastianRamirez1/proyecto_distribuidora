-- Datos iniciales para perfil H2 (desarrollo sin Docker)
-- Las tablas ya fueron creadas por Hibernate (ddl-auto: create-drop)

INSERT INTO inventario (stock_extra, stock_aa, stock_a, stock_b) VALUES (0, 0, 0, 0);
INSERT INTO precio_publico (precio_extra, precio_aa, precio_a, precio_b) VALUES (0.00, 0.00, 0.00, 0.00);
INSERT INTO precio_costo (costo_extra, costo_aa, costo_a, costo_b) VALUES (0.00, 0.00, 0.00, 0.00);
INSERT INTO configuracion_factura (razon_social, nit, direccion, ciudad, telefono, regimen,
    resolucion_numero, resolucion_prefijo, resolucion_desde, resolucion_hasta, consecutivo_actual)
VALUES ('', '', '', '', '', 'No responsable de IVA', '', 'FAC', 1, 9999, 1);
