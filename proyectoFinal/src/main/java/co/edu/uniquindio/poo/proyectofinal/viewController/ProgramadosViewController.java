package co.edu.uniquindio.poo.proyectofinal.viewController;

import co.edu.uniquindio.poo.proyectofinal.controller.ModelFactory;
import co.edu.uniquindio.poo.proyectofinal.controller.TransaccionController;
import co.edu.uniquindio.poo.proyectofinal.controller.Validaciones;
import co.edu.uniquindio.poo.proyectofinal.model.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.util.List;

public class ProgramadosViewController {

    @FXML private ComboBox<TipoTransaccion> cmbTipo;
    @FXML private ComboBox<Monedero> cmbOrigen;
    @FXML private HBox boxDestino;
    @FXML private ComboBox<Cliente> cmbClienteDestino;
    @FXML private ComboBox<Monedero> cmbMonederoDestino;
    @FXML private TextField txtMonto;
    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<Frecuencia> cmbFrecuencia;
    @FXML private Label lblEstado;
    @FXML private TableView<TransaccionProgramada> tabla;
    @FXML private TableColumn<TransaccionProgramada, String> colFecha;
    @FXML private TableColumn<TransaccionProgramada, String> colFrecuencia;
    @FXML private TableColumn<TransaccionProgramada, String> colTipo;
    @FXML private TableColumn<TransaccionProgramada, String> colMonedero;
    @FXML private TableColumn<TransaccionProgramada, String> colDetalle;
    @FXML private TableColumn<TransaccionProgramada, String> colMonto;

    private final ModelFactory modelo = ModelFactory.getInstance();
    private final TransaccionController controller = new TransaccionController(modelo.getMonederoVirtual());
    private final Cliente cliente = modelo.getClienteActivo();
    private SelectorDestino selector;

    @FXML
    void initialize() {
        Ui.mostrarMonederos(cmbOrigen);
        selector = new SelectorDestino(boxDestino, cmbClienteDestino, cmbMonederoDestino);

        cmbTipo.setItems(FXCollections.observableArrayList(TipoTransaccion.values()));
        cmbTipo.valueProperty().addListener((obs, anterior, nuevo) ->
                selector.mostrar(nuevo != null && nuevo.requiereDestino()));
        cmbTipo.setValue(TipoTransaccion.DEPOSITO);

        cmbFrecuencia.setItems(FXCollections.observableArrayList(Frecuencia.values()));
        cmbFrecuencia.setValue(Frecuencia.MENSUAL);
        dpFecha.setValue(LocalDate.now().plusDays(1));

        Ui.columna(colFecha, p -> p.getFechaEjecucion().toString());
        Ui.columna(colFrecuencia, p -> p.getFrecuencia().name());
        Ui.columna(colTipo, p -> TipoTransaccion.de(p.getTransaccion()).toString());
        Ui.columna(colMonedero, p -> p.getTransaccion().getMonedero().getNombre());
        Ui.columna(colDetalle, p -> p.getTransaccion() instanceof Transferencia tr
                ? "Para " + tr.getDestino().getPropietario().getNombre() + " (" + tr.getDestino().getNombre() + ")"
                : "");
        Ui.columna(colMonto, p -> Ui.dinero(p.getTransaccion().getMonto()));

        selector.recargar();
        recargarMonederos();
        refrescarTabla();
    }

    @FXML
    void onProgramar() {
        try {
            TipoTransaccion tipo = Validaciones.exigir(cmbTipo.getValue(), "Seleccione el tipo de operación");
            double monto = Validaciones.parsearMonto(txtMonto.getText());
            controller.programar(tipo, cmbOrigen.getValue(), selector.getMonedero(), monto,
                    dpFecha.getValue(), cmbFrecuencia.getValue());
            Ui.ok(lblEstado, tipo + " programado para el " + dpFecha.getValue());
            txtMonto.clear();
            refrescarTabla();
        } catch (MonederoException e) {
            Ui.fallo(lblEstado, e.getMessage());
            Ui.error(e.getMessage());
        }
    }

    @FXML
    void onProcesar() {
        List<String> resultados = controller.procesarVencidas();
        if (resultados.isEmpty()) {
            Ui.info("Pagos programados", "No hay transacciones programadas que ya estén vencidas.");
        } else {
            Ui.info("Pagos programados", String.join("\n\n", resultados));
        }
        modelo.notificarCambio();
        recargarMonederos();
        refrescarTabla();
    }

    @FXML
    void onCancelar() {
        try {
            controller.cancelarProgramada(tabla.getSelectionModel().getSelectedItem());
            Ui.ok(lblEstado, "Transacción programada cancelada");
            refrescarTabla();
        } catch (MonederoException e) {
            Ui.error(e.getMessage());
        }
    }

    private void recargarMonederos() {
        Ui.recargar(cmbOrigen, cliente.getMonederos(), true);
    }

    private void refrescarTabla() {
        tabla.setItems(FXCollections.observableArrayList(controller.programadasDe(cliente)));
    }
}
