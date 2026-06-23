package cl.municipalidad.bff.dto;

import java.time.LocalDateTime;

/**
 * DTO que representa la respuesta cruda del MS-Alertas, en inglés.
 * Campos en inglés porque refleja directamente el contrato del microservicio.
 * El BFF lo recibe y lo transforma al AlertDTO en español para el frontend.
 *
 * @param id          Identificador único de la alerta en MongoDB.
 * @param title       Título de la alerta.
 * @param description Descripción detallada.
 * @param severity    Nivel de severidad: HIGH, MEDIUM o LOW.
 * @param status      Estado de la alerta: ACTIVE o RESOLVED.
 * @param date        Fecha de creación.
 * @param reportId    Id del reporte asociado. Puede ser null.
 * @param userId      Id del usuario relacionado. Puede ser null.
 * @param latitude    Latitud del incidente. Puede ser null.
 * @param longitude   Longitud del incidente. Puede ser null.
 *
 * @author Beltran
 * @version 1.1
 * @since 1.0
 */
public record AlertMsResponseDTO(
    String id,
    String title,
    String description,
    String severity,
    String status,
    LocalDateTime date,
    Long reportId,
    Long userId,
    Double latitude,
    Double longitude
) {}