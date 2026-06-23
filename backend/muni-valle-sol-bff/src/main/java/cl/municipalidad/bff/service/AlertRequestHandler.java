package cl.municipalidad.bff.service;

import cl.municipalidad.bff.dto.AlertDTO;
import cl.municipalidad.bff.dto.CreateAlertRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Handler de requests de alertas del BFF.
 * Valida y transforma el record CreateAlertRequest antes de delegar al servicio.
 *
 * <p>Patrones aplicados:</p>
 * <ul>
 *   <li>Handler Pattern: centraliza validacion y transformacion del request</li>
 *   <li>Single Responsibility: solo valida y transforma, no ejecuta logica de negocio</li>
 * </ul>
 *
 * @author Beltran
 * @version 1.0
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class AlertRequestHandler {

    private final AlertService alertService;

    /**
     * Valida el CreateAlertRequest y delega la creacion al servicio.
     * Lanza IllegalArgumentException si los campos obligatorios son nulos o vacios.
     *
     * @param request record con titulo, descripcion y severidad
     * @return AlertDTO con la alerta creada
     * @throws IllegalArgumentException si algun campo obligatorio es invalido
     */
    public AlertDTO handleCreate(CreateAlertRequest request) {
        if (request.titulo() == null || request.titulo().isBlank()) {
            throw new IllegalArgumentException("El titulo es obligatorio");
        }
        if (request.descripcion() == null || request.descripcion().isBlank()) {
            throw new IllegalArgumentException("La descripcion es obligatoria");
        }
        if (request.severidad() == null || request.severidad().isBlank()) {
            throw new IllegalArgumentException("La severidad es obligatoria");
        }
        return alertService.create(
                request.titulo(),
                request.descripcion(),
                request.severidad(),
                request.latitud(),
                request.longitud()
        );
    }
}