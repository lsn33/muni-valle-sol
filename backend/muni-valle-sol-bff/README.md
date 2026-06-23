# BFF — Backend For Frontend

Backend For Frontend de la plataforma de gestión de incendios de la Municipalidad Valle del Sol. Actúa como capa de orquestación entre el API Gateway KrakenD y los 4 microservicios, gestionando autenticación JWT, cookies HttpOnly y Circuit Breaker.

## Tabla Técnica

| Ítem | Detalle |
|---|---|
| Lenguaje | Java 21 |
| Framework | Spring Boot 4.0.6 |
| Autenticación | JWT RSA RS256 validado contra JWKS de MS-Usuarios |
| HTTP Client | Spring WebFlux (WebClient) |
| Circuit Breaker | Resilience4j standalone |
| Documentación API | SpringDoc OpenAPI 3.0.3 (Swagger UI) |
| Testing | JUnit 5 + Mockito + JaCoCo |
| Cobertura | 61% |
| Puerto | 8080 |
| Patrones de diseño | Facade (BFF), Gateway (WebClient), Circuit Breaker (Resilience4j), Singleton (Spring Beans) |

## Librerías principales

- `spring-boot-starter-web`
- `spring-boot-starter-webflux`
- `spring-boot-starter-validation`
- `spring-boot-starter-actuator`
- `nimbus-jose-jwt:9.37.3`
- `resilience4j-spring-boot3:2.2.0`
- `spring-aspects`
- `lombok`
- `springdoc-openapi-starter-webmvc-ui:3.0.3`
- `jacoco-maven-plugin:0.8.14`

## Requisitos

- Java 21
- Maven (incluido con `./mvnw`)
- MS-Usuarios corriendo en puerto 8081
- MS-Reportes corriendo en puerto 8082
- MS-Alertas corriendo en puerto 8083
- MS-Brigadas corriendo en puerto 8084

## Instalación y ejecución

```bash
# Clonar el repositorio
git clone https://github.com/JOAKOO123/muni-valle-sol-bff.git
cd muni-valle-sol-bff

# Ejecutar
./mvnw clean spring-boot:run
```

El servicio estará disponible en `http://localhost:8080`

## Variables de entorno

| Variable | Descripción | Default |
|---|---|---|
| `MS_USUARIOS_URL` | URL de MS-Usuarios | `http://localhost:8081` |
| `MS_REPORTES_URL` | URL de MS-Reportes | `http://localhost:8082` |
| `MS_ALERTAS_URL` | URL de MS-Alertas | `http://localhost:8083` |
| `MS_BRIGADAS_URL` | URL de MS-Brigadas | `http://localhost:8084` |
| `JWT_COOKIE_SECURE` | Cookie segura (HTTPS) | `false` |
| `CORS_ORIGINS` | Orígenes permitidos CORS | `http://localhost:3000` |

## Endpoints principales

| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| POST | `/api/auth/login` | Público | Login y obtención de cookie JWT |
| POST | `/api/auth/register` | Público | Registro de nuevo usuario |
| GET | `/api/auth/me` | JWT | Datos del usuario autenticado |
| POST | `/api/auth/logout` | JWT | Cerrar sesión |
| GET | `/api/reportes` | Público | Listar reportes |
| POST | `/api/reportes` | CIUDADANO+ | Crear reporte |
| GET | `/api/alertas` | Público | Listar alertas |
| POST | `/api/alertas` | BRIGADISTA/ADMIN | Crear alerta |
| GET | `/api/brigadas` | BRIGADISTA/ADMIN | Listar brigadas |
| POST | `/api/brigadas` | BRIGADISTA/ADMIN | Crear brigada |

## Swagger UI

```
http://localhost:8080/swagger-ui.html
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

## Docker Compose (todos los servicios)

```bash
# Desde la raíz del BFF
docker-compose up --build
```

Levanta: MS-Usuarios, MS-Reportes, MS-Brigadas, MS-Alertas, BFF y Frontend.

## Docker individual

```bash
# Build
docker build -t bff .

# Run
docker run -p 8080:8080 \
  -e MS_USUARIOS_URL="http://ms-usuarios:8081" \
  -e MS_REPORTES_URL="http://ms-reportes:8082" \
  -e MS_ALERTAS_URL="http://ms-alertas:8083/api/v1" \
  -e MS_BRIGADAS_URL="http://ms-brigadas:8084" \
  bff
```