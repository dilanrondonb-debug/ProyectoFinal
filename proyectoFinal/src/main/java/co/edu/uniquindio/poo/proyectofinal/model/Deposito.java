package co.edu.uniquindio.poo.proyectofinal.model;

public class Deposito extends Transaccion {
    public Deposito(Monedero destino, double monto) throws MonederoException {
        super(destino, monto);
    }

    @Override
    public void ejecutar() throws MonederoException {
        exigirNoEjecutada();
        getMonedero().depositar(getMonto());
        finalizarEjecucion();
    }

    @Override
    protected void deshacer() throws MonederoException {
        getMonedero().retirar(getMonto());
    }

    @Override
    public int calcularPuntos() {
        return (int) (getMonto() / 100) * 1;
    }
}
