package cl.municipalidad.bff.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manejador global de excepciones del BFF.
 * Intercepta excepciones y retorna respuestas HTTP con formato estandarizado.
 *
 * <p>Patrones aplicados:</p>
 * <ul>
 *   <li>Chain of Responsibility: intercepta excepciones en cascada</li>
 *   <li>Single Responsibility: centraliza el manejo de errores</li>
 * </ul>
 *
 * @author Beltran
 * @version 1.2
 * @since 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja errores de validacion de campos ({@code @Valid}).
     *
     * <p>Se activa cuando un DTO de entrada (CreateBrigadaRequest, CreateReportRequest,
     * etc.) no pasa las validaciones de Jakarta Bean Validation. Sin este handler,
     * Spring devuelve su formato por defecto con el stack trace completo expuesto.</p>
     *
     * @param ex Excepcion con el detalle de que campos fallaron.
     * @return HTTP 400 con un mensaje limpio por cada campo invalido.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            Map.of(
                "error", mensaje,
                "timestamp", LocalDateTime.now().toString(),
                "status", 400
            )
        );
    }

    /**
     * Maneja errores de validacion manual lanzados con IllegalArgumentException.
     *
     * <p>Se activa, por ejemplo, en AlertRequestHandler cuando un campo obligatorio
     * (titulo, severidad) falta o tiene un valor invalido. Debe declararse ANTES
     * del handler de RuntimeException, ya que IllegalArgumentException es una
     * subclase de RuntimeException y Spring prioriza el tipo mas especifico, pero
     * declarar el orden explicito evita ambiguedad y deja la intencion clara.</p>
     *
     * @param ex Excepcion con el mensaje descriptivo del campo invalido.
     * @return HTTP 400 con el mensaje de validacion.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            Map.of(
                "error", ex.getMessage(),
                "timestamp", LocalDateTime.now().toString(),
                "status", 400
            )
        );
    }

    /**
     * Maneja excepciones de microservicios con status HTTP especifico.
     * Debe declararse ANTES del handler de RuntimeException porque MsException
     * extiende RuntimeException; Spring resuelve por tipo mas especifico primero,
     * pero declarar el mas especifico primero evita ambiguedad.
     *
     * @param ex MsException con mensaje y status HTTP del microservicio
     * @return ResponseEntity con el status y detalle del error del microservicio
     */
    @ExceptionHandler(MsException.class)
    public ResponseEntity<Map<String, Object>> handleMsException(MsException ex) {
        return ResponseEntity.status(ex.getStatus()).body(
            Map.of(
                "error", ex.getMessage(),
                "timestamp", LocalDateTime.now().toString(),
                "status", ex.getStatus().value()
            )
        );
    }

    /**
     * Maneja excepciones genericas de runtime no controladas.
     *
     * @param ex excepcion capturada
     * @return ResponseEntity con status 500 y detalle del error
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            Map.of(
                "error", ex.getMessage(),
                "timestamp", LocalDateTime.now().toString(),
                "status", 500
            )
        );
    }
}