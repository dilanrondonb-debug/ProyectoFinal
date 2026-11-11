package co.edu.uniquindio.poo.proyectofinal.model;

//Regla de negocio: no se puede retirar ni transferir más de lo que hay en el monedero.
public class SaldoInsuficienteException extends MonederoException {
    public SaldoInsuficienteException(double saldo, double requerido) {
        super(String.format("Saldo insuficiente: tiene %.2f y necesita %.2f", saldo, requerido));
    }
}
