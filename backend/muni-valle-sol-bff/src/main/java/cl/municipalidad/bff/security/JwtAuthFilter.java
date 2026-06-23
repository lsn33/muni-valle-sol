package cl.municipalidad.bff.security;

import cl.municipalidad.bff.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Filtro de autenticación y autorización para rutas protegidas del BFF.
 *
 * <p>Implementa una doble capa de seguridad sobre las rutas de gestión interna
 * municipal ({@code /api/brigadas/**} y {@code /api/alertas/**}):</p>
 * <ul>
 *   <li><b>Autenticación:</b> exige una cookie {@code access_token} con un JWT
 *       válido, verificado contra la clave pública RSA de MS-Usuarios mediante
 *       {@link JwtService}.</li>
 *   <li><b>Autorización:</b> exige que el rol contenido en el token sea
 *       {@code ADMIN} o {@code FUNCIONARIO}. Un {@code CIUDADANO} autenticado
 *       no puede operar sobre brigadas ni alertas.</li>
 * </ul>
 *
 * <p>Se manejan cuatro niveles de protección distintos:</p>
 * <ul>
 *   <li><b>Solo autenticación — cualquier rol</b> ({@code POST /api/reportes},
 *       {@code GET /api/alertas}): cualquier usuario autenticado puede crear un
 *       reporte o consultar alertas.</li>
 *   <li><b>Autenticación + rol ADMIN o BRIGADISTA</b>
 *       ({@code POST /api/reportes/{id}/emitir-alerta}):
 *       solo ADMIN o BRIGADISTA pueden emitir alertas desde reportes.</li>
 *   <li><b>Solo ADMIN</b> ({@code GET /api/reportes/**},
 *       {@code PUT /api/reportes/**}, {@code DELETE /api/reportes/**}):
 *       solo el ADMIN puede leer, editar o eliminar reportes.</li>
 *   <li><b>Autenticación + rol ADMIN o FUNCIONARIO</b>
 *       ({@code /api/brigadas/**}, {@code POST/PUT/DELETE /api/alertas/**}):
 *       operaciones de escritura sobre brigadas y alertas.</li>
 * </ul>
 *
 * <p>Solo {@code /api/usuarios/register} y {@code /api/usuarios/login} quedan
 * completamente públicos.</p>
 *
 * <p><b>Códigos de respuesta:</b>
 * <ul>
 *   <li>{@code 401 Unauthorized} — no hay token, está vacío, o es inválido/expirado.</li>
 *   <li>{@code 403 Forbidden} — el token es válido pero el rol no alcanza para
 *       la ruta solicitada.</li>
 * </ul></p>
 *
 * @author Municipalidad Valle del Sol
 * @version 1.3
 * @see JwtService
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    /** Nombre de la cookie HttpOnly donde viaja el JWT, igual que en AuthController. */
    private static final String COOKIE_NAME = "access_token";

    /** Prefijos de ruta que requieren token válido Y rol ADMIN o FUNCIONARIO. */
    private static final List<String> RUTAS_CON_ROL = List.of(
            "/api/brigadas",
            "/api/alertas"
    );

    /**
     * Rutas que solo requieren autenticación (cualquier rol válido).
     * GET /api/alertas permite que ciudadanos consulten alertas activas.
     */
    private static final List<String> RUTAS_SOLO_AUTENTICACION = List.of(
            "/api/alertas"
    );

    /** Roles con permiso para operar (escritura) sobre brigadas y alertas. */
    private static final Set<String> ROLES_AUTORIZADOS = Set.of("ADMIN", "FUNCIONARIO");

    /** Roles con permiso para emitir alertas desde reportes. */
    private static final Set<String> ROLES_EMITIR_ALERTA = Set.of("ADMIN", "BRIGADISTA");

    /**
     * Intercepta cada request HTTP antes de llegar al controller.
     *
     * <p>Lógica de protección:</p>
     * <ul>
     *   <li>{@code GET /api/alertas/**}: cualquier usuario autenticado puede consultar.</li>
     *   <li>{@code POST/PUT/DELETE /api/alertas/**}: requiere rol ADMIN o FUNCIONARIO.</li>
     *   <li>{@code POST /api/reportes} exacto: cualquier usuario autenticado puede crear reportes.</li>
     *   <li>{@code POST /api/reportes/{id}/emitir-alerta}: requiere rol ADMIN o BRIGADISTA.</li>
     *   <li>Resto de {@code /api/reportes/**}: solo ADMIN puede ver/editar/eliminar.</li>
     *   <li>{@code /api/brigadas/**}: requiere rol ADMIN o FUNCIONARIO.</li>
     * </ul>
     *
     * @param request     Solicitud HTTP entrante.
     * @param response    Respuesta HTTP saliente, usada para escribir el error si corresponde.
     * @param filterChain Cadena de filtros a continuar si la validación es exitosa.
     * @throws ServletException si ocurre un error propio del servlet.
     * @throws IOException      si ocurre un error al escribir la respuesta de error.
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String path   = request.getRequestURI();
        String method = request.getMethod();

        boolean esRutaReportes        = path.startsWith("/api/reportes");
        boolean requiereRol           = RUTAS_CON_ROL.stream().anyMatch(path::startsWith);
        boolean esSoloAutenticacion   = RUTAS_SOLO_AUTENTICACION.stream().anyMatch(path::startsWith)
                                        && "GET".equalsIgnoreCase(method);

        // Ruta pública: no es reportes, no requiere rol y no es de solo autenticación.
        if (!esRutaReportes && !requiereRol) {
            filterChain.doFilter(request, response);
            return;
        }

        // --- Validación del token (obligatoria a partir de aquí) ---
        String token = extraerTokenDeCookie(request);

        if (token == null || token.isBlank()) {
            escribirError(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "No se encontró un token de autenticación. Inicie sesión para continuar.");
            return;
        }

        String email = jwtService.extractEmail(token);
        if (email == null) {
            escribirError(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "El token de autenticación es inválido o ha expirado.");
            return;
        }

        // --- GET /api/alertas: autenticado con cualquier rol ---
        if (esSoloAutenticacion) {
            filterChain.doFilter(request, response);
            return;
        }

        // --- Lógica de reportes ---
        if (esRutaReportes) {

            // POST /api/reportes exacto: cualquier usuario autenticado puede crear un reporte.
            if ("POST".equalsIgnoreCase(method) && "/api/reportes".equals(path)) {
                filterChain.doFilter(request, response);
                return;
            }

            // POST /api/reportes/{id}/emitir-alerta: requiere ADMIN o BRIGADISTA.
            if ("POST".equalsIgnoreCase(method) && path.endsWith("/emitir-alerta")) {
                String rol = jwtService.extractRol(token);
                if (rol == null || !ROLES_EMITIR_ALERTA.contains(rol)) {
                    log.warn("Acceso denegado a emitir-alerta: email={}, rol={}, ruta={}", email, rol, path);
                    escribirError(response, HttpServletResponse.SC_FORBIDDEN,
                            "No tiene permisos para emitir alertas. Se requiere rol ADMIN o BRIGADISTA.");
                    return;
                }
                filterChain.doFilter(request, response);
                return;
            }

            // GET, PUT, DELETE /api/reportes/**: solo ADMIN.
            String rol = jwtService.extractRol(token);
            if (!"ADMIN".equals(rol)) {
                log.warn("Acceso denegado a reportes por rol insuficiente: email={}, rol={}, ruta={}", email, rol, path);
                escribirError(response, HttpServletResponse.SC_FORBIDDEN,
                        "No tiene permisos para acceder a este recurso. Se requiere rol ADMIN.");
                return;
            }
            filterChain.doFilter(request, response);
            return;
        }

        // --- Brigadas y escritura en alertas: requiere ADMIN o FUNCIONARIO ---
        String rol = jwtService.extractRol(token);
        if (rol == null || !ROLES_AUTORIZADOS.contains(rol)) {
            log.warn("Acceso denegado por rol insuficiente: email={}, rol={}, ruta={}", email, rol, path);
            escribirError(response, HttpServletResponse.SC_FORBIDDEN,
                    "No tiene permisos para acceder a este recurso. Se requiere rol ADMIN o FUNCIONARIO.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el valor del token desde la cookie {@code access_token}.
     *
     * @param request Solicitud HTTP de la cual leer las cookies.
     * @return El valor del token si la cookie existe, {@code null} en caso contrario.
     */
    private String extraerTokenDeCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * Escribe una respuesta de error JSON con formato consistente al resto del BFF.
     *
     * @param response Respuesta HTTP donde escribir el error.
     * @param status   Código de estado HTTP (401 o 403).
     * @param mensaje  Mensaje descriptivo del motivo del rechazo.
     * @throws IOException si ocurre un error al escribir el cuerpo de la respuesta.
     */
    private void escribirError(HttpServletResponse response, int status, String mensaje) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        String json = String.format(
                "{\"error\":\"%s\",\"timestamp\":\"%s\",\"status\":%d}",
                mensaje, LocalDateTime.now(), status
        );
        response.getWriter().write(json);
    }
}