package co.edu.uniquindio.poo.proyectofinal.viewController;

import co.edu.uniquindio.poo.proyectofinal.App;
import co.edu.uniquindio.poo.proyectofinal.controller.ClienteController;
import co.edu.uniquindio.poo.proyectofinal.model.MonederoVirtual;
import javafx.fxml.FXML;

public class ClienteViewController {

    // Declaramos el modelo principal y el controlador puente
    private MonederoVirtual monederoVirtual;
    private ClienteController clienteController;

    @FXML
    void onVolver() {
        // Llamada correcta al método estático en App para volver al inicio
        App.openViewPrincipal();
    }

    @FXML
    void initialize() {
        // Inicializamos el monedero para resolver el error de la línea 72
        // Nota: Asegúrate de pasar los parámetros correctos según tu constructor real
        monederoVirtual = new MonederoVirtual("");

        // Inicializamos el controlador pasándole el modelo
        clienteController = new ClienteController(monederoVirtual);

        System.out.println("¡Vista de Clientes cargada correctamente!");
    }

    // Aquí debajo dejas intactos tus métodos void onAgregar(), onEliminar(), etc.
}