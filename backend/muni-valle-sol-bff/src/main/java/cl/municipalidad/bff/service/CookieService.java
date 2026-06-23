package cl.municipalidad.bff.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

/**
 * Servicio para gestion de cookies de autenticacion.
 * Encapsula la logica de creacion y eliminacion de cookies HttpOnly.
 *
 * <p>Patrones aplicados:</p>
 * <ul>
 *   <li>Single Responsibility: solo gestiona cookies</li>
 *   <li>Singleton: bean de Spring con instancia unica</li>
 * </ul>
 *
 * <p><b>Nota sobre {@code secure}:</b> el atributo {@code Secure} de la cookie
 * obliga al navegador a enviarla solo por HTTPS. En desarrollo local (HTTP)
 * esto bloquea silenciosamente la cookie y produce 401 en endpoints protegidos.
 * Por eso es configurable: {@code false} en local, {@code true} en producción
 * (donde el tráfico siempre es HTTPS).</p>
 *
 * @author Beltran
 * @version 1.1
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class CookieService {

    @Value("${jwt.cookie.name}")
    private String cookieName;

    @Value("${jwt.cookie.maxage}")
    private int cookieMaxAge;

    @Value("${jwt.cookie.secure:false}")
    private boolean cookieSecure;

    /**
     * Establece la cookie HttpOnly con el token JWT en la respuesta.
     *
     * @param response HttpServletResponse donde se agrega la cookie
     * @param token    token JWT a almacenar en la cookie
     */
    public void setAuthCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = buildCookie(token, cookieMaxAge);
        response.addHeader("Set-Cookie", cookie.toString());
    }

    /**
     * Elimina la cookie de autenticacion estableciendo su maxAge a 0.
     *
     * @param response HttpServletResponse donde se limpia la cookie
     */
    public void clearAuthCookie(HttpServletResponse response) {
        ResponseCookie cookie = buildCookie("", 0);
        response.addHeader("Set-Cookie", cookie.toString());
    }

    /**
     * Construye una cookie con los atributos de seguridad correctos.
     *
     * <p>{@code secure} se lee desde {@code jwt.cookie.secure}: {@code false}
     * por defecto (desarrollo local con HTTP), {@code true} en producción
     * vía variable de entorno (HTTPS real).</p>
     *
     * @param value  valor de la cookie
     * @param maxAge tiempo de vida en segundos
     * @return ResponseCookie configurada
     */
    private ResponseCookie buildCookie(String value, int maxAge) {
        return ResponseCookie.from(cookieName, value)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSecure ? "Strict" : "Lax")
                .path("/")
                .maxAge(maxAge)
                .build();
    }
}