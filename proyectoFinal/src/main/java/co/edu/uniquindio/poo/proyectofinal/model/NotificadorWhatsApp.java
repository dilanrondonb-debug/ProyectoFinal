package co.edu.uniquindio.poo.proyectofinal.model;

public class NotificadorWhatsApp implements INotificable{
    private final String telefono;

    public NotificadorWhatsApp(String telefono) { this.telefono = telefono; }

    @Override
    public void enviar(String mensaje) {
        System.out.println("[WhatsApp a " + telefono + "] " + mensaje);
    }
}
