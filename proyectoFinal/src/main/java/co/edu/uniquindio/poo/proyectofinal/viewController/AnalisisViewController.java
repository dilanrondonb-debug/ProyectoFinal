package co.edu.uniquindio.poo.proyectofinal.viewController;

import co.edu.uniquindio.poo.proyectofinal.controller.AnalisisController;
import co.edu.uniquindio.poo.proyectofinal.controller.ModelFactory;
import co.edu.uniquindio.poo.proyectofinal.model.Cliente;
import co.edu.uniquindio.poo.proyectofinal.model.Monedero;
import co.edu.uniquindio.poo.proyectofinal.model.TipoTransaccion;
import co.edu.uniquindio.poo.proyectofinal.model.Transaccion;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;

public class AnalisisViewController {

    @FXML private ComboBox<Monedero> cmbMonedero;
    @FXML private Label lblTotal;
    @FXML private Label lblPromedio;
    @FXML private Label lblDesviacion;
    @FXML private Label lblUmbral;
    @FXML private TableView<Transaccion> tablaAtipicas;
    @FXML private TableColumn<Transaccion, String> colFecha;
    @FXML private TableColumn<Transaccion, String> colTipo;
    @FXML private TableColumn<Transaccion, String> colMonedero;
    @FXML private TableColumn<Transaccion, String> colMonto;
    @FXML private TableColumn<Transaccion, String> colExceso;
    @FXML private PieChart graficoTipos;
    @FXML private ListView<String> lstAlertas;

    private final ModelFactory modelo = ModelFactory.getInstance();
    private final AnalisisController controller = new AnalisisController(modelo.getMonederoVirtual());
    private final Cliente cliente = modelo.getClienteActivo();
    private double umbralActual;

    @FXML
    void initialize() {
        Ui.mostrarMonederos(cmbMonedero);
        Ui.recargar(cmbMonedero, cliente.getMonederos(), false); // sin selección = todos los monederos
        cmbMonedero.valueProperty().addListener((obs, anterior, nuevo) -> analizar());

        Ui.columna(colFecha, t -> Ui.fecha(t.getFecha()));
        Ui.columna(colTipo, t -> TipoTransaccion.de(t).toString());
        Ui.columna(colMonedero, t -> t.getMonedero().getNombre());
        Ui.columna(colMonto, t -> Ui.dinero(t.getMonto()));
        Ui.columna(colExceso, t -> Ui.dinero(t.getMonto() - umbralActual));

        mostrarAlertas();
        analizar();
    }

    @FXML
    void onTodos() {
        cmbMonedero.setValue(null);
    }

    @FXML
    void onLimpiarAlertas() {
        modelo.limpiarAlertas();
        mostrarAlertas();
    }

    private void analizar() {
        AnalisisController.Resultado r = controller.analizar(cliente, cmbMonedero.getValue());
        umbralActual = r.umbral();
        lblTotal.setText(String.valueOf(r.total()));
        lblPromedio.setText(Ui.dinero(r.promedio()));
        lblDesviacion.setText(Ui.dinero(r.desviacion()));
        lblUmbral.setText(r.total() < 2 ? "Se necesitan al menos 2 transacciones" : Ui.dinero(r.umbral()));
        tablaAtipicas.setItems(FXCollections.observableArrayList(r.atipicas()));

        graficoTipos.setData(FXCollections.observableArrayList(
                r.montoPorTipo().entrySet().stream()
                        .map(e -> new PieChart.Data(nombreTipo(e.getKey()), e.getValue()))
                        .toList()));
    }

    private void mostrarAlertas() {
        lstAlertas.setItems(FXCollections.observableArrayList(modelo.getAlertas()));
    }

    private String nombreTipo(String claseTransaccion) {
        return "Deposito".equals(claseTransaccion) ? "Depósito" : claseTransaccion;
    }
}
