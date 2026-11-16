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

    // ---------- Operaciones ----------

    @Test
    public void depositarPorCedulaSumaAlMonedero() throws Exception {
        LOG.info("Inicio de prueba depositarPorCedulaSumaAlMonedero...");
        monederoVirtual.depositar("12345678", gastosAna.getId(), 1000);
        assertEquals(1000.0, gastosAna.getSaldo(), DELTA);
        assertEquals(10, ana.getPuntos());
    }
    // ---------- Puntos y rangos ----------

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
}
