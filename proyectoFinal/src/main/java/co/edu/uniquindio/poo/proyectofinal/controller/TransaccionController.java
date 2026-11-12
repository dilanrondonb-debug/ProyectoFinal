package co.edu.uniquindio.poo.proyectofinal.controller;

import co.edu.uniquindio.poo.proyectofinal.model.*;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class TransaccionController {

    private final MonederoVirtual monederoVirtual;

    public TransaccionController(MonederoVirtual monederoVirtual) {
        this.monederoVirtual = monederoVirtual;
    }

    /** Ejecuta la operación a través del MonederoVirtual para que se activen puntos y alertas. */
    public Transaccion ejecutar(TipoTransaccion tipo, Monedero origen, Monedero destino, double monto)
            throws MonederoException {
        Validaciones.exigir(tipo, "Seleccione el tipo de operación");
        Validaciones.exigir(origen, "Seleccione un monedero");
        String cedula = origen.getPropietario().getCedula();
        return switch (tipo) {
            case DEPOSITO -> monederoVirtual.depositar(cedula, origen.getId(), monto);
            case RETIRO -> monederoVirtual.retirar(cedula, origen.getId(), monto);
            case TRANSFERENCIA -> {
                Validaciones.exigir(destino, "Seleccione el cliente y el monedero destino");
                yield monederoVirtual.transferir(cedula, origen.getId(),
                        destino.getPropietario().getCedula(), destino.getId(), monto);
            }
        };
    }

    /** Solo el dueño del monedero de origen puede revertir la operación. */
    public void revertir(Cliente solicitante, Transaccion transaccion) throws MonederoException {
        Validaciones.exigir(transaccion, "Seleccione una transacción de la tabla");
        Monedero origen = transaccion.getMonedero();
        if (origen.getPropietario() != solicitante) {
            throw new MonederoException("Solo el cliente que originó la operación puede revertirla");
        }
        monederoVirtual.revertirTransaccion(solicitante.getCedula(), origen.getId(), transaccion.getId());
    }

    /** Historial de todos los monederos del cliente, de la más reciente a la más antigua. */
    public List<Transaccion> historial(Cliente cliente) {
        return cliente.getMonederos().stream()
                .flatMap(m -> m.getTransacciones().stream())
                .distinct()
                .sorted(Comparator.comparing(Transaccion::getFecha).reversed())
                .toList();
    }

    public List<Transaccion> historial(Monedero monedero) {
        return monedero.getTransacciones().stream()
                .sorted(Comparator.comparing(Transaccion::getFecha).reversed())
                .toList();
    }

    // ---------- Programadas ----------

    public void programar(TipoTransaccion tipo, Monedero origen, Monedero destino, double monto,
                          LocalDate fecha, Frecuencia frecuencia) throws MonederoException {
        Validaciones.exigir(tipo, "Seleccione el tipo de operación");
        Validaciones.exigir(origen, "Seleccione un monedero");
        Validaciones.exigir(fecha, "Seleccione la fecha de ejecución");
        Validaciones.exigir(frecuencia, "Seleccione la frecuencia");
        Transaccion transaccion = tipo.crear(origen, destino, monto);
        monederoVirtual.programarTransaccion(new TransaccionProgramada(fecha, frecuencia, transaccion));
    }

    /** Programadas del cliente, ordenadas por fecha con el algoritmo de inserción del modelo. */
    public List<TransaccionProgramada> programadasDe(Cliente cliente) {
        return monederoVirtual.obtenerProgramadasOrdenadas().stream()
                .filter(p -> p.getTransaccion().getMonedero().getPropietario() == cliente)
                .toList();
    }

    public List<String> procesarVencidas() {
        return monederoVirtual.procesarProgramadasVencidas();
    }

    public void cancelarProgramada(TransaccionProgramada programada) throws DatoInvalidoException {
        Validaciones.exigir(programada, "Seleccione una transacción programada");
        monederoVirtual.cancelarProgramada(programada);
    }
}
