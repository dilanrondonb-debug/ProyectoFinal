package co.edu.uniquindio.poo.proyectofinal.viewController;

import co.edu.uniquindio.poo.proyectofinal.controller.ModelFactory;
import co.edu.uniquindio.poo.proyectofinal.controller.TransaccionController;
import co.edu.uniquindio.poo.proyectofinal.model.Cliente;
import co.edu.uniquindio.poo.proyectofinal.model.TipoTransaccion;
import co.edu.uniquindio.poo.proyectofinal.model.Transaccion;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ResumenViewController {

    @FXML private Label lblSaldo;
    @FXML private Label lblDetalleSaldo;
    @FXML private Label lblPuntos;
    @FXML private Label lblRango;
    @FXML private TableView<Transaccion> tabla;
    @FXML private TableColumn<Transaccion, String> colFecha;
    @FXML private TableColumn<Transaccion, String> colTipo;
    @FXML private TableColumn<Transaccion, String> colMonedero;
    @FXML private TableColumn<Transaccion, String> colMonto;
    @FXML private TableColumn<Transaccion, String> colEstado;

    @FXML
    void initialize() {
        ModelFactory modelo = ModelFactory.getInstance();
        TransaccionController controller = new TransaccionController(modelo.getMonederoVirtual());
        Cliente cliente = modelo.getClienteActivo();

        lblSaldo.setText(Ui.dinero(cliente.getSaldoTotal()));
        lblDetalleSaldo.setText(cliente.getMonederos().size() + " monedero(s) · " + cliente.getNombre());
        lblPuntos.setText(cliente.getPuntos() + " pts");
        lblRango.setText("Rango " + Ui.rango(cliente.getRango()));

        Ui.columna(colFecha, t -> Ui.fecha(t.getFecha()));
        Ui.columna(colTipo, t -> TipoTransaccion.de(t).toString());
        Ui.columna(colMonedero, t -> t.getMonedero().getNombre());
        Ui.columna(colMonto, t -> Ui.dinero(t.getMonto()));
        Ui.columna(colEstado, Ui::estado);
        tabla.setItems(FXCollections.observableArrayList(
                controller.historial(cliente).stream().limit(10).toList()));
    }

    @FXML
    void onCanjear() {
        Navegador.ir(Vista.BENEFICIOS);
    }
}
