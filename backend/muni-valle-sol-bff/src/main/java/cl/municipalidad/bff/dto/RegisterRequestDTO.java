package cl.municipalidad.bff.dto;

/**
 * DTO para la solicitud de registro de un nuevo usuario.
 *
 * @author Beltran
 * @version 1.0
 * @since 1.0
 */
public record RegisterRequestDTO(
    /** Nombre completo del usuario. */
    String nombre,
    /** Email del usuario. */
    String email,
    /** Contrasena del usuario. */
    String password,
    /** Rol asignado al usuario (ej: ADMIN, USER). */
    String rol
) {}
