package cl.municipalidad.msreport.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import cl.municipalidad.msreport.dto.CreateReportRequest;
import cl.municipalidad.msreport.dto.ReportDTO;
import cl.municipalidad.msreport.dto.UpdateTitleRequest;
import cl.municipalidad.msreport.factory.ReportFactory;
import cl.municipalidad.msreport.model.Report;
import cl.municipalidad.msreport.repository.ReportRepository;

import java.util.List;

/**
 * Servicio de lógica de negocio para la gestión de reportes de emergencia.
 *
 * <p>Actúa como capa intermedia entre el controlador y el repositorio,
 * delegando la creación de entidades al {@link ReportFactory} y la
 * conversión a DTO al método privado {@code toDTO()}.</p>
 *
 * <p>Patrones aplicados:</p>
 * <ul>
 *   <li>Service Layer: centraliza la lógica de negocio entre controller y repository</li>
 *   <li>Delegation Pattern: delega creación al factory y persistencia al repository</li>
 *   <li>Single Responsibility: no construye entidades ni maneja HTTP</li>
 * </ul>
 *
 * <p>Flujo de responsabilidades:</p>
 * <pre>{@code
 * ReportController
 *   ↓ delega con record completo
 * ReportService (esta clase)
 *   ↓ crea entidad         ↓ persiste
 * ReportFactory      ReportRepository
 *   ↓ retorna DTO convertido
 * ReportController → HTTP Response
 * }</pre>
 *
 * @author Beltran
 * @version 1.0
 * @since 1.0
 * @see ReportFactory
 * @see ReportRepository
 * @see ReportDTO
 */
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reporteRepository;
    private final ReportFactory reporteFactory;

    /**
     * Crea un nuevo reporte de emergencia.
     *
     * <p>Delega la construcción de la entidad al {@link ReportFactory},
     * pasando el record completo sin desempaquetarlo. El factory
     * asigna el estado inicial y la prioridad según el tipo de reporte.
     * Luego persiste el reporte y retorna su representación como DTO.</p>
     *
     * @param request Record validado con todos los datos del nuevo reporte.
     * @return {@link ReportDTO} con los datos del reporte creado.
     */
    public ReportDTO crear(CreateReportRequest request) {
        Report reporte = reporteFactory.crear(request);
        return toDTO(reporteRepository.save(reporte));
    }

    /**
     * Retorna todos los reportes con estado {@code ACTIVO}.
     *
     * @return Lista de {@link ReportDTO} con reportes activos. Vacía si no hay ninguno.
     */
    public List<ReportDTO> listarActivos() {
        return reporteRepository.findByEstado("ACTIVO")
                .stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Retorna todos los reportes sin filtro de estado.
     *
     * @return Lista completa de {@link ReportDTO}.
     */
    public List<ReportDTO> listarTodos() {
        return reporteRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Busca un reporte por su identificador único.
     *
     * @param id Identificador del reporte.
     * @return {@link ReportDTO} con los datos del reporte encontrado.
     * @throws RuntimeException si no existe un reporte con el id dado.
     */
    public ReportDTO buscarPorId(Long id) {
        return reporteRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado con id: " + id));
    }

    /**
     * Actualiza el estado de un reporte existente.
     *
     * @param id          Identificador del reporte a actualizar.
     * @param nuevoEstado Nuevo estado a asignar (ACTIVO, EN_REVISION, PENDIENTE, CERRADO).
     * @return {@link ReportDTO} con el estado actualizado.
     * @throws RuntimeException si no existe un reporte con el id dado.
     */
    public ReportDTO actualizarEstado(Long id, String nuevoEstado) {
        Report reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado con id: " + id));
        reporte.setEstado(nuevoEstado);
        return toDTO(reporteRepository.save(reporte));
    }

    /**
     * Actualiza el título de un reporte existente.
     *
     * <p>Recibe el record {@link UpdateTitleRequest} completo en lugar
     * de un String suelto, manteniendo consistencia con el patrón
     * de delegación por records del proyecto.</p>
     *
     * @param id      Identificador del reporte a actualizar.
     * @param request Record con el nuevo título validado.
     * @return {@link ReportDTO} con el título actualizado.
     * @throws RuntimeException si no existe un reporte con el id dado.
     */
    public ReportDTO actualizarTitulo(Long id, UpdateTitleRequest request) {
        Report reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado con id: " + id));
        reporte.setTitulo(request.titulo());
        return toDTO(reporteRepository.save(reporte));
    }

    /**
     * Elimina un reporte por su identificador único.
     *
     * @param id Identificador del reporte a eliminar.
     * @throws RuntimeException si no existe un reporte con el id dado.
     */
    public void eliminar(Long id) {
        if (!reporteRepository.existsById(id)) {
            throw new RuntimeException("Reporte no encontrado con id: " + id);
        }
        reporteRepository.deleteById(id);
    }

    /**
     * Convierte una entidad {@link Report} a su representación pública {@link ReportDTO}.
     *
     * @param reporte Entidad a convertir.
     * @return {@link ReportDTO} con los datos del reporte.
     */
    private ReportDTO toDTO(Report reporte) {
        return new ReportDTO(
                reporte.getId(),
                reporte.getTitulo(),
                reporte.getDescripcion(),
                reporte.getLatitud(),
                reporte.getLongitud(),
                reporte.getTipo(),
                reporte.getEstado(),
                reporte.getEmailUsuario(),
                reporte.getFechaCreacion()
        );
    }
}