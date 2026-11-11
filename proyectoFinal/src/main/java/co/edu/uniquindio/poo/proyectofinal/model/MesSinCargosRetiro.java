package co.edu.uniquindio.poo.proyectofinal.model;

import java.time.LocalDate;

public class MesSinCargosRetiro implements IBeneficio{
    @Override
    public String getNombre() { return "Un mes sin cargos por retiro"; }

    @Override
    public int getCostoPuntos() { return 500; }

    @Override
    public void aplicar(Cliente cliente) {
        cliente.setMesSinCargosHasta(LocalDate.now().plusMonths(1));
    }
}
