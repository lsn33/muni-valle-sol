package cl.municipalidad.bff.service;

import cl.municipalidad.bff.client.UserClient;
import cl.municipalidad.bff.dto.LoginRequestDTO;
import cl.municipalidad.bff.dto.RegisterRequestDTO;
import cl.municipalidad.bff.dto.TokenResponseDTO;
import cl.municipalidad.bff.dto.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - pruebas unitarias")
class AuthServiceTest {

    @Mock
    private UserClient userClient;

    @InjectMocks
    private AuthService authService;

    private final TokenResponseDTO mockToken = new TokenResponseDTO("jwt-token-mock", "Bearer", 1800L);
    private final UserDTO mockUser = new UserDTO(1L, "Juan Pérez", "juan@gmail.com", "CIUDADANO", true);

    @Test
    @DisplayName("login() debería delegar en UserClient y retornar el token")
    void login_delegaEnClienteYRetornaToken() {
        LoginRequestDTO request = new LoginRequestDTO("juan@gmail.com", "Segura123!");
        when(userClient.login(request)).thenReturn(mockToken);

        TokenResponseDTO resultado = authService.login(request);

        assertThat(resultado.accessToken()).isEqualTo("jwt-token-mock");
        assertThat(resultado.tokenType()).isEqualTo("Bearer");
        assertThat(resultado.expiresIn()).isEqualTo(1800L);
        verify(userClient, times(1)).login(request);
    }

    @Test
    @DisplayName("login() debería llamar al cliente exactamente una vez")
    void login_llamaClienteUnaVez() {
        LoginRequestDTO request = new LoginRequestDTO("juan@gmail.com", "Segura123!");
        when(userClient.login(request)).thenReturn(mockToken);

        authService.login(request);

        verify(userClient, times(1)).login(request);
        verifyNoMoreInteractions(userClient);
    }

    @Test
    @DisplayName("register() debería delegar en UserClient y retornar el usuario creado")
    void register_delegaYRetornaUsuario() {
        RegisterRequestDTO request = new RegisterRequestDTO("Juan Pérez", "juan@gmail.com", "Segura123!", "CIUDADANO");
        when(userClient.register(request)).thenReturn(mockUser);

        UserDTO resultado = authService.register(request);

        assertThat(resultado.email()).isEqualTo("juan@gmail.com");
        assertThat(resultado.rol()).isEqualTo("CIUDADANO");
        verify(userClient).register(request);
    }

    @Test
    @DisplayName("register() debería retornar el usuario con todos sus campos")
    void register_retornaUsuarioCompleto() {
        RegisterRequestDTO request = new RegisterRequestDTO("Juan Pérez", "juan@gmail.com", "Segura123!", "CIUDADANO");
        when(userClient.register(request)).thenReturn(mockUser);

        UserDTO resultado = authService.register(request);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.nombre()).isEqualTo("Juan Pérez");
        assertThat(resultado.activo()).isTrue();
    }

    @Test
    @DisplayName("getUser() debería retornar el usuario correspondiente al email dado")
    void getUser_retornaUsuarioPorEmail() {
        when(userClient.findByEmail("juan@gmail.com")).thenReturn(mockUser);

        UserDTO resultado = authService.getUser("juan@gmail.com");

        assertThat(resultado.email()).isEqualTo("juan@gmail.com");
        assertThat(resultado.nombre()).isEqualTo("Juan Pérez");
        verify(userClient).findByEmail("juan@gmail.com");
    }

    @Test
    @DisplayName("getUser() debería retornar el rol correcto del usuario")
    void getUser_retornaRolCorrecto() {
        when(userClient.findByEmail("admin@gmail.com")).thenReturn(
                new UserDTO(2L, "Admin", "admin@gmail.com", "ADMIN", true));

        UserDTO resultado = authService.getUser("admin@gmail.com");

        assertThat(resultado.rol()).isEqualTo("ADMIN");
        verify(userClient).findByEmail("admin@gmail.com");
    }

    @Test
    @DisplayName("getUser() debería llamar al cliente exactamente una vez con el email exacto")
    void getUser_llamaClienteUnaVezConEmailExacto() {
        when(userClient.findByEmail("juan@gmail.com")).thenReturn(mockUser);

        authService.getUser("juan@gmail.com");

        verify(userClient, times(1)).findByEmail("juan@gmail.com");
        verifyNoMoreInteractions(userClient);
    }
}