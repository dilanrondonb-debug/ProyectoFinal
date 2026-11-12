package co.edu.uniquindio.poo.proyectofinal.viewController;

import co.edu.uniquindio.poo.proyectofinal.controller.ClienteController;
import co.edu.uniquindio.poo.proyectofinal.controller.ModelFactory;
import co.edu.uniquindio.poo.proyectofinal.model.Cliente;
import co.edu.uniquindio.poo.proyectofinal.model.Monedero;
import co.edu.uniquindio.poo.proyectofinal.model.MonederoException;
import co.edu.uniquindio.poo.proyectofinal.model.TipoMonedero;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class MonederosViewController {

    @FXML private ComboBox<TipoMonedero> cmbTipo;
    @FXML private TextField txtNombre;
    @FXML private Label lblEstado;
    @FXML private TableView<Monedero> tabla;
    @FXML private TableColumn<Monedero, String> colNombre;
    @FXML private TableColumn<Monedero, String> colTipo;
    @FXML private TableColumn<Monedero, String> colId;
    @FXML private TableColumn<Monedero, String> colSaldo;
    @FXML private TableColumn<Monedero, String> colComision;
    @FXML private TableColumn<Monedero, String> colEstado;

    private final ModelFactory modelo = ModelFactory.getInstance();
    private final ClienteController controller = new ClienteController(modelo.getMonederoVirtual());
    private final Cliente cliente = modelo.getClienteActivo();

    @FXML
    void initialize() {
        cmbTipo.setItems(FXCollections.observableArrayList(TipoMonedero.values()));
        cmbTipo.setValue(TipoMonedero.AHORROS);

        Ui.columna(colNombre, Monedero::getNombre);
        Ui.columna(colTipo, m -> TipoMonedero.de(m).toString());
        Ui.columna(colId, Monedero::getId);
        Ui.columna(colSaldo, m -> Ui.dinero(m.getSaldo()));
        // Comisión que realmente se aplicaría hoy a un retiro de 100 (ya con los beneficios canjeados).
        Ui.columna(colComision, m -> String.format("%.2f%%", m.calcularComisionAplicable(100)));
        Ui.columna(colEstado, m -> m.isSaldoBajo() ? "Saldo bajo" : "Normal");
        refrescar();
    }

    @FXML
    void onAgregar() {
        try {
            Monedero nuevo = controller.agregarMonedero(cliente, cmbTipo.getValue(), txtNombre.getText());
            Ui.ok(lblEstado, "Monedero «" + nuevo.getNombre() + "» creado");
            txtNombre.clear();
            modelo.notificarCambio();
            refrescar();
        } catch (MonederoException e) {
            Ui.fallo(lblEstado, e.getMessage());
            Ui.error(e.getMessage());
        }
    }

    private void refrescar() {
        tabla.setItems(FXCollections.observableArrayList(cliente.getMonederos()));
    }
}
