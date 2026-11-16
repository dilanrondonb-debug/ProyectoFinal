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
    public void canjeDeMesSinCargosActivaElBeneficio() throws Exception {
        LOG.info("Inicio de prueba canjeDeMesSinCargosActivaElBeneficio...");
        cliente.agregarPuntos(500, "t");
        cliente.canjear(new MesSinCargosRetiro());
        assertTrue(cliente.tieneMesSinCargos());
        assertEquals(0, cliente.getPuntos());
    }

    // ---------- Monederos ----------


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
