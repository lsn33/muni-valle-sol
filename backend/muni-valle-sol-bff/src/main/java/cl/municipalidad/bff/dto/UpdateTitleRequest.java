package cl.municipalidad.bff.dto;

/**
 * Record que encapsula el nuevo título para actualizar un reporte.
 * Utiliza Java Records para simplificar la transferencia de un único parámetro.
 *
 * <p>Patrones aplicados:</p>
 * <ul>
 *   <li>Data Transfer Object (DTO): encapsula el título a actualizar</li>
 *   <li>Record: ideal para transferencias de un único campo</li>
 * </ul>
 *
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * UpdateTitleRequest request = new UpdateTitleRequest("Incendio Zona Crítica");
 * }</pre>
 *
 * @param titulo nuevo título descriptivo del reporte
 *
 * @author Beltran
 * @version 1.0
 * @since 1.0
 */
public record UpdateTitleRequest(
    String titulo
) {}