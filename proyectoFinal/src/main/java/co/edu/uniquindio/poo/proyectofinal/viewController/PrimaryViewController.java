package co.edu.uniquindio.poo.proyectofinal.viewController;

import co.edu.uniquindio.poo.proyectofinal.App;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;

public class PrimaryViewController {

    @FXML private Button btnResumen;
    @FXML private Button btnTransacciones;
    @FXML private Button btnProgramar;
    @FXML private Button btnAnalisis;
    @FXML private Button btnBeneficios;
    @FXML private Button btnClientes;

    @FXML private TableView<?> tablaTransacciones;

    @FXML
    void initialize() {
        System.out.println("¡Dashboard cargado correctamente!");

        // 1. Botón que cambia de pantalla hacia el CRUD de Clientes
        btnClientes.setOnAction(event -> {
            System.out.println("Navegando a Gestión de Clientes...");
            App.openViewClientes();
        });

        // 2. Los demás botones (Por ahora imprimirán un mensaje en la consola de IntelliJ)
        btnResumen.setOnAction(event -> {
            System.out.println("Clic en Resumen General - Aquí recargaremos la vista principal");
        });

        btnTransacciones.setOnAction(event -> {
            System.out.println("Clic en Transacciones - Aquí abriremos la pestaña de Depósitos/Retiros");
        });

        btnProgramar.setOnAction(event -> {
            System.out.println("Clic en Pagos Programados - Aquí abriremos la lista ordenada");
        });

        btnAnalisis.setOnAction(event -> {
            System.out.println("Clic en Análisis - Aquí mostraremos el algoritmo de Desviación Estándar");
        });

        btnBeneficios.setOnAction(event -> {
            System.out.println("Clic en Beneficios - Aquí abriremos la tienda de canje de puntos");
        });
    }
}