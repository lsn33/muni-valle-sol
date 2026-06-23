package cl.municipalidad.bff.service;

import cl.municipalidad.bff.client.ReportClient;
import cl.municipalidad.bff.dto.ReportDTO;
import cl.municipalidad.bff.mapper.ReportMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportClient reportClient;
    private final ReportMapper reportMapper;

    @CircuitBreaker(name = "ms-reportes", fallbackMethod = "listAllFallback")
    public List<ReportDTO> listAll() {
        return reportClient.listAll()
                .stream()
                .map(reportMapper::toDTO)
                .toList();
    }

    @CircuitBreaker(name = "ms-reportes", fallbackMethod = "listActiveFallback")
    public List<ReportDTO> listActive() {
        return reportClient.listActive()
                .stream()
                .map(reportMapper::toDTO)
                .toList();
    }

    @CircuitBreaker(name = "ms-reportes", fallbackMethod = "findByIdFallback")
    public ReportDTO findById(Long id) {
        return reportMapper.toDTO(reportClient.findById(id));
    }

    @CircuitBreaker(name = "ms-reportes", fallbackMethod = "createFallback")
    public ReportDTO create(Map<String, Object> body) {
        return reportMapper.toDTO(reportClient.create(body));
    }

    @CircuitBreaker(name = "ms-reportes", fallbackMethod = "updateStatusFallback")
    public ReportDTO updateStatus(Long id, String status) {
        return reportMapper.toDTO(reportClient.updateStatus(id, status));
    }

    @CircuitBreaker(name = "ms-reportes", fallbackMethod = "updateTitleFallback")
    public ReportDTO updateTitle(Long id, String title) {
        return reportMapper.toDTO(reportClient.updateTitle(id, title));
    }

    @CircuitBreaker(name = "ms-reportes", fallbackMethod = "deleteFallback")
    public void delete(Long id) {
        reportClient.delete(id);
    }

    // ─── Fallbacks ────────────────────────────────────────────────────────────

    public List<ReportDTO> listAllFallback(Throwable ex) {
        log.warn("[CircuitBreaker] ms-reportes abierto – listAll: {}", ex.getMessage());
        return Collections.emptyList();
    }

    public List<ReportDTO> listActiveFallback(Throwable ex) {
        log.warn("[CircuitBreaker] ms-reportes abierto – listActive: {}", ex.getMessage());
        return Collections.emptyList();
    }

    public ReportDTO findByIdFallback(Long id, Throwable ex) {
        log.warn("[CircuitBreaker] ms-reportes abierto – findById: {}", ex.getMessage());
        throw new RuntimeException("Servicio de reportes no disponible temporalmente");
    }

    public ReportDTO createFallback(Map<String, Object> body, Throwable ex) {
        log.warn("[CircuitBreaker] ms-reportes abierto – create: {}", ex.getMessage());
        throw new RuntimeException("Servicio de reportes no disponible temporalmente");
    }

    public ReportDTO updateStatusFallback(Long id, String status, Throwable ex) {
        log.warn("[CircuitBreaker] ms-reportes abierto – updateStatus: {}", ex.getMessage());
        throw new RuntimeException("Servicio de reportes no disponible temporalmente");
    }

    public ReportDTO updateTitleFallback(Long id, String title, Throwable ex) {
        log.warn("[CircuitBreaker] ms-reportes abierto – updateTitle: {}", ex.getMessage());
        throw new RuntimeException("Servicio de reportes no disponible temporalmente");
    }

    public void deleteFallback(Long id, Throwable ex) {
        log.warn("[CircuitBreaker] ms-reportes abierto – delete: {}", ex.getMessage());
        throw new RuntimeException("Servicio de reportes no disponible temporalmente");
    }
}