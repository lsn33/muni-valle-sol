package cl.municipalidad.bff.dto;

/**
 * DTO para la solicitud de autenticacion.
 *
 * @author Beltran
 * @version 1.0
 * @since 1.0
 */
public record LoginRequestDTO(
    /** Email del usuario. */
    String email,
    /** Contrasena del usuario. */
    String password
) {}
