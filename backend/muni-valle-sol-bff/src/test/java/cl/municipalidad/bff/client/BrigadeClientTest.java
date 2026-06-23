package cl.municipalidad.bff.client;

import cl.municipalidad.bff.dto.BrigadeDTO;
import cl.municipalidad.bff.exception.MsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de BrigadaClient, mockeando la cadena fluida de WebClient
 * ({@code .get().uri().retrieve()...}) para aislar la lógica de mapeo de
 * errores y construcción de requests sin necesitar un servidor HTTP real.
 *
 * <p>Se usan tipos crudos (raw types) deliberadamente para los specs
 * intermedios de WebClient, ya que sus interfaces genéricas
 * ({@code RequestHeadersUriSpec<S>}) no se pueden mockear con un tipo
 * parametrizado concreto sin recurrir a casts inseguros.</p>
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@ExtendWith(MockitoExtension.class)
@DisplayName("BrigadaClient - pruebas unitarias")
class BrigadeClientTest {

    @Mock
    private WebClient webClient;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock
    private WebClient.RequestBodySpec requestBodySpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;

    private BrigadeClient brigadaClient;

    private final BrigadeDTO mockBrigada = new BrigadeDTO(
            1L, "Brigada Norte", "DISPONIBLE", "INCENDIO",
            -33.45, -70.65, "jefe@municipalidad.cl", LocalDateTime.now());

    @BeforeEach
    void setUp() {
        brigadaClient = new BrigadeClient(webClient);
    }

    @Test
    @DisplayName("listAll() debería retornar la lista de brigadas cuando la respuesta es exitosa")
    void listAll_retornaListaDeBrigadas() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/api/brigadas")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(BrigadeDTO.class)).thenReturn(Flux.just(mockBrigada));

        List<BrigadeDTO> resultado = brigadaClient.listAll();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).nombre()).isEqualTo("Brigada Norte");
    }

    @Test
    @DisplayName("findById() debería retornar la brigada cuando existe")
    void findById_retornaBrigadaCuandoExiste() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/api/brigadas/{id}", 1L)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(BrigadeDTO.class)).thenReturn(Mono.just(mockBrigada));

        BrigadeDTO resultado = brigadaClient.findById(1L);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.nombre()).isEqualTo("Brigada Norte");
    }

    @Test
    @DisplayName("findById() debería propagar MsException 404 cuando la brigada no existe")
    void findById_lanzaMsException404CuandoNoExiste() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/api/brigadas/{id}", 99L)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(BrigadeDTO.class))
                .thenReturn(Mono.error(new MsException("Brigada no encontrada", HttpStatus.NOT_FOUND)));

        assertThatThrownBy(() -> brigadaClient.findById(99L))
                .isInstanceOf(MsException.class)
                .hasMessageContaining("Brigada no encontrada");
    }

    @Test
    @DisplayName("create() debería enviar el body y retornar la brigada creada")
    void create_enviaBodyYRetornaBrigadaCreada() {
        Map<String, Object> body = Map.of(
                "nombre", "Brigada Norte",
                "tipo", "INCENDIO",
                "emailResponsable", "jefe@municipalidad.cl",
                "latitud", -33.45,
                "longitud", -70.65
        );

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/api/brigadas")).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(body)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(BrigadeDTO.class)).thenReturn(Mono.just(mockBrigada));

        BrigadeDTO resultado = brigadaClient.create(body);

        assertThat(resultado.nombre()).isEqualTo("Brigada Norte");
    }

    @Test
    @DisplayName("updateEstado() debería enviar el nuevo estado y retornar la brigada actualizada")
    void updateEstado_enviaEstadoYRetornaBrigadaActualizada() {
        BrigadeDTO actualizada = new BrigadeDTO(
                1L, "Brigada Norte", "EN_CAMINO", "INCENDIO",
                -33.45, -70.65, "jefe@municipalidad.cl", LocalDateTime.now());

        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/api/brigadas/{id}/estado", 1L)).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(Map.of("estado", "EN_CAMINO"))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(BrigadeDTO.class)).thenReturn(Mono.just(actualizada));

        BrigadeDTO resultado = brigadaClient.updateEstado(1L, "EN_CAMINO");

        assertThat(resultado.estado()).isEqualTo("EN_CAMINO");
    }

    @Test
    @DisplayName("delete() debería completar sin error cuando la brigada existe")
    void delete_completaSinErrorCuandoExiste() {
        when(webClient.delete()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/api/brigadas/{id}", 1L)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.empty());

        brigadaClient.delete(1L);

        verify(requestHeadersUriSpec).uri("/api/brigadas/{id}", 1L);
    }
}