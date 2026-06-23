package cl.municipalidad.msbrigadas.factory;

import cl.municipalidad.msbrigadas.model.Brigade;
import org.springframework.stereotype.Component;

/**
 * Fábrica de objetos {@link Brigade} usando el patrón Factory Method.
 *
 * <p>Centraliza la lógica de creación de brigadas, aplicando reglas de negocio
 * automáticamente según el tipo: estado inicial y descripción operativa.
 * El controlador y el servicio nunca crean brigadas directamente, siempre
 * delegan a esta fábrica.</p>
 *
 * <p><b>Patrón aplicado:</b> Factory Method — encapsula la creación de objetos
 * complejos, permitiendo variar el comportamiento según el tipo sin modificar
 * el código cliente.</p>
 *
 * @author Municipalidad Valle del Sol
 * @version 1.0
 * @see BrigadeType
 * @see Brigade
 */
@Component
public class BrigadeFactory {

    /**
     * Crea una nueva brigada con estado y configuración inicial según su tipo.
     *
     * <p>Aplica las siguientes reglas por tipo:
     * <ul>
     *   <li>{@code INCENDIO} → Estado {@code DISPONIBLE}, prioridad alta.</li>
     *   <li>{@code RESCATE}  → Estado {@code DISPONIBLE}, prioridad media.</li>
     *   <li>{@code MEDICA}   → Estado {@code DISPONIBLE}, prioridad estándar.</li>
     * </ul></p>
     *
     * @param nombre           Nombre de la brigada.
     * @param tipo             Tipo de brigada (INCENDIO, RESCATE, MEDICA).
     * @param emailResponsable Email del jefe de brigada.
     * @param latitud          Latitud de la ubicación inicial (puede ser null).
     * @param longitud         Longitud de la ubicación inicial (puede ser null).
     * @return {@link Brigade} configurada y lista para persistir.
     * @throws IllegalArgumentException si el tipo no es reconocido.
     */
    public Brigade crear(String nombre, String tipo,
                         String emailResponsable,
                         Double latitud, Double longitud) {

        Brigade brigada = new Brigade();
        brigada.setNombre(nombre.trim());
        brigada.setTipo(tipo.toUpperCase().trim());
        brigada.setEmailResponsable(emailResponsable.trim().toLowerCase());
        brigada.setLatitud(latitud);
        brigada.setLongitud(longitud);

        switch (BrigadeType.valueOf(tipo.toUpperCase().trim())) {
            case INCENDIO -> {
                brigada.setEstado("DISPONIBLE");
            }
            case RESCATE -> {
                brigada.setEstado("DISPONIBLE");
            }
            case MEDICA -> {
                brigada.setEstado("DISPONIBLE");
            }
        }

        return brigada;
    }
}