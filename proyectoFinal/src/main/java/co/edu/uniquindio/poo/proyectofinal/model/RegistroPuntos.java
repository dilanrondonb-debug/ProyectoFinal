package co.edu.uniquindio.poo.proyectofinal.model;
import java.time.LocalDateTime;

public class RegistroPuntos {
    private final int puntos;
    private final String motivo;
    private final LocalDateTime fecha;

    public RegistroPuntos(int puntos, String motivo) {
        this.puntos = puntos;
        this.motivo = motivo;
        this.fecha = LocalDateTime.now();
    }

    public int getPuntos() { return puntos; }
    public String getMotivo() { return motivo; }
    public LocalDateTime getFecha() { return fecha; }

    @Override
    public String toString() {
        return fecha + " | " + (puntos >= 0 ? "+" : "") + puntos + " | " + motivo;
    }
}
