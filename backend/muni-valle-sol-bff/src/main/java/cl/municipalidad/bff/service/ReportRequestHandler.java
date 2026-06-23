package cl.municipalidad.bff.service;

import cl.municipalidad.bff.dto.CreateReportRequest;
import cl.municipalidad.bff.dto.ReportDTO;
import cl.municipalidad.bff.dto.UpdateStatusRequest;
import cl.municipalidad.bff.dto.UpdateTitleRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Handler que procesa y transforma las solicitudes de reportes.
 * Convierte records en mapas de datos y delega al servicio de reportes.
 * Centraliza la lógica de transformación de DTOs a formatos internos.
 *
 * <p>Patrones aplicados:</p>
 * <ul>
 *   <li>Handler Pattern: procesa requests y los transforma</li>
 *   <li>Adapter Pattern: adapta records a Map para el servicio</li>
 *   <li>Single Responsibility: solo transforma datos de reportes</li>
 * </ul>
 *
 * <p>Transformaciones manejadas:</p>
 * <pre>{@code
 * CreateReportRequest (record)
 *     ↓
 * Map<String, Object> (formato interno)
 *     ↓
 * ReportService.create()
 * }</pre>
 *
 * @author Beltran
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class ReportRequestHandler {

    private final ReportService reportService;

    /**
     * Procesa la creación de un reporte transformando el record a Map.
     * Recibe un record con campos simples y los agrupa en un Map
     * que se envía al cliente HTTP del MS-Reportes.
     *
     * <p>Transformación de ejemplo:</p>
     * <pre>{@code
     * CreateReportRequest:
     *   titulo: "Incendio en Cerro"
     *   latitud: -33.8688
     *   longitud: -71.5203
     *
     * Se transforma a Map:
     *   {
     *     "titulo": "Incendio en Cerro",
     *     "latitud": -33.8688,
     *     "longitud": -71.5203,
     *     ...
     *   }
     * }</pre>
     *
     * @param request record con datos del reporte
     * @return ReportDTO con el reporte creado
     */
    public ReportDTO handleCreate(CreateReportRequest request) {
        Map<String, Object> body = Map.of(
            "titulo", request.titulo(),
            "descripcion", request.descripcion(),
            "tipo", request.tipo(),
            "emailUsuario", request.emailUsuario(),
            "latitud", request.latitud(),
            "longitud", request.longitud()
        );
        return reportService.create(body);
    }

    /**
     * Procesa la actualización del estado de un reporte.
     * Extrae el estado del record y lo pasa al servicio.
     *
     * @param id identificador del reporte
     * @param request record con el nuevo estado
     * @return ReportDTO con el reporte actualizado
     */
    public ReportDTO handleUpdateStatus(Long id, UpdateStatusRequest request) {
        return reportService.updateStatus(id, request.estado());
    }

    /**
     * Procesa la actualización del título de un reporte.
     * Extrae el título del record y lo pasa al servicio.
     *
     * @param id identificador del reporte
     * @param request record con el nuevo título
     * @return ReportDTO con el reporte actualizado
     */
    public ReportDTO handleUpdateTitle(Long id, UpdateTitleRequest request) {
        return reportService.updateTitle(id, request.titulo());
    }
}