package co.edu.uniquindio.poo;

import static org.junit.jupiter.api.Assertions.*;

import co.edu.uniquindio.poo.proyectofinal.model.*;
import java.util.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de los depósitos, retiros y transferencias: saldos, puntos,
 * validaciones y reversión.
 */
public class TransaccionTest {

    private static final Logger LOG = Logger.getLogger(TransaccionTest.class.getName());
    private static final double DELTA = 0.0001;

    private Cliente ana;
    private Cliente luis;
    private Monedero gastosAna;
    private Monedero gastosLuis;

    @BeforeEach
    public void preparar() throws Exception {
        ana = new Cliente("12345678", "Ana", "ana@mail.com", "3001234567");
        luis = new Cliente("87654321", "Luis", "luis@mail.com", "3109876543");
        gastosAna = new MonederoGastos("Gastos Ana");
        gastosLuis = new MonederoGastos("Gastos Luis");
        ana.agregarMonedero(gastosAna);
        luis.agregarMonedero(gastosLuis);
    }

    // ---------- Puntos ----------

    @Test
    public void depositoDaUnPuntoPorCadaCien() throws Exception {
        LOG.info("Inicio de prueba depositoDaUnPuntoPorCadaCien...");
        assertEquals(10, new Deposito(gastosAna, 1000).calcularPuntos());
        assertEquals(2, new Deposito(gastosAna, 250).calcularPuntos());
        assertEquals(0, new Deposito(gastosAna, 99).calcularPuntos());
    }

    // ---------- Validaciones ----------
    @Test
    public void noSePuedeEjecutarDosVeces() throws Exception {
        LOG.info("Inicio de prueba noSePuedeEjecutarDosVeces...");
        Deposito deposito = new Deposito(gastosAna, 1000);
        deposito.ejecutar();
        assertThrows(MonederoException.class, deposito::ejecutar);
        assertEquals(1000.0, gastosAna.getSaldo(), DELTA);
    }

    // ---------- Ejecución ----------

    @Test
    public void ejecutarRetiroCobraComisionYDaPuntos() throws Exception {
        LOG.info("Inicio de prueba ejecutarRetiroCobraComisionYDaPuntos...");
        new Deposito(gastosAna, 10000).ejecutar();          // +100 puntos
        Retiro retiro = new Retiro(gastosAna, 1000);
        retiro.ejecutar();                                    // +20 puntos
        assertEquals(10.0, retiro.getComisionCobrada(), DELTA);
        assertEquals(8990.0, gastosAna.getSaldo(), DELTA);   // 10000 - 1000 - 10
        assertEquals(120, ana.getPuntos());
    }

    @Test
    public void laComisionTambienCuentaParaElSaldoInsuficiente() throws Exception {
        LOG.info("Inicio de prueba laComisionTambienCuentaParaElSaldoInsuficiente...");
        gastosAna.depositar(100);
        // Retirar 100 cuesta 101 con la comisión del 1%
        assertThrows(SaldoInsuficienteException.class, () -> new Retiro(gastosAna, 100).ejecutar());
        assertEquals(100.0, gastosAna.getSaldo(), DELTA);
    }

    @Test
    public void ejecutarTransferenciaMueveElDineroYDaPuntosAlOrigen() throws Exception {
        LOG.info("Inicio de prueba ejecutarTransferenciaMueveElDineroYDaPuntosAlOrigen...");
        new Deposito(gastosAna, 5000).ejecutar();            // +50 puntos
        new Transferencia(gastosAna, gastosLuis, 2000).ejecutar(); // +60 puntos
        assertEquals(3000.0, gastosAna.getSaldo(), DELTA);
        assertEquals(2000.0, gastosLuis.getSaldo(), DELTA);
        assertEquals(110, ana.getPuntos());
        assertEquals(0, luis.getPuntos());
    }

    // ---------- Reversión ----------

    @Test
    public void revertirTransferenciaFallaSiElDestinoYaGastoElDinero() throws Exception {
        LOG.info("Inicio de prueba revertirTransferenciaFallaSiElDestinoYaGastoElDinero...");
        new Deposito(gastosAna, 5000).ejecutar();
        Transferencia transferencia = new Transferencia(gastosAna, gastosLuis, 2000);
        transferencia.ejecutar();
        gastosLuis.retirar(1500);   // Luis ya gastó casi todo
        assertThrows(SaldoInsuficienteException.class, transferencia::revertir);
        assertFalse(transferencia.isRevertida());
        assertEquals(3000.0, gastosAna.getSaldo(), DELTA);
    }
}
