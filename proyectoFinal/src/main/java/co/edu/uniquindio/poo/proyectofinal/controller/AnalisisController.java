package co.edu.uniquindio.poo.proyectofinal.controller;

import co.edu.uniquindio.poo.proyectofinal.model.*;

import java.util.List;
import java.util.Map;

public class AnalisisController {

    /** Resultado del análisis: todo lo que la pantalla necesita mostrar. */
    public record Resultado(int total, double promedio, double desviacion, double umbral,
                            List<Transaccion> atipicas, Map<String, Double> montoPorTipo) { }

    private final AnalizadorGastos analizador;

    public AnalisisController(MonederoVirtual monederoVirtual) {
        this.analizador = monederoVirtual.getAnalizador();
    }

    /** Analiza un monedero, o todos los del cliente si monedero es null. */
    public Resultado analizar(Cliente cliente, Monedero monedero) {
        List<Transaccion> transacciones = (monedero != null)
                ? monedero.getTransacciones()
                : cliente.getMonederos().stream().flatMap(m -> m.getTransacciones().stream()).distinct().toList();
        List<Transaccion> activas = transacciones.stream().filter(t -> !t.isRevertida()).toList();
        double promedio = analizador.promedio(activas);
        double desviacion = analizador.desviacion(activas);
        return new Resultado(activas.size(), promedio, desviacion, promedio + 2 * desviacion,
                analizador.detectarAtipicas(transacciones), analizador.montoPorTipo(transacciones));
    }
}
