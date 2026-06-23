package cl.municipalidad.bff.dto;

/**
 * Record que encapsula el nuevo estado para actualizar un reporte.
 * Utiliza Java Records para simplificar la transferencia de un único parámetro.
 *
 * <p>Patrones aplicados:</p>
 * <ul>
 *   <li>Data Transfer Object (DTO): encapsula el estado a actualizar</li>
 *   <li>Record: ideal para transferencias de un único campo</li>
 * </ul>
 *
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * UpdateStatusRequest request = new UpdateStatusRequest("RESUELTO");
 * }</pre>
 *
 * @param estado nuevo estado del reporte (ej: ACTIVO, RESUELTO, CERRADO)
 *
 * @author Beltran
 * @version 1.0
 * @since 1.0
 */
public record UpdateStatusRequest(
    String estado
) {}