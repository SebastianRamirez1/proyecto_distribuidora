# Distribuidora de Huevos — Sistema de Gestión

Sistema de gestión integral para una distribuidora de huevos. Cubre ventas, inventario, caja diaria, créditos de clientes (fiado) y registro de abonos, con autenticación JWT y un frontend React integrado en el mismo JAR.

---

## Stack tecnológico

| Capa | Tecnología |
|------|-----------|
| Backend | Java 21 · Spring Boot 3.3.5 · Spring Security + JWT |
| Persistencia | PostgreSQL 15 · Spring Data JPA · Hibernate |
| Frontend | React 18 · Vite · Tailwind CSS |
| Build | Maven 3 (perfil `with-frontend` compila React) |
| Tests | JUnit 5 · Mockito · AssertJ |
| Empaquetado | JAR auto-contenido (frontend embebido como recursos estáticos) |

---

## Arquitectura

El proyecto sigue **Clean Architecture / DDD** con tres capas bien separadas:

```
src/main/java/com/distribuidora/huevos/
├── domain/                         ← Núcleo del negocio (sin dependencias de Spring)
│   ├── entities/                   ← Entidades con comportamiento (no anémicas)
│   │   ├── Abono.java
│   │   ├── Caja.java
│   │   ├── Cliente.java
│   │   ├── Credito.java
│   │   ├── Inventario.java
│   │   └── Venta.java
│   ├── valueobjects/               ← Objetos de valor inmutables
│   │   ├── Cantidad.java
│   │   ├── DescuentoPorVolumen.java
│   │   ├── Dinero.java
│   │   ├── Precio.java
│   │   ├── PrecioEspecial.java
│   │   └── PrecioPublico.java
│   ├── enums/                      ← TipoCliente · TipoProducto · TipoPago
│   ├── repositories/               ← Interfaces (definidas en dominio)
│   └── exceptions/                 ← Excepciones específicas del negocio
│
├── application/                    ← Casos de uso (un servicio por operación)
│   ├── service/
│   │   ├── RegistrarVentaService
│   │   ├── AnularVentaService
│   │   ├── RegistrarAbonoService
│   │   ├── CargarInventarioService
│   │   ├── CargarInventarioBulkService
│   │   ├── CrearClienteService
│   │   ├── ActualizarPrecioClienteService
│   │   ├── ActualizarPrecioPublicoService
│   │   ├── ConsultarDeudoresService
│   │   ├── ConsultarHistorialAbonosService
│   │   ├── ConsultarCreditoService
│   │   ├── ConsultarVentasDiaService
│   │   ├── ConsultarInventarioService
│   │   ├── ConsultarPrecioPublicoService
│   │   └── GenerarReporteCajaService
│   ├── dto/command/                ← Entrada (comandos)
│   ├── dto/response/               ← Salida
│   └── mapper/                     ← Dominio ↔ DTO
│
└── infrastructure/                 ← Detalles técnicos
    ├── persistence/
    │   ├── entity/                 ← Entidades JPA
    │   ├── repository/             ← Spring Data + implementaciones
    │   └── mapper/                 ← Dominio ↔ JPA Entity
    ├── controller/                 ← REST Controllers + GlobalExceptionHandler
    ├── security/                   ← Spring Security · JWT filter
    └── config/                     ← CORS · ApplicationConfig
```

---

## Cómo levantar el proyecto

### 1. Base de datos

```bash
docker-compose up -d
```

Levanta PostgreSQL en `localhost:5432`:
- Base de datos: `distribuidora_huevos`
- Usuario: `postgres`
- Contraseña: `postgres`

El `schema.sql` se ejecuta automáticamente al arrancar (Spring SQL Init). Es **idempotente**: crea tablas, aplica migraciones y carga datos iniciales de forma segura sobre una BD existente.

### 2. Backend (solo API)

```bash
mvn spring-boot:run
```

Con logs SQL visibles (perfil local):

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

### 3. Backend + Frontend empaquetados juntos

```bash
mvn package -P with-frontend
java -jar target/huevos-1.0.0-SNAPSHOT.jar
```

El JAR sirve el SPA React en `http://localhost:8080` y la API en `http://localhost:8080/api/`.

### 4. Frontend en modo desarrollo (hot-reload)

```bash
cd frontend
npm install
npm run dev          # http://localhost:5173 — proxy → backend :8080
```

### 5. Ejecutar los tests

```bash
mvn test
```

---

## Autenticación

Todos los endpoints `/api/**` requieren un token JWT en el header:

```
Authorization: Bearer <token>
```

Obtener token:

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
{ "token": "eyJ..." }
```

> El endpoint `/api/auth/login` y los recursos estáticos del frontend son públicos (sin token).

---

## Endpoints disponibles

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

**Crear cliente ESPECIAL con precio por tipo y descuento por volumen:**
```json
POST /api/clientes
{
  "nombre": "Bodega López",
  "tipo": "ESPECIAL",
  "precioEspecialExtra": 3.50,
  "precioEspecialAA": 3.20,
  "precioEspecialA": 2.90,
  "precioEspecialB": 2.50,
  "descuentoDesdeCanastas": 5,
  "descuentoPrecioExtra": 3.20,
  "descuentoPrecioAA": 2.90,
  "descuentoPrecioA": 2.60,
  "descuentoPrecioB": 2.20
}
```

**Actualizar precio especial:**
```json
PUT /api/clientes/1/precio-especial
{
  "precioEspecialExtra": 3.30,
  "precioEspecialAA": 3.00,
  "precioEspecialA": 2.70,
  "precioEspecialB": 2.30
}
```

---

### Ventas

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/ventas/hoy` | Ventas del día actual |
| GET | `/api/ventas` | Historial de ventas |
| POST | `/api/ventas` | Registrar una venta |
| POST | `/api/ventas/abono` | Registrar abono a crédito |
| DELETE | `/api/ventas/{id}` | Anular venta (soft delete) |

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

