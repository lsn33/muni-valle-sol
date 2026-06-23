package cl.municipalidad.msbrigadas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manejador global de excepciones para el microservicio de brigadas.
 *
 * <p>Intercepta todas las excepciones lanzadas en cualquier controlador
 * y las convierte en respuestas HTTP estructuradas y consistentes.
 * Sin esta clase, Spring devolvería respuestas genéricas poco informativas.</p>
 *
 * <p><b>Patrón aplicado:</b> Cross-Cutting Concern — centraliza el manejo
 * de errores en un único lugar, evitando try-catch en cada controlador.</p>
 *
 * @author Municipalidad Valle del Sol
 * @version 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja errores de validación de campos ({@code @Valid}).
     *
     * <p>Se activa cuando un DTO de entrada no pasa las validaciones de
     * Jakarta Bean Validation (campos vacíos, formato inválido, etc.).</p>
     *
     * @param ex Excepción con el detalle de qué campos fallaron.
     * @return HTTP 400 con el mensaje de cada campo inválido.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
            "error", mensaje,
            "timestamp", LocalDateTime.now().toString()
        ));
    }

    /**
     * Maneja argumentos ilegales, como tipos de brigada no reconocidos.
     *
     * <p>Se activa cuando {@link cl.municipalidad.msbrigadas.factory.BrigadeFactory}
     * recibe un tipo inválido o el servicio recibe datos que violan reglas de negocio.</p>
     *
     * @param ex Excepción con el mensaje de error.
     * @return HTTP 400 con el mensaje descriptivo del error.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
            "error", ex.getMessage(),
            "timestamp", LocalDateTime.now().toString()
        ));
    }

    /**
     * Maneja recursos no encontrados (brigadas que no existen).
     *
     * <p>Se activa cuando el servicio lanza {@link RuntimeException} al no
     * encontrar una brigada por su ID.</p>
     *
     * @param ex Excepción con el mensaje de error.
     * @return HTTP 404 con el mensaje descriptivo del error.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
            "error", ex.getMessage(),
            "timestamp", LocalDateTime.now().toString()
        ));
    }

    /**
     * Maneja cualquier error inesperado del servidor.
     *
     * <p>Captura excepciones no previstas para evitar exponer stack traces
     * al cliente. Siempre devuelve un mensaje genérico.</p>
     *
     * @param ex Excepción inesperada.
     * @return HTTP 500 con mensaje genérico sin detalles internos.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
            "error", "Error interno del servidor. Intente más tarde.",
            "timestamp", LocalDateTime.now().toString()
        ));
    }
}