package cl.municipalidad.bff.exception;

import cl.municipalidad.bff.controller.AlertController;
import cl.municipalidad.bff.service.AlertService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler - pruebas unitarias")
class GlobalExceptionHandlerTest {

        @Mock
        private AlertService alertService;

        @InjectMocks
        private AlertController alertController;

        private GlobalExceptionHandler globalExceptionHandler;

    private MockMvc mockMvc;

        @BeforeEach
        void setUp() {
                globalExceptionHandler = new GlobalExceptionHandler();
                mockMvc = MockMvcBuilders.standaloneSetup(alertController)
                                .setControllerAdvice(globalExceptionHandler)
                                .build();
        }

    @Test
    @DisplayName("debería retornar 500 cuando ocurre RuntimeException")
    void handleRuntimeException_retorna500() throws Exception {
        when(alertService.listAlerts()).thenThrow(new RuntimeException("Error inesperado del servidor"));

        mockMvc.perform(get("/api/alertas"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Error inesperado del servidor"))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("debería retornar el status de MsException cuando falla un microservicio")
    void handleMsException_retornaStatusCorrecto() throws Exception {
        when(alertService.listAlerts())
                .thenThrow(new MsException("Microservicio no disponible", HttpStatus.SERVICE_UNAVAILABLE));

        mockMvc.perform(get("/api/alertas"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").value("Microservicio no disponible"))
                .andExpect(jsonPath("$.status").value(503))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("debería retornar 404 cuando MsException tiene status NOT_FOUND")
    void handleMsException_retorna404() throws Exception {
        when(alertService.listAlerts())
                .thenThrow(new MsException("Recurso no encontrado", HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/alertas"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Recurso no encontrado"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("debería retornar 401 cuando MsException tiene status UNAUTHORIZED")
    void handleMsException_retorna401() throws Exception {
        when(alertService.listAlerts())
                .thenThrow(new MsException("Credenciales incorrectas", HttpStatus.UNAUTHORIZED));

        mockMvc.perform(get("/api/alertas"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Credenciales incorrectas"))
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("la respuesta de error siempre debería incluir el campo timestamp")
    void errorResponse_siempreIncluyeTimestamp() throws Exception {
        when(alertService.listAlerts()).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/alertas"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }
}