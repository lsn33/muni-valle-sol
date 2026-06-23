package cl.municipalidad.bff.dto;

/**
 * DTO espejo de la respuesta de login de MS-Usuarios.
 *
 * <p>Desde la migración de MS-Usuarios a JWT firmado con RSA-256, el endpoint
 * {@code POST /api/usuarios/login} retorna este contrato exacto:
 * <pre>{@code
 * {
 *   "accessToken": "eyJhbGciOiJSUzI1NiJ9...",
 *   "tokenType": "Bearer",
 *   "expiresIn": 1800
 * }
 * }</pre></p>
 *
 * @param accessToken Token JWT firmado con RSA-256 generado por MS-Usuarios.
 * @param tokenType   Tipo de token, siempre "Bearer".
 * @param expiresIn   Tiempo de expiración en segundos desde la emisión.
 *
 * @author Beltran
 * @version 2.0
 * @since 1.0
 */
public record TokenResponseDTO(
    String accessToken,
    String tokenType,
    long expiresIn
) {}