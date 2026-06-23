package cl.municipalidad.bff.dto;

/**
 * DTO que representa una ubicacion geografica mediante coordenadas.
 *
 * @author Beltran
 * @version 1.0
 * @since 1.0
 */
public record LocationDTO(
    /** Latitud geografica. */
    Double lat,
    /** Longitud geografica. */
    Double lng
) {}
