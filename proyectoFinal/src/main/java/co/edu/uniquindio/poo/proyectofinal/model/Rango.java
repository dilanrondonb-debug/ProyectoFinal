package co.edu.uniquindio.poo.proyectofinal.model;

import java.util.Arrays;

public enum Rango {
    BRONCE(0, 500),
    PLATA(501, 1000),
    ORO(1001, 5000),
    PLATINO(5001, Integer.MAX_VALUE);

    private final int minimo;
    private final int maximo;

    Rango(int minimo, int maximo) {
        this.minimo = minimo;
        this.maximo = maximo;
    }

    public int getMinimo() { return minimo; }
    public int getMaximo() { return maximo; }

    /** Los valores están en orden, así que basta con buscar el primero cuyo máximo alcance. */
    public static Rango desdePuntos(int puntos) {
        return Arrays.stream(values())
                .filter(rango -> puntos <= rango.maximo)
                .findFirst()
                .orElse(PLATINO);
    }
}
