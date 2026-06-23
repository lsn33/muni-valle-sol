package cl.municipalidad.bff.dto;

/**
 * Record que encapsula los datos necesarios para crear una alerta manual.
 * Utiliza Java Records para inmutabilidad y código más limpio.
 *
 * <p>Patrones aplicados:</p>
 * <ul>
 *   <li>Data Transfer Object (DTO): transporta datos del request al handler</li>
 *   <li>Record: aprovecha inmutabilidad y deconstrucción automática</li>
 * </ul>
 *
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * CreateAlertRequest request = new CreateAlertRequest(
 *     "Incendio Forestal",
 *     "Se detectó incendio en zona norte",
 *     "ALTA",
 *     -33.4569,
 *     -70.6483
 * );
 * }</pre>
 *
 * @param titulo      titulo descriptivo de la alerta (obligatorio)
 * @param descripcion descripcion detallada de la situacion (obligatorio)
 * @param severidad   nivel de severidad: ALTA, MEDIA o BAJA (obligatorio)
 * @param latitud     latitud geografica del incidente (opcional)
 * @param longitud    longitud geografica del incidente (opcional)
 *
 * @author Beltran
 * @version 1.1
 * @since 1.0
 */
public record CreateAlertRequest(
    String titulo,
    String descripcion,
    String severidad,
    Double latitud,
    Double longitud
) {}