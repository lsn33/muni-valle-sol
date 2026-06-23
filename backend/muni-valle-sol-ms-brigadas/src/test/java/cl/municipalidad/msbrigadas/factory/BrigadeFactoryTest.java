package cl.municipalidad.msbrigadas.factory;

import cl.municipalidad.msbrigadas.model.Brigade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Pruebas unitarias para {@link BrigadeFactory}.
 *
 * <p>Verifica que la fábrica cree brigadas con el estado correcto
 * según el tipo, siguiendo el patrón Factory Method.</p>
 *
 * @author Municipalidad Valle del Sol
 * @version 1.0
 */
@DisplayName("BrigadaFactory - Pruebas Unitarias")
class BrigadeFactoryTest {

    private BrigadeFactory brigadaFactory;

    @BeforeEach
    void setUp() {
        brigadaFactory = new BrigadeFactory();
    }

    @Test
    @DisplayName("crear INCENDIO - debería crear brigada con estado DISPONIBLE")
    void crear_tipoIncendio_estadoDisponible() {
        Brigade brigada = brigadaFactory.crear(
            "Brigada Norte", "INCENDIO", "jefe@municipalidad.cl", -33.45, -70.65
        );

        assertThat(brigada).isNotNull();
        assertThat(brigada.getNombre()).isEqualTo("Brigada Norte");
        assertThat(brigada.getTipo()).isEqualTo("INCENDIO");
        assertThat(brigada.getEstado()).isEqualTo("DISPONIBLE");
        assertThat(brigada.getEmailResponsable()).isEqualTo("jefe@municipalidad.cl");
    }

    @Test
    @DisplayName("crear RESCATE - debería crear brigada con estado DISPONIBLE")
    void crear_tipoRescate_estadoDisponible() {
        Brigade brigada = brigadaFactory.crear(
            "Brigada Rescate", "RESCATE", "rescate@municipalidad.cl", -33.45, -70.65
        );

        assertThat(brigada.getTipo()).isEqualTo("RESCATE");
        assertThat(brigada.getEstado()).isEqualTo("DISPONIBLE");
    }

    @Test
    @DisplayName("crear MEDICA - debería crear brigada con estado DISPONIBLE")
    void crear_tipoMedica_estadoDisponible() {
        Brigade brigada = brigadaFactory.crear(
            "Brigada Médica", "MEDICA", "medica@municipalidad.cl", null, null
        );

        assertThat(brigada.getTipo()).isEqualTo("MEDICA");
        assertThat(brigada.getEstado()).isEqualTo("DISPONIBLE");
        assertThat(brigada.getLatitud()).isNull();
        assertThat(brigada.getLongitud()).isNull();
    }

    @Test
    @DisplayName("crear - debería normalizar nombre y email (trim y lowercase)")
    void crear_normalizaDatos() {
        Brigade brigada = brigadaFactory.crear(
            "  Brigada Sur  ", "INCENDIO", "  JEFE@municipalidad.cl  ", -33.45, -70.65
        );

        assertThat(brigada.getNombre()).isEqualTo("Brigada Sur");
        assertThat(brigada.getEmailResponsable()).isEqualTo("jefe@municipalidad.cl");
    }

    @Test
    @DisplayName("crear - debería lanzar excepción si tipo es inválido")
    void crear_tipoInvalido_lanzaExcepcion() {
        assertThatThrownBy(() -> brigadaFactory.crear(
            "Brigada Test", "INVALIDO", "test@municipalidad.cl", null, null
        )).isInstanceOf(IllegalArgumentException.class);
    }
}