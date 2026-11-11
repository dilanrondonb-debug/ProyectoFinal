package co.edu.uniquindio.poo;

import static org.junit.jupiter.api.Assertions.*;
import co.edu.uniquindio.poo.proyectofinal.model.*;
import java.util.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pruebas del cliente: validación de datos, puntos, rangos y canje de beneficios.
 */
public class ClienteTest {

    private static final Logger LOG = Logger.getLogger(ClienteTest.class.getName());
    private static final double DELTA = 0.0001;

    private Cliente cliente;
    private Monedero gastos;

    @BeforeEach
    public void preparar() throws Exception {
        cliente = new Cliente("12345678", "Ana", "ana@mail.com", "3001234567");
        gastos = new MonederoGastos("Gastos");
        cliente.agregarMonedero(gastos);
    }

    // ---------- Validación de datos ----------

    @Test
    public void crearClienteConDatosValidosGuardaTodosLosCampos() {
        LOG.info("Inicio de prueba crearClienteConDatosValidosGuardaTodosLosCampos...");
        assertEquals("12345678", cliente.getCedula());
        assertEquals("Ana", cliente.getNombre());
        assertEquals("ana@mail.com", cliente.getCorreo());
        assertEquals("3001234567", cliente.getTelefono());
        assertEquals(0, cliente.getPuntos());
        assertEquals(Rango.BRONCE, cliente.getRango());
    }

    @Test
    public void crearClienteConDatosInvalidosLanzaExcepcion() {
        LOG.info("Inicio de prueba crearClienteConDatosInvalidosLanzaExcepcion...");
        assertThrows(DatoInvalidoException.class,
                () -> new Cliente("abc", "Ana", "ana@mail.com", "3001234567"));
        assertThrows(DatoInvalidoException.class,
                () -> new Cliente("12345678", "  ", "ana@mail.com", "3001234567"));
        assertThrows(DatoInvalidoException.class,
                () -> new Cliente("12345678", "Ana", "sin-arroba", "3001234567"));
        assertThrows(DatoInvalidoException.class,
                () -> new Cliente("12345678", "Ana", "ana@mail.com", "12"));
    }

    // ---------- Puntos y rango ----------

    @Test
    public void agregarPuntosActualizaElHistorial() {
        LOG.info("Inicio de prueba agregarPuntosActualizaElHistorial...");
        cliente.agregarPuntos(10, "ganados");
        cliente.agregarPuntos(-4, "revertidos");
        assertEquals(6, cliente.getPuntos());
        assertEquals(2, cliente.getHistorialPuntos().size());
    }

    @Test
    public void elRangoSubeAlGanarPuntos() {
        LOG.info("Inicio de prueba elRangoSubeAlGanarPuntos...");
        cliente.agregarPuntos(500, "t");
        assertEquals(Rango.BRONCE, cliente.getRango());
        cliente.agregarPuntos(1, "t");
        assertEquals(Rango.PLATA, cliente.getRango());
        cliente.agregarPuntos(500, "t");
        assertEquals(Rango.ORO, cliente.getRango());
        cliente.agregarPuntos(4000, "t");
        assertEquals(Rango.PLATINO, cliente.getRango());
    }

    // ---------- Canje de beneficios ----------

    @Test
    public void canjeDeReduccionDescuentaPuntosYAplicaElDescuento() throws Exception {
        LOG.info("Inicio de prueba canjeDeReduccionDescuentaPuntosYAplicaElDescuento...");
        cliente.agregarPuntos(100, "t");
        cliente.canjear(new ReduccionComision());
        assertEquals(0, cliente.getPuntos());
        assertEquals(0.10, cliente.getDescuentoComision(), DELTA);
    }

    @Test
    public void canjeDeMesSinCargosActivaElBeneficio() throws Exception {
        LOG.info("Inicio de prueba canjeDeMesSinCargosActivaElBeneficio...");
        cliente.agregarPuntos(500, "t");
        cliente.canjear(new MesSinCargosRetiro());
        assertTrue(cliente.tieneMesSinCargos());
        assertEquals(0, cliente.getPuntos());
    }

    @Test
    public void canjeDeBonoSumaCincuentaAlSaldoYBajaElRango() throws Exception {
        LOG.info("Inicio de prueba canjeDeBonoSumaCincuentaAlSaldoYBajaElRango...");
        cliente.agregarPuntos(1000, "t");
        assertEquals(Rango.PLATA, cliente.getRango());
        cliente.canjear(new BonoSaldo());
        assertEquals(50.0, gastos.getSaldo(), DELTA);
        assertEquals(0, cliente.getPuntos());
        assertEquals(Rango.BRONCE, cliente.getRango());
    }

    @Test
    public void canjeSinPuntosSuficientesLanzaExcepcionYNoCambiaNada() {
        LOG.info("Inicio de prueba canjeSinPuntosSuficientesLanzaExcepcionYNoCambiaNada...");
        cliente.agregarPuntos(99, "t");
        assertThrows(PuntosInsuficientesException.class, () -> cliente.canjear(new ReduccionComision()));
        assertEquals(99, cliente.getPuntos());
        assertEquals(0.0, cliente.getDescuentoComision(), DELTA);
    }

    @Test
    public void siElBeneficioFallaNoSeDescuentanLosPuntos() throws Exception {
        LOG.info("Inicio de prueba siElBeneficioFallaNoSeDescuentanLosPuntos...");
        Cliente sinMonederos = new Cliente("11111111", "Sin", "sin@mail.com", "3000000000");
        sinMonederos.agregarPuntos(1000, "t");
        assertThrows(MonederoException.class, () -> sinMonederos.canjear(new BonoSaldo()));
        assertEquals(1000, sinMonederos.getPuntos());
    }

    // ---------- Monederos ----------

    @Test
    public void buscarMonederoInexistenteLanzaExcepcion() {
        LOG.info("Inicio de prueba buscarMonederoInexistenteLanzaExcepcion...");
        assertThrows(MonederoException.class, () -> cliente.buscarMonedero("no-existe"));
    }

    @Test
    public void buscarMonederoExistenteLoDevuelve() throws Exception {
        LOG.info("Inicio de prueba buscarMonederoExistenteLoDevuelve...");
        assertEquals(gastos, cliente.buscarMonedero(gastos.getId()));
    }

    @Test
    public void saldoTotalSumaTodosLosMonederos() throws Exception {
        LOG.info("Inicio de prueba saldoTotalSumaTodosLosMonederos...");
        Monedero ahorros = new MonederoAhorros("Ahorros");
        cliente.agregarMonedero(ahorros);
        gastos.depositar(100);
        ahorros.depositar(250);
        assertEquals(350.0, cliente.getSaldoTotal(), DELTA);
    }
}
