package co.edu.uniquindio.poo;

import static org.junit.jupiter.api.Assertions.*;

import co.edu.uniquindio.poo.proyectofinal.model.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de la clase fachada MonederoVirtual: gestión de clientes,
 * operaciones, canje, programadas y alertas.
 */
public class MonederoVirtualTest {

    private static final Logger LOG = Logger.getLogger(MonederoVirtualTest.class.getName());
    private static final double DELTA = 0.0001;

    private MonederoVirtual monederoVirtual;
    private Cliente ana;
    private Cliente luis;
    private Monedero gastosAna;
    private Monedero gastosLuis;

    @BeforeEach
    public void preparar() throws Exception {
        monederoVirtual = new MonederoVirtual("UQ");
        ana = new Cliente("12345678", "Ana", "ana@mail.com", "3001234567");
        luis = new Cliente("87654321", "Luis", "luis@mail.com", "3109876543");
        gastosAna = new MonederoGastos("Gastos Ana");
        gastosLuis = new MonederoGastos("Gastos Luis");
        monederoVirtual.registrarCliente(ana);
        monederoVirtual.registrarCliente(luis);
        monederoVirtual.agregarMonedero("12345678", gastosAna);
        monederoVirtual.agregarMonedero("87654321", gastosLuis);
    }

    // ---------- Clientes ----------

    @Test
    public void registrarClienteDuplicadoLanzaExcepcion() throws Exception {
        LOG.info("Inicio de prueba registrarClienteDuplicadoLanzaExcepcion...");
        Cliente repetido = new Cliente("12345678", "Otra", "otra@mail.com", "3000000000");
        assertThrows(DatoInvalidoException.class, () -> monederoVirtual.registrarCliente(repetido));
        assertEquals(2, monederoVirtual.getClientes().size());
    }

    @Test
    public void buscarClienteExistenteLoDevuelve() throws Exception {
        LOG.info("Inicio de prueba buscarClienteExistenteLoDevuelve...");
        assertEquals(ana, monederoVirtual.buscarCliente("12345678"));
    }

    @Test
    public void buscarClienteInexistenteLanzaExcepcion() {
        LOG.info("Inicio de prueba buscarClienteInexistenteLanzaExcepcion...");
        assertThrows(ClienteNoEncontradoException.class, () -> monederoVirtual.buscarCliente("000"));
    }

    @Test
    public void verificarClienteDiceSiExiste() {
        LOG.info("Inicio de prueba verificarClienteDiceSiExiste...");
        assertTrue(monederoVirtual.verificarCliente("12345678"));
        assertFalse(monederoVirtual.verificarCliente("000"));
    }

    @Test
    public void eliminarClienteLoQuitaDeLaLista() throws Exception {
        LOG.info("Inicio de prueba eliminarClienteLoQuitaDeLaLista...");
        monederoVirtual.eliminarCliente("12345678");
        assertFalse(monederoVirtual.verificarCliente("12345678"));
        assertThrows(ClienteNoEncontradoException.class, () -> monederoVirtual.eliminarCliente("12345678"));
    }

    @Test
    public void actualizarClienteCambiaSusDatos() throws Exception {
        LOG.info("Inicio de prueba actualizarClienteCambiaSusDatos...");
        monederoVirtual.actualizarCliente("12345678", "Ana María", "nueva@mail.com", "3205550000");
        assertEquals("Ana María", ana.getNombre());
        assertEquals("nueva@mail.com", ana.getCorreo());
    }

    @Test
    public void actualizarClienteConDatosInvalidosLanzaExcepcion() {
        LOG.info("Inicio de prueba actualizarClienteConDatosInvalidosLanzaExcepcion...");
        assertThrows(DatoInvalidoException.class,
                () -> monederoVirtual.actualizarCliente("12345678", "", "nueva@mail.com", "3205550000"));
    }

    // ---------- Operaciones ----------

