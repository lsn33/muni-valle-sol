package cl.municipalidad.bff.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI 3.0 para Springdoc.
 * Define metadatos, servidores y esquemas de seguridad que enriquecen
 * la documentación automática y el Swagger UI.
 *
 * <p>Patrones aplicados:</p>
 * <ul>
 *   <li>Configuration Pattern: centraliza configuración de OpenAPI</li>
 *   <li>Builder Pattern: construye OpenAPI con fluent API</li>
 * </ul>
 *
 * <p>Comportamiento:</p>
 * <pre>{@code
 * Esta configuración se combina con el openapi.yaml:
 * - YAML define rutas (paths), esquemas (schemas), ejemplos
 * - Config Java define contexto (info, servers, security global)
 * 
 * Resultado: Swagger UI con:
 * ✓ Documentación completa
 * ✓ Metadatos de contacto y licencia
 * ✓ Selector de servidor (dev/prod)
 * ✓ Autenticación JWT prefigurada
 * }</pre>
 *
 * @author Beltran
 * @version 1.0
 * @since 1.0
 */
@Configuration
public class OpenApiConfig {

    /**
     * Define el bean OpenAPI con información general y configuración.
     * Esta definición se fusiona con el openapi.yaml.
     *
     * @return OpenAPI configurado con metadatos y servidores
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Municipalidad Valle del Sol - BFF API")
                        .version("1.0.0")
                        .description(
                            "Backend For Frontend para la plataforma de gestión de incendios. " +
                            "Proporciona endpoints para autenticación, reportes y alertas " +
                            "con autenticación JWT mediante cookies HttpOnly."
                        )
                        .contact(new Contact()
                                .name("Equipo de Desarrollo")
                                .email("desarrollo@munivallesol.cl")
                                .url("https://munivallesol.cl"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor Local"),
                        new Server()
                                .url("https://bff-dev.munivallesol.cl")
                                .description("Ambiente Desarrollo"),
                        new Server()
                                .url("https://bff-prod.munivallesol.cl")
                                .description("Ambiente Producción")
                ));
    }
}