package cl.municipalidad.bff.service;

import cl.municipalidad.bff.client.UserClient;
import cl.municipalidad.bff.dto.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserClient userClient;

    @CircuitBreaker(name = "ms-usuarios", fallbackMethod = "loginFallback")
    public TokenResponseDTO login(LoginRequestDTO request) {
        return userClient.login(request);
    }

    @CircuitBreaker(name = "ms-usuarios", fallbackMethod = "registerFallback")
    public UserDTO register(RegisterRequestDTO request) {
        return userClient.register(request);
    }

    @CircuitBreaker(name = "ms-usuarios", fallbackMethod = "getUserFallback")
    public UserDTO getUser(String email) {
        return userClient.findByEmail(email);
    }

    // ─── Fallbacks ────────────────────────────────────────────────────────────

    public TokenResponseDTO loginFallback(LoginRequestDTO request, Throwable ex) {
        log.warn("[CircuitBreaker] ms-usuarios abierto – login: {}", ex.getMessage());
        throw new RuntimeException("Servicio de autenticación no disponible temporalmente");
    }

    public UserDTO registerFallback(RegisterRequestDTO request, Throwable ex) {
        log.warn("[CircuitBreaker] ms-usuarios abierto – register: {}", ex.getMessage());
        throw new RuntimeException("Servicio de usuarios no disponible temporalmente");
    }

    public UserDTO getUserFallback(String email, Throwable ex) {
        log.warn("[CircuitBreaker] ms-usuarios abierto – getUser: {}", ex.getMessage());
        throw new RuntimeException("Servicio de usuarios no disponible temporalmente");
    }
}