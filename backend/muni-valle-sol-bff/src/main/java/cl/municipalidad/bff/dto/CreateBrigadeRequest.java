package cl.municipalidad.bff.dto;

import jakarta.validation.constraints.*;

/**
 * DTO de entrada del BFF para crear una brigada. Replica las validaciones
 * de MS-Brigadas para rechazar datos inválidos antes de llamar al microservicio.
 *
 * @param nombre           Nombre de la brigada. Entre 2 y 100 caracteres.
 * @param tipo             Tipo de brigada: INCENDIO, RESCATE o MEDICA.
 * @param emailResponsable Email del jefe de brigada.
 * @param latitud          Latitud GPS inicial (opcional).
 * @param longitud         Longitud GPS inicial (opcional).
 *
 * @author Municipalidad Valle del Sol
 * @version 1.0
 */
public record CreateBrigadeRequest(

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    String nombre,

    @NotBlank(message = "El tipo es obligatorio")
    @Pattern(regexp = "INCENDIO|RESCATE|MEDICA", message = "El tipo debe ser INCENDIO, RESCATE o MEDICA")
    String tipo,

    @NotBlank(message = "El email del responsable es obligatorio")
    @Email(message = "El email del responsable no tiene un formato válido")
    String emailResponsable,

    @DecimalMin(value = "-90.0", message = "La latitud debe ser mayor o igual a -90.0")
    @DecimalMax(value = "90.0", message = "La latitud debe ser menor o igual a 90.0")
    Double latitud,

    @DecimalMin(value = "-180.0", message = "La longitud debe ser mayor o igual a -180.0")
    @DecimalMax(value = "180.0", message = "La longitud debe ser menor o igual a 180.0")
    Double longitud

) {}