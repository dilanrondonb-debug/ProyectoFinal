package co.edu.uniquindio.poo.proyectofinal.model;

import java.util.function.Consumer;

/** Notificador que entrega el mensaje a la interfaz gráfica en lugar de imprimirlo en consola. */
public class NotificadorPantalla implements INotificable {
    private final Consumer<String> destino;

    public NotificadorPantalla(Consumer<String> destino) { this.destino = destino; }

    @Override
    public void enviar(String mensaje) { destino.accept(mensaje); }
}
