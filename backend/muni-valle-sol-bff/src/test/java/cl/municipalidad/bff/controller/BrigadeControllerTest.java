package cl.municipalidad.bff.controller;

import cl.municipalidad.bff.dto.BrigadeDTO;
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
import cl.municipalidad.bff.service.BrigadeService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BrigadaController - pruebas de integración web")
class BrigadeControllerTest {

    @Mock
    private BrigadeService brigadaService;

    @InjectMocks
    private BrigadeController brigadaController;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final BrigadeDTO mockBrigada = new BrigadeDTO(
            1L, "Brigada Norte", "DISPONIBLE", "INCENDIO",
            -33.45, -70.65, "jefe@municipalidad.cl", LocalDateTime.now());

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(brigadaController).build();
    }

    @Test
    @DisplayName("GET /api/brigadas debería retornar 200 con todas las brigadas")
    void listAll_retorna200ConBrigadas() throws Exception {
        when(brigadaService.listAll()).thenReturn(List.of(mockBrigada));

        mockMvc.perform(get("/api/brigadas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Brigada Norte"))
                .andExpect(jsonPath("$[0].tipo").value("INCENDIO"));
    }

    @Test
    @DisplayName("GET /api/brigadas/disponibles debería retornar 200 con brigadas disponibles")
    void listDisponibles_retorna200ConDisponibles() throws Exception {
        when(brigadaService.listDisponibles()).thenReturn(List.of(mockBrigada));

        mockMvc.perform(get("/api/brigadas/disponibles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("DISPONIBLE"));
    }

    @Test
    @DisplayName("GET /api/brigadas/tipo/{tipo} debería retornar 200 con brigadas del tipo solicitado")
    void listByTipo_retorna200ConBrigadasDelTipo() throws Exception {
        when(brigadaService.listByTipo("INCENDIO")).thenReturn(List.of(mockBrigada));

        mockMvc.perform(get("/api/brigadas/tipo/INCENDIO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipo").value("INCENDIO"));
    }

    @Test
    @DisplayName("GET /api/brigadas/{id} debería retornar 200 con la brigada solicitada")
    void findById_retorna200ConBrigada() throws Exception {
        when(brigadaService.findById(1L)).thenReturn(mockBrigada);

        mockMvc.perform(get("/api/brigadas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Brigada Norte"));
    }

    @Test
    @DisplayName("POST /api/brigadas debería retornar 201 y construir el mapa correcto desde el request")
    void create_retorna201YConstruyeMapaCorrecto() throws Exception {
        when(brigadaService.create(any())).thenReturn(mockBrigada);

        String json = """
                {
                  "nombre": "Brigada Norte",
                  "tipo": "INCENDIO",
                  "emailResponsable": "jefe@municipalidad.cl",
                  "latitud": -33.45,
                  "longitud": -70.65
                }
                """;

        mockMvc.perform(post("/api/brigadas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Brigada Norte"));

        verify(brigadaService).create(Map.of(
                "nombre", "Brigada Norte",
                "tipo", "INCENDIO",
                "emailResponsable", "jefe@municipalidad.cl",
                "latitud", -33.45,
                "longitud", -70.65
        ));
    }

    @Test
    @DisplayName("POST /api/brigadas con tipo inválido debería retornar 400 por validación")
    void create_conTipoInvalido_retorna400() throws Exception {
        String json = """
                {
                  "nombre": "Brigada Test",
                  "tipo": "INVALIDO",
                  "emailResponsable": "jefe@municipalidad.cl",
                  "latitud": -33.45,
                  "longitud": -70.65
                }
                """;

        mockMvc.perform(post("/api/brigadas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(brigadaService);
    }

    @Test
    @DisplayName("PUT /api/brigadas/{id}/estado debería retornar 200 con el estado actualizado")
    void updateEstado_retorna200ConEstadoActualizado() throws Exception {
        BrigadeDTO actualizada = new BrigadeDTO(
                1L, "Brigada Norte", "EN_CAMINO", "INCENDIO",
                -33.45, -70.65, "jefe@municipalidad.cl", LocalDateTime.now());
        when(brigadaService.updateEstado(eq(1L), eq("EN_CAMINO"))).thenReturn(actualizada);

        String json = """
                { "estado": "EN_CAMINO" }
                """;

        mockMvc.perform(put("/api/brigadas/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EN_CAMINO"));
    }

    @Test
    @DisplayName("PUT /api/brigadas/{id}/ubicacion debería retornar 200 con la ubicación actualizada")
    void updateUbicacion_retorna200ConUbicacionActualizada() throws Exception {
        BrigadeDTO actualizada = new BrigadeDTO(
                1L, "Brigada Norte", "DISPONIBLE", "INCENDIO",
                -33.50, -70.60, "jefe@municipalidad.cl", LocalDateTime.now());
        when(brigadaService.updateUbicacion(eq(1L), eq(-33.50), eq(-70.60))).thenReturn(actualizada);

        String json = """
                { "latitud": -33.50, "longitud": -70.60 }
                """;

        mockMvc.perform(put("/api/brigadas/1/ubicacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.latitud").value(-33.50))
                .andExpect(jsonPath("$.longitud").value(-70.60));
    }

    @Test
    @DisplayName("DELETE /api/brigadas/{id} debería retornar 204 sin contenido")
    void delete_retorna204SinContenido() throws Exception {
        doNothing().when(brigadaService).delete(1L);

        mockMvc.perform(delete("/api/brigadas/1"))
                .andExpect(status().isNoContent());

        verify(brigadaService).delete(1L);
    }
}