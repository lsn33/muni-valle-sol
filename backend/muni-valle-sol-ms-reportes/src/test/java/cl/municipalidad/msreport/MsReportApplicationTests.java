package cl.municipalidad.msreport;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Prueba de integración que verifica el arranque del contexto de Spring Boot.
 *
 * <p>Valida que todas las dependencias, configuraciones y beans del
 * microservicio se cargan correctamente sin necesidad de una base de
 * datos real, usando propiedades que desactivan Flyway y la DDL automática.</p>
 *
 * <p>Patrones aplicados:</p>
 * <ul>
 *   <li>Smoke Test: verifica que el sistema arranca sin errores de configuración</li>
 *   <li>Test Isolation: propiedades de prueba evitan dependencias externas</li>
 * </ul>
 *
 * @author Beltran
 * @version 1.0
 * @since 1.0
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.flyway.enabled=false"
})
class MsReportApplicationTests {

    /**
     * Verifica que el contexto de Spring Boot carga correctamente.
     * Si algún bean falla al inicializarse, este test lo detecta.
     */
    @Test
    void contextLoads() {
    }
}