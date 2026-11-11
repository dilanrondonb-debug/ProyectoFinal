package co.edu.uniquindio.poo.proyectofinal.model;

public class Retiro extends Transaccion{
    private double comisionCobrada;

    public Retiro(Monedero origen, double monto) throws MonederoException {
        super(origen, monto);
    }

    @Override
    public void ejecutar() throws MonederoException {
        exigirNoEjecutada();
        double comision = getMonedero().calcularComisionAplicable(getMonto());
        getMonedero().retirar(getMonto() + comision);
        comisionCobrada = comision;
        finalizarEjecucion();
    }

    @Override
    protected void deshacer() throws MonederoException {
        getMonedero().depositar(getMonto() + comisionCobrada);
    }

    @Override
    public int calcularPuntos() {
        return (int) (getMonto() / 100) * 2;
    }

    public double getComisionCobrada() { return comisionCobrada; }
}
