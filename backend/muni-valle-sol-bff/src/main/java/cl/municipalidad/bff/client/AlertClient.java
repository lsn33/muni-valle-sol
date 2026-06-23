package cl.municipalidad.bff.client;

import cl.municipalidad.bff.dto.AlertMsResponseDTO;
import cl.municipalidad.bff.exception.MsException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cliente HTTP para el MS-Alertas.
 * Encapsula todas las llamadas al microservicio de alertas via WebClient.
 *
 * <p>Patrones aplicados:</p>
 * <ul>
 *   <li>Gateway Pattern: punto unico de acceso al MS-Alertas</li>
 *   <li>Single Responsibility: solo gestiona comunicacion con MS-Alertas</li>
 * </ul>
 *
 * @author Beltran
 * @version 1.1
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class AlertClient {

    @Qualifier("msAlertasClient")
    private final WebClient msAlertasClient;

    /**
     * Obtiene todas las alertas activas del MS-Alertas.
     *
     * @return lista de AlertMsResponseDTO con alertas en estado ACTIVE
     * @throws MsException si ocurre un error interno en el MS-Alertas (500)
     */
    public List<AlertMsResponseDTO> listActive() {
        return msAlertasClient.get()
                .uri("/api/alerts")
                .retrieve()
                .onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals,
                    response -> response.bodyToMono(String.class)
                        .map(body -> new MsException("Error al obtener alertas", HttpStatus.INTERNAL_SERVER_ERROR)))
                .bodyToFlux(AlertMsResponseDTO.class)
                .collectList()
                .block();
    }

    /**
     * Crea una nueva alerta en el MS-Alertas.
     * Incluye coordenadas geograficas si fueron proporcionadas.
     *
     * @param title       titulo de la alerta
     * @param description descripcion de la alerta
     * @param severity    severidad: HIGH, MEDIUM o LOW
     * @param latitude    latitud del incidente, puede ser null
     * @param longitude   longitud del incidente, puede ser null
     * @return AlertMsResponseDTO con la alerta creada
     * @throws MsException si los datos son invalidos (400)
     */
    public AlertMsResponseDTO create(String title, String description, String severity, Double latitude, Double longitude) {
        Map<String, Object> body = new HashMap<>();
        body.put("title", title);
        body.put("description", description);
        body.put("severity", severity);
        if (latitude != null) body.put("latitude", latitude);
        if (longitude != null) body.put("longitude", longitude);

        return msAlertasClient.post()
                .uri("/api/alerts")
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatus.BAD_REQUEST::equals,
                    response -> response.bodyToMono(String.class)
                        .map(b -> new MsException("Error al crear alerta", HttpStatus.BAD_REQUEST)))
                .bodyToMono(AlertMsResponseDTO.class)
                .block();
    }
}