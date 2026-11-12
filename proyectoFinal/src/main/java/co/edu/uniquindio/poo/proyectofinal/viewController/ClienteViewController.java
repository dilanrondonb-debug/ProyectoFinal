package co.edu.uniquindio.poo.proyectofinal.viewController;

import co.edu.uniquindio.poo.proyectofinal.App;
import co.edu.uniquindio.poo.proyectofinal.controller.ClienteController;
import co.edu.uniquindio.poo.proyectofinal.model.Cliente;
import co.edu.uniquindio.poo.proyectofinal.model.DatoInvalidoException;
import co.edu.uniquindio.poo.proyectofinal.model.MonederoVirtual;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ClienteViewController {

    private MonederoVirtual monederoVirtual;
    private ClienteController clienteController;

    // Lista observable para actualizar la tabla dinámicamente
    private ObservableList<Cliente> listaClientes = FXCollections.observableArrayList();

    @FXML private TextField txtCedula;
    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;

    // Componentes de la Tabla
    @FXML private TableView<Cliente> tablaClientes;
    @FXML private TableColumn<Cliente, String> colCedula;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colCorreo;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colRango;

    @FXML
    void initialize() {
        monederoVirtual = new MonederoVirtual("");
        clienteController = new ClienteController(monederoVirtual);

        // Vinculamos las columnas de la tabla con los atributos de la clase Cliente
        colCedula.setCellValueFactory(new PropertyValueFactory<>("cedula"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colRango.setCellValueFactory(new PropertyValueFactory<>("rango"));

        // Cargamos los datos en la tabla
        tablaClientes.setItems(listaClientes);
    }

    @FXML
    void onVolver() {
        App.openViewPrincipal();
    }

    @FXML
    void onAgregar() {
        String cedula = txtCedula.getText();
        String nombre = txtNombre.getText();
        String correo = txtCorreo.getText();
        String telefono = txtTelefono.getText();

        if (cedula.isEmpty() || nombre.isEmpty() || correo.isEmpty() || telefono.isEmpty()) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        try {
            // Intentamos crear el cliente (aquí se atrapa la excepción si los datos son inválidos)
            Cliente nuevoCliente = new Cliente(cedula, nombre, correo, telefono);
            listaClientes.add(nuevoCliente);

            mostrarAlerta("¡Éxito!", "Cliente registrado correctamente.");
            onLimpiar();

        } catch (DatoInvalidoException e) {
            // Mostramos el mensaje exacto de tu excepción en una ventana emergente
            mostrarAlerta("Dato Inválido", e.getMessage());
        }
    }

    @FXML
    void onActualizar() {
        Cliente clienteSeleccionado = tablaClientes.getSelectionModel().getSelectedItem();
        if (clienteSeleccionado != null) {
            clienteSeleccionado.setNombre(txtNombre.getText());
            clienteSeleccionado.setCorreo(txtCorreo.getText());
            clienteSeleccionado.setTelefono(txtTelefono.getText());
            tablaClientes.refresh(); // Refresca la tabla para mostrar los cambios
            mostrarAlerta("Actualizado", "Los datos del cliente han sido modificados.");
            onLimpiar();
        } else {
            mostrarAlerta("Aviso", "Selecciona un cliente de la tabla para actualizar.");
        }
    }

    @FXML
    void onEliminar() {
        Cliente clienteSeleccionado = tablaClientes.getSelectionModel().getSelectedItem();
        if (clienteSeleccionado != null) {
            listaClientes.remove(clienteSeleccionado);
            mostrarAlerta("Eliminado", "El cliente ha sido borrado del sistema.");
            onLimpiar();
        } else {
            mostrarAlerta("Aviso", "Selecciona un cliente de la tabla para eliminar.");
        }
    }

    @FXML
    void onLimpiar() {
        txtCedula.clear();
        txtNombre.clear();
        txtCorreo.clear();
        txtTelefono.clear();
        txtCedula.setDisable(false); // Reactivar cédula si estaba bloqueada
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}