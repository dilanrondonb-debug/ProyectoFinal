package co.edu.uniquindio.poo.proyectofinal.model;

public class Transferencia extends Transaccion{
    private final Monedero destino;

    public Transferencia(Monedero origen, Monedero destino, double monto) throws MonederoException {
        super(origen, monto);
        if (destino == null || destino == origen) {
            throw new MonederoException("El monedero destino debe existir y ser distinto del origen");
        }
        this.destino = destino;
    }

    @Override
    public void ejecutar() throws MonederoException {
        exigirNoEjecutada();
        getMonedero().retirar(getMonto());
        destino.depositar(getMonto());
        destino.registrarTransaccion(this);
        finalizarEjecucion();
    }

    @Override
    protected void deshacer() throws MonederoException {
        destino.retirar(getMonto());
        getMonedero().depositar(getMonto());
    }

    @Override
    public int calcularPuntos() {
        return (int) (getMonto() / 100) * 3;
    }

    public Monedero getDestino() { return destino; }
}
