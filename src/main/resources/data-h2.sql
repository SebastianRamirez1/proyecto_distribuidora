-- Datos iniciales para perfil H2 (desarrollo sin Docker)
-- Las tablas ya fueron creadas por Hibernate (ddl-auto: create-drop)
-- Este script corre DESPUÉS gracias a defer-datasource-initialization: true

INSERT INTO inventario (stock_extra, stock_normal) VALUES (0, 0);
INSERT INTO precio_publico (precio_extra, precio_normal) VALUES (0.00, 0.00);
