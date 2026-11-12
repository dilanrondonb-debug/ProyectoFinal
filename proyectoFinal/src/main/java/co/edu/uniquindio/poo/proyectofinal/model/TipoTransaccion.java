package co.edu.uniquindio.poo.proyectofinal.model;

/** Tipos de transacción disponibles en la interfaz. Cada valor construye su transacción (polimorfismo). */
public enum TipoTransaccion {
    DEPOSITO("Depósito") {
        @Override
        public Transaccion crear(Monedero origen, Monedero destino, double monto) throws MonederoException {
            return new Deposito(origen, monto);
        }
    },
    RETIRO("Retiro") {
        @Override
        public Transaccion crear(Monedero origen, Monedero destino, double monto) throws MonederoException {
            return new Retiro(origen, monto);
        }
    },
    TRANSFERENCIA("Transferencia") {
        @Override
        public Transaccion crear(Monedero origen, Monedero destino, double monto) throws MonederoException {
            return new Transferencia(origen, destino, monto);
        }
    };

    private final String etiqueta;

    TipoTransaccion(String etiqueta) { this.etiqueta = etiqueta; }

    /** Para depósitos y retiros el destino se ignora. */
    public abstract Transaccion crear(Monedero origen, Monedero destino, double monto) throws MonederoException;

    public boolean requiereDestino() { return this == TRANSFERENCIA; }

    public static TipoTransaccion de(Transaccion transaccion) {
        if (transaccion instanceof Deposito) return DEPOSITO;
        if (transaccion instanceof Retiro) return RETIRO;
        return TRANSFERENCIA;
    }

    /** Crea una transacción nueva (sin ejecutar) igual a la dada; se usa al reprogramar. */
    public static Transaccion copiarDe(Transaccion t) throws MonederoException {
        Monedero destino = t instanceof Transferencia tr ? tr.getDestino() : null;
        return de(t).crear(t.getMonedero(), destino, t.getMonto());
    }

    @Override
    public String toString() { return etiqueta; }
}