    @Test
    public void depositarPorCedulaSumaAlMonedero() throws Exception {
        LOG.info("Inicio de prueba depositarPorCedulaSumaAlMonedero...");
        monederoVirtual.depositar("12345678", gastosAna.getId(), 1000);
        assertEquals(1000.0, gastosAna.getSaldo(), DELTA);
        assertEquals(10, ana.getPuntos());
    }

    @Test
    public void retirarSinSaldoLanzaExcepcion() {
        LOG.info("Inicio de prueba retirarSinSaldoLanzaExcepcion...");
        assertThrows(SaldoInsuficienteException.class,
                () -> monederoVirtual.retirar("12345678", gastosAna.getId(), 100));
        assertEquals(0.0, gastosAna.getSaldo(), DELTA);
    }

    @Test
    public void operarConClienteInexistenteNoModificaNingunSaldo() {
        LOG.info("Inicio de prueba operarConClienteInexistenteNoModificaNingunSaldo...");
        assertThrows(ClienteNoEncontradoException.class,
                () -> monederoVirtual.transferir("12345678", gastosAna.getId(), "000", "x", 100));
        assertEquals(0.0, gastosAna.getSaldo(), DELTA);
    }

    @Test
    public void transferirEntreClientesMueveElDinero() throws Exception {
        LOG.info("Inicio de prueba transferirEntreClientesMueveElDinero...");
        monederoVirtual.depositar("12345678", gastosAna.getId(), 5000);
        monederoVirtual.transferir("12345678", gastosAna.getId(), "87654321", gastosLuis.getId(), 2000);
        assertEquals(3000.0, gastosAna.getSaldo(), DELTA);
        assertEquals(2000.0, gastosLuis.getSaldo(), DELTA);
    }

    @Test
    public void revertirDesdeLaFachadaDevuelveElDinero() throws Exception {
        LOG.info("Inicio de prueba revertirDesdeLaFachadaDevuelveElDinero...");
        Deposito deposito = monederoVirtual.depositar("12345678", gastosAna.getId(), 1000);
        monederoVirtual.revertirTransaccion("12345678", gastosAna.getId(), deposito.getId());
        assertEquals(0.0, gastosAna.getSaldo(), DELTA);
        assertEquals(0, ana.getPuntos());
    }

    @Test
    public void consultarHistorialDevuelveTodasLasOperaciones() throws Exception {
        LOG.info("Inicio de prueba consultarHistorialDevuelveTodasLasOperaciones...");
        monederoVirtual.depositar("12345678", gastosAna.getId(), 5000);
        monederoVirtual.retirar("12345678", gastosAna.getId(), 1000);
        monederoVirtual.transferir("12345678", gastosAna.getId(), "87654321", gastosLuis.getId(), 500);
        assertEquals(3, monederoVirtual.consultarHistorial("12345678", gastosAna.getId()).size());
        assertEquals(1, monederoVirtual.consultarHistorial("87654321", gastosLuis.getId()).size());
    }

    // ---------- Puntos y rangos ----------

    @Test
    public void canjearBeneficioDesdeLaFachada() throws Exception {
        LOG.info("Inicio de prueba canjearBeneficioDesdeLaFachada...");
        monederoVirtual.depositar("12345678", gastosAna.getId(), 10000);   // +100 puntos
        monederoVirtual.canjearBeneficio("12345678", new ReduccionComision());
        assertEquals(0, ana.getPuntos());
        assertEquals(0.10, ana.getDescuentoComision(), DELTA);
    }

    @Test
    public void canjearSinPuntosLanzaExcepcion() {
        LOG.info("Inicio de prueba canjearSinPuntosLanzaExcepcion...");
        assertThrows(PuntosInsuficientesException.class,
                () -> monederoVirtual.canjearBeneficio("12345678", new BonoSaldo()));
    }

    @Test
    public void agruparClientesPorRango() throws Exception {
        LOG.info("Inicio de prueba agruparClientesPorRango...");
        monederoVirtual.depositar("87654321", gastosLuis.getId(), 60000);  // +600 puntos
        Map<Rango, List<Cliente>> grupos = monederoVirtual.agruparClientesPorRango();
        assertIterableEquals(List.of(ana), grupos.get(Rango.BRONCE));
        assertIterableEquals(List.of(luis), grupos.get(Rango.PLATA));
    }

