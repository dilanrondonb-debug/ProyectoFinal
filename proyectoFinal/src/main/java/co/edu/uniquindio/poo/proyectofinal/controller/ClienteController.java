package co.edu.uniquindio.poo.proyectofinal.controller;

import co.edu.uniquindio.poo.proyectofinal.model.*;

import java.util.List;

public class ClienteController {

    private final MonederoVirtual monederoVirtual;

    public ClienteController(MonederoVirtual monederoVirtual) {
        this.monederoVirtual = monederoVirtual;
    }

    public List<Cliente> listar() {
        return monederoVirtual.getClientes();
    }

    public Cliente agregarCliente(String cedula, String nombre, String correo, String telefono)
            throws DatoInvalidoException {
        Cliente cliente = new Cliente(cedula.trim(), nombre, correo.trim(), telefono.trim());
        monederoVirtual.registrarCliente(cliente);
        return cliente;
    }

    public void actualizarCliente(String cedula, String nombre, String correo, String telefono)
            throws MonederoException {
        monederoVirtual.actualizarCliente(cedula, nombre, correo.trim(), telefono.trim());
    }

    public void eliminarCliente(String cedula) throws ClienteNoEncontradoException {
        monederoVirtual.eliminarCliente(cedula);
    }

    public Monedero agregarMonedero(Cliente cliente, TipoMonedero tipo, String nombre) throws MonederoException {
        Validaciones.exigir(cliente, "Seleccione un cliente");
        Validaciones.exigir(tipo, "Seleccione el tipo de monedero");
        if (nombre == null || nombre.isBlank()) {
            throw new DatoInvalidoException("El monedero necesita un nombre");
        }
        Monedero monedero = tipo.crear(nombre.trim());
        monederoVirtual.agregarMonedero(cliente.getCedula(), monedero);
        return monedero;
    }
}
