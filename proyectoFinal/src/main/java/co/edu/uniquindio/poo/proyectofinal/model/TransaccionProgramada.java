package co.edu.uniquindio.poo.proyectofinal.model;

import java.time.LocalDate;

public class TransaccionProgramada implements Comparable<TransaccionProgramada>{
    private final LocalDate fechaEjecucion;
    private final Frecuencia frecuencia;
    private final Transaccion transaccion;

    public TransaccionProgramada(LocalDate fechaEjecucion, Frecuencia frecuencia, Transaccion transaccion)
            throws DatoInvalidoException {
        if (fechaEjecucion == null || fechaEjecucion.isBefore(LocalDate.now())) {
            throw new DatoInvalidoException("La fecha programada no puede estar en el pasado");
        }
        if (frecuencia == null || transaccion == null) {
            throw new DatoInvalidoException("Debe indicar la frecuencia y la transacción");
        }
        this.fechaEjecucion = fechaEjecucion;
        this.frecuencia = frecuencia;
        this.transaccion = transaccion;
    }

    public void ejecutar() throws MonederoException {
        transaccion.ejecutar();
    }

    @Override
    public int compareTo(TransaccionProgramada otra) {
        return fechaEjecucion.compareTo(otra.fechaEjecucion);
    }

    public LocalDate getFechaEjecucion() { return fechaEjecucion; }
    public Frecuencia getFrecuencia() { return frecuencia; }
    public Transaccion getTransaccion() { return transaccion; }

    @Override
    public String toString() {
        return fechaEjecucion + " (" + frecuencia + ") -> " + transaccion;
    }
}
