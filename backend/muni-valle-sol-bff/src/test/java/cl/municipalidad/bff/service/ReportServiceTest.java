package cl.municipalidad.bff.service;

import cl.municipalidad.bff.client.ReportClient;
import cl.municipalidad.bff.dto.ReportDTO;
import cl.municipalidad.bff.dto.ReportMsDTO;
import cl.municipalidad.bff.mapper.ReportMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReportService - pruebas unitarias")
class ReportServiceTest {

    @Mock
    private ReportClient reportClient;

    private ReportService reportService;

    private ReportMsDTO mockMsDTO;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        mockMsDTO = new ReportMsDTO(1L, "Incendio cerro", "Fuego activo",
                -33.4569, -70.6483, "INCENDIO", "ACTIVO", "juan@gmail.com", now);
        reportService = new ReportService(reportClient, new ReportMapper());
    }

    @Test
    @DisplayName("listAll() debería retornar todos los reportes mapeados a ReportDTO")
    void listAll_retornaTodosLosDTOs() {
        when(reportClient.listAll()).thenReturn(List.of(mockMsDTO));

        List<ReportDTO> resultado = reportService.listAll();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).titulo()).isEqualTo("Incendio cerro");
        verify(reportClient, times(1)).listAll();
    }

    @Test
    @DisplayName("listAll() debería retornar lista vacía si el cliente no retorna reportes")
    void listAll_retornaVacio() {
        when(reportClient.listAll()).thenReturn(List.of());
        assertThat(reportService.listAll()).isEmpty();
    }

    @Test
    @DisplayName("listActive() debería retornar reportes activos mapeados correctamente")
    void listActive_retornaActivosMapeados() {
        when(reportClient.listActive()).thenReturn(List.of(mockMsDTO));

        List<ReportDTO> resultado = reportService.listActive();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).estado()).isEqualTo("ACTIVO");
        verify(reportClient, times(1)).listActive();
    }

    @Test
    @DisplayName("findById() debería retornar el reporte correcto para un ID dado")
    void findById_retornaReporteCorrecto() {
        when(reportClient.findById(1L)).thenReturn(mockMsDTO);

        ReportDTO resultado = reportService.findById(1L);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.titulo()).isEqualTo("Incendio cerro");
        verify(reportClient).findById(1L);
    }

    @Test
    @DisplayName("create() debería delegar en el cliente y retornar el reporte creado")
    void create_delegaEnClienteYRetornaDTO() {
        Map<String, Object> body = Map.of("titulo", "Nuevo", "tipo", "HUMO");
        when(reportClient.create(body)).thenReturn(mockMsDTO);

        ReportDTO resultado = reportService.create(body);

        assertThat(resultado).isNotNull();
        verify(reportClient).create(body);
    }

    @Test
    @DisplayName("updateStatus() debería llamar al cliente con ID y estado correctos")
    void updateStatus_llamaClienteConParametrosCorrectos() {
        ReportMsDTO actualizado = new ReportMsDTO(1L, "Incendio", "Desc",
                -33.0, -70.0, "INCENDIO", "EN_REVISION", "juan@gmail.com", now);
        when(reportClient.updateStatus(1L, "EN_REVISION")).thenReturn(actualizado);

        ReportDTO resultado = reportService.updateStatus(1L, "EN_REVISION");

        assertThat(resultado.estado()).isEqualTo("EN_REVISION");
        verify(reportClient).updateStatus(1L, "EN_REVISION");
    }

    @Test
    @DisplayName("updateTitle() debería llamar al cliente con ID y título correctos")
    void updateTitle_llamaClienteConTituloCorrecto() {
        ReportMsDTO actualizado = new ReportMsDTO(1L, "Nuevo título", "Desc",
                -33.0, -70.0, "INCENDIO", "ACTIVO", "juan@gmail.com", now);
        when(reportClient.updateTitle(1L, "Nuevo título")).thenReturn(actualizado);

        ReportDTO resultado = reportService.updateTitle(1L, "Nuevo título");

        assertThat(resultado.titulo()).isEqualTo("Nuevo título");
        verify(reportClient).updateTitle(1L, "Nuevo título");
    }

    @Test
    @DisplayName("delete() debería delegar en el cliente sin retornar nada")
    void delete_delegaEnCliente() {
        doNothing().when(reportClient).delete(1L);
        reportService.delete(1L);
        verify(reportClient, times(1)).delete(1L);
    }

    @Test
    @DisplayName("toDTO debería mapear latitud y longitud a LocationDTO correctamente")
    void toDTO_mapeaUbicacionCorrectamente() {
        when(reportClient.findById(1L)).thenReturn(mockMsDTO);

        ReportDTO resultado = reportService.findById(1L);

        assertThat(resultado.ubicacion()).isNotNull();
        assertThat(resultado.ubicacion().lat()).isEqualTo(-33.4569);
        assertThat(resultado.ubicacion().lng()).isEqualTo(-70.6483);
    }

    @Test
    @DisplayName("toDTO debería preservar todos los campos del ReportMsDTO")
    void toDTO_preservaTodosLosCampos() {
        when(reportClient.findById(1L)).thenReturn(mockMsDTO);

        ReportDTO resultado = reportService.findById(1L);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.titulo()).isEqualTo("Incendio cerro");
        assertThat(resultado.descripcion()).isEqualTo("Fuego activo");
        assertThat(resultado.tipo()).isEqualTo("INCENDIO");
        assertThat(resultado.estado()).isEqualTo("ACTIVO");
        assertThat(resultado.emailUsuario()).isEqualTo("juan@gmail.com");
        assertThat(resultado.fechaCreacion()).isEqualTo(now);
    }

    @Test
    @DisplayName("listAll() debería mapear múltiples reportes correctamente")
    void listAll_mapeaMultiplesReportes() {
        ReportMsDTO segundo = new ReportMsDTO(2L, "Humo", "Desc",
                -34.0, -71.0, "HUMO", "PENDIENTE", "otra@gmail.com", now);
        when(reportClient.listAll()).thenReturn(List.of(mockMsDTO, segundo));

        List<ReportDTO> resultado = reportService.listAll();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(1).tipo()).isEqualTo("HUMO");
    }
}