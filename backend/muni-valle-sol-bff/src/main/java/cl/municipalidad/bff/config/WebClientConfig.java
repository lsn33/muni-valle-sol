package cl.municipalidad.bff.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuracion de los clientes HTTP WebClient del BFF.
 * Define los beans para comunicarse con MS-Usuarios, MS-Reportes, MS-Alertas
 * y MS-Brigadas.
 *
 * <p>Patrones aplicados:</p>
 * <ul>
 *   <li>Factory Pattern: creacion centralizada de clientes HTTP</li>
 *   <li>Configuration Pattern: externalizacion de URLs en application.properties</li>
 * </ul>
 *
 * @author Beltran
 * @version 1.1
 * @since 1.0
 */
@Configuration
public class WebClientConfig {

    @Value("${ms.usuarios.url}")
    private String msUsuariosUrl;

    @Value("${ms.reportes.url}")
    private String msReportesUrl;

    @Value("${ms.alertas.url}")
    private String msAlertasUrl;

    @Value("${ms.brigadas.url}")
    private String msBrigadasUrl;

    /**
     * Crea el WebClient para el MS-Usuarios.
     *
     * @return WebClient configurado con la URL base del MS-Usuarios
     */
    @Bean
    public WebClient msUsuariosClient() {
        return WebClient.builder()
                .baseUrl(msUsuariosUrl)
                .build();
    }

    /**
     * Crea el WebClient para el MS-Reportes.
     *
     * @return WebClient configurado con la URL base del MS-Reportes
     */
    @Bean
    public WebClient msReportesClient() {
        return WebClient.builder()
                .baseUrl(msReportesUrl)
                .build();
    }

    /**
     * Crea el WebClient para el MS-Alertas.
     *
     * @return WebClient configurado con la URL base del MS-Alertas
     */
    @Bean
    public WebClient msAlertasClient() {
        return WebClient.builder()
                .baseUrl(msAlertasUrl)
                .build();
    }

    /**
     * Crea el WebClient para el MS-Brigadas.
     *
     * @return WebClient configurado con la URL base del MS-Brigadas
     */
    @Bean
    public WebClient msBrigadasClient() {
        return WebClient.builder()
                .baseUrl(msBrigadasUrl)
                .build();
    }
}