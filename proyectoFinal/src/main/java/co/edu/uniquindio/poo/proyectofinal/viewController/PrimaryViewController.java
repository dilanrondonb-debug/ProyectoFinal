package co.edu.uniquindio.poo.proyectofinal.viewController;

import co.edu.uniquindio.poo.proyectofinal.App;
import co.edu.uniquindio.poo.proyectofinal.controller.ModelFactory;
import co.edu.uniquindio.poo.proyectofinal.model.Cliente;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

/** Marco de la aplicación: menú lateral + cliente activo + área central intercambiable. */
public class PrimaryViewController {

    @FXML private Button btnResumen;
    @FXML private Button btnTransacciones;
    @FXML private Button btnProgramar;
    @FXML private Button btnAnalisis;
    @FXML private Button btnBeneficios;
    @FXML private Button btnMonederos;
    @FXML private Button btnClientes;
    @FXML private ComboBox<Cliente> cmbCliente;
    @FXML private Label lblRango;
    @FXML private StackPane contenedor;

    private final ModelFactory modelo = ModelFactory.getInstance();
    private final Map<Vista, Button> botones = new EnumMap<>(Vista.class);
    private Vista vistaActual = Vista.RESUMEN;
    private boolean sincronizando;

    @FXML
    void initialize() {
        botones.put(Vista.RESUMEN, btnResumen);
        botones.put(Vista.TRANSACCIONES, btnTransacciones);
        botones.put(Vista.PROGRAMADOS, btnProgramar);
        botones.put(Vista.ANALISIS, btnAnalisis);
        botones.put(Vista.BENEFICIOS, btnBeneficios);
        botones.put(Vista.MONEDEROS, btnMonederos);
        botones.put(Vista.CLIENTES, btnClientes);
        botones.forEach((vista, boton) -> boton.setOnAction(e -> cargar(vista)));

        Navegador.registrar(this::cargar);
        modelo.agregarOyente(this::sincronizar);

        cmbCliente.valueProperty().addListener((obs, anterior, nuevo) -> {
            if (!sincronizando && nuevo != null) {
                modelo.setClienteActivo(nuevo);
                sincronizar();
                cargar(vistaActual); // la pantalla actual se vuelve a pintar con el nuevo cliente
            }
        });

        sincronizar();
        cargar(Vista.RESUMEN);
    }

    /** Actualiza la lista de clientes, el rango y los puntos que se ven en el menú. */
    private void sincronizar() {
        sincronizando = true;
        try {
            cmbCliente.setItems(FXCollections.observableArrayList(modelo.getMonederoVirtual().getClientes()));
            cmbCliente.setValue(null);
            cmbCliente.setValue(modelo.getClienteActivo());
        } finally {
            sincronizando = false;
        }
        Cliente cliente = modelo.getClienteActivo();
        lblRango.setText(cliente == null
                ? "Sin cliente activo"
                : "Rango: " + Ui.rango(cliente.getRango()) + " · " + cliente.getPuntos() + " pts");
    }

    private void cargar(Vista vista) {
        vistaActual = vista;
        botones.forEach((v, boton) -> boton.getStyleClass().remove("sidebar-btn-active"));
        botones.get(vista).getStyleClass().add("sidebar-btn-active");

        try {
            Node contenido;
            if (vista != Vista.CLIENTES && modelo.getClienteActivo() == null) {
                contenido = mensajeSinCliente();
            } else {
                contenido = new FXMLLoader(App.class.getResource(vista.getArchivo())).load();
            }
            ScrollPane scroll = new ScrollPane(contenido);
            scroll.setFitToWidth(true);
            scroll.setFitToHeight(true);
            contenedor.getChildren().setAll(scroll);
        } catch (IOException e) {
            e.printStackTrace();
            Ui.error("No se pudo cargar la pantalla " + vista + ": " + e.getMessage());
        }
    }

    private Node mensajeSinCliente() {
        Label texto = new Label("No hay un cliente activo.\nRegistre uno en «Gestión Clientes» para empezar.");
        texto.getStyleClass().add("text-subtitle");
        VBox caja = new VBox(texto);
        caja.setAlignment(Pos.CENTER);
        return caja;
    }
}
