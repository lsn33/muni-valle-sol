package cl.municipalidad.msreport.factory;

/**
 * Enumeración de los tipos de reporte de emergencia soportados por el sistema.
 *
 * <p>Usado por {@link ReportFactory} para determinar el estado inicial
 * y la prioridad de cada reporte mediante el patrón Factory Method.
 * Actúa como fuente de verdad para los valores válidos de tipo de reporte.</p>
 *
 * <p>Patrones aplicados:</p>
 * <ul>
 *   <li>Type Safety: enum garantiza valores válidos en tiempo de compilación</li>
 *   <li>Strategy implícita: cada valor implica un comportamiento distinto en el factory</li>
 * </ul>
 *
 * <p>Mapa de prioridades:</p>
 * <pre>{@code
 * INCENDIO   → Prioridad ALTA   | estado inicial: ACTIVO
 * HUMO       → Prioridad MEDIA  | estado inicial: EN_REVISION
 * SOSPECHOSO → Prioridad BAJA   | estado inicial: PENDIENTE
 * }</pre>
 *
 * @author Beltran
 * @version 1.0
 * @since 1.0
 * @see ReportFactory
 */
public enum ReportType {

    /** Incendio confirmado o en curso. Máxima prioridad. */
    INCENDIO,

    /** Presencia de humo sin llamas visibles. Prioridad media. */
    HUMO,

    /** Actividad sospechosa que podría derivar en emergencia. Prioridad baja. */
    SOSPECHOSO
}