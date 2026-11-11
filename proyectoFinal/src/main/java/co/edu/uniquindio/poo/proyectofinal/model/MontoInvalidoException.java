package co.edu.uniquindio.poo.proyectofinal.model;

//Regla de negocio: todo monto debe ser un número mayor que cero.
public class MontoInvalidoException extends MonederoException {
    public MontoInvalidoException(double monto) {
        super("Monto inválido: " + monto + ". Debe ser mayor que cero");
    }
}

