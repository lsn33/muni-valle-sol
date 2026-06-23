package cl.municipalidad.msbrigadas.service;

import cl.municipalidad.msbrigadas.dto.BrigadeDTO;
import cl.municipalidad.msbrigadas.dto.CreateBrigadeRequest;
import cl.municipalidad.msbrigadas.dto.UpdateStatusRequest;
import cl.municipalidad.msbrigadas.dto.UpdateLocationRequest;
import cl.municipalidad.msbrigadas.factory.BrigadeFactory;
import cl.municipalidad.msbrigadas.model.Brigade;
import cl.municipalidad.msbrigadas.repository.BrigadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de lógica de negocio para la gestión de brigadas de emergencia.
 *
 * <p>Actúa como capa intermedia entre el controlador
 * ({@link cl.municipalidad.msbrigadas.controller.BrigadeController}) y el repositorio
 * ({@link BrigadeRepository}), encapsulando todas las reglas de negocio.</p>
 *
 * <p>Usa {@link BrigadeFactory} para crear brigadas siguiendo el patrón
 * Factory Method, y {@code @Transactional} para garantizar la integridad
 * de los datos en operaciones de escritura.</p>
 *
 * @author Municipalidad Valle del Sol
 * @version 1.0
 * @see BrigadeRepository
 * @see BrigadeFactory
 */
@Service
@RequiredArgsConstructor
public class BrigadeService {

    private final BrigadeRepository brigadaRepository;
    private final BrigadeFactory brigadaFactory;

    /**
     * Registra una nueva brigada en el sistema.
     *
     * <p>Delega la creación del objeto a {@link BrigadeFactory} para aplicar
     * las reglas de negocio según el tipo de brigada.</p>
     *
     * @param request DTO validado con los datos de la nueva brigada.
     * @return {@link BrigadeDTO} con los datos de la brigada creada.
     * @throws IllegalArgumentException si el tipo de brigada no es reconocido.
     */
    @Transactional
    public BrigadeDTO crear(CreateBrigadeRequest request) {
        Brigade brigada = brigadaFactory.crear(
            request.nombre(),
            request.tipo(),
            request.emailResponsable(),
            request.latitud(),
            request.longitud()
        );
        return toDTO(brigadaRepository.save(brigada));
    }

    /**
     * Retorna todas las brigadas registradas en el sistema.
     *
     * @return Lista de {@link BrigadeDTO}. Vacía si no hay brigadas registradas.
     */
    @Transactional(readOnly = true)
    public List<BrigadeDTO> listarTodas() {
        return brigadaRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Retorna todas las brigadas con estado {@code DISPONIBLE}.
     *
     * @return Lista de {@link BrigadeDTO} con brigadas disponibles.
     */
    @Transactional(readOnly = true)
    public List<BrigadeDTO> listarDisponibles() {
        return brigadaRepository.findByEstado("DISPONIBLE")
                .stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Retorna todas las brigadas de un tipo específico.
     *
     * @param tipo Tipo de brigada a filtrar (INCENDIO, RESCATE, MEDICA).
     * @return Lista de {@link BrigadeDTO} del tipo indicado.
     */
    @Transactional(readOnly = true)
    public List<BrigadeDTO> listarPorTipo(String tipo) {
        return brigadaRepository.findByTipo(tipo.toUpperCase())
                .stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Busca una brigada por su identificador único.
     *
     * @param id Identificador de la brigada.
     * @return {@link BrigadeDTO} con los datos de la brigada.
     * @throws RuntimeException si no existe una brigada con ese ID.
     */
    @Transactional(readOnly = true)
    public BrigadeDTO buscarPorId(Long id) {
        return brigadaRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Brigada no encontrada con id: " + id));
    }

    /**
     * Actualiza el estado operativo de una brigada.
     *
     * @param id      Identificador de la brigada a actualizar.
     * @param request DTO validado con el nuevo estado.
     * @return {@link BrigadeDTO} con el estado actualizado.
     * @throws RuntimeException si no existe una brigada con ese ID.
     */
    @Transactional
    public BrigadeDTO actualizarEstado(Long id, UpdateStatusRequest request) {
        Brigade brigada = brigadaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brigada no encontrada con id: " + id));
        brigada.setEstado(request.estado());
        return toDTO(brigadaRepository.save(brigada));
    }

    /**
     * Actualiza la ubicación GPS de una brigada.
     *
     * @param id      Identificador de la brigada a actualizar.
     * @param request DTO validado con las nuevas coordenadas.
     * @return {@link BrigadeDTO} con la ubicación actualizada.
     * @throws RuntimeException si no existe una brigada con ese ID.
     */
    @Transactional
    public BrigadeDTO actualizarUbicacion(Long id, UpdateLocationRequest request) {
        Brigade brigada = brigadaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brigada no encontrada con id: " + id));
        brigada.setLatitud(request.latitud());
        brigada.setLongitud(request.longitud());
        return toDTO(brigadaRepository.save(brigada));
    }

    /**
     * Elimina una brigada del sistema por su identificador.
     *
     * @param id Identificador de la brigada a eliminar.
     * @throws RuntimeException si no existe una brigada con ese ID.
     */
    @Transactional
    public void eliminar(Long id) {
        if (!brigadaRepository.existsById(id)) {
            throw new RuntimeException("Brigada no encontrada con id: " + id);
        }
        brigadaRepository.deleteById(id);
    }

    /**
     * Convierte una entidad {@link Brigade} a su representación pública {@link BrigadeDTO}.
     *
     * <p>Método privado auxiliar que centraliza la conversión, evitando
     * duplicación de código en cada operación del servicio.</p>
     *
     * @param brigada Entidad a convertir.
     * @return {@link BrigadeDTO} con los datos públicos de la brigada.
     */
    private BrigadeDTO toDTO(Brigade brigada) {
        return new BrigadeDTO(
            brigada.getId(),
            brigada.getNombre(),
            brigada.getEstado(),
            brigada.getTipo(),
            brigada.getLatitud(),
            brigada.getLongitud(),
            brigada.getEmailResponsable(),
            brigada.getFechaCreacion()
        );
    }
}