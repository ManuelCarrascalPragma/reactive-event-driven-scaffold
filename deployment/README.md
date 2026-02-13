# Deployment - Infraestructura Local

## Kafka Local

Para levantar Kafka y Zookeeper localmente:

```bash
cd deployment
docker compose up -d
```

Para detener:

```bash
docker compose down
```

### Configuración

- **Kafka**: `localhost:9092`
- **Zookeeper**: `localhost:2181`
- **Versión**: Confluent Platform 7.4.0

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
