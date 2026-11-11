package co.edu.uniquindio.poo.proyectofinal.model;

//Regla de negocio: la cédula buscada no pertenece a ningún cliente registrado.
public class ClienteNoEncontradoException extends MonederoException {
    public ClienteNoEncontradoException(String cedula) {
        super("No existe un cliente con cédula " + cedula);
    }
}

