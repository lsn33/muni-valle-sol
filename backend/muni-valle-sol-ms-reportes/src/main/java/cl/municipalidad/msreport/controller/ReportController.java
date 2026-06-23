package cl.municipalidad.msreport.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.municipalidad.msreport.dto.CreateReportRequest;
import cl.municipalidad.msreport.dto.ReportDTO;
import cl.municipalidad.msreport.dto.UpdateStatusRequest;
import cl.municipalidad.msreport.dto.UpdateTitleRequest;
import cl.municipalidad.msreport.service.ReportService;

import java.util.List;

/**
 * Controlador REST para la gestión de reportes de emergencia.
 *
 * <p>Responsabilidad única: recibir requests HTTP, delegar al
 * {@link ReportService} y retornar la respuesta. No contiene
 * lógica de negocio ni desempaquetado de datos.</p>
 *
 * <p>Patrones aplicados:</p>
 * <ul>
 *   <li>Single Responsibility: solo maneja endpoints HTTP</li>
 *   <li>Delegation Pattern: delega toda lógica al service</li>
 *   <li>Facade Pattern: interfaz simplificada para clientes HTTP externos</li>
 * </ul>
 *
 * <p>Flujo de responsabilidades:</p>
 * <pre>{@code
 * ReportController
 *   ↓ solo recibe y responde HTTP
 * ReportService
 *   ↓ implementa lógica de negocio
 * ReportFactory + ReportRepository
 *   ↓ crea y persiste entidades
 * }</pre>
 *
 * <p><b>Base URL:</b> {@code /api/reportes}</p>
 *
 * @author Beltran
 * @version 1.0
 * @since 1.0
 * @see ReportService
 */
@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reporteService;

    /**
     * Crea un nuevo reporte de emergencia.
     *
     * <p><b>POST</b> {@code /api/reportes}</p>
     *
     * @param request Record validado con los datos del nuevo reporte.
     * @return {@link ReportDTO} con el reporte creado y HTTP 201.
     *         HTTP 400 si algún campo no pasa la validación.
     */
    @PostMapping
    public ResponseEntity<ReportDTO> crear(@Valid @RequestBody CreateReportRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reporteService.crear(request));
    }

    /**
     * Retorna todos los reportes registrados en el sistema.
     *
     * <p><b>GET</b> {@code /api/reportes}</p>
     *
     * @return HTTP 200 con lista de {@link ReportDTO}. Lista vacía si no hay reportes.
     */
    @GetMapping
    public ResponseEntity<List<ReportDTO>> listarTodos() {
        return ResponseEntity.ok(reporteService.listarTodos());
    }

    /**
     * Retorna solo los reportes con estado {@code ACTIVO}.
     *
     * <p><b>GET</b> {@code /api/reportes/activos}</p>
     *
     * @return HTTP 200 con lista de {@link ReportDTO} activos.
     */
    @GetMapping("/activos")
    public ResponseEntity<List<ReportDTO>> listarActivos() {
        return ResponseEntity.ok(reporteService.listarActivos());
    }

    /**
     * Busca un reporte específico por su identificador.
     *
     * <p><b>GET</b> {@code /api/reportes/{id}}</p>
     *
     * @param id Identificador del reporte.
     * @return HTTP 200 con {@link ReportDTO} si existe.
     *         HTTP 409 si no se encuentra el reporte.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReportDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reporteService.buscarPorId(id));
    }

    /**
     * Actualiza el estado de un reporte existente.
     *
     * <p><b>PUT</b> {@code /api/reportes/{id}/estado}</p>
     *
     * @param id      Identificador del reporte a actualizar.
     * @param request Record validado con el nuevo estado.
     * @return HTTP 200 con {@link ReportDTO} actualizado.
     *         HTTP 400 si el estado no es válido.
     */
    @PutMapping("/{id}/estado")
    public ResponseEntity<ReportDTO> actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(reporteService.actualizarEstado(id, request.estado()));
    }

    /**
     * Actualiza el título de un reporte existente.
     *
     * <p><b>PUT</b> {@code /api/reportes/{id}}</p>
     *
     * @param id      Identificador del reporte a actualizar.
     * @param request Record validado con el nuevo título.
     * @return HTTP 200 con {@link ReportDTO} actualizado.
     *         HTTP 400 si el título no pasa la validación.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReportDTO> actualizarTitulo(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTitleRequest request) {
        return ResponseEntity.ok(reporteService.actualizarTitulo(id, request));
    }

    /**
     * Elimina un reporte por su identificador.
     *
     * <p><b>DELETE</b> {@code /api/reportes/{id}}</p>
     *
     * @param id Identificador del reporte a eliminar.
     * @return HTTP 204 sin contenido si se eliminó correctamente.
     *         HTTP 409 si no se encuentra el reporte.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        reporteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}