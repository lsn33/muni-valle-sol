package cl.municipalidad.msreport.factory;

import cl.municipalidad.msreport.dto.CreateReportRequest;
import cl.municipalidad.msreport.model.Report;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Pruebas unitarias para {@link ReportFactory}.
 *
 * <p>Verifica que el patrón Factory Method aplica correctamente las reglas
 * de negocio al crear entidades {@link Report}: estado inicial, etiqueta de
 * prioridad, normalización del tipo y campos comunes.</p>
 *
 * <p>Patrones aplicados:</p>
 * <ul>
 *   <li>Arrange-Act-Assert: estructura clara en cada test</li>
 *   <li>Test Isolation: instancia nueva del factory en cada test via {@code @BeforeEach}</li>
 *   <li>Descriptive Naming: nombre del test describe escenario y resultado esperado</li>
 * </ul>
 *
 * <p>Cobertura de escenarios:</p>
 * <pre>{@code
 * INCENDIO   → ACTIVO      + [PRIORIDAD ALTA]   ✓
 * HUMO       → EN_REVISION + [PRIORIDAD MEDIA]  ✓
 * SOSPECHOSO → PENDIENTE   + [PRIORIDAD BAJA]   ✓
 * incendio   → normaliza a INCENDIO             ✓
 * campos comunes asignados correctamente        ✓
 * tipo inválido → IllegalArgumentException      ✓
 * }</pre>
 *
 * @author Beltran
 * @version 1.0
 * @since 1.0
 * @see ReportFactory
 */
@DisplayName("ReportFactory - Pruebas unitarias (Factory Method)")
class ReportFactoryTest {

    private ReportFactory reportFactory;

    /**
     * Inicializa una instancia limpia del factory antes de cada test.
     * No requiere Spring context ya que {@link ReportFactory} no tiene dependencias.
     */
    @BeforeEach
    void setUp() {
        reportFactory = new ReportFactory();
    }

    /**
     * Verifica que un reporte de tipo INCENDIO recibe estado ACTIVO y etiqueta de prioridad alta.
     */
    @Test
    @DisplayName("crear INCENDIO: debe asignar estado ACTIVO y prioridad alta")
    void crear_incendio_estadoActivoYPrioridadAlta() {
        Report reporte = reportFactory.crear(new CreateReportRequest(
                "Incendio en plaza", "Humo visible",
                -33.45, -70.65, "INCENDIO", "vecino@test.cl"));

        assertThat(reporte.getEstado()).isEqualTo("ACTIVO");
        assertThat(reporte.getDescripcion()).contains("[PRIORIDAD ALTA]");
        assertThat(reporte.getTipo()).isEqualTo("INCENDIO");
    }

    /**
     * Verifica que el tipo en minúsculas se normaliza a mayúsculas antes de procesarse.
     */
    @Test
    @DisplayName("crear INCENDIO: debe normalizar tipo a mayúsculas")
    void crear_incendio_normalizaAMayusculas() {
        Report reporte = reportFactory.crear(new CreateReportRequest(
                "Título", "Desc",
                -33.45, -70.65, "incendio", "user@test.cl"));

        assertThat(reporte.getTipo()).isEqualTo("INCENDIO");
    }

    /**
     * Verifica que un reporte de tipo HUMO recibe estado EN_REVISION y etiqueta de prioridad media.
     */
    @Test
    @DisplayName("crear HUMO: debe asignar estado EN_REVISION y prioridad media")
    void crear_humo_estadoEnRevisionYPrioridadMedia() {
        Report reporte = reportFactory.crear(new CreateReportRequest(
                "Humo sector norte", "Se aprecia humo",
                -33.46, -70.66, "HUMO", "usuario@test.cl"));

        assertThat(reporte.getEstado()).isEqualTo("EN_REVISION");
        assertThat(reporte.getDescripcion()).contains("[PRIORIDAD MEDIA]");
        assertThat(reporte.getTipo()).isEqualTo("HUMO");
    }

    /**
     * Verifica que un reporte de tipo SOSPECHOSO recibe estado PENDIENTE y etiqueta de prioridad baja.
     */
    @Test
    @DisplayName("crear SOSPECHOSO: debe asignar estado PENDIENTE y prioridad baja")
    void crear_sospechoso_estadoPendienteYPrioridadBaja() {
        Report reporte = reportFactory.crear(new CreateReportRequest(
                "Persona sospechosa", "Rondando el edificio",
                -33.47, -70.67, "SOSPECHOSO", "user@test.cl"));

        assertThat(reporte.getEstado()).isEqualTo("PENDIENTE");
        assertThat(reporte.getDescripcion()).contains("[PRIORIDAD BAJA]");
        assertThat(reporte.getTipo()).isEqualTo("SOSPECHOSO");
    }

    /**
     * Verifica que todos los campos comunes (título, coordenadas, email) se asignan correctamente.
     */
    @Test
    @DisplayName("crear: debe asignar correctamente todos los campos comunes")
    void crear_asignaCamposComunes() {
        Report reporte = reportFactory.crear(new CreateReportRequest(
                "Título test", "Descripción test",
                -33.45, -70.65, "INCENDIO", "test@municipalidad.cl"));

        assertThat(reporte.getTitulo()).isEqualTo("Título test");
        assertThat(reporte.getLatitud()).isEqualTo(-33.45);
        assertThat(reporte.getLongitud()).isEqualTo(-70.65);
        assertThat(reporte.getEmailUsuario()).isEqualTo("test@municipalidad.cl");
    }

    /**
     * Verifica que un tipo no existente en {@link ReportType} lanza {@link IllegalArgumentException}.
     */
    @Test
    @DisplayName("crear: debe lanzar excepción cuando el tipo no existe")
    void crear_tipoInvalido_lanzaExcepcion() {
        assertThatThrownBy(() ->
                reportFactory.crear(new CreateReportRequest(
                        "T", "D", -33.0, -70.0, "TIPO_INVALIDO", "e@e.cl")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}