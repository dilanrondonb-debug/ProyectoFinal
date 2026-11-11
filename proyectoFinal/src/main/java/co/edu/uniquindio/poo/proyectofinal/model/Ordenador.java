package co.edu.uniquindio.poo.proyectofinal.model;

import java.util.List;

public final class Ordenador {
    private Ordenador() { }

    public static <T extends Comparable<? super T>> void ordenarPorInsercion(List<T> lista) {
        for (int i = 1; i < lista.size(); i++) {
            T clave = lista.get(i);
            int j = i - 1;
            while (j >= 0 && lista.get(j).compareTo(clave) > 0) {
                lista.set(j + 1, lista.get(j));
                j--;
            }
            lista.set(j + 1, clave);
        }
    }
}
