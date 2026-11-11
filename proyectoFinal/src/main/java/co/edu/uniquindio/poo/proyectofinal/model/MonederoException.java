package co.edu.uniquindio.poo.proyectofinal.model;
//Excepción base del proyecto. Todas las excepciones de negocio heredan de ella.
public class MonederoException extends Exception {
    public MonederoException(String mensaje) {
        super(mensaje);
    }
}
