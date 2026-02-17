# 🚀 Deployment - Infraestructura Compartida

Infraestructura base para los microservicios ShieldFlow y FraudAnalyzer.

## 📦 Servicios Incluidos

- **PostgreSQL ShieldFlow** (Puerto 5434)
- **PostgreSQL FraudAnalyzer** (Puerto 5433)
- **Apache Kafka** (Puerto 9092)
- **Zookeeper** (Puerto 2181)
- **Jaeger** (Puerto 16686 UI, 4318 OTLP)

## 🚀 Inicio Rápido

```bash
docker-compose up -d
```

## 📊 Verificar Servicios

```bash
docker-compose ps
```

**Salida esperada:**
```
NAME                    STATUS    PORTS
postgres-shieldflow     Up        0.0.0.0:5434->5432/tcp
postgres-fraudanalyzer  Up        0.0.0.0:5433->5432/tcp
kafka                   Up        0.0.0.0:9092->9092/tcp
zookeeper               Up        0.0.0.0:2181->2181/tcp
jaeger                  Up        0.0.0.0:16686->16686/tcp, 0.0.0.0:4318->4318/tcp
```

## 🔍 Acceso a Servicios

### PostgreSQL ShieldFlow
```bash
docker exec -it postgres-shieldflow psql -U postgres -d shieldflow_db
```

### PostgreSQL FraudAnalyzer
```bash
docker exec -it postgres-fraudanalyzer psql -U postgres -d fraudanalyzer_db
```

### Kafka Topics
```bash
docker exec -it deployment-kafka-1 kafka-topics --list --bootstrap-server localhost:9092
```

### Jaeger UI
```
http://localhost:16686
```

## 🧪 Validar Observabilidad

```bash
# Desde la raíz del proyecto
./validate-observability.sh
```

## 🛑 Detener Servicios

```bash
docker-compose down
```

## 🗑️ Limpiar Datos

```bash
docker-compose down -v
```

## 📝 Configuración

### Puertos Expuestos

| Servicio | Puerto | Uso |
|----------|--------|-----|
| PostgreSQL ShieldFlow | 5434 | Base de datos ShieldFlow |
| PostgreSQL FraudAnalyzer | 5433 | Base de datos FraudAnalyzer |
| Kafka | 9092 | Broker de mensajería |
| Zookeeper | 2181 | Coordinación Kafka |
| Jaeger UI | 16686 | Interfaz web de trazas |
| Jaeger OTLP | 4318 | Receptor de trazas (HTTP) |

### Topics Kafka (Creados Automáticamente)

- `shieldflow.transaction.received`
- `fraudanalyzer.result`
- `shieldflow.transaction.received.dlq`

### Volúmenes

- `postgres-shieldflow-data`: Persistencia de datos ShieldFlow
- `postgres-fraudanalyzer-data`: Persistencia de datos FraudAnalyzer

## 🐛 Troubleshooting

### Puerto ya en uso

```bash
# Ver qué proceso usa el puerto
lsof -i :5434
lsof -i :9092
lsof -i :16686

# Detener proceso o cambiar puerto en docker-compose.yaml
```

### Kafka no inicia

```bash
# Ver logs
docker logs deployment-kafka-1

# Reiniciar
docker-compose restart kafka
```

### Jaeger no muestra trazas

```bash
# Verificar que está corriendo
docker ps | grep jaeger

# Ver logs
docker logs jaeger

# Verificar endpoint
curl http://localhost:4318/v1/traces
```

## 📄 Licencia

MIT License

- **Usuario**: `postgres`
- **Password**: `postgres`

Las tablas se crean automáticamente al iniciar los contenedores.

### Verificar estado

```bash
docker compose ps
docker compose logs kafka
```

## Configuración en la aplicación

La aplicación ya está configurada para conectarse a Kafka local en `application.yaml`:

```yaml
reactive:
  commons:
    kafka:
      app:
        connectionProperties:
          bootstrap-servers: "localhost:9092"
```

Para deshabilitar Kafka temporalmente, agregar:

```yaml
reactive:
  commons:
    kafka:
      app:
        enabled: false
```
