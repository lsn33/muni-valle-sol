package cl.municipalidad.msbrigadas.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Entidad JPA que representa una brigada de emergencia del sistema municipal.
 *
 * <p>Mapeada a la tabla {@code brigada} en PostgreSQL. Almacena los datos
 * operativos de cada brigada: nombre, estado, ubicación GPS y responsable.</p>
 *
 * <p><b>Nota:</b> Esta entidad no se expone directamente al frontend.
 * Se convierte a {@link cl.municipalidad.msbrigadas.dto.BrigadeDTO} antes
 * de cualquier respuesta HTTP.</p>
 *
 * @author Municipalidad Valle del Sol
 * @version 1.0
 * @see cl.municipalidad.msbrigadas.dto.BrigadeDTO
 */
@Data
@Entity
@Table(name = "brigada")
public class Brigade {

    /**
     * Identificador único generado automáticamente por la base de datos (BIGSERIAL).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre de la brigada. Máximo 100 caracteres, obligatorio.
     * Ejemplo: "Brigada Norte", "Unidad 7".
     */
    @Column(nullable = false, length = 100)
    private String nombre;

    /**
     * Estado operativo actual de la brigada.
     * Valores posibles: {@code DISPONIBLE}, {@code EN_CAMINO}, {@code OCUPADA}, {@code INACTIVA}.
     * Por defecto {@code DISPONIBLE} al crear la brigada.
     */
    @Column(nullable = false, length = 50)
    private String estado = "DISPONIBLE";

    /**
     * Tipo de brigada según su especialidad.
     * Valores posibles: {@code INCENDIO}, {@code RESCATE}, {@code MEDICA}.
     */
    @Column(nullable = false, length = 50)
    private String tipo;

    /**
     * Latitud de la ubicación GPS actual de la brigada.
     * Rango válido: -90.0 a 90.0.
     */
    @Column
    private Double latitud;

    /**
     * Longitud de la ubicación GPS actual de la brigada.
     * Rango válido: -180.0 a 180.0.
     */
    @Column
    private Double longitud;

    /**
     * Email del responsable o jefe de la brigada.
     * Permite identificar al encargado del equipo.
     */
    @Column(name = "email_responsable", nullable = false, length = 150)
    private String emailResponsable;

    /**
     * Fecha y hora en que se registró la brigada en el sistema.
     * Se asigna automáticamente al instanciar la entidad.
     */
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}