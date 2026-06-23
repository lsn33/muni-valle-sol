package cl.municipalidad.bff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Clase principal del BFF (Backend For Frontend) de Municipalidad Valle del Sol.
 * Punto de entrada de la aplicacion Spring Boot.
 *
 * <p>{@code @EnableScheduling} habilita la tarea programada de
 * {@link cl.municipalidad.bff.service.JwtService} que refresca periódicamente
 * la clave pública RSA obtenida desde el JWKS de MS-Usuarios.</p>
 *
 * @author Beltran
 * @version 1.1
 * @since 1.0
 */
@SpringBootApplication
@EnableScheduling
public class BffApplication {

    /**
     * Inicia la aplicacion Spring Boot.
     *
     * @param args argumentos de linea de comandos
     */
    public static void main(String[] args) {
        SpringApplication.run(BffApplication.class, args);
    }
}