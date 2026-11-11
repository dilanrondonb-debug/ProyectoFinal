package co.edu.uniquindio.poo.proyectofinal.model;

public class ReduccionComision implements IBeneficio{
    @Override
    public String getNombre() { return "10% menos de comisión"; }

    @Override
    public int getCostoPuntos() { return 100; }

    @Override
    public void aplicar(Cliente cliente) {
        cliente.setDescuentoComision(0.10);
    }
}
