package cl.municipalidad.msreport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.municipalidad.msreport.model.Report;

import java.util.List;

/**
 * Repositorio de acceso a datos para la entidad {@link Report}.
 *
 * <p>Implementa el patrón <b>Repository</b>: define una interfaz de acceso
 * a datos sin exponer detalles de implementación (SQL, JDBC, etc.).
 * Spring Data JPA genera automáticamente la implementación en tiempo de
 * ejecución a partir de los nombres de los métodos.</p>
 *
 * <p>Patrones aplicados:</p>
 * <ul>
 *   <li>Repository Pattern: abstrae la capa de persistencia del servicio</li>
 *   <li>Query by Method Name: Spring Data deriva el SQL del nombre del método</li>
 *   <li>Dependency Inversion: el service depende de la interfaz, no de la implementación</li>
 * </ul>
 *
 * <p>Operaciones heredadas de {@link JpaRepository}:</p>
 * <pre>{@code
 * save()        → INSERT / UPDATE
 * findById()    → SELECT WHERE id = ?
 * findAll()     → SELECT *
 * deleteById()  → DELETE WHERE id = ?
 * existsById()  → SELECT COUNT WHERE id = ?
 * }</pre>
 *
 * @author Beltran
 * @version 1.0
 * @since 1.0
 * @see Report
 */
@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    /**
     * Busca todos los reportes con un estado específico.
     * Usado principalmente para listar reportes activos en el mapa.
     *
     * @param estado Estado a filtrar (ej: "ACTIVO", "CERRADO").
     * @return Lista de reportes con el estado dado. Vacía si no hay resultados.
     */
    List<Report> findByEstado(String estado);

    /**
     * Busca todos los reportes creados por un usuario específico.
     *
     * @param emailUsuario Correo del usuario cuyos reportes se quieren obtener.
     * @return Lista de reportes del usuario. Vacía si no tiene reportes.
     */
    List<Report> findByEmailUsuario(String emailUsuario);

    /**
     * Busca todos los reportes de un tipo específico.
     *
     * @param tipo Tipo de reporte a filtrar (ej: "INCENDIO", "HUMO").
     * @return Lista de reportes del tipo dado. Vacía si no hay resultados.
     */
    List<Report> findByTipo(String tipo);
}