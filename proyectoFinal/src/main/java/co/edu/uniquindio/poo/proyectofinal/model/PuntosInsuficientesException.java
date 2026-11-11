package co.edu.uniquindio.poo.proyectofinal.model;

//Regla de negocio: no se puede canjear un beneficio sin los puntos necesarios.
public class PuntosInsuficientesException extends MonederoException {
    public PuntosInsuficientesException(int tiene, int necesita) {
        super("Puntos insuficientes: tiene " + tiene + " y necesita " + necesita);
    }
}