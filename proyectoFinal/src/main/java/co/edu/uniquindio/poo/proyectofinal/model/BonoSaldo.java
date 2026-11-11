package co.edu.uniquindio.poo.proyectofinal.model;

public class BonoSaldo implements IBeneficio{
    private static final double BONO = 50;

    @Override
    public String getNombre() { return "Bono de " + BONO + " en saldo"; }

    @Override
    public int getCostoPuntos() { return 1000; }

    @Override
    public void aplicar(Cliente cliente) throws MonederoException {
        if (cliente.getMonederos().isEmpty()) {
            throw new MonederoException("El cliente no tiene monederos para recibir el bono");
        }
        cliente.getMonederos().get(0).depositar(BONO);
    }
}
