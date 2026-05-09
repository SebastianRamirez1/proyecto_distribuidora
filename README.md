# Distribuidora de Huevos - Sistema de Gestión

Sistema de gestión para distribuidora de huevos que controla ventas, inventario, caja diaria y créditos de clientes.

## Tecnologías

- Java 17
- Spring Boot 3.2
- PostgreSQL 15
- Maven
- JUnit 5 + Mockito

## Cómo levantar el proyecto

### 1. Levantar la base de datos

```bash
docker-compose up -d
```

Esto levanta PostgreSQL en `localhost:5432` con:
- Base de datos: `distribuidora_huevos`
- Usuario: `postgres`
- Contraseña: `postgres`

El `schema.sql` se ejecuta automáticamente al iniciar la aplicación (Spring SQL Init) creando las tablas e insertando la fila única de inventario y precio público.

### 2. Levantar la aplicación

```bash
mvn spring-boot:run
```

La API estará disponible en `http://localhost:8080`.

Para modo local con SQL visible:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

### 3. Ejecutar los tests

```bash
mvn test
```

---

## Estructura del proyecto

```
src/main/java/com/distribuidora/huevos/
├── domain/                         ← Núcleo del negocio (sin dependencias de Spring)
│   ├── entities/                   ← Entidades con comportamiento (no anémicas)
│   ├── valueobjects/               ← Objetos de valor inmutables
│   ├── enums/                      ← TipoCliente, TipoProducto, TipoPago
│   ├── repositories/               ← Interfaces (definidas en dominio)
│   └── exceptions/                 ← Excepciones específicas del negocio
│
├── application/                    ← Casos de uso
│   ├── service/                    ← Orquestación de la lógica de negocio
│   ├── dto/command/                ← Entrada de datos (comandos)
│   ├── dto/response/               ← Salida de datos
│   └── mapper/                     ← Dominio ↔ DTO
│
└── infrastructure/                 ← Detalles técnicos
    ├── persistence/
    │   ├── entity/                 ← Entidades JPA
    │   ├── repository/             ← Spring Data + implementaciones
    │   └── mapper/                 ← Dominio ↔ JPA Entity
    ├── controller/                 ← REST Controllers + ExceptionHandler
    └── config/                     ← Configuración Spring
```

---

## Endpoints disponibles

### Clientes

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/clientes` | Crear cliente (NORMAL o ESPECIAL) |
| PUT | `/api/clientes/{id}/precio-especial` | Actualizar precio especial de cliente |

**Crear cliente NORMAL:**
```json
POST /api/clientes
{
  "nombre": "Juan Pérez",
  "tipo": "NORMAL"
}
```

**Crear cliente ESPECIAL con descuento por volumen:**
```json
POST /api/clientes
{
  "nombre": "Bodega López",
  "tipo": "ESPECIAL",
  "precioEspecialExtra": 3.50,
  "precioEspecialNormal": 2.80,
  "descuentoDesdeCanastas": 5,
  "descuentoPrecioExtra": 3.20,
  "descuentoPrecioNormal": 2.50
}
```

**Actualizar precio especial:**
```json
PUT /api/clientes/1/precio-especial
{
  "precioEspecialExtra": 3.30,
  "precioEspecialNormal": 2.60
}
```

---

### Ventas

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/ventas` | Registrar venta |
| POST | `/api/ventas/abono` | Registrar abono a fiado |

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
`tipoPago` puede ser: `EFECTIVO`, `TRANSFERENCIA`, `FIADO`, `ABONO`

**Registrar abono:**
```json
POST /api/ventas/abono
{
  "clienteId": 1,
  "monto": 50.00
}
```

---

### Inventario

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/inventario` | Consultar stock actual |
| POST | `/api/inventario/cargar` | Agregar canastas al stock |

**Cargar inventario:**
```json
POST /api/inventario/cargar
{
  "tipoProducto": "EXTRA",
  "cantidad": 100
}
```

---

### Reportes

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/reportes/caja/hoy` | Reporte de caja del día |

---

### Precios públicos

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| PUT | `/api/precios/publico` | Actualizar precio público (para clientes NORMAL) |

```json
PUT /api/precios/publico
{
  "precioExtra": 4.00,
  "precioNormal": 3.00
}
```

---

## Reglas de negocio implementadas

1. **Cliente NORMAL** → paga precio público (actualizable diariamente por el dueño)
2. **Cliente ESPECIAL** → paga su precio especial personalizado
3. **Descuento por volumen** → si el cliente ESPECIAL tiene descuento configurado y compra >= N canastas
4. **Cliente ESPECIAL sin descuento** → siempre paga su precio especial sin importar la cantidad
5. **Stock en tiempo real** → no se puede vender más del stock disponible
6. **Venta inmutable** → el precio se calcula automáticamente, no se puede modificar
7. **Fiado** → genera crédito en la cuenta del cliente
8. **Abono** → reduce el saldo pendiente del crédito

## Decisiones de diseño

- `PrecioPublico` es una fila única en BD inicializada en `schema.sql` con valor `0.00`. El dueño debe actualizarlo antes de operar.
- Los precios usan `BigDecimal` con escala 2 en todo el sistema (nunca `double`).
- `Venta` es `final` con campos `final` — inmutabilidad garantizada en tiempo de compilación.
- Los repositorios del dominio son interfaces puras; las implementaciones JPA están en infraestructura.
