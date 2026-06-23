package cl.municipalidad.bff.service;

import cl.municipalidad.bff.client.AlertClient;
import cl.municipalidad.bff.dto.AlertDTO;
import cl.municipalidad.bff.dto.AlertMsResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de alertas del BFF.
 * Delega al MS-Alertas real via AlertClient y mapea la respuesta al AlertDTO del BFF.
 *
 * <p>Patrones aplicados:</p>
 * <ul>
 *   <li>Facade Pattern: expone interfaz simplificada al controller</li>
 *   <li>Single Responsibility: solo gestiona logica de alertas</li>
 * </ul>
 *
 * @author Beltran
 * @version 2.1
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertClient alertClient;

    /**
     * Lista todas las alertas activas del MS-Alertas.
     * Mapea los campos en inglés del ms a los campos en español del BFF.
     *
     * @return lista de AlertDTO con las alertas activas
     */
    public List<AlertDTO> listAlerts() {
        return alertClient.listActive()
                .stream()
                .map(this::toAlertDTO)
                .toList();
    }

    /**
     * Crea una nueva alerta en el MS-Alertas.
     * Convierte la severidad del español (ALTA/MEDIA/BAJA) al inglés del ms (HIGH/MEDIUM/LOW).
     *
     * @param title       titulo de la alerta
     * @param description descripcion de la alerta
     * @param severity    severidad en español: ALTA, MEDIA o BAJA
     * @param latitude    latitud del incidente, puede ser null
     * @param longitude   longitud del incidente, puede ser null
     * @return AlertDTO con la alerta creada
     */
    public AlertDTO create(String title, String description, String severity, Double latitude, Double longitude) {
        AlertMsResponseDTO created = alertClient.create(title, description, toEnglishSeverity(severity), latitude, longitude);
        return toAlertDTO(created);
    }

    /**
     * Mapea un AlertMsResponseDTO al AlertDTO del BFF.
     * Convierte severity del inglés al español y date a fecha del DTO.
     *
     * @param ms respuesta del MS-Alertas
     * @return AlertDTO con campos en español para el frontend
     */
    private AlertDTO toAlertDTO(AlertMsResponseDTO ms) {
        return new AlertDTO(
                ms.id(),
                ms.title(),
                ms.description(),
                toSpanishSeverity(ms.severity()),
                ms.date(),
                ms.latitude(),
                ms.longitude()
        );
    }

    /**
     * Convierte severidad del inglés (MS-Alertas) al español (BFF/Frontend).
     * HIGH → ALTA, MEDIUM → MEDIA, LOW → BAJA.
     *
     * @param severity severidad en inglés
     * @return severidad en español
     */
    private String toSpanishSeverity(String severity) {
        return switch (severity) {
            case "HIGH"   -> "ALTA";
            case "MEDIUM" -> "MEDIA";
            case "LOW"    -> "BAJA";
            default       -> severity;
        };
    }

    /**
     * Convierte severidad del español (Frontend) al inglés (MS-Alertas).
     * ALTA → HIGH, MEDIA → MEDIUM, BAJA → LOW.
     *
     * @param severity severidad en español
     * @return severidad en inglés
     */
    private String toEnglishSeverity(String severity) {
        return switch (severity) {
            case "ALTA"  -> "HIGH";
            case "MEDIA" -> "MEDIUM";
            case "BAJA"  -> "LOW";
            default      -> severity;
        };
    }
}