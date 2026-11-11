package co.edu.uniquindio.poo.proyectofinal.model;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class Transaccion implements IPuntuable, IReversible {
    private final String id;
    private final double monto;
    private final LocalDateTime fecha;
    private final Monedero monedero;
    private boolean ejecutada;
    private boolean revertida;

    protected Transaccion(Monedero monedero, double monto) throws MonederoException {
        if (monedero == null) {
            throw new MonederoException("La transacción necesita un monedero");
        }
        if (Double.isNaN(monto) || Double.isInfinite(monto) || monto <= 0) {
            throw new MontoInvalidoException(monto);
        }
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.monedero = monedero;
        this.monto = monto;
        this.fecha = LocalDateTime.now();
    }

    /** Cada tipo de transacción mueve el dinero a su manera. */
    public abstract void ejecutar() throws MonederoException;

    /** Cada tipo sabe cómo deshacer su propio movimiento. */
    protected abstract void deshacer() throws MonederoException;

    /** Polimorfismo: cada tipo da una cantidad distinta de puntos. */
    @Override
    public abstract int calcularPuntos();

    @Override
    public void revertir() throws MonederoException {
        if (!ejecutada) {
            throw new MonederoException("No se puede revertir una transacción que no se ha ejecutado");
        }
        if (revertida) {
            throw new MonederoException("La transacción " + id + " ya fue revertida");
        }
        deshacer();
        revertida = true;
        Cliente cliente = monedero.getPropietario();
        if (cliente != null) {
            cliente.agregarPuntos(-calcularPuntos(), "Reversión " + getClass().getSimpleName() + " " + id);
        }
    }

    protected void exigirNoEjecutada() throws MonederoException {
        if (ejecutada) {
            throw new MonederoException("La transacción " + id + " ya fue ejecutada");
        }
    }

    /** Se llama al final de ejecutar(): marca, guarda en el historial y da los puntos. */
    protected void finalizarEjecucion() {
        ejecutada = true;
        monedero.registrarTransaccion(this);
        Cliente cliente = monedero.getPropietario();
        if (cliente != null) {
            cliente.agregarPuntos(calcularPuntos(), getClass().getSimpleName() + " " + id);
        }
    }

    public String getId() { return id; }
    public double getMonto() { return monto; }
    public LocalDateTime getFecha() { return fecha; }
    public Monedero getMonedero() { return monedero; }
    public boolean isEjecutada() { return ejecutada; }
    public boolean isRevertida() { return revertida; }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" + id + "] " + monto + (revertida ? " (revertida)" : "");
    }
}

