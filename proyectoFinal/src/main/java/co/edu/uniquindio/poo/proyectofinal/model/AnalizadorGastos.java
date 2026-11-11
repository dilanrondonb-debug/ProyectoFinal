package co.edu.uniquindio.poo.proyectofinal.model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AnalizadorGastos {
    public double promedio(List<Transaccion> transacciones) {
        return transacciones.stream()
                .mapToDouble(Transaccion::getMonto)
                .average()
                .orElse(0);
    }

    /** Desviación estándar poblacional: raíz del promedio de (monto - promedio)². */
    public double desviacion(List<Transaccion> transacciones) {
        double promedio = promedio(transacciones);
        double varianza = transacciones.stream()
                .mapToDouble(t -> Math.pow(t.getMonto() - promedio, 2))
                .average()
                .orElse(0);
        return Math.sqrt(varianza);
    }

    /** Una transacción es atípica si su monto supera promedio + 2 × desviación. */
    public List<Transaccion> detectarAtipicas(List<Transaccion> transacciones) {
        List<Transaccion> activas = transacciones.stream()
                .filter(t -> !t.isRevertida())
                .toList();
        if (activas.size() < 2) {
            return List.of();
        }
        double umbral = promedio(activas) + 2 * desviacion(activas);
        return activas.stream()
                .filter(t -> t.getMonto() > umbral)
                .toList();
    }

    /** Agrupa el dinero movido por tipo de transacción (Deposito, Retiro, Transferencia). */
    public Map<String, Double> montoPorTipo(List<Transaccion> transacciones) {
        return transacciones.stream()
                .filter(t -> !t.isRevertida())
                .collect(Collectors.groupingBy(
                        t -> t.getClass().getSimpleName(),
                        Collectors.summingDouble(Transaccion::getMonto)));
    }
}
