package cl.municipalidad.msbrigadas.controller;

import cl.municipalidad.msbrigadas.dto.BrigadeDTO;
import cl.municipalidad.msbrigadas.dto.CreateBrigadeRequest;
import cl.municipalidad.msbrigadas.dto.UpdateStatusRequest;
import cl.municipalidad.msbrigadas.dto.UpdateLocationRequest;
import cl.municipalidad.msbrigadas.service.BrigadeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de brigadas de emergencia.
 *
 * <p>Expone los endpoints CRUD de brigadas y operaciones específicas como
 * actualización de estado y ubicación GPS. Delega toda la lógica al
 * {@link BrigadeService} y usa {@code @Valid} para validar los DTOs de
 * entrada automáticamente antes de procesarlos.</p>
 *
 * <p>Todos los endpoints son públicos, configurados en
 * {@link cl.municipalidad.msbrigadas.security.SecurityConfig}.</p>
 *
 * <p><b>Base URL:</b> {@code /api/brigadas}</p>
 *
 * @author Municipalidad Valle del Sol
 * @version 1.0
 * @see BrigadeService
 */
@RestController
@RequestMapping("/api/brigadas")
@RequiredArgsConstructor
public class BrigadeController {

    private final BrigadeService brigadaService;

    /**
     * Registra una nueva brigada en el sistema.
     *
     * <p><b>POST</b> {@code /api/brigadas}</p>
     *
     * <p>Ejemplo de cuerpo de solicitud:
     * <pre>{@code
     * {
     *   "nombre": "Brigada Norte",
     *   "tipo": "INCENDIO",
     *   "emailResponsable": "jefe@municipalidad.cl",
     *   "latitud": -33.45,
     *   "longitud": -70.65
     * }
     * }</pre></p>
     *
     * @param request DTO validado con los datos de la nueva brigada.
     * @return {@link BrigadeDTO} con los datos de la brigada creada y HTTP 201.
     *         HTTP 400 si algún campo no pasa la validación.
     */
    @PostMapping
    public ResponseEntity<BrigadeDTO> crear(@Valid @RequestBody CreateBrigadeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(brigadaService.crear(request));
    }

    /**
     * Retorna todas las brigadas registradas en el sistema.
     *
     * <p><b>GET</b> {@code /api/brigadas}</p>
     *
     * @return HTTP 200 con lista de {@link BrigadeDTO}. Lista vacía si no hay brigadas.
     */
    @GetMapping
    public ResponseEntity<List<BrigadeDTO>> listarTodas() {
        return ResponseEntity.ok(brigadaService.listarTodas());
    }

    /**
     * Retorna todas las brigadas con estado {@code DISPONIBLE}.
     *
     * <p><b>GET</b> {@code /api/brigadas/disponibles}</p>
     *
     * @return HTTP 200 con lista de {@link BrigadeDTO} disponibles.
     */
    @GetMapping("/disponibles")
    public ResponseEntity<List<BrigadeDTO>> listarDisponibles() {
        return ResponseEntity.ok(brigadaService.listarDisponibles());
    }

    /**
     * Retorna todas las brigadas de un tipo específico.
     *
     * <p><b>GET</b> {@code /api/brigadas/tipo/{tipo}}</p>
     *
     * @param tipo Tipo de brigada a filtrar (INCENDIO, RESCATE, MEDICA).
     * @return HTTP 200 con lista de {@link BrigadeDTO} del tipo indicado.
     */
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<BrigadeDTO>> listarPorTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(brigadaService.listarPorTipo(tipo));
    }

    /**
     * Busca una brigada por su identificador único.
     *
     * <p><b>GET</b> {@code /api/brigadas/{id}}</p>
     *
     * @param id Identificador de la brigada.
     * @return HTTP 200 con {@link BrigadeDTO} si existe.
     *         HTTP 404 si no se encuentra.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BrigadeDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(brigadaService.buscarPorId(id));
    }

    /**
     * Actualiza el estado operativo de una brigada.
     *
     * <p><b>PUT</b> {@code /api/brigadas/{id}/estado}</p>
     *
     * <p>Ejemplo de cuerpo:
     * <pre>{@code
     * { "estado": "EN_CAMINO" }
     * }</pre></p>
     *
     * @param id      Identificador de la brigada.
     * @param request DTO validado con el nuevo estado.
     * @return HTTP 200 con {@link BrigadeDTO} actualizado.
     *         HTTP 400 si el estado no es válido.
     *         HTTP 404 si la brigada no existe.
     */
    @PutMapping("/{id}/estado")
    public ResponseEntity<BrigadeDTO> actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(brigadaService.actualizarEstado(id, request));
    }

    /**
     * Actualiza la ubicación GPS de una brigada.
     *
     * <p><b>PUT</b> {@code /api/brigadas/{id}/ubicacion}</p>
     *
     * <p>Ejemplo de cuerpo:
     * <pre>{@code
     * { "latitud": -33.45, "longitud": -70.65 }
     * }</pre></p>
     *
     * @param id      Identificador de la brigada.
     * @param request DTO validado con las nuevas coordenadas.
     * @return HTTP 200 con {@link BrigadeDTO} actualizado.
     *         HTTP 400 si las coordenadas están fuera de rango.
     *         HTTP 404 si la brigada no existe.
     */
    @PutMapping("/{id}/ubicacion")
    public ResponseEntity<BrigadeDTO> actualizarUbicacion(
            @PathVariable Long id,
            @Valid @RequestBody UpdateLocationRequest request) {
        return ResponseEntity.ok(brigadaService.actualizarUbicacion(id, request));
    }

    /**
     * Elimina una brigada del sistema.
     *
     * <p><b>DELETE</b> {@code /api/brigadas/{id}}</p>
     *
     * @param id Identificador de la brigada a eliminar.
     * @return HTTP 204 sin contenido si se eliminó correctamente.
     *         HTTP 404 si la brigada no existe.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        brigadaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}