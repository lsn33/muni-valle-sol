package cl.municipalidad.msreport.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manejador global de excepciones para el microservicio de reportes.
 *
 * <p>Intercepta las excepciones lanzadas en cualquier controlador y las
 * transforma en respuestas HTTP estructuradas y legibles, evitando que
 * Spring devuelva stack traces o mensajes genéricos al cliente.</p>
 *
 * <p>Patrones aplicados:</p>
 * <ul>
 *   <li>Chain of Responsibility: cada handler atiende su tipo de excepción</li>
 *   <li>Facade Pattern: oculta los detalles internos del error al cliente</li>
 *   <li>Single Responsibility: centraliza el manejo de errores fuera de los controladores</li>
 * </ul>
 *
 * <p>Mapa de excepciones a respuestas HTTP:</p>
 * <pre>{@code
 * MethodArgumentNotValidException → 400 Bad Request  (falla @Valid)
 * IllegalArgumentException        → 400 Bad Request  (tipo inválido en factory)
 * RuntimeException                → 409 Conflict     (reporte no encontrado)
 * Exception                       → 500 Internal     (error inesperado)
 * }</pre>
 *
 * @author Beltran
 * @version 1.0
 * @since 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja errores de validación de Bean Validation ({@code @Valid}).
     *
     * <p>Ejemplo de respuesta:
     * <pre>{@code
     * {
     *   "error": "El título es obligatorio, El tipo debe ser INCENDIO, HUMO o SOSPECHOSO",
     *   "timestamp": "2025-06-01T12:00:00"
     * }
     * }</pre></p>
     *
     * @param ex Excepción lanzada por Spring cuando falla la validación.
     * @return HTTP 400 con los mensajes de error de cada campo inválido.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildError(mensaje));
    }

    /**
     * Maneja tipos de reporte inválidos lanzados por el {@link cl.municipalidad.msreport.factory.ReportFactory}.
     *
     * <p>Se activa cuando se envía un tipo que no existe en el enum
     * {@link cl.municipalidad.msreport.factory.ReportType}.</p>
     *
     * @param ex Excepción lanzada por el factory al hacer {@code ReportType.valueOf()}.
     * @return HTTP 400 con mensaje descriptivo del error.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildError("Tipo de reporte inválido. Use: INCENDIO, HUMO o SOSPECHOSO"));
    }

    /**
     * Maneja excepciones de reglas de negocio (reporte no encontrado, etc.).
     *
     * @param ex Excepción lanzada por el servicio.
     * @return HTTP 409 Conflict con el mensaje descriptivo del error.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(buildError(ex.getMessage()));
    }

    /**
     * Maneja cualquier excepción no contemplada por los handlers anteriores.
     *
     * @param ex Excepción genérica no manejada.
     * @return HTTP 500 con mensaje genérico de error interno.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError("Error interno del servidor. Intente más tarde."));
    }

    /**
     * Construye la estructura estándar de respuesta de error.
     *
     * @param mensaje Descripción del error ocurrido.
     * @return Mapa con los campos {@code error} y {@code timestamp}.
     */
    private Map<String, Object> buildError(String mensaje) {
        return Map.of(
                "error", mensaje,
                "timestamp", LocalDateTime.now().toString()
        );
    }
}