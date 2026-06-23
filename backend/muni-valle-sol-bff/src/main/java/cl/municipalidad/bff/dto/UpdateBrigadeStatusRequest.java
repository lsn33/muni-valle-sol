package cl.municipalidad.bff.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO de entrada del BFF para actualizar el estado de una brigada.
 *
 * @param estado Nuevo estado: DISPONIBLE, EN_CAMINO, OCUPADA o INACTIVA.
 *
 * @author Municipalidad Valle del Sol
 * @version 1.0
 */
public record UpdateBrigadeStatusRequest(

    @NotBlank(message = "El estado es obligatorio")
    @Pattern(regexp = "DISPONIBLE|EN_CAMINO|OCUPADA|INACTIVA",
             message = "El estado debe ser DISPONIBLE, EN_CAMINO, OCUPADA o INACTIVA")
    String estado

) {}