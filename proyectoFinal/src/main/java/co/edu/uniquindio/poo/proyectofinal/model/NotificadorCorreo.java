package co.edu.uniquindio.poo.proyectofinal.model;

public class NotificadorCorreo implements INotificable{
    private final String correo;

    public NotificadorCorreo(String correo) { this.correo = correo; }

    @Override
    public void enviar(String mensaje) {
        System.out.println("[Correo a " + correo + "] " + mensaje);
    }
}
