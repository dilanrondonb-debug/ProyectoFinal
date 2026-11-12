package co.edu.uniquindio.poo.proyectofinal.controller;

import co.edu.uniquindio.poo.proyectofinal.model.Cliente;
import co.edu.uniquindio.poo.proyectofinal.model.MonederoVirtual;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ClienteController {

    private MonederoVirtual monederoVirtual;

    public ClienteController(MonederoVirtual monederoVirtual) {
        this.monederoVirtual = monederoVirtual;
    }

    // Método para agregar un cliente validando reglas de negocio
    public boolean registrarCliente(String cedula, String nombre, String correo, String telefono) {
        // Validar si ya existe o crear la lógica de excepción personalizada
        Cliente nuevoCliente = new Cliente(cedula, nombre, correo, telefono);
        return monederoVirtual.getClientes().add(nuevoCliente); // Asumiendo que MonederoVirtual tiene una lista de clientes
    }

    // Obtener la lista observable para la TableView
    public ObservableList<Cliente> obtenerListaClientes() {
        if (monederoVirtual.getClientes() != null) {
            return FXCollections.observableArrayList(monederoVirtual.getClientes());
        }
        return FXCollections.observableArrayList();
    }
}