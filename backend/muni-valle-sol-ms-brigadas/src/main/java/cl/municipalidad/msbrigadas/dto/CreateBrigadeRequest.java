package cl.municipalidad.msbrigadas.dto;

import jakarta.validation.constraints.*;

/**
 * DTO de entrada para el registro de una nueva brigada.
 *
 * <p>Implementa el patrón <b>Record</b> de Java para garantizar inmutabilidad.
 * Las anotaciones de Jakarta Bean Validation se ejecutan automáticamente
 * al usar {@code @Valid} en el controlador, rechazando datos inválidos
 * antes de llegar a la capa de servicio.</p>
 *
 * <p><b>Patrón aplicado:</b> Data Transfer Object (DTO) con Java Record + Bean Validation.</p>
 *
 * @param nombre           Nombre de la brigada. Entre 2 y 100 caracteres.
 * @param tipo             Tipo de brigada. Solo acepta: INCENDIO, RESCATE, MEDICA.
 * @param emailResponsable Email válido del jefe de brigada. Máximo 150 caracteres.
 * @param latitud          Latitud GPS inicial (opcional). Rango: -90.0 a 90.0.
 * @param longitud         Longitud GPS inicial (opcional). Rango: -180.0 a 180.0.
 *
 * @author Municipalidad Valle del Sol
 * @version 1.0
 */
public record CreateBrigadeRequest(

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    String nombre,

    @NotBlank(message = "El tipo es obligatorio")
    @Pattern(
        regexp = "INCENDIO|RESCATE|MEDICA",
        message = "El tipo debe ser INCENDIO, RESCATE o MEDICA"
    )
    String tipo,

    @NotBlank(message = "El email del responsable es obligatorio")
    @Email(message = "El email del responsable no tiene un formato válido")
    @Size(max = 150, message = "El email no puede superar los 150 caracteres")
    String emailResponsable,

    @DecimalMin(value = "-90.0", message = "La latitud debe ser mayor o igual a -90.0")
    @DecimalMax(value = "90.0", message = "La latitud debe ser menor o igual a 90.0")
    Double latitud,

    @DecimalMin(value = "-180.0", message = "La longitud debe ser mayor o igual a -180.0")
    @DecimalMax(value = "180.0", message = "La longitud debe ser menor o igual a 180.0")
    Double longitud

) {}