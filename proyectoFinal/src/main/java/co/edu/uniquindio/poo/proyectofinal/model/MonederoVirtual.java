package co.edu.uniquindio.poo.proyectofinal.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MonederoVirtual {
    private String nombre;
    private final List<Cliente> clientes = new ArrayList<>();
    private final List<TransaccionProgramada> programadas = new ArrayList<>();
    private final List<INotificable> notificadores = new ArrayList<>();
    private final AnalizadorGastos analizador = new AnalizadorGastos();

    public MonederoVirtual(String nombre) {
        this.nombre = nombre;
    }

    // ---------- Clientes ----------

    public void registrarCliente(Cliente cliente) throws DatoInvalidoException {
        if (verificarCliente(cliente.getCedula())) {
            throw new DatoInvalidoException("Ya existe un cliente con cédula " + cliente.getCedula());
        }
        clientes.add(cliente);
    }

    public boolean verificarCliente(String cedula) {
        return clientes.stream().anyMatch(c -> c.getCedula().equals(cedula));
    }

    public Cliente buscarCliente(String cedula) throws ClienteNoEncontradoException {
        return clientes.stream()
                .filter(c -> c.getCedula().equals(cedula))
                .findFirst()
                .orElseThrow(() -> new ClienteNoEncontradoException(cedula));
    }

    public void eliminarCliente(String cedula) throws ClienteNoEncontradoException {
        clientes.remove(buscarCliente(cedula));
    }

    public void actualizarCliente(String cedula, String nombre, String correo, String telefono)
            throws MonederoException {
        Cliente cliente = buscarCliente(cedula);
        cliente.setNombre(nombre);
        cliente.setCorreo(correo);
        cliente.setTelefono(telefono);
    }

    public void agregarMonedero(String cedula, Monedero monedero) throws ClienteNoEncontradoException {
        buscarCliente(cedula).agregarMonedero(monedero);
    }

    // ---------- Transacciones ----------

    public Deposito depositar(String cedula, String idMonedero, double monto) throws MonederoException {
        Monedero monedero = buscarCliente(cedula).buscarMonedero(idMonedero);
        Deposito deposito = new Deposito(monedero, monto);
        deposito.ejecutar();
        return deposito;
    }

    public Retiro retirar(String cedula, String idMonedero, double monto) throws MonederoException {
        Monedero monedero = buscarCliente(cedula).buscarMonedero(idMonedero);
        Retiro retiro = new Retiro(monedero, monto);
        retiro.ejecutar();
        revisarAlertas(monedero, retiro);
        return retiro;
    }

    public Transferencia transferir(String cedulaOrigen, String idMonederoOrigen,
                                    String cedulaDestino, String idMonederoDestino,
                                    double monto) throws MonederoException {
        Monedero origen = buscarCliente(cedulaOrigen).buscarMonedero(idMonederoOrigen);
        Monedero destino = buscarCliente(cedulaDestino).buscarMonedero(idMonederoDestino);
        Transferencia transferencia = new Transferencia(origen, destino, monto);
        transferencia.ejecutar();
        revisarAlertas(origen, transferencia);
        return transferencia;
    }

    public void revertirTransaccion(String cedula, String idMonedero, String idTransaccion)
            throws MonederoException {
        Monedero monedero = buscarCliente(cedula).buscarMonedero(idMonedero);
        monedero.buscarTransaccion(idTransaccion).revertir();
    }

    public List<Transaccion> consultarHistorial(String cedula, String idMonedero) throws MonederoException {
        return buscarCliente(cedula).buscarMonedero(idMonedero).getTransacciones();
    }

    // ---------- Puntos y rangos ----------

    public void canjearBeneficio(String cedula, IBeneficio beneficio) throws MonederoException {
        buscarCliente(cedula).canjear(beneficio);
    }

    public Map<Rango, List<Cliente>> agruparClientesPorRango() {
        return clientes.stream().collect(Collectors.groupingBy(Cliente::getRango));
    }

    // ---------- Programadas ----------

    public void programarTransaccion(TransaccionProgramada programada) {
        programadas.add(programada);
    }

    /** Devuelve una copia ordenada por fecha con nuestro algoritmo de inserción. */
    public List<TransaccionProgramada> obtenerProgramadasOrdenadas() {
        List<TransaccionProgramada> copia = new ArrayList<>(programadas);
        Ordenador.ordenarPorInsercion(copia);
        return copia;
    }

    /** Ejecuta, en orden de fecha, las programadas cuya fecha ya llegó. Las recurrentes se reprograman. */
    public List<String> procesarProgramadasVencidas() {
        LocalDate hoy = LocalDate.now();
        List<String> resultados = new ArrayList<>();
        for (TransaccionProgramada p : obtenerProgramadasOrdenadas()) {
            if (p.getFechaEjecucion().isAfter(hoy)) {
                break; // la lista está ordenada: el resto es posterior
            }
            programadas.remove(p);
            try {
                p.ejecutar();
                revisarAlertas(p.getTransaccion().getMonedero(), p.getTransaccion());
                resultados.add("Ejecutada: " + p);
            } catch (MonederoException e) {
                resultados.add("Falló: " + p + " -> " + e.getMessage());
            }
            reprogramar(p, hoy);
        }
        return resultados;
    }

    public List<TransaccionProgramada> getProgramadas() { return List.copyOf(programadas); }

    public void cancelarProgramada(TransaccionProgramada programada) { programadas.remove(programada); }

    private void reprogramar(TransaccionProgramada p, LocalDate hoy) {
        if (p.getFrecuencia() == Frecuencia.UNICA) {
            return;
        }
        LocalDate siguiente = p.getFechaEjecucion();
        do {
            siguiente = p.getFrecuencia() == Frecuencia.SEMANAL ? siguiente.plusWeeks(1) : siguiente.plusMonths(1);
        } while (siguiente.isBefore(hoy));
        try {
            programadas.add(new TransaccionProgramada(siguiente, p.getFrecuencia(), TipoTransaccion.copiarDe(p.getTransaccion())));
        } catch (MonederoException e) {
            notificar("No se pudo reprogramar: " + p);
        }
    }

    // ---------- Alertas ----------

    public void agregarNotificador(INotificable notificador) {
        notificadores.add(notificador);
    }

    private void revisarAlertas(Monedero monedero, Transaccion transaccion) {
        if (monedero.isSaldoBajo()) {
            notificar("Saldo bajo en " + monedero.getNombre() + ": " + monedero.getSaldo());
        }
        if (analizador.detectarAtipicas(monedero.getTransacciones()).contains(transaccion)) {
            notificar("Transacción atípica detectada: " + transaccion);
        }
    }

    private void notificar(String mensaje) {
        notificadores.forEach(n -> n.enviar(mensaje));
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public List<Cliente> getClientes() { return List.copyOf(clientes); }
    public AnalizadorGastos getAnalizador() { return analizador; }
}
