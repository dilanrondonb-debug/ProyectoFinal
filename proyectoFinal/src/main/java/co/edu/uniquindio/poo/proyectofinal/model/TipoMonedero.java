package co.edu.uniquindio.poo.proyectofinal.model;

/** Tipos de monedero que el cliente puede crear. Cada valor sabe construir su propio monedero. */
public enum TipoMonedero {
    AHORROS("Ahorros") {
        @Override
        public Monedero crear(String nombre) { return new MonederoAhorros(nombre); }
    },
    GASTOS("Gastos") {
        @Override
        public Monedero crear(String nombre) { return new MonederoGastos(nombre); }
    };

    private final String etiqueta;

    TipoMonedero(String etiqueta) { this.etiqueta = etiqueta; }

    public abstract Monedero crear(String nombre);

    public static TipoMonedero de(Monedero monedero) {
        return monedero instanceof MonederoAhorros ? AHORROS : GASTOS;
    }

    @Override
    public String toString() { return etiqueta; }
}
