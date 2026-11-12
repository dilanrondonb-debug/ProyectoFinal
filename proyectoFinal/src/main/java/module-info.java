module co.edu.uniquindio.poo.proyectofinal {
    requires javafx.controls;
    requires javafx.fxml;

    // Permite que JavaFX acceda a tu aplicación principal
    opens co.edu.uniquindio.poo.proyectofinal to javafx.fxml;
    exports co.edu.uniquindio.poo.proyectofinal;

    // ESTA ES LA LÍNEA CLAVE: Permite que JavaFX lea tus controladores
    opens co.edu.uniquindio.poo.proyectofinal.viewController to javafx.fxml;
    exports co.edu.uniquindio.poo.proyectofinal.viewController;
}