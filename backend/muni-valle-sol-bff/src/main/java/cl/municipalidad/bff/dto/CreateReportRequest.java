package cl.municipalidad.bff.dto;

/**
 * Record que encapsula todos los datos necesarios para crear un nuevo reporte.
 * Utiliza Java Records para inmutabilidad y validación en el handler.
 *
 * <p>Patrones aplicados:</p>
 * <ul>
 *   <li>Data Transfer Object (DTO): transporta datos del request al handler</li>
 *   <li>Record: aprovechar para deconstrucción y pattern matching</li>
 * </ul>
 *
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * CreateReportRequest request = new CreateReportRequest(
 *     "Incendio en Cerro",
 *     "Fuego activo en ladera norte",
 *     "INCENDIO",
 *     "juan@example.com",
 *     -33.8688,
 *     -71.5203
 * );
 * }</pre>
 *
 * @param titulo titulo descriptivo del reporte
 * @param descripcion descripcion detallada del incidente
 * @param tipo tipo de incidente: INCENDIO, HUMO, SOSPECHOSO
 * @param emailUsuario email del usuario que genera el reporte
 * @param latitud coordenada geográfica de latitud
 * @param longitud coordenada geográfica de longitud
 *
 * @author Beltran
 * @version 1.0
 * @since 1.0
 */
public record CreateReportRequest(
    String titulo,
    String descripcion,
    String tipo,
    String emailUsuario,
    Double latitud,
    Double longitud
) {}