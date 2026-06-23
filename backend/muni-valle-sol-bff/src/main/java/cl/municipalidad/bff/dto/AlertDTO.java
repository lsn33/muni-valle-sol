package cl.municipalidad.bff.dto;

import java.time.LocalDateTime;

/**
 * DTO que representa una alerta activa derivada de un reporte de incendio.
 *
 * @param id          Identificador unico de la alerta.
 * @param titulo      Titulo descriptivo de la alerta.
 * @param descripcion Descripcion detallada de la situacion.
 * @param severidad   Nivel de severidad (ej: BAJA, MEDIA, ALTA, CRITICA).
 * @param fecha       Fecha y hora de creacion de la alerta.
 * @param latitud     Latitud geografica del incidente. Puede ser null.
 * @param longitud    Longitud geografica del incidente. Puede ser null.
 *
 * @author Beltran
 * @version 1.1
 * @since 1.0
 */
public record AlertDTO(
    String id,
    String titulo,
    String descripcion,
    String severidad,
    LocalDateTime fecha,
    Double latitud,
    Double longitud
) {}