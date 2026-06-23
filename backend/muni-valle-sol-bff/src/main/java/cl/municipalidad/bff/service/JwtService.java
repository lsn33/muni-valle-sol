package cl.municipalidad.bff.service;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

/**
 * Servicio de validación de tokens JWT emitidos por MS-Usuarios.
 *
 * <p>MS-Usuarios firma los tokens con su clave privada RSA (RS256). El BFF
 * nunca tiene esa clave privada; en su lugar consulta el endpoint público
 * {@code GET /.well-known/jwks.json} de MS-Usuarios para obtener la clave
 * pública y verificar la firma localmente, sin red adicional en cada request.</p>
 *
 * <p><b>Patrón aplicado:</b> Cache local de la clave pública, refrescada
 * periódicamente, para no depender de MS-Usuarios en cada validación.</p>
 *
 * @author Municipalidad Valle del Sol
 * @version 2.0
 */
@Slf4j
@Service
public class JwtService {

    @Value("${ms.usuarios.url}")
    private String msUsuariosUrl;

    private volatile RSAKey cachedPublicKey;

    /**
     * Obtiene la clave pública RSA desde el endpoint JWKS de MS-Usuarios.
     *
     * <p>Se cachea en memoria para no consultar MS-Usuarios en cada request.
     * Se refresca automáticamente cada 30 minutos via {@code @Scheduled}.</p>
     *
     * @return Clave pública RSA cacheada, o null si nunca se pudo obtener.
     */
    private RSAKey getPublicKey() {
        if (cachedPublicKey == null) {
            refreshPublicKey();
        }
        return cachedPublicKey;
    }

    /**
     * Refresca la clave pública consultando el JWKS de MS-Usuarios.
     *
     * <p>Se ejecuta automáticamente cada 30 minutos para soportar rotación
     * de claves sin reiniciar el BFF.</p>
     */
    @Scheduled(fixedRate = 1800000)
    public void refreshPublicKey() {
        try {
            String jwksJson = WebClient.create(msUsuariosUrl)
                    .get()
                    .uri("/.well-known/jwks.json")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JWKSet jwkSet = JWKSet.parse(jwksJson);
            cachedPublicKey = (RSAKey) jwkSet.getKeys().get(0);
            log.info("Clave pública RSA de MS-Usuarios actualizada correctamente");
        } catch (Exception e) {
            log.warn("No se pudo refrescar la clave pública de MS-Usuarios: {}", e.getMessage());
        }
    }

    /**
     * Extrae el email (subject) del token JWT, validando previamente su firma.
     *
     * @param token Token JWT en formato compacto (header.payload.signature).
     * @return Email del usuario si el token es válido y no expiró, o null en caso contrario.
     */
    public String extractEmail(String token) {
        Map<String, Object> claims = validateAndGetClaims(token);
        return claims != null ? (String) claims.get("sub") : null;
    }

    /**
     * Extrae el rol del usuario desde los claims del token.
     *
     * @param token Token JWT en formato compacto.
     * @return Rol del usuario (ej: ADMIN, FUNCIONARIO, CIUDADANO), o null si el token no es válido.
     */
    public String extractRol(String token) {
        Map<String, Object> claims = validateAndGetClaims(token);
        return claims != null ? (String) claims.get("rol") : null;
    }

    /**
     * Valida si un token JWT es válido (firma correcta y no expirado).
     *
     * @param token Token JWT a validar.
     * @return true si el token es válido, false en caso contrario.
     */
    public boolean isTokenValid(String token) {
        return validateAndGetClaims(token) != null;
    }

    /**
     * Verifica la firma RSA del token contra la clave pública de MS-Usuarios
     * y comprueba que no haya expirado.
     *
     * <p>Si la verificación falla con la clave cacheada (por ejemplo, porque
     * MS-Usuarios rotó sus claves), intenta refrescar una vez antes de fallar.</p>
     *
     * @param token Token JWT en formato compacto.
     * @return Mapa de claims del token si es válido, o null si la firma es
     *         inválida, expiró, o el token está malformado.
     */
    private Map<String, Object> validateAndGetClaims(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            RSAKey publicKey = getPublicKey();

            if (publicKey == null) {
                log.warn("No hay clave pública disponible para validar el token");
                return null;
            }

            JWSVerifier verifier = new RSASSAVerifier(publicKey.toRSAPublicKey());

            if (!signedJWT.verify(verifier)) {
                refreshPublicKey();
                RSAKey refreshedKey = getPublicKey();
                if (refreshedKey == null || !signedJWT.verify(new RSASSAVerifier(refreshedKey.toRSAPublicKey()))) {
                    log.warn("Firma de token inválida");
                    return null;
                }
            }

            Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (expiration == null || expiration.before(new Date())) {
                log.warn("Token expirado");
                return null;
            }

            return signedJWT.getJWTClaimsSet().getClaims();

        } catch (ParseException e) {
            log.warn("Token JWT malformado: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.warn("Error validando token: {}", e.getMessage());
            return null;
        }
    }
}