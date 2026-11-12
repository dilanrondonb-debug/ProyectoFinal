package co.edu.uniquindio.poo.proyectofinal.viewController;

import co.edu.uniquindio.poo.proyectofinal.controller.ModelFactory;
import co.edu.uniquindio.poo.proyectofinal.controller.TransaccionController;
import co.edu.uniquindio.poo.proyectofinal.controller.Validaciones;
import co.edu.uniquindio.poo.proyectofinal.model.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.util.List;

public class TransaccionesViewController {

    @FXML private ComboBox<TipoTransaccion> cmbTipo;
    @FXML private ComboBox<Monedero> cmbOrigen;
    @FXML private HBox boxDestino;
    @FXML private ComboBox<Cliente> cmbClienteDestino;
    @FXML private ComboBox<Monedero> cmbMonederoDestino;
    @FXML private TextField txtMonto;
    @FXML private Label lblInfo;
    @FXML private Label lblEstado;
    @FXML private ComboBox<Monedero> cmbFiltro;
    @FXML private TableView<Transaccion> tabla;
    @FXML private TableColumn<Transaccion, String> colFecha;
    @FXML private TableColumn<Transaccion, String> colId;
    @FXML private TableColumn<Transaccion, String> colTipo;
    @FXML private TableColumn<Transaccion, String> colMonedero;
    @FXML private TableColumn<Transaccion, String> colDetalle;
    @FXML private TableColumn<Transaccion, String> colMonto;
    @FXML private TableColumn<Transaccion, String> colPuntos;
    @FXML private TableColumn<Transaccion, String> colEstado;

    private final ModelFactory modelo = ModelFactory.getInstance();
    private final TransaccionController controller = new TransaccionController(modelo.getMonederoVirtual());
    private final Cliente cliente = modelo.getClienteActivo();
    private SelectorDestino selector;

    @FXML
    void initialize() {
        Ui.mostrarMonederos(cmbOrigen);
        Ui.mostrarMonederos(cmbFiltro);
        selector = new SelectorDestino(boxDestino, cmbClienteDestino, cmbMonederoDestino);

        cmbTipo.setItems(FXCollections.observableArrayList(TipoTransaccion.values()));
        cmbTipo.valueProperty().addListener((obs, anterior, nuevo) -> {
            selector.mostrar(nuevo != null && nuevo.requiereDestino());
            actualizarInfo();
        });
        cmbOrigen.valueProperty().addListener((obs, anterior, nuevo) -> actualizarInfo());
        txtMonto.textProperty().addListener((obs, anterior, nuevo) -> actualizarInfo());
        cmbFiltro.valueProperty().addListener((obs, anterior, nuevo) -> refrescarTabla());

        Ui.columna(colFecha, t -> Ui.fecha(t.getFecha()));
        Ui.columna(colId, Transaccion::getId);
        Ui.columna(colTipo, t -> TipoTransaccion.de(t).toString());
        Ui.columna(colMonedero, t -> t.getMonedero().getNombre());
        Ui.columna(colDetalle, this::detalle);
        Ui.columna(colMonto, t -> Ui.dinero(t.getMonto()));
        Ui.columna(colPuntos, this::puntos);
        Ui.columna(colEstado, Ui::estado);

        selector.recargar();
        recargarMonederos();
        cmbTipo.setValue(TipoTransaccion.DEPOSITO);
    }

    @FXML
    void onEjecutar() {
        try {
            TipoTransaccion tipo = Validaciones.exigir(cmbTipo.getValue(), "Seleccione el tipo de operación");
            double monto = Validaciones.parsearMonto(txtMonto.getText());
            Transaccion t = controller.ejecutar(tipo, cmbOrigen.getValue(), selector.getMonedero(), monto);
            Ui.ok(lblEstado, tipo + " realizado por " + Ui.dinero(monto) + ". Puntos ganados: " + t.calcularPuntos());
            txtMonto.clear();
            modelo.notificarCambio();
            recargarMonederos();
            selector.recargar();
        } catch (MonederoException e) {
            Ui.fallo(lblEstado, e.getMessage());
            Ui.error(e.getMessage());
        }
    }

    @FXML
    void onRevertir() {
        Transaccion seleccionada = tabla.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            Ui.error("Seleccione una transacción de la tabla");
            return;
        }
        if (!Ui.confirmar("¿Revertir " + TipoTransaccion.de(seleccionada) + " por "
                + Ui.dinero(seleccionada.getMonto()) + "?\nSe devolverá el dinero y se descontarán los puntos.")) {
            return;
        }
        try {
            controller.revertir(cliente, seleccionada);
            Ui.ok(lblEstado, "Transacción " + seleccionada.getId() + " revertida");
            modelo.notificarCambio();
            recargarMonederos();
            selector.recargar();
        } catch (MonederoException e) {
            Ui.fallo(lblEstado, e.getMessage());
            Ui.error(e.getMessage());
        }
    }

    @FXML
    void onVerTodas() {
        cmbFiltro.setValue(null);
    }

    private void recargarMonederos() {
        List<Monedero> monederos = cliente.getMonederos();
        Ui.recargar(cmbOrigen, monederos, true);
        Ui.recargar(cmbFiltro, monederos, false);
        refrescarTabla();
        actualizarInfo();
    }

    private void refrescarTabla() {
        Monedero filtro = cmbFiltro.getValue();
        List<Transaccion> datos = filtro == null ? controller.historial(cliente) : controller.historial(filtro);
        tabla.setItems(FXCollections.observableArrayList(datos));
    }

    /** Para los retiros muestra de antemano cuánto se va a cobrar de comisión. */
    private void actualizarInfo() {
        lblInfo.setText("");
        Monedero origen = cmbOrigen.getValue();
        if (cmbTipo.getValue() != TipoTransaccion.RETIRO || origen == null || txtMonto.getText().isBlank()) {
            return;
        }
        try {
            double monto = Validaciones.parsearMonto(txtMonto.getText());
            double comision = origen.calcularComisionAplicable(monto);
            lblInfo.setText("Comisión: " + Ui.dinero(comision) + " · Total a descontar: " + Ui.dinero(monto + comision));
        } catch (MonederoException e) {
            // Todavía está escribiendo: no se muestra nada.
        }
    }

    private String detalle(Transaccion t) {
        if (t instanceof Transferencia tr) {
            return tr.getMonedero().getPropietario() == cliente
                    ? "Para " + tr.getDestino().getPropietario().getNombre()
                    : "De " + tr.getMonedero().getPropietario().getNombre();
        }
        if (t instanceof Retiro r) {
            return "Comisión " + Ui.dinero(r.getComisionCobrada());
        }
        return "";
    }

    /** Los puntos los gana quien origina la operación; el receptor de una transferencia no suma. */
    private String puntos(Transaccion t) {
        if (t.getMonedero().getPropietario() != cliente) {
            return "—";
        }
        return t.isRevertida() ? "0" : String.valueOf(t.calcularPuntos());
    }
}
