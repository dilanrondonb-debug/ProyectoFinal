package co.edu.uniquindio.poo.proyectofinal.model;

public interface IBeneficio {
    String getNombre();
    int getCostoPuntos();
    void aplicar(Cliente cliente) throws MonederoException;
}
