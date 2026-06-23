package cl.municipalidad.bff.dto;

/**
 * DTO que representa los datos publicos de un usuario del sistema.
 *
 * @author Beltran
 * @version 1.0
 * @since 1.0
 */
public record UserDTO(
    /** Identificador unico del usuario. */
    Long id,
    /** Nombre completo del usuario. */
    String nombre,
    /** Email del usuario. */
    String email,
    /** Rol del usuario en el sistema. */
    String rol,
    /** Indica si la cuenta del usuario se encuentra activa. */
    Boolean activo
) {}
