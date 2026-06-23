package cl.municipalidad.msbrigadas.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de entrada para actualizar la ubicación GPS de una brigada.
 *
 * <p>Implementa el patrón <b>Record</b> de Java para garantizar inmutabilidad.
 * Valida que las coordenadas estén dentro de rangos geográficamente válidos.</p>
 *
 * <p><b>Patrón aplicado:</b> Data Transfer Object (DTO) con Java Record + Bean Validation.</p>
 *
 * @param latitud  Nueva latitud GPS. Rango válido: -90.0 a 90.0.
 * @param longitud Nueva longitud GPS. Rango válido: -180.0 a 180.0.
 *
 * @author Municipalidad Valle del Sol
 * @version 1.0
 */
public record UpdateLocationRequest(

    @NotNull(message = "La latitud es obligatoria")
    @DecimalMin(value = "-90.0", message = "La latitud debe ser mayor o igual a -90.0")
    @DecimalMax(value = "90.0", message = "La latitud debe ser menor o igual a 90.0")
    Double latitud,

    @NotNull(message = "La longitud es obligatoria")
    @DecimalMin(value = "-180.0", message = "La longitud debe ser mayor o igual a -180.0")
    @DecimalMax(value = "180.0", message = "La longitud debe ser menor o igual a 180.0")
    Double longitud

) {}