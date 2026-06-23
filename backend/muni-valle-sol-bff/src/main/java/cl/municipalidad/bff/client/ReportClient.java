package cl.municipalidad.bff.client;

import cl.municipalidad.bff.dto.ReportMsDTO;
import cl.municipalidad.bff.exception.MsException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

/**
 * Cliente HTTP para el MS-Reportes.
 * Encapsula todas las llamadas al microservicio de reportes via WebClient.
 *
 * <p>Patrones aplicados:</p>
 * <ul>
 *   <li>Gateway Pattern: punto unico de acceso al MS-Reportes</li>
 *   <li>Single Responsibility: solo gestiona comunicacion con MS-Reportes</li>
 * </ul>
 *
 * @author Beltran
 * @version 1.0
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class ReportClient {

    @Qualifier("msReportesClient")
    private final WebClient msReportesClient;

    /**
     * Obtiene todos los reportes del sistema.
     *
     * @return lista de ReportMsDTO con todos los reportes
     * @throws MsException si ocurre un error interno en el MS-Reportes (500)
     */
    public List<ReportMsDTO> listAll() {
        return msReportesClient.get()
                .uri("/api/reportes")
                .retrieve()
                .onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals,
                    response -> response.bodyToMono(String.class)
                        .map(body -> new MsException("Error al obtener reportes", HttpStatus.INTERNAL_SERVER_ERROR)))
                .bodyToFlux(ReportMsDTO.class)
                .collectList()
                .block();
    }

    /**
     * Obtiene solo los reportes con estado ACTIVO.
     *
     * @return lista de ReportMsDTO con reportes activos
     * @throws MsException si ocurre un error interno en el MS-Reportes (500)
     */
    public List<ReportMsDTO> listActive() {
        return msReportesClient.get()
                .uri("/api/reportes/activos")
                .retrieve()
                .onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals,
                    response -> response.bodyToMono(String.class)
                        .map(body -> new MsException("Error al obtener reportes activos", HttpStatus.INTERNAL_SERVER_ERROR)))
                .bodyToFlux(ReportMsDTO.class)
                .collectList()
                .block();
    }

    /**
     * Busca un reporte por su identificador unico.
     *
     * @param id identificador del reporte
     * @return ReportMsDTO con los datos del reporte
     * @throws MsException si el reporte no existe (404)
     */
    public ReportMsDTO findById(Long id) {
        return msReportesClient.get()
                .uri("/api/reportes/{id}", id)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals,
                    response -> response.bodyToMono(String.class)
                        .map(body -> new MsException("Reporte no encontrado", HttpStatus.NOT_FOUND)))
                .bodyToMono(ReportMsDTO.class)
                .block();
    }

    /**
     * Crea un nuevo reporte de incendio.
     *
     * @param body mapa con los datos del reporte
     * @return ReportMsDTO con el reporte creado
     * @throws MsException si los datos son invalidos (400)
     */
    public ReportMsDTO create(Map<String, Object> body) {
        return msReportesClient.post()
                .uri("/api/reportes")
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatus.BAD_REQUEST::equals,
                    response -> response.bodyToMono(String.class)
                        .map(b -> new MsException("Error al crear reporte", HttpStatus.BAD_REQUEST)))
                .bodyToMono(ReportMsDTO.class)
                .block();
    }

    /**
     * Actualiza el estado de un reporte existente.
     *
     * @param id     identificador del reporte
     * @param status nuevo estado del reporte
     * @return ReportMsDTO con el reporte actualizado
     * @throws MsException si el reporte no existe (404)
     */
    public ReportMsDTO updateStatus(Long id, String status) {
        return msReportesClient.put()
                .uri("/api/reportes/{id}/estado", id)
                .bodyValue(Map.of("estado", status))
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals,
                    response -> response.bodyToMono(String.class)
                        .map(body -> new MsException("Reporte no encontrado", HttpStatus.NOT_FOUND)))
                .bodyToMono(ReportMsDTO.class)
                .block();
    }

    /**
     * Actualiza el titulo de un reporte existente.
     *
     * @param id    identificador del reporte
     * @param title nuevo titulo del reporte
     * @return ReportMsDTO con el reporte actualizado
     * @throws MsException si el reporte no existe (404)
     */
    public ReportMsDTO updateTitle(Long id, String title) {
        return msReportesClient.put()
                .uri("/api/reportes/{id}", id)
                .bodyValue(Map.of("titulo", title))
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals,
                    response -> response.bodyToMono(String.class)
                        .map(body -> new MsException("Reporte no encontrado", HttpStatus.NOT_FOUND)))
                .bodyToMono(ReportMsDTO.class)
                .block();
    }

    /**
     * Elimina un reporte por su identificador.
     *
     * @param id identificador del reporte a eliminar
     * @throws MsException si el reporte no existe (404)
     */
    public void delete(Long id) {
        msReportesClient.delete()
                .uri("/api/reportes/{id}", id)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals,
                    response -> response.bodyToMono(String.class)
                        .map(body -> new MsException("Reporte no encontrado", HttpStatus.NOT_FOUND)))
                .bodyToMono(Void.class)
                .block();
    }
}