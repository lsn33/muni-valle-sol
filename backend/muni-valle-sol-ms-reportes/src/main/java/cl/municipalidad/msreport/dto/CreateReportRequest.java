package cl.municipalidad.msreport.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para la creación de un nuevo reporte de emergencia.
 *
 * <p>Implementa el patrón <b>Record</b> de Java para garantizar inmutabilidad.
 * Incluye validaciones de Jakarta Bean Validation que se ejecutan automáticamente
 * al usar {@code @Valid} en el controlador, antes de que el dato llegue a la
 * capa de servicio o factory.</p>
 *
 * <p>Patrones aplicados:</p>
 * <ul>
 *   <li>Data Transfer Object (DTO): encapsula datos de entrada sin exponer la entidad</li>
 *   <li>Value Object: inmutable por diseño gracias al Record de Java</li>
 *   <li>Fail Fast: Bean Validation rechaza datos inválidos antes del service</li>
 * </ul>
 *
 * <p>Flujo de validación:</p>
 * <pre>{@code
 * HTTP Request (JSON)
 *   ↓ @Valid en ReportController
 * CreateReportRequest (Bean Validation)
 *   ↓ si pasa validación
 * ReportService → ReportFactory
 * }</pre>
 *
 * @param titulo       Título descriptivo del reporte. Obligatorio, máximo 150 caracteres.
 * @param descripcion  Descripción detallada de la emergencia. Máximo 1000 caracteres.
 * @param latitud      Coordenada de latitud. Debe estar entre -90 y 90.
 * @param longitud     Coordenada de longitud. Debe estar entre -180 y 180.
 * @param tipo         Tipo de reporte. Solo acepta: INCENDIO, HUMO, SOSPECHOSO.
 * @param emailUsuario Correo del usuario que crea el reporte.
 *
 * @author Beltran
 * @version 1.0
 * @since 1.0
 */
public record CreateReportRequest(

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 150, message = "El título no puede superar los 150 caracteres")
    String titulo,

    @Size(max = 1000, message = "La descripción no puede superar los 1000 caracteres")
    String descripcion,

    @NotNull(message = "La latitud es obligatoria")
    @DecimalMin(value = "-90.0", message = "La latitud debe ser mayor o igual a -90")
    @DecimalMax(value = "90.0", message = "La latitud debe ser menor o igual a 90")
    Double latitud,

    @NotNull(message = "La longitud es obligatoria")
    @DecimalMin(value = "-180.0", message = "La longitud debe ser mayor o igual a -180")
    @DecimalMax(value = "180.0", message = "La longitud debe ser menor o igual a 180")
    Double longitud,

    @NotBlank(message = "El tipo de reporte es obligatorio")
    @Pattern(
        regexp = "INCENDIO|HUMO|SOSPECHOSO",
        message = "El tipo debe ser INCENDIO, HUMO o SOSPECHOSO"
    )
    String tipo,

    @NotBlank(message = "El email del usuario es obligatorio")
    @jakarta.validation.constraints.Email(message = "El email no tiene un formato válido")
    String emailUsuario

) {}