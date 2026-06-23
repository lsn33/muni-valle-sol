package cl.municipalidad.bff.security;

import cl.municipalidad.bff.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de JwtAuthFilter, cubriendo los cuatro niveles de
 * protección: rutas públicas, solo autenticación, autenticación + rol
 * ADMIN/FUNCIONARIO, y autenticación + rol ADMIN/BRIGADISTA.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthFilter - pruebas unitarias")
class JwtAuthFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    // ─── Rutas públicas ─────────────────────────────────────────────────────

    @Test
    @DisplayName("Ruta pública (login) debería dejar pasar sin validar token")
    void rutaPublica_dejaPasarSinValidarToken() throws Exception {
        request.setRequestURI("/api/usuarios/login");
        request.setMethod("POST");

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtService);
    }

    // ─── Solo autenticación (GET /api/alertas) ─────────────────────────────

    @Test
    @DisplayName("GET /api/alertas sin token debería retornar 401")
    void getAlertas_sinToken_retorna401() throws Exception {
        request.setRequestURI("/api/alertas");
        request.setMethod("GET");

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(401);
        verifyNoInteractions(filterChain);
    }

    @Test
    @DisplayName("GET /api/alertas con token válido de cualquier rol debería dejar pasar")
    void getAlertas_conTokenValido_dejaPasar() throws Exception {
        request.setRequestURI("/api/alertas");
        request.setMethod("GET");
        request.setCookies(new Cookie("access_token", "token-valido"));
        when(jwtService.extractEmail("token-valido")).thenReturn("ciudadano@test.cl");

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtService, never()).extractRol(any());
    }

    @Test
    @DisplayName("GET /api/alertas con token inválido debería retornar 401")
    void getAlertas_conTokenInvalido_retorna401() throws Exception {
        request.setRequestURI("/api/alertas");
        request.setMethod("GET");
        request.setCookies(new Cookie("access_token", "token-invalido"));
        when(jwtService.extractEmail("token-invalido")).thenReturn(null);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(401);
        verifyNoInteractions(filterChain);
    }

    // ─── Reportes: POST exacto = cualquier autenticado ─────────────────────

    @Test
    @DisplayName("POST /api/reportes sin token debería retornar 401")
    void postReportes_sinToken_retorna401() throws Exception {
        request.setRequestURI("/api/reportes");
        request.setMethod("POST");

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(401);
        verifyNoInteractions(filterChain);
    }

    @Test
    @DisplayName("POST /api/reportes con token válido de CIUDADANO debería dejar pasar")
    void postReportes_conCiudadanoAutenticado_dejaPasar() throws Exception {
        request.setRequestURI("/api/reportes");
        request.setMethod("POST");
        request.setCookies(new Cookie("access_token", "token-ciudadano"));
        when(jwtService.extractEmail("token-ciudadano")).thenReturn("ciudadano@test.cl");

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    // ─── Reportes: GET/PUT/DELETE = solo ADMIN ─────────────────────────────

    @Test
    @DisplayName("GET /api/reportes con rol ADMIN debería dejar pasar")
    void getReportes_conRolAdmin_dejaPasar() throws Exception {
        request.setRequestURI("/api/reportes");
        request.setMethod("GET");
        request.setCookies(new Cookie("access_token", "token-admin"));
        when(jwtService.extractEmail("token-admin")).thenReturn("admin@municipalidad.cl");
        when(jwtService.extractRol("token-admin")).thenReturn("ADMIN");

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("GET /api/reportes con rol CIUDADANO debería retornar 403")
    void getReportes_conRolCiudadano_retorna403() throws Exception {
        request.setRequestURI("/api/reportes");
        request.setMethod("GET");
        request.setCookies(new Cookie("access_token", "token-ciudadano"));
        when(jwtService.extractEmail("token-ciudadano")).thenReturn("ciudadano@test.cl");
        when(jwtService.extractRol("token-ciudadano")).thenReturn("CIUDADANO");

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(403);
        verifyNoInteractions(filterChain);
    }

    // ─── Reportes: emitir-alerta = ADMIN o BRIGADISTA ──────────────────────

    @Test
    @DisplayName("POST /api/reportes/{id}/emitir-alerta con rol BRIGADISTA debería dejar pasar")
    void emitirAlerta_conRolBrigadista_dejaPasar() throws Exception {
        request.setRequestURI("/api/reportes/5/emitir-alerta");
        request.setMethod("POST");
        request.setCookies(new Cookie("access_token", "token-brigadista"));
        when(jwtService.extractEmail("token-brigadista")).thenReturn("brigadista@municipalidad.cl");
        when(jwtService.extractRol("token-brigadista")).thenReturn("BRIGADISTA");

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("POST /api/reportes/{id}/emitir-alerta con rol CIUDADANO debería retornar 403")
    void emitirAlerta_conRolCiudadano_retorna403() throws Exception {
        request.setRequestURI("/api/reportes/5/emitir-alerta");
        request.setMethod("POST");
        request.setCookies(new Cookie("access_token", "token-ciudadano"));
        when(jwtService.extractEmail("token-ciudadano")).thenReturn("ciudadano@test.cl");
        when(jwtService.extractRol("token-ciudadano")).thenReturn("CIUDADANO");

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(403);
        verifyNoInteractions(filterChain);
    }

    // ─── Brigadas: requiere ADMIN o FUNCIONARIO ────────────────────────────

    @Test
    @DisplayName("POST /api/brigadas sin token debería retornar 401")
    void postBrigadas_sinToken_retorna401() throws Exception {
        request.setRequestURI("/api/brigadas");
        request.setMethod("POST");

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(401);
        verifyNoInteractions(filterChain);
    }

    @Test
    @DisplayName("POST /api/brigadas con rol FUNCIONARIO debería dejar pasar")
    void postBrigadas_conRolFuncionario_dejaPasar() throws Exception {
        request.setRequestURI("/api/brigadas");
        request.setMethod("POST");
        request.setCookies(new Cookie("access_token", "token-funcionario"));
        when(jwtService.extractEmail("token-funcionario")).thenReturn("juan@municipalidad.cl");
        when(jwtService.extractRol("token-funcionario")).thenReturn("FUNCIONARIO");

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("POST /api/brigadas con rol CIUDADANO debería retornar 403")
    void postBrigadas_conRolCiudadano_retorna403() throws Exception {
        request.setRequestURI("/api/brigadas");
        request.setMethod("POST");
        request.setCookies(new Cookie("access_token", "token-ciudadano"));
        when(jwtService.extractEmail("token-ciudadano")).thenReturn("ciudadano@test.cl");
        when(jwtService.extractRol("token-ciudadano")).thenReturn("CIUDADANO");

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(403);
        verifyNoInteractions(filterChain);
    }

    @Test
    @DisplayName("POST /api/brigadas con token inválido debería retornar 401, no 403")
    void postBrigadas_conTokenInvalido_retorna401() throws Exception {
        request.setRequestURI("/api/brigadas");
        request.setMethod("POST");
        request.setCookies(new Cookie("access_token", "token-malformado"));
        when(jwtService.extractEmail("token-malformado")).thenReturn(null);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(401);
        verify(jwtService, never()).extractRol(any());
    }

    @Test
    @DisplayName("Cookie vacía en ruta protegida debería retornar 401")
    void rutaProtegida_conCookieVacia_retorna401() throws Exception {
        request.setRequestURI("/api/brigadas");
        request.setMethod("POST");
        request.setCookies(new Cookie("access_token", ""));

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(401);
        verifyNoInteractions(jwtService);
        verifyNoInteractions(filterChain);
    }
}