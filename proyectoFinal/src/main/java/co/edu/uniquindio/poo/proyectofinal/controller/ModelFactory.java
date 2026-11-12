package co.edu.uniquindio.poo.proyectofinal.controller;

import co.edu.uniquindio.poo.proyectofinal.model.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Único punto de acceso al modelo (Singleton). Todas las pantallas comparten el mismo
 * MonederoVirtual, el cliente activo y la lista de alertas.
 */
public final class ModelFactory {
    private static ModelFactory instancia;

    private final MonederoVirtual monederoVirtual = new MonederoVirtual("MonederoX");
    private final List<String> alertas = new ArrayList<>();
    private final List<Runnable> oyentes = new ArrayList<>();
    private Cliente clienteActivo;

    private ModelFactory() {
        cargarDatosDemo();
        // Se registra después de cargar la demo para no llenar las alertas con datos de ejemplo.
        monederoVirtual.agregarNotificador(new NotificadorPantalla(this::registrarAlerta));
    }

    public static ModelFactory getInstance() {
        if (instancia == null) {
            instancia = new ModelFactory();
        }
        return instancia;
    }

    public MonederoVirtual getMonederoVirtual() { return monederoVirtual; }

    // ---------- Cliente activo ----------

    public Cliente getClienteActivo() { return clienteActivo; }

    public void setClienteActivo(Cliente cliente) { this.clienteActivo = cliente; }

    // ---------- Alertas ----------

    public List<String> getAlertas() { return List.copyOf(alertas); }

    public void limpiarAlertas() { alertas.clear(); }

    private void registrarAlerta(String mensaje) {
        alertas.add(0, LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "  " + mensaje);
    }

    // ---------- Oyentes: la interfaz se entera cuando cambian los datos ----------

    public void agregarOyente(Runnable oyente) { oyentes.add(oyente); }

    public void notificarCambio() { oyentes.forEach(Runnable::run); }

    // ---------- Datos de ejemplo ----------

    private void cargarDatosDemo() {
        try {
            Cliente ana = new Cliente("1094000001", "Ana Gómez", "ana@correo.com", "3101234567");
            Cliente luis = new Cliente("1094000002", "Luis Pérez", "luis@correo.com", "3157654321");
            monederoVirtual.registrarCliente(ana);
            monederoVirtual.registrarCliente(luis);

            Monedero anaAhorros = new MonederoAhorros("Ahorros");
            Monedero anaGastos = new MonederoGastos("Gastos diarios");
            Monedero luisGastos = new MonederoGastos("Gastos diarios");
            monederoVirtual.agregarMonedero(ana.getCedula(), anaAhorros);
            monederoVirtual.agregarMonedero(ana.getCedula(), anaGastos);
            monederoVirtual.agregarMonedero(luis.getCedula(), luisGastos);

            monederoVirtual.depositar(ana.getCedula(), anaAhorros.getId(), 80000);
            monederoVirtual.depositar(ana.getCedula(), anaGastos.getId(), 100000);
            monederoVirtual.depositar(luis.getCedula(), luisGastos.getId(), 50000);
            for (int i = 0; i < 5; i++) {
                monederoVirtual.retirar(ana.getCedula(), anaGastos.getId(), 10000 + i * 1000);
            }
            clienteActivo = ana;
        } catch (MonederoException e) {
            throw new IllegalStateException("No se pudieron cargar los datos de ejemplo", e);
        }
    }
}
