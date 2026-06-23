package cl.municipalidad.msreport.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de seguridad HTTP para el microservicio de reportes.
 *
 * <p>Define la política de autenticación y autorización usando Spring Security.
 * Al ser un servicio REST sin estado (stateless), se desactivan las sesiones HTTP
 * y la protección CSRF (no aplica para APIs JSON).</p>
 *
 * <p>Patrones aplicados:</p>
 * <ul>
 *   <li>Security by Design: configuración explícita, nada habilitado por defecto</li>
 *   <li>Stateless Architecture: sin sesión HTTP, cada request es autónomo</li>
 *   <li>Defense in Depth: la autenticación real ocurre en el BFF antes de llegar aquí</li>
 * </ul>
 *
 * <p>Modelo de seguridad del sistema:</p>
 * <pre>{@code
 * Internet
 *   ↓ JWT validado
 * BFF (ms-bff)
 *   ↓ red interna del clúster
 * ms-reportes (este servicio)
 *   → permitAll() en /api/reportes/**
 * }</pre>
 *
 * @author Beltran
 * @version 1.0
 * @since 1.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Define la cadena de filtros de seguridad HTTP.
     *
     * <p>Configuración aplicada:</p>
     * <ul>
     *   <li>CSRF desactivado: no necesario en APIs REST sin sesión.</li>
     *   <li>Sesiones STATELESS: cada request debe ser autónomo.</li>
     *   <li>Todos los endpoints de reportes permitidos sin token.</li>
     * </ul>
     *
     * @param http Objeto de configuración de seguridad HTTP inyectado por Spring.
     * @return {@link SecurityFilterChain} construida con la configuración definida.
     * @throws Exception si ocurre un error en la configuración de seguridad.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/reportes/**").permitAll()
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
                ).permitAll()
                .anyRequest().authenticated()
            );
        return http.build();
    }
}