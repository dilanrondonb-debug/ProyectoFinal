package co.edu.uniquindio.poo.proyectofinal.model;

public class MonederoAhorros extends  Monedero {
    private static final double TASA = 0.02;

    public MonederoAhorros(String nombre) {
        super(nombre);
    }

    @Override
    public double calcularComision(double monto) {
        return monto * TASA;
    }
}
