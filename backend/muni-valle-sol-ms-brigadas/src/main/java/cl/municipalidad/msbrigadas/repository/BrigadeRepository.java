package cl.municipalidad.msbrigadas.repository;

import cl.municipalidad.msbrigadas.model.Brigade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para operaciones de persistencia de {@link Brigade}.
 *
 * <p>Extiende {@link JpaRepository} para heredar automáticamente las
 * operaciones CRUD básicas (save, findById, findAll, deleteById, etc.)
 * sin necesidad de escribir SQL.</p>
 *
 * <p><b>Patrón aplicado:</b> Repository Pattern — abstrae completamente
 * el acceso a datos. El servicio nunca escribe SQL ni sabe que existe
 * PostgreSQL; solo llama métodos de este repositorio.</p>
 *
 * @author Municipalidad Valle del Sol
 * @version 1.0
 * @see cl.municipalidad.msbrigadas.service.BrigadeService
 */
@Repository
public interface BrigadeRepository extends JpaRepository<Brigade, Long> {

    /**
     * Busca todas las brigadas que tengan un estado específico.
     *
     * <p>Spring Data JPA genera el SQL automáticamente a partir del nombre
     * del método: {@code SELECT * FROM brigada WHERE estado = ?}</p>
     *
     * @param estado Estado operativo a filtrar (ej: "DISPONIBLE", "EN_CAMINO").
     * @return Lista de brigadas con el estado indicado. Vacía si no hay ninguna.
     */
    List<Brigade> findByEstado(String estado);

    /**
     * Busca todas las brigadas de un tipo específico.
     *
     * <p>Genera: {@code SELECT * FROM brigada WHERE tipo = ?}</p>
     *
     * @param tipo Tipo de brigada a filtrar (ej: "INCENDIO", "RESCATE", "MEDICA").
     * @return Lista de brigadas del tipo indicado. Vacía si no hay ninguna.
     */
    List<Brigade> findByTipo(String tipo);
}