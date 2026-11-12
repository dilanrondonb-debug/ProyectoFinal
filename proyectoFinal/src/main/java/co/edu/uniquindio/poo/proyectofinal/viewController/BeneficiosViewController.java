package co.edu.uniquindio.poo.proyectofinal.viewController;

import co.edu.uniquindio.poo.proyectofinal.controller.BeneficioController;
import co.edu.uniquindio.poo.proyectofinal.controller.ModelFactory;
import co.edu.uniquindio.poo.proyectofinal.model.Cliente;
import co.edu.uniquindio.poo.proyectofinal.model.IBeneficio;
import co.edu.uniquindio.poo.proyectofinal.model.MonederoException;
import co.edu.uniquindio.poo.proyectofinal.model.RegistroPuntos;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class BeneficiosViewController {

    @FXML private Label lblPuntos;
    @FXML private Label lblRango;
    @FXML private ProgressBar barraRango;
    @FXML private Label lblSiguienteRango;
    @FXML private Label lblDescuento;
    @FXML private Label lblMesSinCargos;
    @FXML private Label lblEstado;
    @FXML private TableView<IBeneficio> tablaBeneficios;
    @FXML private TableColumn<IBeneficio, String> colBeneficio;
    @FXML private TableColumn<IBeneficio, String> colCosto;
    @FXML private TableView<RegistroPuntos> tablaPuntos;
    @FXML private TableColumn<RegistroPuntos, String> colFecha;
    @FXML private TableColumn<RegistroPuntos, String> colPuntosMov;
    @FXML private TableColumn<RegistroPuntos, String> colMotivo;

    private final ModelFactory modelo = ModelFactory.getInstance();
    private final BeneficioController controller = new BeneficioController(modelo.getMonederoVirtual());
    private final Cliente cliente = modelo.getClienteActivo();

    @FXML
    void initialize() {
        Ui.columna(colBeneficio, IBeneficio::getNombre);
        Ui.columna(colCosto, b -> b.getCostoPuntos() + " pts");
        Ui.columna(colFecha, r -> Ui.fecha(r.getFecha()));
        Ui.columna(colPuntosMov, r -> (r.getPuntos() >= 0 ? "+" : "") + r.getPuntos());
        Ui.columna(colMotivo, RegistroPuntos::getMotivo);
        tablaBeneficios.setItems(FXCollections.observableArrayList(controller.getCatalogo()));
        refrescar();
    }

    @FXML
    void onCanjear() {
        IBeneficio beneficio = tablaBeneficios.getSelectionModel().getSelectedItem();
        try {
            controller.canjear(cliente, beneficio);
            Ui.ok(lblEstado, "Canjeaste: " + beneficio.getNombre());
            modelo.notificarCambio();
            refrescar();
        } catch (MonederoException e) {
            Ui.fallo(lblEstado, e.getMessage());
            Ui.error(e.getMessage());
        }
    }

    private void refrescar() {
        lblPuntos.setText(cliente.getPuntos() + " pts");
        lblRango.setText("Rango " + Ui.rango(cliente.getRango()));
        barraRango.setProgress(controller.progresoSiguienteRango(cliente));
        lblSiguienteRango.setText(controller.textoSiguienteRango(cliente));
        lblDescuento.setText(cliente.getDescuentoComision() > 0
                ? "Descuento en comisión: " + Math.round(cliente.getDescuentoComision() * 100) + "%"
                : "Descuento en comisión: ninguno");
        lblMesSinCargos.setText(cliente.tieneMesSinCargos()
                ? "Retiros sin cargo hasta: " + cliente.getMesSinCargosHasta()
                : "Retiros sin cargo: inactivo");
        tablaPuntos.setItems(FXCollections.observableArrayList(controller.historialPuntos(cliente)));
    }
}
