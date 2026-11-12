package co.edu.uniquindio.poo.proyectofinal.viewController;

/** Pantallas que se pueden mostrar en el área central. */
public enum Vista {
    RESUMEN("resumen.fxml"),
    TRANSACCIONES("transacciones.fxml"),
    PROGRAMADOS("programados.fxml"),
    ANALISIS("analisis.fxml"),
    BENEFICIOS("beneficios.fxml"),
    MONEDEROS("monederos.fxml"),
    CLIENTES("crudCliente.fxml");

    private final String archivo;

    Vista(String archivo) { this.archivo = archivo; }

    public String getArchivo() { return archivo; }
}
