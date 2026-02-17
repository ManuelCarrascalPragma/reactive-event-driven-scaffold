# 🛡️ ShieldFlow - Microservicio de Gestión de Transacciones

Microservicio reactivo para recepción, validación y gestión de transacciones financieras. Implementa patrones de seguridad para prevenir fraude y garantizar integridad de datos.

## 📋 Descripción

ShieldFlow es el punto de entrada del sistema de detección de fraude. Recibe transacciones vía API REST, valida datos, detecta intentos de manipulación y publica eventos a Kafka para análisis posterior.

## 🔒 Características de Seguridad

- ✅ **Validación de datos** (Jakarta Bean Validation)
- ✅ **Detección de fraude por modificación** (Validación de integridad)
- ✅ **Limpieza de transacciones zombie** (Scheduler automático)
- ✅ **Idempotencia** (Prevención de duplicados)

## 🏗️ Arquitectura

```
API REST (8080)
    ↓
Handler (Validation)
    ↓
UseCase (Business Logic)
    ↓
Repository (R2DBC)
    ↓
PostgreSQL (5432)
    ↓
Kafka Producer → transaction.received
```

## 🚀 Inicio Rápido

### Prerrequisitos
- Java 17+
- Gradle 7.x+
- PostgreSQL (puerto 5432)
- Kafka (puerto 9092)

### Configuración

**application.yaml**
```yaml
server:
  port: 8080

spring:
  r2dbc:
    url: r2dbc:postgresql://localhost:5432/shieldflow_db
    username: postgres
    password: postgres

reactive:
  commons:
    kafka:
      app:
        connectionProperties:
          bootstrap-servers: "localhost:9092"
```

### Ejecutar

```bash
./gradlew bootRun
```

## 📡 API Endpoints

### POST /api/transactions
Crear nueva transacción

**Request:**
```json
{
  "id": "tx-001",
  "accountId": "ACC123",
  "amount": 500.00,
  "currency": "USD"
}
```

**Response 200 OK:**
```json
{
  "id": "tx-001",
  "accountId": "ACC123",
  "amount": 500.00,
  "currency": "USD",
  "status": "PENDING",
  "createdAt": "2026-02-16T20:00:00",
  "updatedAt": "2026-02-16T20:00:00"
}
```

**Response 400 Bad Request:**
```json
{
  "errors": [
    "amount: must be greater than 0",
    "currency: must match pattern [A-Z]{3}"
  ]
}
```

**Response 500 Internal Server Error:**
```json
{
  "message": "Fraud attempt detected: Transaction ID exists with different data"
}
```

## 🧪 Pruebas

### Validación de Datos
```bash
# Rechazar monto negativo
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{"id":"test-001","accountId":"ACC123","amount":-100,"currency":"USD"}'
```

### Detección de Fraude
```bash
# Crear transacción
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{"id":"fraud-001","accountId":"ACC999","amount":500,"currency":"USD"}'

# Intentar modificar (debe fallar)
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{"id":"fraud-001","accountId":"ACC999","amount":5000,"currency":"USD"}'
```

## 🔗 Integración con FraudAnalyzer

ShieldFlow publica eventos al topic `shieldflow.transaction.received` que son consumidos por **FraudAnalyzer** para análisis de fraude.

**Repositorio FraudAnalyzer:** [Link al repo]

**Topic Kafka:**
- **Produce:** `shieldflow.transaction.received`
- **Consume:** `fraudanalyzer.result`

## 🛠️ Tecnologías

- Spring Boot 3.x
- Spring WebFlux
- R2DBC PostgreSQL
- Reactive Commons (Kafka)
- Jakarta Bean Validation
- Lombok

## 📊 Estructura del Proyecto

```
domain/
  ├── model/          # Entidades de dominio
  └── usecase/        # Lógica de negocio
infrastructure/
  ├── driven-adapters/
  │   ├── r2dbc-postgresql/    # Persistencia
  │   └── async-event-bus/     # Kafka producer
  └── entry-points/
      ├── reactive-web/        # API REST
      ├── async-event-handler/ # Kafka consumer
      └── scheduler/           # Jobs programados
applications/
  └── app-service/    # Configuración principal
```

## 🔧 Configuración Avanzada

### Scheduler de Zombies
Ejecuta cada 5 minutos, marca transacciones PENDING > 10 minutos como TIMEOUT.

**Configuración en:** `ZombieTransactionScheduler.java`

### Validaciones
- `amount`: Debe ser positivo
- `currency`: Código de 3 letras (USD, EUR, etc.)
- `accountId`: No puede estar vacío

## 📝 Logs

```
INFO: Transaction created: tx-001
WARN: Fraud attempt detected for ID: fraud-001
INFO: Processed 5 zombie transactions (PENDING > 10 minutes)
```

## 🐛 Troubleshooting

### Error: "Connection refused to PostgreSQL"
```bash
docker ps | grep postgres-shieldflow
docker logs postgres-shieldflow
```

### Error: "Kafka broker not available"
```bash
docker ps | grep kafka
docker exec -it deployment-kafka-1 kafka-topics --list --bootstrap-server localhost:9092
```
