package co.edu.uniquindio.poo.proyectofinal.controller;

import co.edu.uniquindio.poo.proyectofinal.model.DatoInvalidoException;

/** Validaciones de lo que escribe el usuario en la interfaz, antes de llegar al modelo. */
public final class Validaciones {
    private Validaciones() { }

    public static double parsearMonto(String texto) throws DatoInvalidoException {
        if (texto == null || texto.isBlank()) {
            throw new DatoInvalidoException("Escriba un monto");
        }
        try {
            return Double.parseDouble(texto.trim().replace(" ", "").replace(",", "."));
        } catch (NumberFormatException e) {
            throw new DatoInvalidoException("El monto debe ser un número (ejemplo: 25000)");
        }
    }

    public static <T> T exigir(T valor, String mensaje) throws DatoInvalidoException {
        if (valor == null) {
            throw new DatoInvalidoException(mensaje);
        }
        return valor;
    }
}
