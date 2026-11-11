package co.edu.uniquindio.poo;

import static org.junit.jupiter.api.Assertions.*;
import co.edu.uniquindio.poo.proyectofinal.model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import co.edu.uniquindio.poo.proyectofinal.model.*;
/**
 * Pruebas del análisis de gastos: promedio, desviación y transacciones atípicas
 * (monto mayor que promedio + 2 × desviación estándar).
 */
public class AnalizadorGastosTest {

    private static final Logger LOG = Logger.getLogger(AnalizadorGastosTest.class.getName());
    private static final double DELTA = 0.001;

    private AnalizadorGastos analizador;
    private Monedero monedero;

    @BeforeEach
    public void preparar() {
        analizador = new AnalizadorGastos();
        monedero = new MonederoGastos("Gastos");
    }

    /** Crea una lista de depósitos con los montos dados (sin ejecutarlos). */
    private List<Transaccion> depositos(double... montos) throws Exception {
        List<Transaccion> lista = new ArrayList<>();
        for (double monto : montos) {
            lista.add(new Deposito(monedero, monto));
        }
        return lista;
    }

    @Test
    public void promedioDeLosMontos() throws Exception {
        LOG.info("Inicio de prueba promedioDeLosMontos...");
        assertEquals(200.0, analizador.promedio(depositos(100, 200, 300)), DELTA);
    }

    @Test
    public void promedioDeUnaListaVaciaEsCero() {
        LOG.info("Inicio de prueba promedioDeUnaListaVaciaEsCero...");
        assertEquals(0.0, analizador.promedio(List.of()), DELTA);
    }

    @Test
    public void desviacionEstandarDeLosMontos() throws Exception {
        LOG.info("Inicio de prueba desviacionEstandarDeLosMontos...");
        // desviaciones: -100, 0, 100 -> varianza 20000/3 -> raíz = 81.6497
        assertEquals(81.6497, analizador.desviacion(depositos(100, 200, 300)), DELTA);
    }

    @Test
    public void detectaLaTransaccionAtipica() throws Exception {
        LOG.info("Inicio de prueba detectaLaTransaccionAtipica...");
        List<Transaccion> lista = depositos(100, 100, 100, 100, 100, 100, 50000);
        Transaccion grande = lista.get(6);
        assertIterableEquals(List.of(grande), analizador.detectarAtipicas(lista));
    }

    @Test
    public void sinValoresExtremosNoHayAtipicas() throws Exception {
        LOG.info("Inicio de prueba sinValoresExtremosNoHayAtipicas...");
        assertTrue(analizador.detectarAtipicas(depositos(100, 100, 100, 100, 100, 100)).isEmpty());
    }

    @Test
    public void conMenosDeSeisTransaccionesNuncaHayAtipicas() throws Exception {
        LOG.info("Inicio de prueba conMenosDeSeisTransaccionesNuncaHayAtipicas...");
        // Con pocos datos ningún monto puede superar promedio + 2 × desviación
        assertTrue(analizador.detectarAtipicas(depositos(100, 100, 100, 50000)).isEmpty());
    }

    @Test
    public void unaListaVaciaONoTieneAtipicas() {
        LOG.info("Inicio de prueba unaListaVaciaONoTieneAtipicas...");
        assertTrue(analizador.detectarAtipicas(List.of()).isEmpty());
    }

    @Test
    public void lasTransaccionesRevertidasNoCuentanParaElAnalisis() throws Exception {
        LOG.info("Inicio de prueba lasTransaccionesRevertidasNoCuentanParaElAnalisis...");
        List<Transaccion> lista = depositos(100, 100, 100, 100, 100, 100, 50000);
        for (Transaccion t : lista) {
            t.ejecutar();
        }
        lista.get(6).revertir();
        assertTrue(analizador.detectarAtipicas(lista).isEmpty());
    }

    @Test
    public void montoPorTipoAgrupaLosMontos() throws Exception {
        LOG.info("Inicio de prueba montoPorTipoAgrupaLosMontos...");
        List<Transaccion> lista = new ArrayList<>(depositos(100, 200));
        lista.add(new Retiro(monedero, 50));
        Map<String, Double> porTipo = analizador.montoPorTipo(lista);
        assertEquals(300.0, porTipo.get("Deposito"), DELTA);
        assertEquals(50.0, porTipo.get("Retiro"), DELTA);
        assertEquals(2, porTipo.size());
    }
}
