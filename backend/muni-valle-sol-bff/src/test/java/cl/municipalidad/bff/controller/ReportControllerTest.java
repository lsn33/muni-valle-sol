package cl.municipalidad.bff.controller;

import cl.municipalidad.bff.dto.AlertDTO;
import cl.municipalidad.bff.dto.CreateReportRequest;
import cl.municipalidad.bff.dto.LocationDTO;
import cl.municipalidad.bff.dto.ReportDTO;
import cl.municipalidad.bff.dto.UpdateStatusRequest;
import cl.municipalidad.bff.dto.UpdateTitleRequest;
import cl.municipalidad.bff.service.AlertService;
import cl.municipalidad.bff.service.ReportRequestHandler;
import cl.municipalidad.bff.service.ReportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReportController - pruebas de integración web")
class ReportControllerTest {

        @Mock
        private ReportService reportService;

        @Mock
        private ReportRequestHandler reportRequestHandler;

        @Mock
        private AlertService alertService;

        @InjectMocks
        private ReportController reportController;

    private MockMvc mockMvc;

        private final ObjectMapper objectMapper = new ObjectMapper();

    private final ReportDTO mockReporte = new ReportDTO(
            1L, "Incendio cerro", "Fuego activo", "INCENDIO", "ACTIVO",
            "juan@gmail.com", new LocationDTO(-33.4569, -70.6483), LocalDateTime.now());

        @BeforeEach
        void setUp() {
                mockMvc = MockMvcBuilders.standaloneSetup(reportController).build();
        }

    @Test
    @DisplayName("GET /api/reportes debería retornar 200 con todos los reportes")
    void listAll_retorna200ConReportes() throws Exception {
        when(reportService.listAll()).thenReturn(List.of(mockReporte));

        mockMvc.perform(get("/api/reportes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Incendio cerro"))
                .andExpect(jsonPath("$[0].tipo").value("INCENDIO"))
                .andExpect(jsonPath("$[0].estado").value("ACTIVO"));
    }

    @Test
    @DisplayName("GET /api/reportes debería retornar 200 con lista vacía si no hay reportes")
    void listAll_retorna200ListaVacia() throws Exception {
        when(reportService.listAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/reportes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("GET /api/reportes/activos debería retornar 200 con reportes activos")
    void listActive_retorna200ConActivos() throws Exception {
        when(reportService.listActive()).thenReturn(List.of(mockReporte));

        mockMvc.perform(get("/api/reportes/activos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("ACTIVO"));
    }

    @Test
    @DisplayName("GET /api/reportes/{id} debería retornar 200 con el reporte correcto")
    void findById_retorna200ConReporte() throws Exception {
        when(reportService.findById(1L)).thenReturn(mockReporte);

        mockMvc.perform(get("/api/reportes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Incendio cerro"));
    }

    @Test
    @DisplayName("GET /api/reportes/{id} debería incluir datos de ubicación en la respuesta")
    void findById_incluyeUbicacion() throws Exception {
        when(reportService.findById(1L)).thenReturn(mockReporte);

        mockMvc.perform(get("/api/reportes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ubicacion.lat").value(-33.4569))
                .andExpect(jsonPath("$.ubicacion.lng").value(-70.6483));
    }

    @Test
    @DisplayName("POST /api/reportes debería retornar 201 con el reporte creado")
    void create_retorna201ConReporte() throws Exception {
        Map<String, Object> body = Map.of("titulo", "Nuevo", "tipo", "HUMO");
        when(reportRequestHandler.handleCreate(any())).thenReturn(mockReporte);

        mockMvc.perform(post("/api/reportes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("PUT /api/reportes/{id}/estado debería retornar 200 con el estado actualizado")
    void updateStatus_retorna200() throws Exception {
        ReportDTO actualizado = new ReportDTO(1L, "Incendio", "Desc", "INCENDIO", "EN_REVISION",
                "juan@gmail.com", new LocationDTO(-33.0, -70.0), LocalDateTime.now());
        when(reportRequestHandler.handleUpdateStatus(eq(1L), any())).thenReturn(actualizado);

        mockMvc.perform(put("/api/reportes/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"EN_REVISION\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EN_REVISION"));
    }

    @Test
    @DisplayName("PUT /api/reportes/{id} debería retornar 200 con el título actualizado")
    void updateTitle_retorna200() throws Exception {
        ReportDTO actualizado = new ReportDTO(1L, "Nuevo título", "Desc", "INCENDIO", "ACTIVO",
                "juan@gmail.com", new LocationDTO(-33.0, -70.0), LocalDateTime.now());
        when(reportRequestHandler.handleUpdateTitle(eq(1L), any())).thenReturn(actualizado);

        mockMvc.perform(put("/api/reportes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Nuevo título\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Nuevo título"));
    }

    @Test
    @DisplayName("DELETE /api/reportes/{id} debería retornar 204 sin contenido")
    void delete_retorna204() throws Exception {
        doNothing().when(reportService).delete(1L);

        mockMvc.perform(delete("/api/reportes/1"))
                .andExpect(status().isNoContent());

        verify(reportService, times(1)).delete(1L);
    }
}