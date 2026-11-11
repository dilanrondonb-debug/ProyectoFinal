package co.edu.uniquindio.poo.proyectofinal.model;

public class NotificadorSms implements INotificable{

    private final String telefono;

    public NotificadorSms(String telefono) { this.telefono = telefono; }

    @Override
    public void enviar(String mensaje) {
        System.out.println("[SMS a " + telefono + "] " + mensaje);
    }
}
