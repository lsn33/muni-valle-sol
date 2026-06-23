package cl.municipalidad.msreport.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Entidad JPA que representa un reporte de emergencia en el sistema municipal.
 *
 * <p>Mapeada a la tabla {@code reporte} en PostgreSQL. Cada reporte tiene
 * un tipo (INCENDIO, HUMO, SOSPECHOSO) que determina su estado inicial
 * y prioridad, asignados por {@link cl.municipalidad.msreport.factory.ReportFactory}
 * mediante el patrón Factory Method.</p>
 *
 * <p>Patrones aplicados:</p>
 * <ul>
 *   <li>Entity Pattern: clase mapeada a tabla relacional con identidad propia</li>
 *   <li>Rich Domain Model: campos con valores por defecto encapsulan reglas simples</li>
 *   <li>Anti-corruption Layer: nunca se expone directamente; se convierte a {@link cl.municipalidad.msreport.dto.ReportDTO}</li>
 * </ul>
 *
 * <p>Flujo de vida de la entidad:</p>
 * <pre>{@code
 * CreateReportRequest
 *   ↓ ReportFactory.crear()
 * Report (entidad configurada)
 *   ↓ ReportRepository.save()
 * PostgreSQL (tabla reporte)
 *   ↓ ReportService.toDTO()
 * ReportDTO (respuesta HTTP)
 * }</pre>
 *
 * @author Beltran
 * @version 1.0
 * @since 1.0
 * @see cl.municipalidad.msreport.factory.ReportFactory
 * @see cl.municipalidad.msreport.dto.ReportDTO
 */
@Data
@Entity
@Table(name = "reporte")
public class Report {

    /**
     * Identificador único generado automáticamente por la base de datos (BIGSERIAL).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Título descriptivo del reporte. Máximo 150 caracteres, obligatorio.
     */
    @Column(nullable = false, length = 150)
    private String titulo;

    /**
     * Descripción detallada de la emergencia.
     * El factory agrega automáticamente la etiqueta de prioridad
     * (ej: {@code [PRIORIDAD ALTA]}) al final del texto.
     */
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    /**
     * Coordenada de latitud de la ubicación del reporte.
     * Usada por el frontend con Leaflet para mostrar el marcador en el mapa.
     */
    @Column(nullable = false)
    private Double latitud;

    /**
     * Coordenada de longitud de la ubicación del reporte.
     * Usada por el frontend con Leaflet para mostrar el marcador en el mapa.
     */
    @Column(nullable = false)
    private Double longitud;

    /**
     * Tipo de emergencia reportada.
     * Valores válidos: {@code INCENDIO}, {@code HUMO}, {@code SOSPECHOSO}.
     * Siempre se almacena en mayúsculas.
     */
    @Column(nullable = false, length = 50)
    private String tipo;

    /**
     * Estado actual del reporte.
     * Valores posibles: {@code ACTIVO}, {@code EN_REVISION}, {@code PENDIENTE}, {@code CERRADO}.
     * El estado inicial es asignado por {@link cl.municipalidad.msreport.factory.ReportFactory}
     * según el tipo de reporte.
     */
    @Column(nullable = false, length = 50)
    private String estado = "ACTIVO";

    /**
     * Correo electrónico del usuario que creó el reporte.
     * Permite relacionar el reporte con el usuario sin necesidad de un JOIN
     * entre microservicios.
     */
    @Column(name = "email_usuario", nullable = false, length = 150)
    private String emailUsuario;

    /**
     * Fecha y hora en que se creó el reporte.
     * Se asigna automáticamente al instanciar la entidad.
     */
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}