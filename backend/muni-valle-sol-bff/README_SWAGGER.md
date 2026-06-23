# API Specification (Swagger/OpenAPI)

To avoid confusion there is a single canonical OpenAPI specification in this project:

- `src/main/resources/static/openapi.yaml` — the authoritative, static OpenAPI file served at `/openapi.yaml`.

Configuration details:

- Swagger UI is configured to load the static spec at `/openapi.yaml` (see `src/main/resources/application.properties`).
- The dynamic OpenAPI generation via springdoc has been disabled for this module to prevent multiple, diverging specs.

If you need to update the API documentation:

1. Edit `src/main/resources/static/openapi.yaml` directly.
2. Rebuild and run the application:

```powershell
cd muni-valle-sol-bff
.\mvnw.cmd -DskipTests package
.\mvnw.cmd spring-boot:run
```

3. Open Swagger UI at: http://localhost:8080/swagger-ui/index.html
4. Static spec is available at: http://localhost:8080/openapi.yaml

If you later prefer automatic generation from code, remove `springdoc.api-docs.enabled=false` and the static file, and enable the springdoc configuration instead.
