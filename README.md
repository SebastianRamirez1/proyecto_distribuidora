# Distribuidora de Huevos — Sistema de Gestión

Sistema de gestión integral para una distribuidora de huevos. Cubre ventas, inventario, clientes, créditos (fiado), abonos, caja diaria y **facturación de venta en PDF** conforme a la normativa colombiana DIAN.

Producción: **https://distribuidora-app-production.up.railway.app**

---

## Tabla de contenidos

1. [Características](#características)
2. [Stack tecnológico](#stack-tecnológico)
3. [Arquitectura](#arquitectura)
4. [Estructura del proyecto](#estructura-del-proyecto)
5. [Cómo levantar el proyecto](#cómo-levantar-el-proyecto)
6. [Despliegue en Railway](#despliegue-en-railway)
7. [Autenticación](#autenticación)
8. [Referencia de la API](#referencia-de-la-api)
9. [Reglas de negocio](#reglas-de-negocio)
10. [Esquema de base de datos](#esquema-de-base-de-datos)
11. [Tests](#tests)
12. [Decisiones de diseño](#decisiones-de-diseño)

---

## Características

| Módulo | Descripción |
|--------|-------------|
| **Ventas** | Registro, historial y anulación de ventas; descuento por volumen automático |
| **Inventario** | Control de stock en tiempo real por tipo de huevo (EXTRA, AA, A, B) |
| **Clientes** | Gestión de clientes normales y especiales con precios personalizados |
| **Créditos (fiado)** | Control de saldo pendiente, historial de abonos y listado de deudores |
| **Caja diaria** | Reporte del día con totales por forma de pago |
| **Precios públicos** | Precio público actualizable para clientes normales |
| **Facturación** | Generación de facturas de venta en PDF con resolución DIAN, consecutivo automático y número único |
| **Dashboard** | Resumen del día: ventas, caja, stock e indicadores clave |
| **Seguridad** | Autenticación JWT stateless; todos los endpoints protegidos |

---

## Stack tecnológico

| Capa | Tecnología |
|------|------------|
| Backend | Java 21 · Spring Boot 3.3.5 · Spring Security + JWT (jjwt 0.12.6) |
| Persistencia | PostgreSQL 15 · Spring Data JPA · Hibernate |
| PDF | openhtmltopdf-pdfbox 1.0.10 (generación server-side) |
| Frontend | React 18 · Vite · Tailwind CSS |
| Build | Maven 3.9.6 · frontend-maven-plugin (Node 20 / npm) |
| Tests | JUnit 5 · Mockito · AssertJ (~102 tests) |
| BD local | H2 en memoria (perfil default, sin necesidad de Docker) |
| Deploy | Railway (Nixpacks · JDK 21 · PostgreSQL 15) |
| Empaquetado | JAR auto-contenido (SPA React embebido como recursos estáticos) |

---

## Arquitectura

El proyecto sigue **Clean Architecture / Domain-Driven Design** con tres capas bien separadas:

```
Dominio  <──  Aplicación  <──  Infraestructura
```

- **Dominio**: entidades con comportamiento real (no anémicas), value objects inmutables, interfaces de repositorio y excepciones de negocio. **No tiene ninguna importación de Spring.**
- **Aplicación**: un servicio por caso de uso. Orquesta el dominio. Solo depende del dominio.
- **Infraestructura**: implementaciones JPA de los repositorios, controladores REST, filtro JWT, generador PDF, configuración CORS/Security.

---

## Estructura del proyecto

```
proyecto_distribuidora/
├── frontend/                           ← SPA React (Vite + Tailwind)
│   └── src/
│       ├── api/                        ← Clientes Axios por módulo
│       ├── components/                 ← Layout, Sidebar, componentes UI
│       ├── context/AuthContext.jsx     ← Estado de autenticación global
│       └── pages/                      ← Dashboard · Ventas · Clientes · Inventario
│                                         Precios · Deudores · Reportes · Facturas
│
└── src/main/java/com/distribuidora/huevos/
    ├── domain/
    │   ├── entities/
    │   │   ├── Abono.java
    │   │   ├── Caja.java
    │   │   ├── Cliente.java
    │   │   ├── ConfiguracionFactura.java    ← Datos del emisor + resolución DIAN
    │   │   ├── Credito.java
    │   │   ├── Factura.java
    │   │   ├── Inventario.java
    │   │   └── Venta.java
    │   ├── valueobjects/
    │   │   ├── Cantidad.java
    │   │   ├── DescuentoPorVolumen.java
    │   │   ├── Dinero.java
    │   │   ├── Precio.java
    │   │   ├── PrecioEspecial.java
    │   │   └── PrecioPublico.java
    │   ├── enums/
    │   │   ├── EstadoFactura.java           ← EMITIDA · ANULADA
    │   │   ├── TipoCliente.java             ← NORMAL · ESPECIAL
    │   │   ├── TipoFactura.java             ← MANUAL · ELECTRONICA
    │   │   ├── TipoPago.java                ← EFECTIVO · TRANSFERENCIA · FIADO
    │   │   └── TipoProducto.java            ← EXTRA · AA · A · B
    │   ├── repositories/                    ← Interfaces puras (sin Spring)
    │   └── exceptions/                      ← Excepciones tipadas del negocio
    │
    ├── application/
    │   ├── service/                         ← Un servicio por caso de uso
    │   │   ├── RegistrarVentaService
    │   │   ├── AnularVentaService
    │   │   ├── RegistrarAbonoService
    │   │   ├── CrearClienteService
    │   │   ├── ActualizarPrecioClienteService
    │   │   ├── ActualizarPrecioPublicoService
    │   │   ├── CargarInventarioService
    │   │   ├── CargarInventarioBulkService
    │   │   ├── GenerarFacturaService        ← Crea factura con consecutivo atómico
    │   │   ├── ListarFacturasService
    │   │   ├── ObtenerConfiguracionFacturaService
    │   │   ├── ActualizarConfiguracionFacturaService
    │   │   ├── ConsultarDeudoresService
    │   │   ├── ConsultarHistorialAbonosService
    │   │   ├── ConsultarCreditoService
    │   │   ├── ConsultarVentasDiaService
    │   │   ├── ConsultarInventarioService
    │   │   ├── ConsultarPrecioPublicoService
    │   │   └── GenerarReporteCajaService
    │   ├── dto/command/                     ← Objetos de entrada (comandos)
    │   ├── dto/response/                    ← Objetos de salida
    │   └── mapper/                          ← Dominio ↔ DTO
    │
    └── infrastructure/
        ├── controller/                      ← REST Controllers + GlobalExceptionHandler
        ├── pdf/FacturaPdfGenerator.java     ← Genera PDF desde HTML con openhtmltopdf
        ├── persistence/
        │   ├── entity/                      ← Entidades JPA (@Entity)
        │   ├── repository/                  ← Spring Data JPA repositories
        │   └── impl/                        ← Implementaciones del dominio
        ├── security/                        ← JwtAuthFilter · JwtUtil · SecurityConfig
        └── config/                          ← CORS · ApplicationConfig
```

---

## Cómo levantar el proyecto

### Opción A — H2 en memoria (más rápido, sin Docker)

Perfil default: base de datos H2 en memoria. Los datos se pierden al reiniciar. Ideal para explorar el código o ejecutar tests.

```bash
mvn spring-boot:run
```

La app queda disponible en `http://localhost:8080`.

---

### Opción B — PostgreSQL real con Docker

#### 1. Levantar la base de datos

```bash
docker-compose up -d
```

PostgreSQL 15 queda disponible en `localhost:5432`:

| Parámetro | Valor |
|-----------|-------|
| Base de datos | `distribuidora_huevos` |
| Usuario | `postgres` |
| Contraseña | `postgres` |

El `schema.sql` se ejecuta automáticamente vía `docker-entrypoint-initdb.d`. Es **idempotente**: usa `CREATE TABLE IF NOT EXISTS`, `ALTER TABLE … ADD COLUMN IF NOT EXISTS` e `INSERT … WHERE NOT EXISTS`, por lo que es seguro correrlo sobre una BD ya existente con datos.

#### 2. Arrancar el backend

```bash
# Perfil postgres (sin SQL en consola)
mvn spring-boot:run -Dspring-boot.run.profiles=postgres

# Perfil local (con SQL visible en consola, útil en desarrollo)
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

#### 3. JAR auto-contenido (backend + frontend juntos)

```bash
mvn package -P with-frontend
java -jar target/huevos-1.0.0-SNAPSHOT.jar
```

El perfil Maven `with-frontend` ejecuta `npm run build` dentro de `frontend/` y copia el `dist/` a `src/main/resources/static/`. El JAR resultante sirve la SPA en `/` y la API en `/api/`.

#### 4. Frontend en modo desarrollo (hot-reload)

```bash
cd frontend
npm install
npm run dev        # http://localhost:5173 — proxy automático al backend :8080
```

---

### Ejecutar los tests

```bash
mvn test
```

---

## Despliegue en Railway

El repositorio incluye configuración lista para Railway.

### Archivos de configuración

| Archivo | Propósito |
|---------|-----------|
| `nixpacks.toml` | Define las fases de build: instala JDK 21 + Node 20; descarga Maven 3.9.6 manualmente (evita conflicto con el paquete nix de Maven que arrastra JDK 19); compila React y luego Spring Boot |
| `railway.toml` | Comando de inicio con flags JVM, ruta de healthcheck, política de reinicio y variable `SPRING_PROFILES_ACTIVE=prod` |

### Variables de entorno requeridas

| Variable | Descripción |
|----------|-------------|
| `DB_URL` | URL JDBC de PostgreSQL. Ej: `jdbc:postgresql://host:5432/distribuidora_huevos` |
| `DB_USERNAME` | Usuario de la base de datos |
| `DB_PASSWORD` | Contraseña de la base de datos |
| `APP_USERNAME` | Usuario de login de la aplicación (default: `admin`) |
| `APP_PASSWORD` | Contraseña de login (default: `admin123`) |
| `JWT_SECRET` | Cadena aleatoria de 64+ chars hex para firmar los tokens JWT |
| `SPRING_PROFILES_ACTIVE` | `prod` (ya configurado en `railway.toml`) |

### Flujo de ramas

```
feature/<nombre>  /  fix/<nombre>  /  docs/<nombre>
        ↓  Pull Request
       main
        ↓  Railway redespliega automáticamente al detectar merge
    producción
```

Nunca se hace commit directo a `main`.

---

## Autenticación

Todos los endpoints `/api/**` requieren un token JWT en el header:

```
Authorization: Bearer <token>
```

### Obtener token

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

Respuesta:
```json
{ "token": "eyJhbGciOiJIUzI1NiJ9..." }
```

El token es válido por **24 horas** (configurable con `app.jwt.expiration` en ms).

> Son públicos (sin token): `POST /api/auth/login`, `GET /api/health` y todos los recursos estáticos del frontend.

---

## Referencia de la API

### Autenticación

| Método | Endpoint | Auth | Descripción |
|--------|----------|------|-------------|
| POST | `/api/auth/login` | No | Obtener token JWT |

---

### Clientes

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/clientes` | Listar todos los clientes |
| POST | `/api/clientes` | Crear cliente (NORMAL o ESPECIAL) |
| PUT | `/api/clientes/{id}/precio-especial` | Actualizar precio especial del cliente |

**Crear cliente NORMAL:**
```json
POST /api/clientes
{
  "nombre": "Juan Pérez",
  "tipo": "NORMAL"
}
```

**Crear cliente ESPECIAL con precio y descuento por volumen:**
```json
POST /api/clientes
{
  "nombre": "Bodega López",
  "tipo": "ESPECIAL",
  "precioEspecialExtra": 16000.00,
  "precioEspecialAA": 14000.00,
  "precioEspecialA": 12000.00,
  "precioEspecialB": 10000.00,
  "descuentoDesdeCanastas": 5,
  "descuentoPrecioExtra": 15000.00,
  "descuentoPrecioAA": 13000.00,
  "descuentoPrecioA": 11000.00,
  "descuentoPrecioB": 9000.00
}
```

Si `descuentoDesdeCanastas` es `null` o `0`, no hay descuento por volumen.

**Actualizar precio especial:**
```json
PUT /api/clientes/1/precio-especial
{
  "precioEspecialExtra": 15500.00,
  "precioEspecialAA": 13500.00,
  "precioEspecialA": 11500.00,
  "precioEspecialB": 9500.00
}
```

---

### Ventas

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/ventas/hoy` | Ventas del día actual |
| GET | `/api/ventas` | Historial completo de ventas |
| POST | `/api/ventas` | Registrar una venta |
| POST | `/api/ventas/abono` | Registrar abono a crédito |
| DELETE | `/api/ventas/{id}` | Anular venta (soft delete, devuelve stock y revierte caja) |

**Registrar venta:**
```json
POST /api/ventas
{
  "clienteId": 1,
  "tipoProducto": "EXTRA",
  "cantidad": 3,
  "tipoPago": "EFECTIVO"
}
```

`tipoProducto`: `EXTRA` · `AA` · `A` · `B`  
`tipoPago`: `EFECTIVO` · `TRANSFERENCIA` · `FIADO`

El precio unitario se calcula automáticamente según el tipo de cliente, el tipo de producto y los descuentos configurados. El stock se descuenta en la misma transacción.

**Registrar abono:**
```json
POST /api/ventas/abono
{
  "clienteId": 1,
  "monto": 50000.00,
  "medioPago": "EFECTIVO"
}
```

`medioPago`: `EFECTIVO` · `TRANSFERENCIA`

---

### Créditos y deudores

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/creditos/deudores` | Clientes con saldo pendiente |
| GET | `/api/creditos/{clienteId}` | Crédito de un cliente |
| GET | `/api/creditos/{clienteId}/abonos` | Historial de abonos de un cliente |

---

### Inventario

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/inventario` | Stock actual por tipo |
| POST | `/api/inventario/cargar` | Agregar canastas de un tipo |
| POST | `/api/inventario/cargar-bulk` | Agregar canastas de múltiples tipos a la vez |

**Cargar un tipo:**
```json
POST /api/inventario/cargar
{
  "tipoProducto": "EXTRA",
  "cantidad": 100
}
```

**Cargar bulk:**
```json
POST /api/inventario/cargar-bulk
{
  "stockExtra": 200,
  "stockAA": 150,
  "stockA": 100,
  "stockB": 50
}
```

---

### Precios públicos

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/precios/publico` | Precios públicos vigentes |
| PUT | `/api/precios/publico` | Actualizar precios públicos |

```json
PUT /api/precios/publico
{
  "precioExtra": 17000.00,
  "precioAA": 15000.00,
  "precioA": 13000.00,
  "precioB": 11000.00
}
```

---

### Facturación

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/facturas` | Listar todas las facturas emitidas |
| POST | `/api/facturas/generar` | Generar factura para una venta |
| GET | `/api/facturas/{id}/pdf` | Descargar PDF de una factura |
| GET | `/api/facturas/configuracion` | Obtener configuración del emisor |
| PUT | `/api/facturas/configuracion` | Actualizar configuración del emisor |

**Generar factura:**
```json
POST /api/facturas/generar
{
  "ventaId": 1,
  "nitCliente": "900123456-1",
  "tipo": "MANUAL"
}
```

`tipo`: `MANUAL` · `ELECTRONICA`  
`nitCliente`: opcional. Si se omite, queda como `"Sin NIT"`.

Cada venta puede tener **una sola** factura. Intentar facturar una venta ya facturada retorna `400`.

**Configurar datos del emisor:**
```json
PUT /api/facturas/configuracion
{
  "razonSocial": "La Golondrina Distribuidora de Huevos",
  "nit": "900.123.456-1",
  "direccion": "Calle 10 # 5-23",
  "ciudad": "Medellín",
  "telefono": "3001234567",
  "regimen": "No responsable de IVA",
  "resolucionNumero": "18764000001",
  "resolucionFecha": "2024-01-15",
  "resolucionPrefijo": "FAC",
  "resolucionDesde": 1,
  "resolucionHasta": 9999
}
```

La configuración es una **fila única** (singleton) en la base de datos. El consecutivo (`FAC00001`, `FAC00002`, …) se incrementa con **bloqueo pesimista de escritura** para evitar duplicados en concurrencia.

**Descargar PDF:**
```
GET /api/facturas/{id}/pdf
```

Responde con `Content-Type: application/pdf` y `Content-Disposition: attachment; filename="factura-FAC00001.pdf"`.

> Los huevos están **excluidos de IVA** según el Art. 424 del Estatuto Tributario colombiano (bienes de la canasta familiar). El PDF lo declara explícitamente.

---

### Reportes de caja

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/reportes/caja/hoy` | Reporte de caja del día actual |
| GET | `/api/reportes/caja?fecha=YYYY-MM-DD` | Reporte de caja de una fecha específica |

---

### Healthcheck

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/health` | Retorna `200 OK` — usado por Railway para el probe de salud |

---

## Reglas de negocio

| # | Regla |
|---|-------|
| 1 | **Cliente NORMAL** → paga precio público vigente (el operador lo actualiza cuando quiera) |
| 2 | **Cliente ESPECIAL** → paga su precio especial personalizado por tipo de huevo |
| 3 | **Descuento por volumen** → si el cliente ESPECIAL tiene descuento configurado y compra ≥ N canastas, se aplica el precio con descuento automáticamente |
| 4 | **Sin descuento configurado** → cliente ESPECIAL siempre paga su precio especial base sin importar la cantidad |
| 5 | **Stock en tiempo real** → no se puede vender más del stock disponible; se descuenta al registrar la venta en la misma transacción |
| 6 | **Venta inmutable** → precio calculado en el constructor; campos `final`; sin setters |
| 7 | **Anulación de venta** → soft delete (`anulada = true`); devuelve stock al inventario y revierte el efecto en caja, todo en la misma transacción |
| 8 | **Fiado** → genera o incrementa el crédito del cliente; queda como deuda pendiente |
| 9 | **Abono** → reduce el saldo pendiente; no puede exceder la deuda actual del cliente |
| 10 | **Caja diaria** → una fila por día; acumula efectivo, transferencias, fiados y abonos por separado para evitar doble conteo en `calcularTotalCobrado()` |
| 11 | **Precios con `BigDecimal`** → escala 2 en todo el sistema; nunca `double` ni `float` |
| 12 | **Una factura por venta** → el sistema valida unicidad antes de generar; no se puede facturar la misma venta dos veces |
| 13 | **No facturar ventas anuladas** → validación explícita antes de generar la factura |
| 14 | **Consecutivo de factura sin duplicados** → `@Lock(PESSIMISTIC_WRITE)` en el repositorio garantiza atomicidad bajo concurrencia |
| 15 | **Huevos excluidos de IVA** → Art. 424 E.T. (canasta familiar); el PDF lo declara explícitamente |

---

## Esquema de base de datos

```sql
-- Clientes
clientes (
    id, nombre, tipo,
    precio_especial_extra, precio_especial_aa, precio_especial_a, precio_especial_b,
    descuento_desde_canastas,
    descuento_precio_extra, descuento_precio_aa, descuento_precio_a, descuento_precio_b
)

-- Ventas con soft delete
ventas (
    id, cliente_id → clientes, tipo_producto, cantidad,
    precio_unitario, tipo_pago, fecha,
    anulada BOOLEAN DEFAULT FALSE, fecha_anulacion
)

-- Inventario (fila única / singleton)
inventario (id, stock_extra, stock_aa, stock_a, stock_b)

-- Caja diaria
caja (
    id, fecha UNIQUE,
    total_efectivo, total_transferencia, total_fiado, total_abonos
)

-- Creditos (uno por cliente)
creditos (id, cliente_id UNIQUE → clientes, monto_total, monto_pagado)

-- Abonos
abonos (id, cliente_id → clientes, monto, medio_pago, fecha)

-- Precio publico (fila única / singleton)
precio_publico (id, precio_extra, precio_aa, precio_a, precio_b)

-- Configuracion de factura (fila única / singleton)
configuracion_factura (
    id, razon_social, nit, direccion, ciudad, telefono, regimen,
    resolucion_numero, resolucion_fecha, resolucion_prefijo,
    resolucion_desde, resolucion_hasta, consecutivo_actual
)

-- Facturas emitidas
facturas (
    id, numero UNIQUE, venta_id → ventas, cliente_id → clientes,
    fecha_emision, tipo, estado,
    nombre_cliente, nit_cliente,
    tipo_producto, cantidad, precio_unitario, total, tipo_pago
)
```

El `schema.sql` es **idempotente**: usa `CREATE TABLE IF NOT EXISTS`, `ALTER TABLE … ADD COLUMN IF NOT EXISTS` e `INSERT … WHERE NOT EXISTS`. Se puede ejecutar sobre una base de datos existente sin riesgo. Actúa además como sistema de migración incremental (contiene los `ALTER` y `UPDATE` de versiones anteriores).

---

## Tests

La suite cubre el dominio puro y los casos de uso con Mockito. Al no tener el dominio dependencias de Spring, los tests son rápidos y no necesitan contexto de aplicación.

| Suite | Tests |
|-------|-------|
| `RegistrarVentaServiceTest` | 7 |
| `RegistrarAbonoServiceTest` | 7 |
| `AnularVentaServiceTest` | 8 |
| `CajaTest` | 19 |
| `CreditoTest` | 14 |
| `DineroTest` | 15 |
| `ClienteTest` | 9 |
| `InventarioTest` | 7 |
| `VentaTest` | 4 |
| `CantidadTest` | 6 |
| `PrecioTest` | 6 |
| **Total** | **~102** |

```bash
mvn test
```

---

## Decisiones de diseño

### Arquitectura limpia
El dominio no tiene ninguna importación de Spring. Los repositorios del dominio son interfaces puras; las implementaciones JPA viven en infraestructura. Esto permite testear el dominio sin Spring y cambiar el ORM o la BD sin tocar la lógica de negocio.

### `schema.sql` como sistema de migración
En lugar de Flyway o Liquibase se usa un único `schema.sql` idempotente. Cada nueva tabla o columna se agrega con `IF NOT EXISTS`. El archivo también contiene las migraciones de datos históricas (ej: `UPDATE ventas SET tipo_producto = 'A' WHERE tipo_producto = 'NORMAL'`). Es más simple de mantener para un proyecto de una sola instancia.

### `Inventario`, `PrecioPublico` y `ConfiguracionFactura` como singleton
Son filas únicas inicializadas en `schema.sql`. El servicio siempre las lee/actualiza; nunca las crea desde la API. Esto simplifica la API (sin IDs ni paginación) y evita estados inconsistentes.

### `Venta` inmutable
Los campos de precio, cantidad y tipo son `final`. El precio se calcula en el constructor del servicio según las reglas del cliente. Esto hace que el historial sea auditable: el precio queda fijado en el momento exacto de la venta.

### Anulación con reversión atómica
`AnularVentaService` revierte stock y caja en la misma transacción `@Transactional`. Si algo falla, ningún efecto parcial queda persistido.

### `Caja.totalAbonos` separado de `totalEfectivo`
Los abonos no se suman a `totalEfectivo` para evitar doble conteo: un crédito (fiado) ya incrementó `totalFiado` al hacer la venta, y el abono solo reduce la deuda. `calcularTotalCobrado()` los trata por separado.

### Consecutivo de factura con lock pesimista
`GenerarFacturaService` llama a `configRepo.findUnicaParaActualizar()` con `@Lock(PESSIMISTIC_WRITE)`. Esto serializa la generación de facturas y garantiza que dos requests concurrentes no produzcan el mismo número de consecutivo.

### Frontend integrado en el JAR
El perfil Maven `with-frontend` compila React y copia el `dist/` a `src/main/resources/static/`. `SpaController` reenvía cualquier ruta no-API a `index.html`. No se necesita Nginx ni servidor de archivos separado: un solo proceso, un solo JAR.

### JWT stateless
No hay sesiones del lado del servidor. Cada request lleva el token en `Authorization: Bearer`. `JwtAuthFilter` valida la firma y carga el usuario en el `SecurityContext` antes de cada request.

### Generación de PDF server-side
`FacturaPdfGenerator` convierte un template HTML a PDF con `openhtmltopdf-pdfbox`. El template usa exclusivamente CSS 2.1 (layout con `<table>`, no flexbox) para compatibilidad total con el motor de layout de la librería, que no soporta CSS3. Sin fuentes del sistema: PDFBox usa sus fuentes PDF estándar integradas (Helvetica), lo que funciona correctamente en entornos headless como Railway.
