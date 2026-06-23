package cl.municipalidad.msbrigadas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO de entrada para actualizar el estado operativo de una brigada.
 *
 * <p>Implementa el patrón <b>Record</b> de Java para garantizar inmutabilidad.
 * Valida que el estado sea uno de los valores permitidos antes de llegar
 * al servicio.</p>
 *
 * <p><b>Patrón aplicado:</b> Data Transfer Object (DTO) con Java Record + Bean Validation.</p>
 *
 * @param estado Nuevo estado operativo de la brigada.
 *               Solo acepta: DISPONIBLE, EN_CAMINO, OCUPADA, INACTIVA.
 *
 * @author Municipalidad Valle del Sol
 * @version 1.0
 */
public record UpdateStatusRequest(

    @NotBlank(message = "El estado es obligatorio")
    @Pattern(
        regexp = "DISPONIBLE|EN_CAMINO|OCUPADA|INACTIVA",
        message = "El estado debe ser DISPONIBLE, EN_CAMINO, OCUPADA o INACTIVA"
    )
    String estado

) {}