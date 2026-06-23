package cl.municipalidad.bff.dto;

import java.time.LocalDateTime;

/**
 * DTO que representa la estructura de un reporte tal como lo retorna
 * el MS-Reportes, con coordenadas planas en lugar de un objeto anidado.
 *
 * @author Beltran
 * @version 1.0
 * @since 1.0
 */
public record ReportMsDTO(
    /** Identificador unico del reporte. */
    Long id,
    /** Titulo descriptivo del reporte. */
    String titulo,
    /** Descripcion detallada del incidente. */
    String descripcion,
    /** Latitud geografica del incidente. */
    Double latitud,
    /** Longitud geografica del incidente. */
    Double longitud,
    /** Tipo de incidente reportado. */
    String tipo,
    /** Estado actual del reporte (ej: ACTIVO, RESUELTO). */
    String estado,
    /** Email del usuario que genero el reporte. */
    String emailUsuario,
    /** Fecha y hora de creacion del reporte. */
    LocalDateTime fechaCreacion
) {}
