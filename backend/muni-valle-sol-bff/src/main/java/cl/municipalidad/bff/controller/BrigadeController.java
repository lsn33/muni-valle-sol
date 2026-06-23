package cl.municipalidad.bff.controller;

import cl.municipalidad.bff.dto.BrigadeDTO;
import cl.municipalidad.bff.dto.CreateBrigadeRequest;
import cl.municipalidad.bff.dto.UpdateBrigadeStatusRequest;
import cl.municipalidad.bff.dto.UpdateBrigadeLocationRequest;
import cl.municipalidad.bff.service.BrigadeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador de brigadas del BFF.
 * Expone los endpoints REST para la gestión de brigadas de emergencia,
 * delegando toda la lógica al BrigadaService que a su vez consulta MS-Brigadas.
 *
 * <p>Patrones aplicados:</p>
 * <ul>
 *   <li>Facade Pattern: interfaz simplificada para el cliente HTTP</li>
 *   <li>Single Responsibility: solo maneja endpoints HTTP de brigadas</li>
 * </ul>
 *
 * <p><b>Base URL:</b> {@code /api/brigadas}</p>
 *
 * @author Municipalidad Valle del Sol
 * @version 1.0
 */
@RestController
@RequestMapping("/api/brigadas")
@RequiredArgsConstructor
public class BrigadeController {

    private final BrigadeService brigadaService;

    /**
     * Lista todas las brigadas registradas en el sistema.
     *
     * @return HTTP 200 con lista de BrigadaDTO
     */
    @GetMapping
    public ResponseEntity<List<BrigadeDTO>> listAll() {
        return ResponseEntity.ok(brigadaService.listAll());
    }

    /**
     * Lista las brigadas con estado DISPONIBLE.
     *
     * @return HTTP 200 con lista de BrigadaDTO disponibles
     */
    @GetMapping("/disponibles")
    public ResponseEntity<List<BrigadeDTO>> listDisponibles() {
        return ResponseEntity.ok(brigadaService.listDisponibles());
    }

    /**
     * Lista las brigadas de un tipo específico.
     *
     * @param tipo tipo de brigada (INCENDIO, RESCATE, MEDICA)
     * @return HTTP 200 con lista de BrigadaDTO del tipo indicado
     */
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<BrigadeDTO>> listByTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(brigadaService.listByTipo(tipo));
    }

    /**
     * Busca una brigada por su identificador.
     *
     * @param id identificador de la brigada
     * @return HTTP 200 con BrigadaDTO si existe, HTTP 404 si no
     */
    @GetMapping("/{id}")
    public ResponseEntity<BrigadeDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(brigadaService.findById(id));
    }

    /**
     * Crea una nueva brigada.
     *
     * @param request DTO validado con los datos de la nueva brigada
     * @return HTTP 201 con BrigadaDTO creada
     */
    @PostMapping
    public ResponseEntity<BrigadeDTO> create(@Valid @RequestBody CreateBrigadeRequest request) {
        Map<String, Object> body = Map.of(
            "nombre", request.nombre(),
            "tipo", request.tipo(),
            "emailResponsable", request.emailResponsable(),
            "latitud", request.latitud(),
            "longitud", request.longitud()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(brigadaService.create(body));
    }

    /**
     * Actualiza el estado de una brigada.
     *
     * @param id      identificador de la brigada
     * @param request DTO validado con el nuevo estado
     * @return HTTP 200 con BrigadaDTO actualizada
     */
    @PutMapping("/{id}/estado")
    public ResponseEntity<BrigadeDTO> updateEstado(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBrigadeStatusRequest request) {
        return ResponseEntity.ok(brigadaService.updateEstado(id, request.estado()));
    }

    /**
     * Actualiza la ubicación GPS de una brigada.
     *
     * @param id      identificador de la brigada
     * @param request DTO validado con las nuevas coordenadas
     * @return HTTP 200 con BrigadaDTO actualizada
     */
    @PutMapping("/{id}/ubicacion")
    public ResponseEntity<BrigadeDTO> updateUbicacion(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBrigadeLocationRequest request) {
        return ResponseEntity.ok(brigadaService.updateUbicacion(id, request.latitud(), request.longitud()));
    }

    /**
     * Elimina una brigada.
     *
     * @param id identificador de la brigada a eliminar
     * @return HTTP 204 sin contenido
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        brigadaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}