    // ---------- Transacciones programadas ----------

    @Test
    public void lasProgramadasSeOrdenanPorFecha() throws Exception {
        LOG.info("Inicio de prueba lasProgramadasSeOrdenanPorFecha...");
        LocalDate hoy = LocalDate.now();
        TransaccionProgramada en9 = new TransaccionProgramada(hoy.plusDays(9), Frecuencia.UNICA,
                new Deposito(gastosAna, 10));
        TransaccionProgramada en2 = new TransaccionProgramada(hoy.plusDays(2), Frecuencia.MENSUAL,
                new Deposito(gastosAna, 10));
        TransaccionProgramada en5 = new TransaccionProgramada(hoy.plusDays(5), Frecuencia.SEMANAL,
                new Deposito(gastosAna, 10));
        monederoVirtual.programarTransaccion(en9);
        monederoVirtual.programarTransaccion(en2);
        monederoVirtual.programarTransaccion(en5);
        assertIterableEquals(List.of(en2, en5, en9), monederoVirtual.obtenerProgramadasOrdenadas());
    }

    @Test
    public void programarConFechaPasadaLanzaExcepcion() {
        LOG.info("Inicio de prueba programarConFechaPasadaLanzaExcepcion...");
        assertThrows(DatoInvalidoException.class, () -> new TransaccionProgramada(
                LocalDate.now().minusDays(1), Frecuencia.UNICA, new Deposito(gastosAna, 10)));
    }

    @Test
    public void ordenadorFuncionaConListasVaciasYDeUnElemento() throws Exception {
        LOG.info("Inicio de prueba ordenadorFuncionaConListasVaciasYDeUnElemento...");
        List<Integer> vacia = new ArrayList<>();
        Ordenador.ordenarPorInsercion(vacia);
        assertTrue(vacia.isEmpty());
        List<Integer> desordenada = new ArrayList<>(List.of(5, 3, 9, 1, 3));
        Ordenador.ordenarPorInsercion(desordenada);
        assertIterableEquals(List.of(1, 3, 3, 5, 9), desordenada);
    }

    // ---------- Alertas ----------

    @Test
    public void unRetiroQueDejaSaldoBajoEnviaUnaAlerta() throws Exception {
        LOG.info("Inicio de prueba unRetiroQueDejaSaldoBajoEnviaUnaAlerta...");
        List<String> mensajes = new ArrayList<>();
        monederoVirtual.agregarNotificador(mensajes::add);
        monederoVirtual.depositar("12345678", gastosAna.getId(), 20000);
        monederoVirtual.retirar("12345678", gastosAna.getId(), 15000);    // quedan 4850
        assertEquals(1, mensajes.size());
    }

    @Test
    public void unRetiroConSaldoSuficienteNoEnviaAlertas() throws Exception {
        LOG.info("Inicio de prueba unRetiroConSaldoSuficienteNoEnviaAlertas...");
        List<String> mensajes = new ArrayList<>();
        monederoVirtual.agregarNotificador(mensajes::add);
        monederoVirtual.depositar("12345678", gastosAna.getId(), 100000);
        monederoVirtual.retirar("12345678", gastosAna.getId(), 1000);
        assertTrue(mensajes.isEmpty());
    }

    @Test
    public void unRetiroAtipicoEnviaUnaAlerta() throws Exception {
        LOG.info("Inicio de prueba unRetiroAtipicoEnviaUnaAlerta...");
        List<String> mensajes = new ArrayList<>();
        gastosAna.depositar(500000);   // directo al monedero: no entra al historial de transacciones
        for (int i = 0; i < 6; i++) {
            monederoVirtual.retirar("12345678", gastosAna.getId(), 100);
        }
        monederoVirtual.agregarNotificador(mensajes::add);
        monederoVirtual.retirar("12345678", gastosAna.getId(), 200000);   // muy por encima del resto
        assertEquals(1, mensajes.size());
        assertTrue(mensajes.get(0).contains("atípica"));
    }
}
