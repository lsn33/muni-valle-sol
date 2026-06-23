package cl.municipalidad.msreport.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para actualizar el título de un reporte existente.
 *
 * <p>Implementa el patrón <b>Record</b> de Java para inmutabilidad.
 * Reemplaza el uso de {@code Map<String, String>} en el controlador,
 * aportando tipado explícito y validación automática mediante {@code @Valid}.</p>
 *
 * <p>Patrones aplicados:</p>
 * <ul>
 *   <li>Single Responsibility: DTO exclusivo para actualización de título</li>
 *   <li>Value Object: inmutable, solo transporta el nuevo título validado</li>
 *   <li>Consistent Design: mismo patrón que {@link UpdateStatusRequest}</li>
 * </ul>
 *
 * @param titulo Nuevo título del reporte. Obligatorio, máximo 150 caracteres.
 *
 * @author Beltran
 * @version 1.0
 * @since 1.0
 */
public record UpdateTitleRequest(

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 150, message = "El título no puede superar los 150 caracteres")
    String titulo

) {}