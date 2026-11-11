package co.edu.uniquindio.poo;

import static org.junit.jupiter.api.Assertions.*;

import co.edu.uniquindio.poo.proyectofinal.model.*;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de la clasificación de rangos según los puntos del cliente.
 * Se prueban los valores límite: 500/501, 1000/1001 y 5000/5001.
 */
public class RangoTest {

    private static final Logger LOG = Logger.getLogger(RangoTest.class.getName());

    @Test
    public void bronceHastaQuinientosPuntos() {
        LOG.info("Inicio de prueba bronceHastaQuinientosPuntos...");
        assertEquals(Rango.BRONCE, Rango.desdePuntos(0));
        assertEquals(Rango.BRONCE, Rango.desdePuntos(500));
    }

    @Test
    public void plataEntre501Y1000() {
        LOG.info("Inicio de prueba plataEntre501Y1000...");
        assertEquals(Rango.PLATA, Rango.desdePuntos(501));
        assertEquals(Rango.PLATA, Rango.desdePuntos(1000));
    }

    @Test
    public void oroEntre1001Y5000() {
        LOG.info("Inicio de prueba oroEntre1001Y5000...");
        assertEquals(Rango.ORO, Rango.desdePuntos(1001));
        assertEquals(Rango.ORO, Rango.desdePuntos(5000));
    }

    @Test
    public void platinoDesde5001() {
        LOG.info("Inicio de prueba platinoDesde5001...");
        assertEquals(Rango.PLATINO, Rango.desdePuntos(5001));
        assertEquals(Rango.PLATINO, Rango.desdePuntos(100000));
    }

    @Test
    public void puntosNegativosSeQuedanEnBronce() {
        LOG.info("Inicio de prueba puntosNegativosSeQuedanEnBronce...");
        assertEquals(Rango.BRONCE, Rango.desdePuntos(-20));
    }
}
