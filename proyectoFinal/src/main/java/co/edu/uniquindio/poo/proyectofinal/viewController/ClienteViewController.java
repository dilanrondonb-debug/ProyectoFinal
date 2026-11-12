package co.edu.uniquindio.poo.proyectofinal.viewController;

import co.edu.uniquindio.poo.proyectofinal.controller.ClienteController;
import co.edu.uniquindio.poo.proyectofinal.controller.ModelFactory;
import co.edu.uniquindio.poo.proyectofinal.model.Cliente;
import co.edu.uniquindio.poo.proyectofinal.model.MonederoException;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class ClienteViewController {

    @FXML private TextField txtCedula;
    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private Label lblEstado;
    @FXML private TableView<Cliente> tablaClientes;
    @FXML private TableColumn<Cliente, String> colCedula;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colCorreo;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colRango;
    @FXML private TableColumn<Cliente, String> colPuntos;
    @FXML private TableColumn<Cliente, String> colSaldo;

    private final ModelFactory modelo = ModelFactory.getInstance();
    private final ClienteController controller = new ClienteController(modelo.getMonederoVirtual());

    @FXML
    void initialize() {
        Ui.columna(colCedula, Cliente::getCedula);
        Ui.columna(colNombre, Cliente::getNombre);
        Ui.columna(colCorreo, Cliente::getCorreo);
        Ui.columna(colTelefono, Cliente::getTelefono);
        Ui.columna(colRango, c -> Ui.rango(c.getRango()));
        Ui.columna(colPuntos, c -> String.valueOf(c.getPuntos()));
        Ui.columna(colSaldo, c -> Ui.dinero(c.getSaldoTotal()));

        // Al elegir una fila se llena el formulario; la cédula no se edita porque es la llave del cliente.
        tablaClientes.getSelectionModel().selectedItemProperty().addListener((obs, anterior, c) -> {
            if (c != null) {
                txtCedula.setText(c.getCedula());
                txtNombre.setText(c.getNombre());
                txtCorreo.setText(c.getCorreo());
                txtTelefono.setText(c.getTelefono());
                txtCedula.setDisable(true);
            }
        });
        refrescar();
    }

    @FXML
    void onAgregar() {
        try {
            Cliente nuevo = controller.agregarCliente(txtCedula.getText(), txtNombre.getText(),
                    txtCorreo.getText(), txtTelefono.getText());
            if (modelo.getClienteActivo() == null) {
                modelo.setClienteActivo(nuevo);
            }
            Ui.ok(lblEstado, "Cliente " + nuevo.getNombre() + " registrado. Créele un monedero en «Monederos».");
            onLimpiar();
            modelo.notificarCambio();
            refrescar();
        } catch (MonederoException e) {
            Ui.fallo(lblEstado, e.getMessage());
            Ui.error(e.getMessage());
        }
    }

    @FXML
    void onActualizar() {
        if (tablaClientes.getSelectionModel().getSelectedItem() == null) {
            Ui.error("Seleccione un cliente de la tabla para actualizarlo");
            return;
        }
        try {
            controller.actualizarCliente(txtCedula.getText(), txtNombre.getText(),
                    txtCorreo.getText(), txtTelefono.getText());
            Ui.ok(lblEstado, "Datos actualizados");
            modelo.notificarCambio();
            refrescar();
        } catch (MonederoException e) {
            Ui.fallo(lblEstado, e.getMessage());
            Ui.error(e.getMessage());
        }
    }

    @FXML
    void onEliminar() {
        Cliente seleccionado = tablaClientes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Ui.error("Seleccione un cliente de la tabla para eliminarlo");
            return;
        }
        if (!Ui.confirmar("¿Eliminar a " + seleccionado + "? Se perderán sus monederos e historial.")) {
            return;
        }
        try {
            controller.eliminarCliente(seleccionado.getCedula());
            if (modelo.getClienteActivo() == seleccionado) {
                List<Cliente> restantes = controller.listar();
                modelo.setClienteActivo(restantes.isEmpty() ? null : restantes.get(0));
            }
            Ui.ok(lblEstado, "Cliente eliminado");
            onLimpiar();
            modelo.notificarCambio();
            refrescar();
        } catch (MonederoException e) {
            Ui.error(e.getMessage());
        }
    }

    @FXML
    void onUsarCliente() {
        Cliente seleccionado = tablaClientes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Ui.error("Seleccione un cliente de la tabla");
            return;
        }
        modelo.setClienteActivo(seleccionado);
        modelo.notificarCambio();
        Ui.ok(lblEstado, seleccionado.getNombre() + " es ahora el cliente activo");
    }

    @FXML
    void onLimpiar() {
        tablaClientes.getSelectionModel().clearSelection();
        txtCedula.clear();
        txtNombre.clear();
        txtCorreo.clear();
        txtTelefono.clear();
        txtCedula.setDisable(false);
    }

    private void refrescar() {
        Cliente seleccionado = tablaClientes.getSelectionModel().getSelectedItem();
        tablaClientes.setItems(FXCollections.observableArrayList(controller.listar()));
        if (seleccionado != null && tablaClientes.getItems().contains(seleccionado)) {
            tablaClientes.getSelectionModel().select(seleccionado);
        }
    }
}
