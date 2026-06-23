package cl.municipalidad.bff.service;

import cl.municipalidad.bff.client.AlertClient;
import cl.municipalidad.bff.dto.AlertDTO;
import cl.municipalidad.bff.dto.AlertMsResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AlertService - pruebas unitarias")
class AlertServiceTest {

    @Mock
    private AlertClient alertClient;

    @InjectMocks
    private AlertService alertService;

    private AlertMsResponseDTO buildMs(String id, String title, String description, String severity) {
        return new AlertMsResponseDTO(id, title, description, severity, "ACTIVE",
                LocalDateTime.now(), null, null, null, null);
    }

    @Test
    @DisplayName("listAlerts() debería retornar las alertas activas del MS-Alertas mapeadas al español")
    void listAlerts_retornaAlertasMapeadas() {
        AlertMsResponseDTO ms = buildMs("uuid-1", "Incendio Norte", "Fuego activo", "HIGH");
        when(alertClient.listActive()).thenReturn(List.of(ms));

        List<AlertDTO> resultado = alertService.listAlerts();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).titulo()).isEqualTo("Incendio Norte");
        assertThat(resultado.get(0).severidad()).isEqualTo("ALTA");
        assertThat(resultado.get(0).id()).isEqualTo("uuid-1");
    }

    @Test
    @DisplayName("listAlerts() debería retornar lista vacía si el MS-Alertas no tiene alertas")
    void listAlerts_retornaVacioSinAlertas() {
        when(alertClient.listActive()).thenReturn(List.of());

        assertThat(alertService.listAlerts()).isEmpty();
    }

    @Test
    @DisplayName("listAlerts() debería mapear severidad HIGH a ALTA")
    void listAlerts_highMapeaAAlta() {
        when(alertClient.listActive()).thenReturn(List.of(buildMs("1", "T", "D", "HIGH")));

        assertThat(alertService.listAlerts().get(0).severidad()).isEqualTo("ALTA");
    }

    @Test
    @DisplayName("listAlerts() debería mapear severidad MEDIUM a MEDIA")
    void listAlerts_mediumMapeaAMedia() {
        when(alertClient.listActive()).thenReturn(List.of(buildMs("1", "T", "D", "MEDIUM")));

        assertThat(alertService.listAlerts().get(0).severidad()).isEqualTo("MEDIA");
    }

    @Test
    @DisplayName("listAlerts() debería mapear severidad LOW a BAJA")
    void listAlerts_lowMapeaABaja() {
        when(alertClient.listActive()).thenReturn(List.of(buildMs("1", "T", "D", "LOW")));

        assertThat(alertService.listAlerts().get(0).severidad()).isEqualTo("BAJA");
    }

    @Test
    @DisplayName("listAlerts() debería preservar título y descripción del MS-Alertas")
    void listAlerts_preservaCamposDelMs() {
        AlertMsResponseDTO ms = buildMs("5", "Título original", "Descripción original", "HIGH");
        when(alertClient.listActive()).thenReturn(List.of(ms));

        AlertDTO alerta = alertService.listAlerts().get(0);
        assertThat(alerta.titulo()).isEqualTo("Título original");
        assertThat(alerta.descripcion()).isEqualTo("Descripción original");
        assertThat(alerta.id()).isEqualTo("5");
    }

    @Test
    @DisplayName("create() debería convertir ALTA a HIGH al llamar al MS-Alertas")
    void create_convierteAltaAHigh() {
        AlertMsResponseDTO ms = buildMs("nuevo", "T", "D", "HIGH");
        when(alertClient.create(eq("T"), eq("D"), eq("HIGH"), isNull(), isNull())).thenReturn(ms);

        alertService.create("T", "D", "ALTA", null, null);

        verify(alertClient).create("T", "D", "HIGH", null, null);
    }

    @Test
    @DisplayName("create() debería convertir MEDIA a MEDIUM al llamar al MS-Alertas")
    void create_convierteMediaAMedium() {
        AlertMsResponseDTO ms = buildMs("nuevo", "T", "D", "MEDIUM");
        when(alertClient.create(eq("T"), eq("D"), eq("MEDIUM"), isNull(), isNull())).thenReturn(ms);

        alertService.create("T", "D", "MEDIA", null, null);

        verify(alertClient).create("T", "D", "MEDIUM", null, null);
    }

    @Test
    @DisplayName("create() debería convertir BAJA a LOW al llamar al MS-Alertas")
    void create_convierteBAjaALow() {
        AlertMsResponseDTO ms = buildMs("nuevo", "T", "D", "LOW");
        when(alertClient.create(eq("T"), eq("D"), eq("LOW"), isNull(), isNull())).thenReturn(ms);

        alertService.create("T", "D", "BAJA", null, null);

        verify(alertClient).create("T", "D", "LOW", null, null);
    }

    @Test
    @DisplayName("create() debería retornar AlertDTO con datos correctos del MS-Alertas")
    void create_retornaAlertaConDatosCorrectos() {
        AlertMsResponseDTO ms = buildMs("uuid-nuevo", "Nueva alerta", "Descripción", "HIGH");
        when(alertClient.create(any(), any(), any(), any(), any())).thenReturn(ms);

        AlertDTO resultado = alertService.create("Nueva alerta", "Descripción", "ALTA", null, null);

        assertThat(resultado.titulo()).isEqualTo("Nueva alerta");
        assertThat(resultado.descripcion()).isEqualTo("Descripción");
        assertThat(resultado.severidad()).isEqualTo("ALTA");
        assertThat(resultado.id()).isEqualTo("uuid-nuevo");
    }

    @Test
    @DisplayName("create() debería pasar coordenadas al MS-Alertas cuando se proporcionan")
    void create_pasaCoordenadas() {
        AlertMsResponseDTO ms = new AlertMsResponseDTO("id", "T", "D", "HIGH", "ACTIVE",
                LocalDateTime.now(), null, null, -33.4569, -70.6483);
        when(alertClient.create(eq("T"), eq("D"), eq("HIGH"), eq(-33.4569), eq(-70.6483))).thenReturn(ms);

        AlertDTO resultado = alertService.create("T", "D", "ALTA", -33.4569, -70.6483);

        assertThat(resultado.latitud()).isEqualTo(-33.4569);
        assertThat(resultado.longitud()).isEqualTo(-70.6483);
        verify(alertClient).create("T", "D", "HIGH", -33.4569, -70.6483);
    }

    @Test
    @DisplayName("listAlerts() debería llamar al cliente exactamente una vez")
    void listAlerts_llamaClienteUnaVez() {
        when(alertClient.listActive()).thenReturn(List.of());

        alertService.listAlerts();

        verify(alertClient, times(1)).listActive();
    }
}