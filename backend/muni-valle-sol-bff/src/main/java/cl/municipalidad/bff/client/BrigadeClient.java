package cl.municipalidad.bff.client;

import cl.municipalidad.bff.dto.BrigadeDTO;
import cl.municipalidad.bff.exception.MsException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

/**
 * Cliente HTTP para el MS-Brigadas.
 * Encapsula todas las llamadas al microservicio de brigadas via WebClient.
 *
 * <p>Patrones aplicados:</p>
 * <ul>
 *   <li>Gateway Pattern: punto unico de acceso al MS-Brigadas</li>
 *   <li>Single Responsibility: solo gestiona comunicacion con MS-Brigadas</li>
 * </ul>
 *
 * @author Municipalidad Valle del Sol
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class BrigadeClient {

    @Qualifier("msBrigadasClient")
    private final WebClient msBrigadasClient;

    /**
     * Obtiene todas las brigadas del sistema.
     *
     * @return lista de BrigadaDTO con todas las brigadas
     * @throws MsException si ocurre un error interno en el MS-Brigadas (500)
     */
    public List<BrigadeDTO> listAll() {
        return msBrigadasClient.get()
                .uri("/api/brigadas")
                .retrieve()
                .onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals,
                    response -> response.bodyToMono(String.class)
                        .map(body -> new MsException("Error al obtener brigadas", HttpStatus.INTERNAL_SERVER_ERROR)))
                .bodyToFlux(BrigadeDTO.class)
                .collectList()
                .block();
    }

    /**
     * Obtiene solo las brigadas con estado DISPONIBLE.
     *
     * @return lista de BrigadaDTO disponibles
     * @throws MsException si ocurre un error interno en el MS-Brigadas (500)
     */
    public List<BrigadeDTO> listDisponibles() {
        return msBrigadasClient.get()
                .uri("/api/brigadas/disponibles")
                .retrieve()
                .onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals,
                    response -> response.bodyToMono(String.class)
                        .map(body -> new MsException("Error al obtener brigadas disponibles", HttpStatus.INTERNAL_SERVER_ERROR)))
                .bodyToFlux(BrigadeDTO.class)
                .collectList()
                .block();
    }

    /**
     * Obtiene las brigadas de un tipo específico.
     *
     * @param tipo tipo de brigada (INCENDIO, RESCATE, MEDICA)
     * @return lista de BrigadaDTO del tipo indicado
     * @throws MsException si ocurre un error interno en el MS-Brigadas (500)
     */
    public List<BrigadeDTO> listByTipo(String tipo) {
        return msBrigadasClient.get()
                .uri("/api/brigadas/tipo/{tipo}", tipo)
                .retrieve()
                .onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals,
                    response -> response.bodyToMono(String.class)
                        .map(body -> new MsException("Error al obtener brigadas por tipo", HttpStatus.INTERNAL_SERVER_ERROR)))
                .bodyToFlux(BrigadeDTO.class)
                .collectList()
                .block();
    }

    /**
     * Busca una brigada por su identificador único.
     *
     * @param id identificador de la brigada
     * @return BrigadaDTO con los datos de la brigada
     * @throws MsException si la brigada no existe (404)
     */
    public BrigadeDTO findById(Long id) {
        return msBrigadasClient.get()
                .uri("/api/brigadas/{id}", id)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals,
                    response -> response.bodyToMono(String.class)
                        .map(body -> new MsException("Brigada no encontrada", HttpStatus.NOT_FOUND)))
                .bodyToMono(BrigadeDTO.class)
                .block();
    }

    /**
     * Crea una nueva brigada.
     *
     * @param body mapa con los datos de la brigada
     * @return BrigadaDTO con la brigada creada
     * @throws MsException si los datos son invalidos (400)
     */
    public BrigadeDTO create(Map<String, Object> body) {
        return msBrigadasClient.post()
                .uri("/api/brigadas")
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatus.BAD_REQUEST::equals,
                    response -> response.bodyToMono(String.class)
                        .map(b -> new MsException("Error al crear brigada", HttpStatus.BAD_REQUEST)))
                .bodyToMono(BrigadeDTO.class)
                .block();
    }

    /**
     * Actualiza el estado de una brigada existente.
     *
     * @param id     identificador de la brigada
     * @param estado nuevo estado de la brigada
     * @return BrigadaDTO con la brigada actualizada
     * @throws MsException si la brigada no existe (404)
     */
    public BrigadeDTO updateEstado(Long id, String estado) {
        return msBrigadasClient.put()
                .uri("/api/brigadas/{id}/estado", id)
                .bodyValue(Map.of("estado", estado))
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals,
                    response -> response.bodyToMono(String.class)
                        .map(body -> new MsException("Brigada no encontrada", HttpStatus.NOT_FOUND)))
                .bodyToMono(BrigadeDTO.class)
                .block();
    }

    /**
     * Actualiza la ubicación GPS de una brigada existente.
     *
     * @param id       identificador de la brigada
     * @param latitud  nueva latitud
     * @param longitud nueva longitud
     * @return BrigadaDTO con la brigada actualizada
     * @throws MsException si la brigada no existe (404)
     */
    public BrigadeDTO updateUbicacion(Long id, Double latitud, Double longitud) {
        return msBrigadasClient.put()
                .uri("/api/brigadas/{id}/ubicacion", id)
                .bodyValue(Map.of("latitud", latitud, "longitud", longitud))
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals,
                    response -> response.bodyToMono(String.class)
                        .map(body -> new MsException("Brigada no encontrada", HttpStatus.NOT_FOUND)))
                .bodyToMono(BrigadeDTO.class)
                .block();
    }

    /**
     * Elimina una brigada por su identificador.
     *
     * @param id identificador de la brigada a eliminar
     * @throws MsException si la brigada no existe (404)
     */
    public void delete(Long id) {
        msBrigadasClient.delete()
                .uri("/api/brigadas/{id}", id)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals,
                    response -> response.bodyToMono(String.class)
                        .map(body -> new MsException("Brigada no encontrada", HttpStatus.NOT_FOUND)))
                .bodyToMono(Void.class)
                .block();
    }
}