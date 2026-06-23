package cl.municipalidad.msreport.factory;

import org.springframework.stereotype.Component;

import cl.municipalidad.msreport.dto.CreateReportRequest;
import cl.municipalidad.msreport.model.Report;

/**
 * Factory de creación de reportes de emergencia.
 *
 * <p>Implementa el patrón <b>Factory Method</b>: centraliza la lógica de
 * creación de objetos {@link Report}, asignando automáticamente el estado
 * inicial y la etiqueta de prioridad según el tipo de reporte, sin que
 * el servicio ni el controlador conozcan estas reglas.</p>
 *
 * <p>Patrones aplicados:</p>
 * <ul>
 *   <li>Factory Method: encapsula la lógica de creación según el tipo</li>
 *   <li>Single Responsibility: única clase responsable de construir entidades Report</li>
 *   <li>Open/Closed: agregar un tipo nuevo solo requiere un nuevo case en el switch</li>
 * </ul>
 *
 * <p>Reglas de negocio encapsuladas:</p>
 * <pre>{@code
 * INCENDIO   → estado ACTIVO      + [PRIORIDAD ALTA]
 * HUMO       → estado EN_REVISION + [PRIORIDAD MEDIA]
 * SOSPECHOSO → estado PENDIENTE   + [PRIORIDAD BAJA]
 * }</pre>
 *
 * @author Beltran
 * @version 1.0
 * @since 1.0
 * @see ReportType
 * @see Report
 */
@Component
public class ReportFactory {

    /**
     * Crea y configura un nuevo reporte de emergencia según su tipo.
     *
     * <p>Recibe el record {@link CreateReportRequest} completo y aplica
     * las reglas de negocio correspondientes al tipo de reporte.
     * El tipo se normaliza a mayúsculas para evitar errores por
     * capitalización inconsistente.</p>
     *
     * @param request Record con los datos validados del nuevo reporte.
     * @return {@link Report} configurado con estado y prioridad según el tipo.
     * @throws IllegalArgumentException si el tipo no corresponde a ningún valor de {@link ReportType}.
     */
    public Report crear(CreateReportRequest request) {

        Report reporte = new Report();
        reporte.setTitulo(request.titulo());
        reporte.setLatitud(request.latitud());
        reporte.setLongitud(request.longitud());
        reporte.setEmailUsuario(request.emailUsuario());
        reporte.setTipo(request.tipo().toUpperCase());

        switch (ReportType.valueOf(request.tipo().toUpperCase())) {
            case INCENDIO -> {
                reporte.setEstado("ACTIVO");
                reporte.setDescripcion(request.descripcion() + " [PRIORIDAD ALTA]");
            }
            case HUMO -> {
                reporte.setEstado("EN_REVISION");
                reporte.setDescripcion(request.descripcion() + " [PRIORIDAD MEDIA]");
            }
            case SOSPECHOSO -> {
                reporte.setEstado("PENDIENTE");
                reporte.setDescripcion(request.descripcion() + " [PRIORIDAD BAJA]");
            }
        }

        return reporte;
    }
}