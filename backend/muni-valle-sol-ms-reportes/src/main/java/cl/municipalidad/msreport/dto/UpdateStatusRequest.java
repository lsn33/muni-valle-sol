package cl.municipalidad.msreport.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO de entrada para actualizar el estado de un reporte existente.
 *
 * <p>Implementa el patrón <b>Record</b> de Java para inmutabilidad.
 * Separa la responsabilidad del {@link CreateReportRequest}, siguiendo
 * el principio de responsabilidad única: cada DTO representa
 * una operación específica.</p>
 *
 * <p>Patrones aplicados:</p>
 * <ul>
 *   <li>Single Responsibility: DTO exclusivo para actualización de estado</li>
 *   <li>Value Object: inmutable, solo transporta el nuevo estado validado</li>
 *   <li>Fail Fast: Bean Validation rechaza estados inválidos en la capa HTTP</li>
 * </ul>
 *
 * <p><b>Estados válidos:</b></p>
 * <ul>
 *   <li>{@code ACTIVO} — reporte activo y visible en el mapa</li>
 *   <li>{@code EN_REVISION} — siendo evaluado por un funcionario</li>
 *   <li>{@code PENDIENTE} — pendiente de confirmación</li>
 *   <li>{@code CERRADO} — reporte resuelto y cerrado</li>
 * </ul>
 *
 * @param estado Nuevo estado del reporte. Obligatorio, debe ser un valor válido.
 *
 * @author Beltran
 * @version 1.0
 * @since 1.0
 */
public record UpdateStatusRequest(

    @NotBlank(message = "El estado es obligatorio")
    @Pattern(
        regexp = "ACTIVO|EN_REVISION|PENDIENTE|CERRADO",
        message = "El estado debe ser ACTIVO, EN_REVISION, PENDIENTE o CERRADO"
    )
    String estado

) {}