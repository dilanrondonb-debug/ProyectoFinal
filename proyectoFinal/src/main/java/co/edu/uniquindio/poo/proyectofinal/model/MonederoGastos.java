package co.edu.uniquindio.poo.proyectofinal.model;

public class MonederoGastos extends Monedero{
    private static final double TASA = 0.01;

    public MonederoGastos(String nombre) {
        super(nombre);
    }

    @Override
    public double calcularComision(double monto) {
        return monto * TASA;
    }
}
