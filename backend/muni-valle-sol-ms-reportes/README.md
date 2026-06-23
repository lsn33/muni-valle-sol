# MS-Reportes

Microservicio de gestión de reportes de incendios para la Municipalidad Valle del Sol. Permite crear, consultar, actualizar y eliminar reportes con coordenadas GPS y estado.

## Tabla Técnica

| Ítem | Detalle |
|---|---|
| Lenguaje | Java 21 |
| Framework | Spring Boot 4.0.6 |
| Base de datos | NeonDB (PostgreSQL serverless) |
| ORM | Spring Data JPA + Hibernate |
| Migraciones | Flyway |
| Seguridad | Spring Security (stateless) |
| Documentación API | SpringDoc OpenAPI 3.0.3 (Swagger UI) |
| Testing | JUnit 5 + Mockito + JaCoCo |
| Cobertura | 88% |
| Puerto | 8082 |
| Patrones de diseño | Repository Pattern, Factory Method, Singleton (Spring Beans) |

## Librerías principales

- `spring-boot-starter-data-jpa`
- `spring-boot-starter-security`
- `spring-boot-starter-validation`
- `spring-boot-starter-flyway`
- `flyway-database-postgresql`
- `postgresql`
- `lombok`
- `springdoc-openapi-starter-webmvc-ui:3.0.3`
- `jacoco-maven-plugin:0.8.14`

## Requisitos

- Java 21
- Maven (incluido con `./mvnw`)
- Conexión a NeonDB

## Instalación y ejecución

```bash
# Clonar el repositorio
git clone https://github.com/lsn33/muni-valle-sol-ms-reportes.git
cd muni-valle-sol-ms-reportes

# Ejecutar
./mvnw clean spring-boot:run
```

El servicio estará disponible en `http://localhost:8082`

## Endpoints principales

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/reportes` | Crear nuevo reporte |
| GET | `/api/reportes` | Listar todos los reportes |
| GET | `/api/reportes/{id}` | Obtener reporte por ID |
| PUT | `/api/reportes/{id}/estado` | Actualizar estado del reporte |
| PUT | `/api/reportes/{id}/titulo` | Actualizar título del reporte |
| DELETE | `/api/reportes/{id}` | Eliminar reporte |

## Swagger UI

```
http://localhost:8082/swagger-ui.html
```

## Ejecutar pruebas y cobertura

```bash
# Ejecutar tests
./mvnw test

# Generar reporte de cobertura JaCoCo
./mvnw clean verify

# Ver reporte en:
# target/site/jacoco/index.html
```

## Docker

```bash
# Build
docker build -t ms-reportes .

# Run
docker run -p 8082:8082 \
  -e DB_URL="..." \
  -e DB_USERNAME="neondb_owner" \
  -e DB_PASSWORD="..." \
  ms-reportes
```