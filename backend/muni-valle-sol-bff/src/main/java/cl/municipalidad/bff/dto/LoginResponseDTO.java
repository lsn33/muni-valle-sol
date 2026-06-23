package cl.municipalidad.bff.dto;

/**
 * DTO con los datos del usuario retornados tras una autenticacion exitosa.
 *
 * @author Beltran
 * @version 1.0
 * @since 1.0
 */
public record LoginResponseDTO(
    /** Identificador unico del usuario. */
    Long id,
    /** Nombre completo del usuario. */
    String nombre,
    /** Email del usuario. */
    String email,
    /** Rol del usuario en el sistema. */
    String rol
) {}
