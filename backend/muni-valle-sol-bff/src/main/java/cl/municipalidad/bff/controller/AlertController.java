package cl.municipalidad.bff.controller;

import cl.municipalidad.bff.dto.AlertDTO;
import cl.municipalidad.bff.dto.CreateAlertRequest;
import cl.municipalidad.bff.service.AlertRequestHandler;
import cl.municipalidad.bff.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador de alertas del BFF.
 * Expone endpoints REST para la gestión de alertas derivadas de reportes.
 * Delega toda validación y transformación al handler, y lógica de negocio al service.
 *
 * <p>Patrones aplicados:</p>
 * <ul>
 *   <li>Facade Pattern: interfaz simplificada para cliente HTTP</li>
 *   <li>Delegation Pattern: delega validación al handler y lógica al service</li>
 *   <li>Single Responsibility: solo maneja endpoints HTTP</li>
 * </ul>
 *
 * <p>Responsabilidades separadas:</p>
 * <pre>{@code
 * AlertController
 *   ↓ solo recibe y responde HTTP
 * AlertRequestHandler
 *   ↓ valida y transforma CreateAlertRequest
 * AlertService
 *   ↓ implementa lógica de negocio
 * }</pre>
 *
 * <p><b>Documentación OpenAPI:</b> Ver /swagger-ui.html para ejemplos interactivos
 * e integración con el openapi.yaml</p>
 *
 * @author Beltran
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/alertas")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;
    private final AlertRequestHandler alertRequestHandler;

    /**
     * Lista todas las alertas activas derivadas de reportes con estado ACTIVO.
     *
     * @return lista de AlertDTO con las alertas activas
     *
     * <p>Ejemplo de respuesta:</p>
     * <pre>{@code
     * [
     *   {
     *     "id": "a1b2c3d4",
     *     "titulo": "Incendio Forestal",
     *     "descripcion": "Fuego en cerro norte",
     *     "severidad": "ALTA",
     *     "fecha": "2026-06-13T14:30:00"
     *   }
     * ]
     * }</pre>
     *
     * <p><b>OpenAPI:</b> GET /alertas - operationId: listAlerts</p>
     */
    @GetMapping
    public ResponseEntity<List<AlertDTO>> listAlerts() {
        return ResponseEntity.ok(alertService.listAlerts());
    }

    /**
     * Crea una nueva alerta manual en el sistema.
     * Delega validación al handler que lanza IllegalArgumentException si falla.
     *
     * @param request record con titulo, descripcion y severidad
     * @return AlertDTO con la alerta creada, o 400 si validación falla
     *
     * <p>Ejemplo de request:</p>
     * <pre>{@code
     * POST /api/alertas
     * Content-Type: application/json
     *
     * {
     *   "titulo": "Alerta Manual",
     *   "descripcion": "Situación crítica detectada",
     *   "severidad": "ALTA"
     * }
     * }</pre>
     *
     * <p>Ejemplo de respuesta (201 Created):</p>
     * <pre>{@code
     * {
     *   "id": "xyz789",
     *   "titulo": "Alerta Manual",
     *   "descripcion": "Situación crítica detectada",
     *   "severidad": "ALTA",
     *   "fecha": "2026-06-13T14:35:00"
     * }
     * }</pre>
     *
     * <p><b>OpenAPI:</b> POST /alertas - operationId: createAlert</p>
     */
    @PostMapping
    public ResponseEntity<AlertDTO> create(@RequestBody CreateAlertRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(alertRequestHandler.handleCreate(request));
    }
}