# MS-Brigadas

Microservicio de gestión de brigadas de emergencia para la Municipalidad Valle del Sol. Permite crear, consultar, actualizar estado y ubicación de brigadas en tiempo real.

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
| Cobertura | 81% |
| Puerto | 8084 |
| Patrones de diseño | Repository Pattern, Factory Method (BrigadaFactory), Singleton (Spring Beans) |

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
git clone https://github.com/lsn33/muni-valle-sol-ms-brigadas.git
cd muni-valle-sol-ms-brigadas

# Ejecutar
./mvnw clean spring-boot:run
```

El servicio estará disponible en `http://localhost:8084`

## Variables de entorno

| Variable | Descripción | Default |
|---|---|---|
| `DB_URL` | URL de conexión a NeonDB | URL de Neon incluida |
| `DB_USERNAME` | Usuario de la BD | `neondb_owner` |
| `DB_PASSWORD` | Contraseña de la BD | — |

## Endpoints principales

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/brigadas` | Crear nueva brigada |
| GET | `/api/brigadas` | Listar todas las brigadas |
| GET | `/api/brigadas/{id}` | Obtener brigada por ID |
| GET | `/api/brigadas/disponibles` | Listar brigadas disponibles |
| GET | `/api/brigadas/tipo/{tipo}` | Listar brigadas por tipo |
| PUT | `/api/brigadas/{id}/estado` | Actualizar estado de brigada |
| PUT | `/api/brigadas/{id}/ubicacion` | Actualizar ubicación de brigada |
| DELETE | `/api/brigadas/{id}` | Eliminar brigada |

## Tipos de brigada

`INCENDIO` · `RESCATE` · `MEDICA`

## Swagger UI

```
http://localhost:8084/swagger-ui.html
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
docker build -t ms-brigadas .

# Run
docker run -p 8084:8084 \
  -e DB_URL="..." \
  -e DB_USERNAME="neondb_owner" \
  -e DB_PASSWORD="..." \
  ms-brigadas
```