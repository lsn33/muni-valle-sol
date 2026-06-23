package cl.municipalidad.msbrigadas.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de seguridad HTTP para el microservicio de brigadas.
 *
 * <p>Define la política de autenticación y autorización usando Spring Security.
 * Al ser un servicio REST sin estado (stateless), se desactivan las sesiones
 * HTTP y la protección CSRF (no aplica para APIs JSON).</p>
 *
 * <p><b>Endpoints públicos</b> (sin token requerido):
 * <ul>
 *   <li>{@code POST /api/brigadas} — registrar nueva brigada</li>
 *   <li>{@code GET /api/brigadas} — listar todas las brigadas</li>
 *   <li>{@code GET /api/brigadas/disponibles} — listar brigadas disponibles</li>
 *   <li>{@code GET /api/brigadas/tipo/**} — listar por tipo</li>
 *   <li>{@code GET /api/brigadas/**} — buscar brigada por ID</li>
 *   <li>{@code PUT /api/brigadas/**} — actualizar estado o ubicación</li>
 *   <li>{@code DELETE /api/brigadas/**} — eliminar brigada</li>
 *   <li>{@code GET /actuator/health} — health check del servicio</li>
 * </ul></p>
 *
 * <p>Cualquier otro endpoint no listado es denegado ({@code denyAll}).</p>
 *
 * @author Municipalidad Valle del Sol
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Define la cadena de filtros de seguridad HTTP.
     *
     * <p>Configuración aplicada:
     * <ul>
     *   <li>CSRF desactivado: no necesario en APIs REST sin sesión.</li>
     *   <li>Sesiones STATELESS: cada request debe ser autónomo.</li>
     *   <li>Todos los endpoints de brigadas permitidos sin token.</li>
     *   <li>Todo lo demás denegado explícitamente.</li>
     * </ul></p>
     *
     * @param http Objeto de configuración de seguridad HTTP inyectado por Spring.
     * @return {@link SecurityFilterChain} construida con la configuración definida.
     * @throws Exception si ocurre un error en la configuración de seguridad.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/api/brigadas").permitAll()
                .requestMatchers(HttpMethod.GET,
                    "/api/brigadas",
                    "/api/brigadas/disponibles",
                    "/api/brigadas/tipo/**",
                    "/api/brigadas/**",
                    "/actuator/health",
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs",
                    "/v3/api-docs/**"

                ).permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/brigadas/**").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/api/brigadas/**").permitAll()
                .anyRequest().denyAll()
            );
        return http.build();
    }
}