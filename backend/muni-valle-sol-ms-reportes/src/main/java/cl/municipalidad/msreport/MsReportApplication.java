package cl.municipalidad.msreport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del microservicio de reportes de emergencia.
 *
 * <p>Inicia el contexto de Spring Boot y arranca el servidor embebido Tomcat.
 * Este microservicio forma parte del sistema municipal de alertas tempranas
 * y expone una API REST para la gestión de reportes ciudadanos.</p>
 *
 * <p>Patrones aplicados:</p>
 * <ul>
 *   <li>Microservice Pattern: servicio autónomo con responsabilidad única</li>
 *   <li>Layered Architecture: controller → service → repository → DB</li>
 * </ul>
 *
 * <p>Flujo de capas:</p>
 * <pre>{@code
 * MsReportApplication (Bootstrap)
 *   ↓ inicia contexto Spring
 * ReportController
 *   ↓ delega lógica
 * ReportService
 *   ↓ delega creación
 * ReportFactory + ReportRepository
 *   ↓ persiste en
 * PostgreSQL (NeonDB)
 * }</pre>
 *
 * @author Beltran
 * @version 1.0
 * @since 1.0
 */
@SpringBootApplication
public class MsReportApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsReportApplication.class, args);
    }
}