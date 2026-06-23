package cl.municipalidad.bff;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Prueba de integracion que verifica que el contexto de Spring Boot
 * carga correctamente todos los beans del BFF.
 *
 * @author Beltran
 * @version 1.0
 * @since 1.0
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(properties = {
    "ms.usuarios.url=http://localhost:8081",
    "ms.reportes.url=http://localhost:8082",
    "ms.alertas.url=http://localhost:8083",
    "cors.allowed.origins=http://localhost:3000",
    "jwt.secret=test-secret-key-de-al-menos-32-caracteres-ok",
    "jwt.cookie.name=access_token",
    "jwt.cookie.maxage=86400"
})
class BffApplicationTests {

    @Test
    void contextLoads() {
    }
}