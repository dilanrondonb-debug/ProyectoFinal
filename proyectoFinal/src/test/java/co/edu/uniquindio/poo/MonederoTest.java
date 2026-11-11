package co.edu.uniquindio.poo;

import static org.junit.jupiter.api.Assertions.*;
import co.edu.uniquindio.poo.proyectofinal.model.*;
import java.time.LocalDate;
import java.util.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pruebas del saldo y las comisiones de los monederos.
 */
public class MonederoTest {

    private static final Logger LOG = Logger.getLogger(MonederoTest.class.getName());
    private static final double DELTA = 0.0001;

    private Monedero gastos;

    @BeforeEach
    public void preparar() {
        gastos = new MonederoGastos("Gastos");
    }

    @Test
    public void depositarAcumulaElSaldoConExactitud() throws Exception {
        LOG.info("Inicio de prueba depositarAcumulaElSaldoConExactitud...");
        gastos.depositar(50.5);
        gastos.depositar(49.5);
        assertEquals(100.0, gastos.getSaldo(), DELTA);
    }

    @Test
    public void depositarMontoInvalidoLanzaExcepcionYNoCambiaElSaldo() {
        LOG.info("Inicio de prueba depositarMontoInvalidoLanzaExcepcionYNoCambiaElSaldo...");
        assertThrows(MontoInvalidoException.class, () -> gastos.depositar(0));
        assertThrows(MontoInvalidoException.class, () -> gastos.depositar(-50));
        assertEquals(0.0, gastos.getSaldo(), DELTA);
    }

    @Test
    public void retirarMontoInvalidoLanzaExcepcionYNoCambiaElSaldo() throws Exception {
        LOG.info("Inicio de prueba retirarMontoInvalidoLanzaExcepcionYNoCambiaElSaldo...");
        gastos.depositar(100);
        assertThrows(MontoInvalidoException.class, () -> gastos.retirar(0));
        assertThrows(MontoInvalidoException.class, () -> gastos.retirar(-10));
        assertEquals(100.0, gastos.getSaldo(), DELTA);
    }

    @Test
    public void retirarMasQueElSaldoLanzaExcepcionYNoCambiaElSaldo() throws Exception {
        LOG.info("Inicio de prueba retirarMasQueElSaldoLanzaExcepcionYNoCambiaElSaldo...");
        gastos.depositar(100);
        assertThrows(SaldoInsuficienteException.class, () -> gastos.retirar(150));
        assertEquals(100.0, gastos.getSaldo(), DELTA);
    }

    @Test
    public void comisionDeAhorrosEsDosPorCiento() {
        LOG.info("Inicio de prueba comisionDeAhorrosEsDosPorCiento...");
        Monedero ahorros = new MonederoAhorros("Ahorros");
        assertEquals(20.0, ahorros.calcularComision(1000), DELTA);
    }

    @Test
    public void comisionDeGastosEsUnoPorCiento() {
        LOG.info("Inicio de prueba comisionDeGastosEsUnoPorCiento...");
        assertEquals(10.0, gastos.calcularComision(1000), DELTA);
    }

    @Test
    public void comisionAplicableSinBeneficiosEsLaComisionNormal() throws Exception {
        LOG.info("Inicio de prueba comisionAplicableSinBeneficiosEsLaComisionNormal...");
        Cliente cliente = new Cliente("12345678", "Ana", "ana@mail.com", "3001234567");
        cliente.agregarMonedero(gastos);
        assertEquals(10.0, gastos.calcularComisionAplicable(1000), DELTA);
    }

    @Test
    public void comisionAplicableConDescuentoDelDiezPorCiento() throws Exception {
        LOG.info("Inicio de prueba comisionAplicableConDescuentoDelDiezPorCiento...");
        Cliente cliente = new Cliente("12345678", "Ana", "ana@mail.com", "3001234567");
        cliente.agregarMonedero(gastos);
        cliente.setDescuentoComision(0.10);
        assertEquals(9.0, gastos.calcularComisionAplicable(1000), DELTA);
    }

    @Test
    public void comisionAplicableConMesSinCargosEsCero() throws Exception {
        LOG.info("Inicio de prueba comisionAplicableConMesSinCargosEsCero...");
        Cliente cliente = new Cliente("12345678", "Ana", "ana@mail.com", "3001234567");
        cliente.agregarMonedero(gastos);
        cliente.setMesSinCargosHasta(LocalDate.now().plusDays(10));
        assertEquals(0.0, gastos.calcularComisionAplicable(1000), DELTA);
    }

    @Test
    public void mesSinCargosVencidoYaNoAplica() throws Exception {
        LOG.info("Inicio de prueba mesSinCargosVencidoYaNoAplica...");
        Cliente cliente = new Cliente("12345678", "Ana", "ana@mail.com", "3001234567");
        cliente.agregarMonedero(gastos);
        cliente.setMesSinCargosHasta(LocalDate.now().minusDays(1));
        assertFalse(cliente.tieneMesSinCargos());
        assertEquals(10.0, gastos.calcularComisionAplicable(1000), DELTA);
    }

    @Test
    public void saldoBajoSeDetectaPorDebajoDelUmbral() throws Exception {
        LOG.info("Inicio de prueba saldoBajoSeDetectaPorDebajoDelUmbral...");
        assertTrue(gastos.isSaldoBajo());
        gastos.depositar(20000);
        assertFalse(gastos.isSaldoBajo());
    }
}
