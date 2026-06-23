package cl.municipalidad.msbrigadas.factory;

/**
 * Enumeración de tipos de brigada disponibles en el sistema municipal.
 *
 * <p>Define las especialidades de brigada reconocidas por el sistema.
 * Usado por {@link BrigadeFactory} para aplicar el patrón Factory Method
 * y asignar automáticamente el estado inicial según el tipo.</p>
 *
 * <p><b>Patrón aplicado:</b> Factory Method — BrigadaFactory usa este enum
 * para determinar el comportamiento de creación de cada tipo de brigada.</p>
 *
 * <ul>
 *   <li>{@code INCENDIO} — Brigada especializada en combate de incendios.</li>
 *   <li>{@code RESCATE} — Brigada de rescate y extracción de personas.</li>
 *   <li>{@code MEDICA}  — Brigada de atención médica de emergencia.</li>
 * </ul>
 *
 * @author Municipalidad Valle del Sol
 * @version 1.0
 * @see BrigadeFactory
 */
public enum BrigadeType {
    INCENDIO,
    RESCATE,
    MEDICA
}