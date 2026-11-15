package co.edu.uniquindio.poo.proyectofinal;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Monedero Virtual - Sistema de Puntos");
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);

        // Arrancamos cargando la vista principal (Dashboard)
        openViewPrincipal();
        primaryStage.show();
    }

    // Método estático para regresar al Dashboard principal
    public static void openViewPrincipal() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("primary.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 900, 600);
            primaryStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método estático para abrir la vista de Gestión de Clientes
    public static void openViewClientes() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("crudCliente.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 900, 600);
            primaryStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}