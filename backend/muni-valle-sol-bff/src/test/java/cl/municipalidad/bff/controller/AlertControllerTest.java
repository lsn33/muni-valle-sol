package cl.municipalidad.bff.controller;

import cl.municipalidad.bff.dto.AlertDTO;
import cl.municipalidad.bff.dto.CreateAlertRequest;
import cl.municipalidad.bff.exception.GlobalExceptionHandler;
import cl.municipalidad.bff.service.AlertRequestHandler;
import cl.municipalidad.bff.service.AlertService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AlertController - pruebas de integración web")
class AlertControllerTest {

    @Mock
    private AlertService alertService;

    @Mock
    private AlertRequestHandler alertRequestHandler;

    @InjectMocks
    private AlertController alertController;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final AlertDTO mockAlerta = new AlertDTO(
            "uuid-123", "Incendio Norte", "Fuego activo", "ALTA", LocalDateTime.now(), null, null);

    @BeforeEach
    void setUp() {
        // Se registra el GlobalExceptionHandler junto al controller para que
        // las excepciones lanzadas por el handler (IllegalArgumentException)
        // se traduzcan a respuestas HTTP reales, igual que en producción.
        mockMvc = MockMvcBuilders.standaloneSetup(alertController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/alertas debería retornar 200 y la lista de alertas")
    void listAlerts_retorna200ConAlertas() throws Exception {
        when(alertService.listAlerts()).thenReturn(List.of(mockAlerta));

        mockMvc.perform(get("/api/alertas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Incendio Norte"))
                .andExpect(jsonPath("$[0].severidad").value("ALTA"))
                .andExpect(jsonPath("$[0].id").value("uuid-123"));
    }

    @Test
    @DisplayName("GET /api/alertas debería retornar 200 con lista vacía si no hay alertas")
    void listAlerts_retorna200ListaVacia() throws Exception {
        when(alertService.listAlerts()).thenReturn(List.of());

        mockMvc.perform(get("/api/alertas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("GET /api/alertas debería llamar al service exactamente una vez")
    void listAlerts_llamaServiceUnaVez() throws Exception {
        when(alertService.listAlerts()).thenReturn(List.of());

        mockMvc.perform(get("/api/alertas")).andExpect(status().isOk());

        verify(alertService, times(1)).listAlerts();
    }

    @Test
    @DisplayName("POST /api/alertas debería retornar 201 y la alerta creada")
    void create_retorna201ConAlertaCreada() throws Exception {
        Map<String, String> body = Map.of(
                "titulo", "Nueva alerta",
                "descripcion", "Descripción",
                "severidad", "ALTA");

        when(alertRequestHandler.handleCreate(any(CreateAlertRequest.class))).thenReturn(mockAlerta);

        mockMvc.perform(post("/api/alertas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Incendio Norte"))
                .andExpect(jsonPath("$.severidad").value("ALTA"));
    }

    @Test
    @DisplayName("POST /api/alertas debería delegar al handler con el request completo")
    void create_delegaAlHandler() throws Exception {
        Map<String, String> body = Map.of(
                "titulo", "Alerta test",
                "descripcion", "Desc test",
                "severidad", "MEDIA");

        when(alertRequestHandler.handleCreate(any(CreateAlertRequest.class))).thenReturn(mockAlerta);

        mockMvc.perform(post("/api/alertas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());

        verify(alertRequestHandler, times(1)).handleCreate(any(CreateAlertRequest.class));
    }

    @Test
    @DisplayName("POST /api/alertas debería retornar 400 si el handler lanza IllegalArgumentException por titulo faltante")
    void create_retorna400SiFaltaTitulo() throws Exception {
        Map<String, String> body = Map.of(
                "descripcion", "Desc",
                "severidad", "ALTA");

        when(alertRequestHandler.handleCreate(any(CreateAlertRequest.class)))
                .thenThrow(new IllegalArgumentException("El campo 'titulo' es obligatorio"));

        mockMvc.perform(post("/api/alertas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El campo 'titulo' es obligatorio"));
    }

    @Test
    @DisplayName("POST /api/alertas debería retornar 400 si el handler lanza IllegalArgumentException por severidad inválida")
    void create_retorna400SiSeveridadInvalida() throws Exception {
        Map<String, String> body = Map.of(
                "titulo", "Test",
                "descripcion", "Desc",
                "severidad", "CRITICA");

        when(alertRequestHandler.handleCreate(any(CreateAlertRequest.class)))
                .thenThrow(new IllegalArgumentException("El campo 'severidad' debe ser ALTA, MEDIA o BAJA"));

        mockMvc.perform(post("/api/alertas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El campo 'severidad' debe ser ALTA, MEDIA o BAJA"));
    }
}