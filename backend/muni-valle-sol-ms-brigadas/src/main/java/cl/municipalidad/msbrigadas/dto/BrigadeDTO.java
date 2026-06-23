package cl.municipalidad.msbrigadas.dto;

import java.time.LocalDateTime;

/**
 * DTO de salida que representa los datos públicos de una brigada.
 *
 * <p>Implementa el patrón <b>Record</b> de Java para garantizar inmutabilidad.
 * Nunca expone datos sensibles. Es el único objeto que el controlador
 * devuelve al cliente, nunca la entidad {@link cl.municipalidad.msbrigadas.model.Brigade}.</p>
 *
 * <p><b>Patrón aplicado:</b> Data Transfer Object (DTO) con Java Record.</p>
 *
 * @param id               Identificador único de la brigada.
 * @param nombre           Nombre de la brigada.
 * @param estado           Estado operativo actual.
 * @param tipo             Tipo de especialidad de la brigada.
 * @param latitud          Latitud GPS actual (puede ser null).
 * @param longitud         Longitud GPS actual (puede ser null).
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