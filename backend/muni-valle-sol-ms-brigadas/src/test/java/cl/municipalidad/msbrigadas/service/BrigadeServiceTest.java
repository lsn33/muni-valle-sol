package cl.municipalidad.msbrigadas.service;

import cl.municipalidad.msbrigadas.dto.BrigadeDTO;
import cl.municipalidad.msbrigadas.dto.CreateBrigadeRequest;
import cl.municipalidad.msbrigadas.dto.UpdateStatusRequest;
import cl.municipalidad.msbrigadas.dto.UpdateLocationRequest;
import cl.municipalidad.msbrigadas.factory.BrigadeFactory;
import cl.municipalidad.msbrigadas.model.Brigade;
import cl.municipalidad.msbrigadas.repository.BrigadeRepository;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para {@link BrigadeService}.
 *
 * <p>Verifica la lógica de negocio de forma aislada, usando Mockito para
 * simular el comportamiento del repositorio y la fábrica sin tocar la BD.</p>
 *
 * @author Municipalidad Valle del Sol
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BrigadaService - Pruebas Unitarias")
class BrigadeServiceTest {

    @Mock
    private BrigadeRepository brigadaRepository;

    @Mock
    private BrigadeFactory brigadaFactory;

    @InjectMocks
    private BrigadeService brigadaService;

    private Brigade brigadaEjemplo;
    private CreateBrigadeRequest createRequest;

    @BeforeEach
    void setUp() {
        brigadaEjemplo = new Brigade();
        brigadaEjemplo.setId(1L);
        brigadaEjemplo.setNombre("Brigada Norte");
        brigadaEjemplo.setEstado("DISPONIBLE");
        brigadaEjemplo.setTipo("INCENDIO");
        brigadaEjemplo.setLatitud(-33.45);
        brigadaEjemplo.setLongitud(-70.65);
        brigadaEjemplo.setEmailResponsable("jefe@municipalidad.cl");
        brigadaEjemplo.setFechaCreacion(LocalDateTime.now());

        createRequest = new CreateBrigadeRequest(
            "Brigada Norte", "INCENDIO", "jefe@municipalidad.cl", -33.45, -70.65
        );
    }

    @Test
    @DisplayName("crear - debería crear brigada correctamente")
    void crear_exitoso() {
        when(brigadaFactory.crear(any(), any(), any(), any(), any())).thenReturn(brigadaEjemplo);
        when(brigadaRepository.save(any(Brigade.class))).thenReturn(brigadaEjemplo);

        BrigadeDTO resultado = brigadaService.crear(createRequest);

        assertThat(resultado).isNotNull();
        assertThat(resultado.nombre()).isEqualTo("Brigada Norte");
        assertThat(resultado.estado()).isEqualTo("DISPONIBLE");
        assertThat(resultado.tipo()).isEqualTo("INCENDIO");
        verify(brigadaRepository, times(1)).save(any(Brigade.class));
    }

    @Test
    @DisplayName("listarTodas - debería retornar lista de brigadas")
    void listarTodas_retornaLista() {
        when(brigadaRepository.findAll()).thenReturn(List.of(brigadaEjemplo));

        List<BrigadeDTO> resultado = brigadaService.listarTodas();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).nombre()).isEqualTo("Brigada Norte");
    }

    @Test
    @DisplayName("listarDisponibles - debería retornar solo brigadas disponibles")
    void listarDisponibles_retornaSoloDisponibles() {
        when(brigadaRepository.findByEstado("DISPONIBLE")).thenReturn(List.of(brigadaEjemplo));

        List<BrigadeDTO> resultado = brigadaService.listarDisponibles();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).estado()).isEqualTo("DISPONIBLE");
    }

    @Test
    @DisplayName("buscarPorId - debería retornar brigada existente")
    void buscarPorId_existe_retornaBrigada() {
        when(brigadaRepository.findById(1L)).thenReturn(Optional.of(brigadaEjemplo));

        BrigadeDTO resultado = brigadaService.buscarPorId(1L);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.nombre()).isEqualTo("Brigada Norte");
    }

    @Test
    @DisplayName("buscarPorId - debería lanzar excepción si no existe")
    void buscarPorId_noExiste_lanzaExcepcion() {
        when(brigadaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> brigadaService.buscarPorId(99L))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("99");
    }

    @Test
    @DisplayName("actualizarEstado - debería actualizar estado correctamente")
    void actualizarEstado_exitoso() {
        UpdateStatusRequest request = new UpdateStatusRequest("EN_CAMINO");
        when(brigadaRepository.findById(1L)).thenReturn(Optional.of(brigadaEjemplo));
        when(brigadaRepository.save(any(Brigade.class))).thenAnswer(inv -> {
            Brigade b = inv.getArgument(0);
            return b;
        });

        BrigadeDTO resultado = brigadaService.actualizarEstado(1L, request);

        assertThat(resultado.estado()).isEqualTo("EN_CAMINO");
    }

    @Test
    @DisplayName("actualizarUbicacion - debería actualizar coordenadas correctamente")
    void actualizarUbicacion_exitoso() {
        UpdateLocationRequest request = new UpdateLocationRequest(-34.0, -71.0);
        when(brigadaRepository.findById(1L)).thenReturn(Optional.of(brigadaEjemplo));
        when(brigadaRepository.save(any(Brigade.class))).thenAnswer(inv -> inv.getArgument(0));

        BrigadeDTO resultado = brigadaService.actualizarUbicacion(1L, request);

        assertThat(resultado.latitud()).isEqualTo(-34.0);
        assertThat(resultado.longitud()).isEqualTo(-71.0);
    }

    @Test
    @DisplayName("eliminar - debería eliminar brigada existente")
    void eliminar_existe_eliminaCorrectamente() {
        when(brigadaRepository.existsById(1L)).thenReturn(true);

        brigadaService.eliminar(1L);

        verify(brigadaRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("eliminar - debería lanzar excepción si no existe")
    void eliminar_noExiste_lanzaExcepcion() {
        when(brigadaRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> brigadaService.eliminar(99L))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("99");
    }
}