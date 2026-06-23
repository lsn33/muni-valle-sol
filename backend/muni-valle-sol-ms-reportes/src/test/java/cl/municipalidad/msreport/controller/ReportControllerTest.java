package cl.municipalidad.msreport.controller;

import cl.municipalidad.msreport.dto.CreateReportRequest;
import cl.municipalidad.msreport.dto.ReportDTO;
import cl.municipalidad.msreport.dto.UpdateTitleRequest;
import cl.municipalidad.msreport.service.ReportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas unitarias de capa web para {@link ReportController}.
 *
 * <p>Verifica el comportamiento HTTP del controlador usando MockMvc con
 * contexto completo de Spring. El {@link ReportService} se reemplaza con
 * un mock de Mockito para aislar la capa de presentación.</p>
 *
 * <p>Patrones aplicados:</p>
 * <ul>
 *   <li>Mock MVC: simula requests HTTP sin levantar servidor real</li>
 *   <li>Test Slice implícito: {@code @SpringBootTest} + {@code @MockitoBean} aísla el service</li>
 *   <li>Arrange-Act-Assert: estructura clara con MockMvc fluent API</li>
 * </ul>
 *
 * <p>Cobertura de endpoints:</p>
 * <pre>{@code
 * POST   /api/reportes         → 201 / 400 (título vacío)    ✓
 * GET    /api/reportes         → 200                          ✓
 * GET    /api/reportes/activos → 200                          ✓
 * GET    /api/reportes/{id}    → 200                          ✓
 * PUT    /api/reportes/{id}/estado → 200                      ✓
 * PUT    /api/reportes/{id}    → 200 / 400 (título vacío)     ✓
 * }</pre>
 *
 * @author Beltran
 * @version 1.0
 * @since 1.0
 * @see ReportController
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.flyway.enabled=false"
})
@DisplayName("ReportController - Pruebas unitarias (capa web)")
class ReportControllerTest {

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private ReportService reporteService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Construye una instancia de MockMvc con el contexto completo de la aplicación.
     *
     * @return {@link MockMvc} configurado para simular requests HTTP.
     */
    private MockMvc mockMvc() {
        return MockMvcBuilders.webAppContextSetup(context).build();
    }

    /**
     * Construye un {@link ReportDTO} mock base reutilizable en los tests.
     *
     * @return DTO de ejemplo con datos de un reporte de incendio activo.
     */
    private ReportDTO reporteDTOMock() {
        return new ReportDTO(1L, "Incendio Av. Principal",
                "Humo visible [PRIORIDAD ALTA]", -33.45, -70.65,
                "INCENDIO", "ACTIVO", "vecino@test.cl", LocalDateTime.now());
    }

    /**
     * Verifica que POST /api/reportes retorna 201 con el DTO del reporte creado.
     */
    @Test
    @DisplayName("crear: debe retornar 201 al crear un reporte correctamente")
    void crear_exitoso_retorna201() throws Exception {
        when(reporteService.crear(any(CreateReportRequest.class)))
                .thenReturn(reporteDTOMock());

        Map<String, Object> body = Map.of(
                "titulo", "Incendio Av. Principal",
                "descripcion", "Humo visible",
                "latitud", -33.45,
                "longitud", -70.65,
                "tipo", "INCENDIO",
                "emailUsuario", "vecino@test.cl"
        );

        mockMvc().perform(post("/api/reportes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tipo").value("INCENDIO"))
                .andExpect(jsonPath("$.estado").value("ACTIVO"));
    }

    /**
     * Verifica que POST /api/reportes retorna 400 cuando el título está vacío.
     */
    @Test
    @DisplayName("crear: debe retornar 400 si el título está vacío")
    void crear_tituloVacio_retorna400() throws Exception {
        Map<String, Object> body = Map.of(
                "titulo", "",
                "descripcion", "Humo visible",
                "latitud", -33.45,
                "longitud", -70.65,
                "tipo", "INCENDIO",
                "emailUsuario", "vecino@test.cl"
        );

        mockMvc().perform(post("/api/reportes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Verifica que GET /api/reportes retorna 200 con la lista completa de reportes.
     */
    @Test
    @DisplayName("listarTodos: debe retornar 200 con lista completa")
    void listarTodos_retorna200() throws Exception {
        when(reporteService.listarTodos()).thenReturn(List.of(reporteDTOMock()));

        mockMvc().perform(get("/api/reportes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    /**
     * Verifica que GET /api/reportes/activos retorna 200 con solo reportes activos.
     */
    @Test
    @DisplayName("listarActivos: debe retornar 200 con solo activos")
    void listarActivos_retorna200() throws Exception {
        when(reporteService.listarActivos()).thenReturn(List.of(reporteDTOMock()));

        mockMvc().perform(get("/api/reportes/activos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("ACTIVO"));
    }

    /**
     * Verifica que GET /api/reportes/{id} retorna 200 con el DTO del reporte encontrado.
     */
    @Test
    @DisplayName("buscarPorId: debe retornar 200 cuando el id existe")
    void buscarPorId_retorna200() throws Exception {
        when(reporteService.buscarPorId(1L)).thenReturn(reporteDTOMock());

        mockMvc().perform(get("/api/reportes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    /**
     * Verifica que PUT /api/reportes/{id}/estado retorna 200 con el estado actualizado.
     */
    @Test
    @DisplayName("actualizarEstado: debe retornar 200 con estado actualizado")
    void actualizarEstado_retorna200() throws Exception {
        ReportDTO actualizado = new ReportDTO(1L, "T", "D", -33.45, -70.65,
                "INCENDIO", "CERRADO", "v@test.cl", LocalDateTime.now());
        when(reporteService.actualizarEstado(eq(1L), eq("CERRADO"))).thenReturn(actualizado);

        mockMvc().perform(put("/api/reportes/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"CERRADO\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CERRADO"));
    }

    /**
     * Verifica que PUT /api/reportes/{id} retorna 200 con el título actualizado.
     */
    @Test
    @DisplayName("actualizarTitulo: debe retornar 200 con título actualizado")
    void actualizarTitulo_retorna200() throws Exception {
        ReportDTO actualizado = new ReportDTO(1L, "Nuevo Título", "D", -33.45, -70.65,
                "INCENDIO", "ACTIVO", "v@test.cl", LocalDateTime.now());
        when(reporteService.actualizarTitulo(eq(1L), any(UpdateTitleRequest.class)))
                .thenReturn(actualizado);

        mockMvc().perform(put("/api/reportes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Nuevo Título\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Nuevo Título"));
    }

    /**
     * Verifica que PUT /api/reportes/{id} retorna 400 cuando el título está vacío.
     */
    @Test
    @DisplayName("actualizarTitulo: debe retornar 400 si el título está vacío")
    void actualizarTitulo_tituloVacio_retorna400() throws Exception {
        mockMvc().perform(put("/api/reportes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"\"}"))
                .andExpect(status().isBadRequest());
    }
}