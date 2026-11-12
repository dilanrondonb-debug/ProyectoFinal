package co.edu.uniquindio.poo.proyectofinal.viewController;

import co.edu.uniquindio.poo.proyectofinal.controller.ModelFactory;
import co.edu.uniquindio.poo.proyectofinal.model.Cliente;
import co.edu.uniquindio.poo.proyectofinal.model.Monedero;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;

import java.util.List;

/** Par de combos "cliente destino / monedero destino" que usan Transacciones y Programados. */
final class SelectorDestino {
    private final Node contenedor;
    private final ComboBox<Cliente> cmbCliente;
    private final ComboBox<Monedero> cmbMonedero;

    SelectorDestino(Node contenedor, ComboBox<Cliente> cmbCliente, ComboBox<Monedero> cmbMonedero) {
        this.contenedor = contenedor;
        this.cmbCliente = cmbCliente;
        this.cmbMonedero = cmbMonedero;
        Ui.mostrarMonederos(cmbMonedero);
        cmbCliente.valueProperty().addListener((obs, anterior, nuevo) -> cargarMonederos(nuevo));
        mostrar(false);
    }

    /** Recarga los clientes y monederos (con saldos nuevos) conservando el destino ya elegido. */
    void recargar() {
        Cliente clientePrevio = cmbCliente.getValue();
        Monedero monederoPrevio = cmbMonedero.getValue();
        List<Cliente> clientes = ModelFactory.getInstance().getMonederoVirtual().getClientes();
        cmbCliente.setItems(FXCollections.observableArrayList(clientes));
        cmbCliente.setValue(null);
        if (clientePrevio != null && clientes.contains(clientePrevio)) {
            cmbCliente.setValue(clientePrevio);
            if (monederoPrevio != null && clientePrevio.getMonederos().contains(monederoPrevio)) {
                cmbMonedero.setValue(null);
                cmbMonedero.setValue(monederoPrevio);
            }
        }
    }

    private void cargarMonederos(Cliente cliente) {
        List<Monedero> monederos = cliente == null ? List.of() : cliente.getMonederos();
        cmbMonedero.setItems(FXCollections.observableArrayList(monederos));
        cmbMonedero.setValue(null);
        cmbMonedero.setValue(monederos.isEmpty() ? null : monederos.get(0));
    }

    void mostrar(boolean visible) {
        contenedor.setVisible(visible);
        contenedor.setManaged(visible);
    }

    Monedero getMonedero() { return cmbMonedero.getValue(); }
}
