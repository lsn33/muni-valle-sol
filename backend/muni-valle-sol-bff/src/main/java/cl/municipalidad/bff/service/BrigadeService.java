package cl.municipalidad.bff.service;

import cl.municipalidad.bff.client.BrigadeClient;
import cl.municipalidad.bff.dto.BrigadeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Servicio de brigadas del BFF.
 * Delega las llamadas al MS-Brigadas a través de BrigadaClient.
 *
 * <p>Patrones aplicados:</p>
 * <ul>
 *   <li>Facade Pattern: expone interfaz simplificada al controller</li>
 *   <li>Single Responsibility: solo gestiona lógica de brigadas</li>
 * </ul>
 *
 * @author Municipalidad Valle del Sol
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class BrigadeService {

    private final BrigadeClient brigadaClient;

    /**
     * Lista todas las brigadas registradas.
     *
     * @return lista de BrigadaDTO
     */
    public List<BrigadeDTO> listAll() {
        return brigadaClient.listAll();
    }

    /**
     * Lista las brigadas disponibles.
     *
     * @return lista de BrigadaDTO con estado DISPONIBLE
     */
    public List<BrigadeDTO> listDisponibles() {
        return brigadaClient.listDisponibles();
    }

    /**
     * Lista las brigadas de un tipo específico.
     *
     * @param tipo tipo de brigada (INCENDIO, RESCATE, MEDICA)
     * @return lista de BrigadaDTO del tipo indicado
     */
    public List<BrigadeDTO> listByTipo(String tipo) {
        return brigadaClient.listByTipo(tipo);
    }

    /**
     * Busca una brigada por su identificador.
     *
     * @param id identificador de la brigada
     * @return BrigadaDTO con los datos de la brigada
     */
    public BrigadeDTO findById(Long id) {
        return brigadaClient.findById(id);
    }

    /**
     * Crea una nueva brigada.
     *
     * @param body mapa con los datos de la brigada
     * @return BrigadaDTO con la brigada creada
     */
    public BrigadeDTO create(Map<String, Object> body) {
        return brigadaClient.create(body);
    }

    /**
     * Actualiza el estado de una brigada.
     *
     * @param id     identificador de la brigada
     * @param estado nuevo estado
     * @return BrigadaDTO con la brigada actualizada
     */
    public BrigadeDTO updateEstado(Long id, String estado) {
        return brigadaClient.updateEstado(id, estado);
    }

    /**
     * Actualiza la ubicación GPS de una brigada.
     *
     * @param id       identificador de la brigada
     * @param latitud  nueva latitud
     * @param longitud nueva longitud
     * @return BrigadaDTO con la brigada actualizada
     */
    public BrigadeDTO updateUbicacion(Long id, Double latitud, Double longitud) {
        return brigadaClient.updateUbicacion(id, latitud, longitud);
    }

    /**
     * Elimina una brigada.
     *
     * @param id identificador de la brigada a eliminar
     */
    public void delete(Long id) {
        brigadaClient.delete(id);
    }
}