`tipoProducto` puede ser: `EXTRA` · `AA` · `A` · `B`  
`tipoPago` puede ser: `EFECTIVO` · `TRANSFERENCIA` · `FIADO`

**Registrar abono:**
```json
POST /api/ventas/abono
{
  "clienteId": 1,
  "monto": 50000.00,
  "medioPago": "EFECTIVO"
}
```

`medioPago` puede ser: `EFECTIVO` · `TRANSFERENCIA`

---

### Créditos y deudores

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/creditos/deudores` | Listar clientes con saldo pendiente |
| GET | `/api/creditos/{clienteId}` | Consultar crédito de un cliente |
| GET | `/api/creditos/{clienteId}/abonos` | Historial de abonos de un cliente |

---

### Inventario

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/inventario` | Consultar stock actual |
| POST | `/api/inventario/cargar` | Agregar canastas de un solo tipo |
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
| GET | `/api/precios/publico` | Consultar precios públicos vigentes |
| PUT | `/api/precios/publico` | Actualizar precios públicos |

```json
PUT /api/precios/publico
{
  "precioExtra": 4.00,
  "precioAA": 3.60,
  "precioA": 3.20,
  "precioB": 2.80
}
```

---

### Reportes de caja

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/reportes/caja/hoy` | Reporte de caja del día actual |
| GET | `/api/reportes/caja?fecha=YYYY-MM-DD` | Reporte de caja de una fecha específica |

---

## Reglas de negocio implementadas

| # | Regla |
|---|-------|
| 1 | **Cliente NORMAL** → paga precio público (actualizable diariamente por el dueño) |
| 2 | **Cliente ESPECIAL** → paga su precio especial personalizado por tipo de huevo |
| 3 | **Descuento por volumen** → si el cliente ESPECIAL tiene descuento configurado y compra ≥ N canastas, se aplica el precio con descuento |
| 4 | **Cliente ESPECIAL sin descuento** → siempre paga su precio especial sin importar la cantidad |
| 5 | **Stock en tiempo real** → no se puede vender más del stock disponible; se descuenta al registrar la venta |
| 6 | **Venta inmutable** → el precio se calcula automáticamente; los campos son `final` |
| 7 | **Anulación de ventas** → soft delete; devuelve el stock al inventario y revierte el efecto en caja |
| 8 | **Fiado** → genera o incrementa el crédito del cliente |
| 9 | **Abono** → reduce el saldo pendiente; no puede exceder la deuda actual |
| 10 | **Caja diaria** → una fila por día; acumula efectivo, transferencias, fiados y abonos sin doble conteo |
| 11 | **Precios** usan `BigDecimal` con escala 2 en todo el sistema (nunca `double` o `float`) |

---

## Tests

La suite cubre dominio puro y casos de uso con Mockito. Archivos de test:

| Archivo | Tests |
|---------|-------|
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

## Esquema de base de datos

```sql
clientes       — id, nombre, tipo, precios especiales por tipo, descuento por volumen
ventas         — id, cliente_id, tipo_producto, cantidad, precio_unitario, tipo_pago, fecha, anulada, fecha_anulacion
inventario     — id (singleton), stock_extra, stock_aa, stock_a, stock_b
caja           — id, fecha (UNIQUE), total_efectivo, total_transferencia, total_fiado, total_abonos
creditos       — id, cliente_id (UNIQUE), monto_total, monto_pagado
abonos         — id, cliente_id, monto, medio_pago, fecha
precio_publico — id (singleton), precio_extra, precio_aa, precio_a, precio_b
```

---

## Decisiones de diseño

- **Arquitectura limpia**: el dominio no tiene ni una sola importación de Spring. Los repositorios del dominio son interfaces puras; las implementaciones JPA viven en infraestructura.
- **`PrecioPublico` e `Inventario` son filas únicas** (singleton en BD), inicializadas en `schema.sql`. El `schema.sql` es idempotente y actúa también como script de migración.
- **`Venta` es inmutable** — campos `final`, sin setters, el precio se calcula en el constructor.
- **Anulación con reversión atómica** — `AnularVentaService` revierte stock y caja en la misma transacción.
- **`Caja.totalAbonos`** es una columna informativa separada de `totalEfectivo`/`totalTransferencia` para evitar doble conteo en `calcularTotalCobrado()`.
- **Frontend integrado** — el perfil Maven `with-frontend` ejecuta `npm run build` y copia el `dist/` a `src/main/resources/static/`, sirviendo el SPA desde el mismo JAR sin servidor de archivos separado.
- **JWT stateless** — no hay sesiones del lado del servidor; cada request se autentica con el token.
