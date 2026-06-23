package cl.municipalidad.msreport.service;

import cl.municipalidad.msreport.dto.CreateReportRequest;
import cl.municipalidad.msreport.dto.ReportDTO;
import cl.municipalidad.msreport.dto.UpdateTitleRequest;
import cl.municipalidad.msreport.factory.ReportFactory;
import cl.municipalidad.msreport.model.Report;
import cl.municipalidad.msreport.repository.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para {@link ReportService}.
 *
 * <p>Verifica la lógica de negocio del servicio de forma aislada,
 * usando Mockito para simular {@link ReportRepository} y {@link ReportFactory}.
 * No levanta contexto de Spring ni accede a base de datos.</p>
 *
 * <p>Patrones aplicados:</p>
 * <ul>
 *   <li>Mock Object: dependencias simuladas con Mockito para aislar el servicio</li>
 *   <li>Arrange-Act-Assert: estructura clara en cada test</li>
 *   <li>Verify Interactions: valida que el servicio delega correctamente al factory y repositorio</li>
 * </ul>
 *
 * <p>Cobertura de escenarios:</p>
 * <pre>{@code
 * crear()           → happy path + delega a factory y repository      ✓
 * listarActivos()   → retorna solo ACTIVOS / lista vacía              ✓
 * listarTodos()     → retorna todos sin filtro                        ✓
 * buscarPorId()     → id existente / id inexistente (excepción)       ✓
 * actualizarEstado()→ happy path / id inexistente (excepción)         ✓
 * actualizarTitulo()→ happy path / id inexistente (excepción)         ✓
 * }</pre>
 *
 * @author Beltran
 * @version 1.0
 * @since 1.0
 * @see ReportService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ReportService - Pruebas unitarias")
class ReportServiceTest {

    @Mock
    private ReportRepository reporteRepository;

    @Mock
    private ReportFactory reporteFactory;

    @InjectMocks
    private ReportService reportService;

    private Report reporteMock;

    /**
     * Configura el reporte mock base reutilizado en múltiples tests.
     */
    @BeforeEach
    void setUp() {
        reporteMock = new Report();
        reporteMock.setId(1L);
        reporteMock.setTitulo("Incendio Av. Principal");
        reporteMock.setDescripcion("Humo visible [PRIORIDAD ALTA]");
        reporteMock.setLatitud(-33.45);
        reporteMock.setLongitud(-70.65);
        reporteMock.setTipo("INCENDIO");
        reporteMock.setEstado("ACTIVO");
        reporteMock.setEmailUsuario("vecino@test.cl");
        reporteMock.setFechaCreacion(LocalDateTime.now());
    }

    /**
     * Verifica que crear() delega al factory con el record completo y retorna el DTO correcto.
     */
    @Test
    @DisplayName("crear: debe delegar al factory con el record completo y retornar DTO")
    void crear_exitoso_retornaDTO() {
        CreateReportRequest request = new CreateReportRequest(
                "Incendio Av. Principal", "Humo visible",
                -33.45, -70.65, "INCENDIO", "vecino@test.cl");

        when(reporteFactory.crear(any(CreateReportRequest.class))).thenReturn(reporteMock);
        when(reporteRepository.save(reporteMock)).thenReturn(reporteMock);

        ReportDTO resultado = reportService.crear(request);

        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.titulo()).isEqualTo("Incendio Av. Principal");
        assertThat(resultado.tipo()).isEqualTo("INCENDIO");
        assertThat(resultado.estado()).isEqualTo("ACTIVO");

