package co.edu.uniquindio.poo.proyectofinal.controller;

import co.edu.uniquindio.poo.proyectofinal.model.MonederoVirtual;

public class ClienteController {

    private MonederoVirtual monederoVirtual;

    // Constructor que recibe el modelo instanciado
    public ClienteController(MonederoVirtual monederoVirtual) {
        this.monederoVirtual = monederoVirtual;
    }

    // Métodos de negocio que llaman al modelo...
    // public void agregarCliente(String nombre, String id) {
    //     monederoVirtual.agregarCliente(new Cliente(...));
    // }
}