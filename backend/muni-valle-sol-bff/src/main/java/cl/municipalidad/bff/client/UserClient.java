package cl.municipalidad.bff.client;

import cl.municipalidad.bff.dto.LoginRequestDTO;
import cl.municipalidad.bff.dto.RegisterRequestDTO;
import cl.municipalidad.bff.dto.TokenResponseDTO;
import cl.municipalidad.bff.dto.UserDTO;
import cl.municipalidad.bff.exception.MsException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Cliente HTTP para el MS-Usuarios.
 * Encapsula todas las llamadas al microservicio de usuarios via WebClient.
 *
 * <p>Patrones aplicados:</p>
 * <ul>
 *   <li>Gateway Pattern: punto unico de acceso al MS-Usuarios</li>
 *   <li>Single Responsibility: solo gestiona comunicacion con MS-Usuarios</li>
 * </ul>
 *
 * @author Beltran
 * @version 1.0
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class UserClient {

    @Qualifier("msUsuariosClient")
    private final WebClient msUsuariosClient;

    /**
     * Autentica un usuario y obtiene el token JWT.
     *
     * @param request DTO con email y password del usuario
     * @return TokenResponseDTO con el token JWT generado
     * @throws MsException si las credenciales son incorrectas (401)
     */
    public TokenResponseDTO login(LoginRequestDTO request) {
        return msUsuariosClient.post()
                .uri("/api/usuarios/login")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatus.UNAUTHORIZED::equals,
                    response -> response.bodyToMono(String.class)
                        .map(body -> new MsException("Credenciales incorrectas", HttpStatus.UNAUTHORIZED)))
                .bodyToMono(TokenResponseDTO.class)
                .block();
    }

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param request DTO con los datos del nuevo usuario
     * @return UserDTO con los datos del usuario registrado
     * @throws MsException si los datos son invalidos (400) o el email ya existe (409)
     */
    public UserDTO register(RegisterRequestDTO request) {
        return msUsuariosClient.post()
                .uri("/api/usuarios/register")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatus.BAD_REQUEST::equals,
                    response -> response.bodyToMono(String.class)
                        .map(body -> new MsException("Error al registrar usuario", HttpStatus.BAD_REQUEST)))
                .onStatus(HttpStatus.CONFLICT::equals,
                    response -> response.bodyToMono(String.class)
                        .map(body -> new MsException("El email ya esta registrado", HttpStatus.CONFLICT)))
                .bodyToMono(UserDTO.class)
                .block();
    }

    /**
     * Busca un usuario por su email.
     *
     * @param email email del usuario a buscar
     * @return UserDTO con los datos del usuario
     * @throws MsException si el usuario no existe (404)
     */
    public UserDTO findByEmail(String email) {
        return msUsuariosClient.get()
                .uri("/api/usuarios/email/{email}", email)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals,
                    response -> response.bodyToMono(String.class)
                        .map(body -> new MsException("Usuario no encontrado", HttpStatus.NOT_FOUND)))
                .bodyToMono(UserDTO.class)
                .block();
    }
}
