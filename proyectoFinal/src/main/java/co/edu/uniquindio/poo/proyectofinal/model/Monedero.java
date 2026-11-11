package co.edu.uniquindio.poo.proyectofinal.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public abstract class Monedero {
    public static final double UMBRAL_SALDO_BAJO = 10000;

    private final String id;
    private String nombre;
    private double saldo;
    private final List<Transaccion> transacciones = new ArrayList<>();
    private Cliente propietario;

    protected Monedero(String nombre) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.nombre = nombre;
    }

    /** Método abstracto: cada subclase define cuánto cobra por un retiro. */
    public abstract double calcularComision(double monto);

    /** Comisión real: aplica los beneficios que el cliente haya canjeado. */
    public double calcularComisionAplicable(double monto) {
        double comision = calcularComision(monto);
        if (propietario == null) {
            return comision;
        }
        if (propietario.tieneMesSinCargos()) {
            return 0;
        }
        return comision * (1 - propietario.getDescuentoComision());
    }

    public void depositar(double monto) throws MontoInvalidoException {
        validarMonto(monto);
        saldo += monto;
    }

    public void retirar(double monto) throws MonederoException {
        validarMonto(monto);
        if (monto > saldo) {
            throw new SaldoInsuficienteException(saldo, monto);
        }
        saldo -= monto;
    }

    private void validarMonto(double monto) throws MontoInvalidoException {
        if (Double.isNaN(monto) || Double.isInfinite(monto) || monto <= 0) {
            throw new MontoInvalidoException(monto);
        }
    }

    public boolean isSaldoBajo() {
        return saldo < UMBRAL_SALDO_BAJO;
    }

    void registrarTransaccion(Transaccion transaccion) {
        transacciones.add(transaccion);
    }

    public Transaccion buscarTransaccion(String idTransaccion) throws MonederoException {
        return transacciones.stream()
                .filter(t -> t.getId().equals(idTransaccion))
                .findFirst()
                .orElseThrow(() -> new MonederoException("No existe la transacción " + idTransaccion));
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public double getSaldo() { return saldo; }
    public Cliente getPropietario() { return propietario; }
    void setPropietario(Cliente propietario) { this.propietario = propietario; }

    /** Lista de solo lectura: nadie de afuera puede modificar el historial. */
    public List<Transaccion> getTransacciones() {
        return Collections.unmodifiableList(transacciones);
    }

    @Override
    public String toString() {
        return nombre + " [" + id + "] saldo: " + saldo;
    }
}
