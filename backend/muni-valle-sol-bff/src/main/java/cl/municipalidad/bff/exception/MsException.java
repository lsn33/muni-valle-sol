package cl.municipalidad.bff.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Excepcion personalizada para errores provenientes de los microservicios.
 * Encapsula el mensaje de error y el status HTTP correspondiente.
 *
 * Patrones aplicados:
 * 
 *   Exception Shielding: oculta detalles internos del microservicio

 * 
 *
 * @author Beltran
 * @version 1.0
 * @since 1.0
 */
@Getter
public class MsException extends RuntimeException {

    private final HttpStatus status;

    /**
     * Construye una nueva excepcion de microservicio.
     *
     * @param message mensaje descriptivo del error
     * @param status  status HTTP a retornar al cliente
     */
    public MsException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
