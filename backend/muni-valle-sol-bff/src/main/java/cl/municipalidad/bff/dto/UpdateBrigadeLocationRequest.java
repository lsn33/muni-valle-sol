package cl.municipalidad.bff.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de entrada del BFF para actualizar la ubicación GPS de una brigada.
 *
 * @param latitud  Nueva latitud GPS.
 * @param longitud Nueva longitud GPS.
 *
 * @author Municipalidad Valle del Sol
 * @version 1.0
 */
public record UpdateBrigadeLocationRequest(

    @NotNull(message = "La latitud es obligatoria")
    @DecimalMin(value = "-90.0", message = "La latitud debe ser mayor o igual a -90.0")
    @DecimalMax(value = "90.0", message = "La latitud debe ser menor o igual a 90.0")
    Double latitud,

    @NotNull(message = "La longitud es obligatoria")
    @DecimalMin(value = "-180.0", message = "La longitud debe ser mayor o igual a -180.0")
    @DecimalMax(value = "180.0", message = "La longitud debe ser menor o igual a 180.0")
    Double longitud

) {}