        verify(reporteFactory).crear(any(CreateReportRequest.class));
        verify(reporteRepository).save(reporteMock);
    }

    /**
     * Verifica que listarActivos() filtra correctamente y retorna solo reportes ACTIVO.
     */
    @Test
    @DisplayName("listarActivos: debe retornar solo reportes con estado ACTIVO")
    void listarActivos_retornaSoloActivos() {
        Report activo1 = crearReporte(1L, "ACTIVO");
        Report activo2 = crearReporte(2L, "ACTIVO");
        when(reporteRepository.findByEstado("ACTIVO")).thenReturn(List.of(activo1, activo2));

        List<ReportDTO> resultado = reportService.listarActivos();

        assertThat(resultado).hasSize(2);
        assertThat(resultado).allMatch(r -> "ACTIVO".equals(r.estado()));
        verify(reporteRepository).findByEstado("ACTIVO");
    }

    /**
     * Verifica que listarActivos() retorna lista vacía cuando no hay reportes activos.
     */
    @Test
    @DisplayName("listarActivos: debe retornar lista vacía cuando no hay activos")
    void listarActivos_sinActivos_retornaVacia() {
        when(reporteRepository.findByEstado("ACTIVO")).thenReturn(List.of());

        List<ReportDTO> resultado = reportService.listarActivos();

        assertThat(resultado).isEmpty();
    }

    /**
     * Verifica que listarTodos() retorna todos los reportes sin filtro de estado.
     */
    @Test
    @DisplayName("listarTodos: debe retornar todos los reportes sin filtro")
    void listarTodos_retornaTodos() {
        when(reporteRepository.findAll()).thenReturn(
                List.of(crearReporte(1L, "ACTIVO"), crearReporte(2L, "PENDIENTE")));

        List<ReportDTO> resultado = reportService.listarTodos();

        assertThat(resultado).hasSize(2);
        verify(reporteRepository).findAll();
    }

    /**
     * Verifica que buscarPorId() retorna el DTO correcto cuando el id existe.
     */
    @Test
    @DisplayName("buscarPorId: debe retornar DTO cuando el id existe")
    void buscarPorId_idExistente_retornaDTO() {
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporteMock));

        ReportDTO resultado = reportService.buscarPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(1L);
        verify(reporteRepository).findById(1L);
    }

    /**
     * Verifica que buscarPorId() lanza RuntimeException cuando el id no existe.
     */
    @Test
    @DisplayName("buscarPorId: debe lanzar excepción cuando el id no existe")
    void buscarPorId_idInexistente_lanzaExcepcion() {
        when(reporteRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reportService.buscarPorId(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("999");
    }

    /**
     * Verifica que actualizarEstado() cambia el estado y retorna el DTO actualizado.
     */
    @Test
    @DisplayName("actualizarEstado: debe cambiar estado y retornar DTO actualizado")
    void actualizarEstado_exitoso_retornaDTOActualizado() {
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporteMock));
        when(reporteRepository.save(reporteMock)).thenReturn(reporteMock);

        ReportDTO resultado = reportService.actualizarEstado(1L, "CERRADO");

        assertThat(resultado.estado()).isEqualTo("CERRADO");
        verify(reporteRepository).save(reporteMock);
    }

    /**
     * Verifica que actualizarEstado() lanza excepción y no persiste cuando el id no existe.
     */
    @Test
    @DisplayName("actualizarEstado: debe lanzar excepción cuando el reporte no existe")
    void actualizarEstado_idInexistente_lanzaExcepcion() {
        when(reporteRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reportService.actualizarEstado(404L, "CERRADO"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("404");

        verify(reporteRepository, never()).save(any());
    }

    /**
     * Verifica que actualizarTitulo() extrae el título del record y actualiza la entidad.
     */
    @Test
    @DisplayName("actualizarTitulo: debe cambiar título desde record y retornar DTO actualizado")
    void actualizarTitulo_exitoso_retornaDTOActualizado() {
        UpdateTitleRequest request = new UpdateTitleRequest("Nuevo Título Incendio");
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporteMock));
        when(reporteRepository.save(reporteMock)).thenReturn(reporteMock);

        ReportDTO resultado = reportService.actualizarTitulo(1L, request);

        assertThat(resultado.titulo()).isEqualTo("Nuevo Título Incendio");
        verify(reporteRepository).save(reporteMock);
    }

    /**
     * Verifica que actualizarTitulo() lanza excepción y no persiste cuando el id no existe.
     */
    @Test
    @DisplayName("actualizarTitulo: debe lanzar excepción cuando el reporte no existe")
    void actualizarTitulo_idInexistente_lanzaExcepcion() {
        UpdateTitleRequest request = new UpdateTitleRequest("Título Inválido");
        when(reporteRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reportService.actualizarTitulo(404L, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("404");

        verify(reporteRepository, never()).save(any());
    }

    /**
     * Método auxiliar que construye un {@link Report} con id y estado configurables.
     * Usado para reducir duplicación en los tests de listado.
     *
     * @param id     Identificador del reporte mock.
     * @param estado Estado a asignar al reporte mock.
     * @return {@link Report} configurado con los valores dados.
     */
    private Report crearReporte(Long id, String estado) {
        Report r = new Report();
        r.setId(id);
        r.setTitulo("Reporte " + id);
        r.setDescripcion("Desc");
        r.setLatitud(-33.0);
        r.setLongitud(-70.0);
        r.setTipo("INCENDIO");
        r.setEstado(estado);
        r.setEmailUsuario("user@test.cl");
        r.setFechaCreacion(LocalDateTime.now());
        return r;
    }
}