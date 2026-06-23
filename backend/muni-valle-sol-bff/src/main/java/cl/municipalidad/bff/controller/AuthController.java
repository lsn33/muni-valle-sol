package cl.municipalidad.bff.controller;

import cl.municipalidad.bff.dto.LoginRequestDTO;
import cl.municipalidad.bff.dto.LoginResponseDTO;
import cl.municipalidad.bff.dto.RegisterRequestDTO;
import cl.municipalidad.bff.dto.TokenResponseDTO;
import cl.municipalidad.bff.dto.UserDTO;
import cl.municipalidad.bff.service.AuthService;
import cl.municipalidad.bff.service.CookieService;
import cl.municipalidad.bff.service.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de autenticación del BFF.
 * Gestiona login, registro, logout y verificación de sesión mediante JWT en cookies HttpOnly.
 *
 * <p>Patrones aplicados:</p>
 * <ul>
 *   <li>Facade Pattern: simplifica acceso a autenticación</li>
 *   <li>Single Responsibility: solo maneja endpoints de auth</li>
 * </ul>
 *
 * <p>Flujo de autenticación:</p>
 * <pre>{@code
 * 1. POST /auth/login (email + password)
 *    ↓
 * 2. AuthService.login() → consulta MS-Usuarios
 *    ↓
 * 3. MS-Usuarios retorna token JWT
 *    ↓
 * 4. CookieService.setAuthCookie() → establece cookie HttpOnly
 *    ↓
 * 5. Respuesta HTTP con Set-Cookie header
 *    ↓
 * 6. Navegador almacena cookie (no accesible desde JS)
 *    ↓
 * 7. Requests posteriores incluyen cookie automáticamente
 * }</pre>
 *
 * <p><b>Seguridad de Cookies:</b></p>
 * <ul>
 *   <li>HttpOnly: impide acceso desde JavaScript (XSS protection)</li>
 *   <li>Secure: solo se envía por HTTPS</li>
 *   <li>SameSite=Strict: previene CSRF attacks</li>
 * </ul>
 *
 * <p><b>Documentación OpenAPI:</b> Ver /swagger-ui.html para ejemplos interactivos</p>
 *
 * @author Beltran
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieService cookieService;
    private final JwtService jwtService;

    /**
     * Autentica un usuario y establece la cookie HttpOnly con el JWT.
     *
     * <p>Flujo:</p>
     * <ol>
     *   <li>Se validan credenciales contra MS-Usuarios</li>
     *   <li>Se genera token JWT</li>
     *   <li>Se establece cookie HttpOnly (segura, no accesible desde JS)</li>
     *   <li>Se retorna datos del usuario autenticado</li>
     * </ol>
     *
     * <p>Nota: La cookie se establece automáticamente en la respuesta.
     * Todos los requests posteriores la envían automáticamente.</p>
     *
     * @param request DTO con email y password
     * @param response HttpServletResponse para agregar la cookie
     * @return LoginResponseDTO con datos del usuario autenticado
     *
     * <p>Ejemplo de request:</p>
     * <pre>{@code
     * POST /api/auth/login
     * Content-Type: application/json
     *
     * {
     *   "email": "juan@example.com",
     *   "password": "SecurePass123!"
     * }
     * }</pre>
     *
     * <p>Respuesta (200 OK) incluye header Set-Cookie:
     * <pre>{@code
     * access_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...;
     * HttpOnly; Secure; SameSite=Strict; Path=/
     * }</pre>
     *
     * <p><b>OpenAPI:</b> POST /auth/login - operationId: login</p>
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody LoginRequestDTO request,
            HttpServletResponse response) {

        TokenResponseDTO tokenResponse = authService.login(request);
        UserDTO user = authService.getUser(request.email());

        cookieService.setAuthCookie(response, tokenResponse.accessToken());

        return ResponseEntity.ok(new LoginResponseDTO(
                user.id(),
                user.nombre(),
                user.email(),
                user.rol()
        ));
    }

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * <p>Restricciones:</p>
     * <ul>
     *   <li>El email debe ser único</li>
     *   <li>La contraseña debe cumplir requisitos de complejidad</li>
     *   <li>Solo se pueden asignar roles USER o VIEWER</li>
     * </ul>
     *
     * @param request DTO con datos del nuevo usuario
     * @return UserDTO con los datos del usuario creado
     *
     * <p><b>OpenAPI:</b> POST /auth/register - operationId: register</p>
     */
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody RegisterRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    /**
     * Cierra la sesión del usuario limpiando la cookie de autenticación.
     *
     * <p>Comportamiento:</p>
     * <ul>
     *   <li>La cookie se elimina del navegador (maxAge=0)</li>
     *   <li>Los requests posteriores sin cookie fallarán con 401</li>
     * </ul>
     *
     * @param response HttpServletResponse para limpiar la cookie
     * @return respuesta sin contenido (204 No Content)
     *
     * <p><b>OpenAPI:</b> POST /auth/logout - operationId: logout</p>
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        cookieService.clearAuthCookie(response);
        return ResponseEntity.noContent().build();
    }

    /**
     * Verifica la sesión activa del usuario mediante la cookie JWT.
     *
     * <p>Flujo:</p>
     * <ol>
     *   <li>Extrae el token de la cookie HttpOnly</li>
     *   <li>Valida y extrae el email del token</li>
     *   <li>Retorna datos del usuario si es válido</li>
     *   <li>Retorna 401 si token no existe o está expirado</li>
     * </ol>
     *
     * @param token token JWT extraído de la cookie HttpOnly
     * @return UserDTO con los datos del usuario autenticado
     *
     * <p><b>OpenAPI:</b> GET /auth/me - operationId: getCurrentUser</p>
     */
    @GetMapping("/me")
    public ResponseEntity<UserDTO> me(
            @CookieValue(name = "access_token", required = false) String token) {

        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = jwtService.extractEmail(token);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(authService.getUser(email));
    }
}