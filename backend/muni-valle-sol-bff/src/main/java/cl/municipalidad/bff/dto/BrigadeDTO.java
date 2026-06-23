package cl.municipalidad.bff.dto;

import java.time.LocalDateTime;

/**
 * DTO espejo de la respuesta de MS-Brigadas, usado por el BFF para
 * deserializar y reenviar los datos de brigadas al frontend.
 *
 * @param id               Identificador único de la brigada.
 * @param nombre           Nombre de la brigada.
 * @param estado           Estado operativo actual (DISPONIBLE, EN_CAMINO, OCUPADA, INACTIVA).
 * @param tipo             Tipo de especialidad (INCENDIO, RESCATE, MEDICA).
 * @param latitud          Latitud GPS actual.
 * @param longitud         Longitud GPS actual.
 * @param emailResponsable Email del jefe de brigada.
 * @param fechaCreacion    Fecha de registro en el sistema.
 *
 * @author Municipalidad Valle del Sol
 * @version 1.0
 */
public record BrigadeDTO(
    Long id,
    String nombre,
    String estado,
    String tipo,
    Double latitud,
    Double longitud,
    String emailResponsable,
    LocalDateTime fechaCreacion
) {}