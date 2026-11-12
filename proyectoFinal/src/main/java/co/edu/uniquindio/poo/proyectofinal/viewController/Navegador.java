package co.edu.uniquindio.poo.proyectofinal.viewController;

import java.util.function.Consumer;

/** Permite que cualquier pantalla pida cambiar a otra (por ejemplo, de Resumen a Beneficios). */
public final class Navegador {
    private static Consumer<Vista> destino;

    private Navegador() { }

    static void registrar(Consumer<Vista> consumidor) { destino = consumidor; }

    public static void ir(Vista vista) {
        if (destino != null) {
            destino.accept(vista);
        }
    }
}
