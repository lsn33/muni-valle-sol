package cl.municipalidad.bff.dto;

import java.time.LocalDateTime;

/**
 * DTO que representa un reporte de incendio con ubicacion anidada,
 * tal como es expuesto por el BFF hacia el frontend.
 *
 * @author Beltran
 * @version 1.0
 * @since 1.0
 */
public record ReportDTO(
    /** Identificador unico del reporte. */
    Long id,
    /** Titulo descriptivo del reporte. */
    String titulo,
    /** Descripcion detallada del incidente. */
    String descripcion,
    /** Tipo de incidente reportado. */
    String tipo,
    /** Estado actual del reporte (ej: ACTIVO, RESUELTO). */
    String estado,
    /** Email del usuario que genero el reporte. */
    String emailUsuario,
    /** Ubicacion geografica del incidente. */
    LocationDTO ubicacion,
    /** Fecha y hora de creacion del reporte. */
    LocalDateTime fechaCreacion
) {}
