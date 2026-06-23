package cl.municipalidad.bff.service;

import cl.municipalidad.bff.client.BrigadeClient;
import cl.municipalidad.bff.dto.BrigadeDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BrigadaService - pruebas unitarias")
class BrigadeServiceTest {

    @Mock
    private BrigadeClient brigadaClient;

    @InjectMocks
    private BrigadeService brigadaService;

    private final BrigadeDTO mockBrigada = new BrigadeDTO(
            1L, "Brigada Norte", "DISPONIBLE", "INCENDIO",
            -33.45, -70.65, "jefe@municipalidad.cl", LocalDateTime.now());

    @Test
    @DisplayName("listAll() debería delegar en BrigadaClient y retornar todas las brigadas")
    void listAll_delegaEnClienteYRetornaLista() {
        when(brigadaClient.listAll()).thenReturn(List.of(mockBrigada));

        List<BrigadeDTO> resultado = brigadaService.listAll();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).nombre()).isEqualTo("Brigada Norte");
        verify(brigadaClient, times(1)).listAll();
    }

    @Test
    @DisplayName("listDisponibles() debería delegar en BrigadaClient y retornar solo disponibles")
    void listDisponibles_delegaEnClienteYRetornaLista() {
        when(brigadaClient.listDisponibles()).thenReturn(List.of(mockBrigada));

        List<BrigadeDTO> resultado = brigadaService.listDisponibles();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).estado()).isEqualTo("DISPONIBLE");
        verify(brigadaClient, times(1)).listDisponibles();
    }

    @Test
    @DisplayName("listByTipo() debería delegar en BrigadaClient pasando el tipo exacto")
    void listByTipo_delegaEnClienteConTipoCorrecto() {
        when(brigadaClient.listByTipo("INCENDIO")).thenReturn(List.of(mockBrigada));

        List<BrigadeDTO> resultado = brigadaService.listByTipo("INCENDIO");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).tipo()).isEqualTo("INCENDIO");
        verify(brigadaClient).listByTipo("INCENDIO");
    }

    @Test
    @DisplayName("findById() debería delegar en BrigadaClient y retornar la brigada correspondiente")
    void findById_delegaEnClienteYRetornaBrigada() {
        when(brigadaClient.findById(1L)).thenReturn(mockBrigada);

        BrigadeDTO resultado = brigadaService.findById(1L);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.nombre()).isEqualTo("Brigada Norte");
        verify(brigadaClient).findById(1L);
    }

    @Test
    @DisplayName("create() debería delegar en BrigadaClient pasando el mapa de datos sin transformarlo")
    void create_delegaEnClienteConElMismoMapa() {
        Map<String, Object> body = Map.of(
                "nombre", "Brigada Norte",
                "tipo", "INCENDIO",
                "emailResponsable", "jefe@municipalidad.cl",
                "latitud", -33.45,
                "longitud", -70.65
        );
        when(brigadaClient.create(body)).thenReturn(mockBrigada);

        BrigadeDTO resultado = brigadaService.create(body);

        assertThat(resultado.nombre()).isEqualTo("Brigada Norte");
        verify(brigadaClient).create(body);
    }

    @Test
    @DisplayName("updateEstado() debería delegar en BrigadaClient con id y estado correctos")
    void updateEstado_delegaEnClienteConIdYEstado() {
        BrigadeDTO actualizada = new BrigadeDTO(
                1L, "Brigada Norte", "EN_CAMINO", "INCENDIO",
                -33.45, -70.65, "jefe@municipalidad.cl", LocalDateTime.now());
        when(brigadaClient.updateEstado(1L, "EN_CAMINO")).thenReturn(actualizada);

        BrigadeDTO resultado = brigadaService.updateEstado(1L, "EN_CAMINO");

        assertThat(resultado.estado()).isEqualTo("EN_CAMINO");
        verify(brigadaClient).updateEstado(1L, "EN_CAMINO");
    }

    @Test
    @DisplayName("updateUbicacion() debería delegar en BrigadaClient con id y coordenadas correctas")
    void updateUbicacion_delegaEnClienteConCoordenadas() {
        BrigadeDTO actualizada = new BrigadeDTO(
                1L, "Brigada Norte", "DISPONIBLE", "INCENDIO",
                -33.50, -70.60, "jefe@municipalidad.cl", LocalDateTime.now());
        when(brigadaClient.updateUbicacion(1L, -33.50, -70.60)).thenReturn(actualizada);

        BrigadeDTO resultado = brigadaService.updateUbicacion(1L, -33.50, -70.60);

        assertThat(resultado.latitud()).isEqualTo(-33.50);
        assertThat(resultado.longitud()).isEqualTo(-70.60);
        verify(brigadaClient).updateUbicacion(1L, -33.50, -70.60);
    }

    @Test
    @DisplayName("delete() debería delegar en BrigadaClient exactamente una vez con el id correcto")
    void delete_delegaEnClienteConIdCorrecto() {
        brigadaService.delete(1L);

        verify(brigadaClient, times(1)).delete(1L);
        verifyNoMoreInteractions(brigadaClient);
    }
}