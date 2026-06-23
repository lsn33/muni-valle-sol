package cl.municipalidad.msbrigadas.controller;

import cl.municipalidad.msbrigadas.dto.BrigadeDTO;
import cl.municipalidad.msbrigadas.dto.CreateBrigadeRequest;
import cl.municipalidad.msbrigadas.dto.UpdateStatusRequest;
import cl.municipalidad.msbrigadas.dto.UpdateLocationRequest;
import cl.municipalidad.msbrigadas.service.BrigadeService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas unitarias para {@link BrigadeController}.
 *
 * <p>Verifica que cada endpoint retorne el código HTTP correcto y el cuerpo
 * esperado, usando MockMvc de forma standalone (sin levantar Spring completo).</p>
 *
 * @author Municipalidad Valle del Sol
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BrigadaController - Pruebas Unitarias")
class BrigadeControllerTest {

    @Mock
    private BrigadeService brigadaService;

    @InjectMocks
    private BrigadeController brigadaController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private BrigadeDTO brigadaDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(brigadaController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        brigadaDTO = new BrigadeDTO(
            1L, "Brigada Norte", "DISPONIBLE", "INCENDIO",
            -33.45, -70.65, "jefe@municipalidad.cl", LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("POST /api/brigadas - debería retornar 201 con brigada creada")
    void crear_exitoso_retorna201() throws Exception {
        CreateBrigadeRequest request = new CreateBrigadeRequest(
            "Brigada Norte", "INCENDIO", "jefe@municipalidad.cl", -33.45, -70.65
        );
        when(brigadaService.crear(any(CreateBrigadeRequest.class))).thenReturn(brigadaDTO);

        mockMvc.perform(post("/api/brigadas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.nombre").value("Brigada Norte"))
            .andExpect(jsonPath("$.estado").value("DISPONIBLE"))
            .andExpect(jsonPath("$.tipo").value("INCENDIO"));
    }

    @Test
    @DisplayName("POST /api/brigadas - debería retornar 400 si tipo es inválido")
    void crear_tipoInvalido_retorna400() throws Exception {
        String body = """
            {
              "nombre": "Brigada Norte",
              "tipo": "INVALIDO",
              "emailResponsable": "jefe@municipalidad.cl",
              "latitud": -33.45,
              "longitud": -70.65
            }
            """;

        mockMvc.perform(post("/api/brigadas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/brigadas - debería retornar 200 con lista")
    void listarTodas_retorna200() throws Exception {
        when(brigadaService.listarTodas()).thenReturn(List.of(brigadaDTO));

        mockMvc.perform(get("/api/brigadas"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].nombre").value("Brigada Norte"));
    }

    @Test
    @DisplayName("GET /api/brigadas/disponibles - debería retornar 200")
    void listarDisponibles_retorna200() throws Exception {
        when(brigadaService.listarDisponibles()).thenReturn(List.of(brigadaDTO));

        mockMvc.perform(get("/api/brigadas/disponibles"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].estado").value("DISPONIBLE"));
    }

    @Test
    @DisplayName("GET /api/brigadas/{id} - debería retornar 200 con brigada")
    void buscarPorId_existe_retorna200() throws Exception {
        when(brigadaService.buscarPorId(1L)).thenReturn(brigadaDTO);

        mockMvc.perform(get("/api/brigadas/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("PUT /api/brigadas/{id}/estado - debería retornar 200 con estado actualizado")
    void actualizarEstado_exitoso_retorna200() throws Exception {
        BrigadeDTO actualizado = new BrigadeDTO(
            1L, "Brigada Norte", "EN_CAMINO", "INCENDIO",
            -33.45, -70.65, "jefe@municipalidad.cl", LocalDateTime.now()
        );
        when(brigadaService.actualizarEstado(eq(1L), any(UpdateStatusRequest.class))).thenReturn(actualizado);

        mockMvc.perform(put("/api/brigadas/1/estado")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"estado\": \"EN_CAMINO\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado").value("EN_CAMINO"));
    }

    @Test
    @DisplayName("PUT /api/brigadas/{id}/ubicacion - debería retornar 200 con ubicación actualizada")
    void actualizarUbicacion_exitoso_retorna200() throws Exception {
        BrigadeDTO actualizado = new BrigadeDTO(
            1L, "Brigada Norte", "DISPONIBLE", "INCENDIO",
            -34.0, -71.0, "jefe@municipalidad.cl", LocalDateTime.now()
        );
        when(brigadaService.actualizarUbicacion(eq(1L), any(UpdateLocationRequest.class))).thenReturn(actualizado);

        mockMvc.perform(put("/api/brigadas/1/ubicacion")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"latitud\": -34.0, \"longitud\": -71.0}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.latitud").value(-34.0));
    }

    @Test
    @DisplayName("DELETE /api/brigadas/{id} - debería retornar 204")
    void eliminar_existe_retorna204() throws Exception {
        doNothing().when(brigadaService).eliminar(1L);

        mockMvc.perform(delete("/api/brigadas/1"))
            .andExpect(status().isNoContent());
    }
}