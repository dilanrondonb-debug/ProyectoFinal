package co.edu.uniquindio.poo.proyectofinal.model;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cliente extends Persona{
    private int puntos;
    private Rango rango = Rango.BRONCE;
    private final List<Monedero> monederos = new ArrayList<>();
    private final List<RegistroPuntos> historialPuntos = new ArrayList<>();
    private double descuentoComision;      // 0.10 = 10% menos de comisión
    private LocalDate mesSinCargosHasta;   // null si no hay beneficio activo

    public Cliente(String cedula, String nombre, String correo, String telefono)
            throws DatoInvalidoException {
        super(cedula, nombre, correo, telefono);
    }

    public void agregarMonedero(Monedero monedero) {
        monederos.add(monedero);
        monedero.setPropietario(this);
    }

    public Monedero buscarMonedero(String idMonedero) throws MonederoException {
        return monederos.stream()
                .filter(m -> m.getId().equals(idMonedero))
                .findFirst()
                .orElseThrow(() -> new MonederoException("No existe el monedero " + idMonedero));
    }

    /** Suma o resta puntos, deja la huella en el historial y recalcula el rango. */
    public void agregarPuntos(int cantidad, String motivo) {
        historialPuntos.add(new RegistroPuntos(cantidad, motivo));
        puntos += cantidad;
        actualizarRango();
    }

    public void actualizarRango() {
        rango = Rango.desdePuntos(puntos);
    }

    /** Canje: primero valida los puntos, luego aplica el beneficio y al final descuenta. */
    public void canjear(IBeneficio beneficio) throws MonederoException {
        if (puntos < beneficio.getCostoPuntos()) {
            throw new PuntosInsuficientesException(puntos, beneficio.getCostoPuntos());
        }
        beneficio.aplicar(this);
        agregarPuntos(-beneficio.getCostoPuntos(), "Canje: " + beneficio.getNombre());
    }

    public boolean tieneMesSinCargos() {
        return mesSinCargosHasta != null && !LocalDate.now().isAfter(mesSinCargosHasta);
    }

    public double getSaldoTotal() {
        return monederos.stream().mapToDouble(Monedero::getSaldo).sum();
    }

    public int getPuntos() { return puntos; }
    public Rango getRango() { return rango; }
    public double getDescuentoComision() { return descuentoComision; }
    public void setDescuentoComision(double descuentoComision) { this.descuentoComision = descuentoComision; }
    public LocalDate getMesSinCargosHasta() { return mesSinCargosHasta; }
    public void setMesSinCargosHasta(LocalDate fecha) { this.mesSinCargosHasta = fecha; }

    public List<Monedero> getMonederos() { return Collections.unmodifiableList(monederos); }
    public List<RegistroPuntos> getHistorialPuntos() { return Collections.unmodifiableList(historialPuntos); }
}
