package co.edu.uniquindio.poo.proyectofinal.viewController;

import co.edu.uniquindio.poo.proyectofinal.App;
import co.edu.uniquindio.poo.proyectofinal.model.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/** Utilidades compartidas por las pantallas: formatos, mensajes y configuración de tablas/combos. */
public final class Ui {
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Ui() { }

    // ---------- Formatos ----------

    public static String dinero(double valor) { return String.format("$ %,.2f", valor); }

    public static String fecha(LocalDateTime fecha) { return fecha.format(FORMATO_FECHA); }

    public static String rango(Rango rango) {
        String n = rango.name();
        return n.charAt(0) + n.substring(1).toLowerCase();
    }

    public static String estado(Transaccion t) { return t.isRevertida() ? "Revertida" : "Ejecutada"; }

    // ---------- Mensajes ----------

    public static void error(String mensaje) { mostrar(Alert.AlertType.ERROR, "No se pudo completar", mensaje); }

    public static void info(String titulo, String mensaje) { mostrar(Alert.AlertType.INFORMATION, titulo, mensaje); }

    public static boolean confirmar(String mensaje) {
        Alert alerta = crearAlerta(Alert.AlertType.CONFIRMATION, "Confirmar", mensaje);
        Optional<ButtonType> respuesta = alerta.showAndWait();
        return respuesta.isPresent() && respuesta.get() == ButtonType.OK;
    }

    private static void mostrar(Alert.AlertType tipo, String titulo, String mensaje) {
        crearAlerta(tipo, titulo, mensaje).showAndWait();
    }

    private static Alert crearAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.getDialogPane().getStylesheets().add(App.class.getResource("styles.css").toExternalForm());
        return alerta;
    }

    /** Mensaje verde junto al formulario. */
    public static void ok(Label etiqueta, String texto) {
        etiqueta.getStyleClass().remove("msg-error");
        if (!etiqueta.getStyleClass().contains("msg-ok")) {
            etiqueta.getStyleClass().add("msg-ok");
        }
        etiqueta.setText(texto);
    }

    /** Mensaje rojo junto al formulario. */
    public static void fallo(Label etiqueta, String texto) {
        etiqueta.getStyleClass().remove("msg-ok");
        if (!etiqueta.getStyleClass().contains("msg-error")) {
            etiqueta.getStyleClass().add("msg-error");
        }
        etiqueta.setText(texto);
    }

    // ---------- Tablas y combos ----------

    public static <T> void columna(TableColumn<T, String> columna, Function<T, String> valor) {
        columna.setCellValueFactory(celda -> new SimpleStringProperty(valor.apply(celda.getValue())));
    }

    public static String textoMonedero(Monedero m) {
        return m.getNombre() + " (" + TipoMonedero.de(m) + ") · " + dinero(m.getSaldo());
    }

    /** Hace que un ComboBox de monederos muestre nombre, tipo y saldo en vez del toString del modelo. */
    public static void mostrarMonederos(ComboBox<Monedero> combo) {
        combo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Monedero m) { return m == null ? "" : textoMonedero(m); }

            @Override
            public Monedero fromString(String texto) { return null; }
        });
    }

    /** Recarga los monederos del combo (con saldos nuevos) conservando la selección si se puede. */
    public static void recargar(ComboBox<Monedero> combo, List<Monedero> monederos, boolean seleccionarPrimero) {
        Monedero previo = combo.getValue();
        combo.setItems(FXCollections.observableArrayList(monederos));
        combo.setValue(null);
        if (previo != null && monederos.contains(previo)) {
            combo.setValue(previo);
        } else if (seleccionarPrimero && !monederos.isEmpty()) {
            combo.setValue(monederos.get(0));
        }
    }
}
