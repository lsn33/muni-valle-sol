package cl.municipalidad.bff.mapper;

import cl.municipalidad.bff.dto.LocationDTO;
import cl.municipalidad.bff.dto.ReportDTO;
import cl.municipalidad.bff.dto.ReportMsDTO;
import org.springframework.stereotype.Component;

/**
 * Mapper para transformar ReportMsDTO a ReportDTO.
 * Encapsula la logica de conversion entre el modelo del MS-Reportes
 * y el modelo esperado por el frontend.
 *
 * <p>Patrones aplicados:</p>
 * <ul>
 *   <li>Single Responsibility: solo convierte entre DTOs</li>
 *   <li>Singleton: bean de Spring con instancia unica</li>
 * </ul>
 *
 * @author Beltran
 * @version 1.0
 * @since 1.0
 */
@Component
public class ReportMapper {

    /**
     * Convierte un ReportMsDTO del MS-Reportes a un ReportDTO para el frontend.
     * Transforma las coordenadas separadas (latitud, longitud) en un objeto LocationDTO.
     *
     * @param ms ReportMsDTO recibido del MS-Reportes
     * @return ReportDTO listo para enviar al frontend
     */
    public ReportDTO toDTO(ReportMsDTO ms) {
        LocationDTO location = new LocationDTO(ms.latitud(), ms.longitud());
        return new ReportDTO(
                ms.id(),
                ms.titulo(),
                ms.descripcion(),
                ms.tipo(),
                ms.estado(),
                ms.emailUsuario(),
                location,
                ms.fechaCreacion()
        );
    }
}
