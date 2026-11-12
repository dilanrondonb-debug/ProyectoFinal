package co.edu.uniquindio.poo.proyectofinal.controller;

import co.edu.uniquindio.poo.proyectofinal.model.*;

import java.util.List;

public class BeneficioController {

    private final MonederoVirtual monederoVirtual;
    private final List<IBeneficio> catalogo =
            List.of(new ReduccionComision(), new MesSinCargosRetiro(), new BonoSaldo());

    public BeneficioController(MonederoVirtual monederoVirtual) {
        this.monederoVirtual = monederoVirtual;
    }

    public List<IBeneficio> getCatalogo() { return catalogo; }

    public void canjear(Cliente cliente, IBeneficio beneficio) throws MonederoException {
        Validaciones.exigir(cliente, "Seleccione un cliente");
        Validaciones.exigir(beneficio, "Seleccione un beneficio de la tabla");
        monederoVirtual.canjearBeneficio(cliente.getCedula(), beneficio);
    }

    /** Avance (0 a 1) hacia el siguiente rango; 1 si ya es Platino. */
    public double progresoSiguienteRango(Cliente cliente) {
        Rango[] rangos = Rango.values();
        int siguiente = cliente.getRango().ordinal() + 1;
        if (siguiente >= rangos.length) {
            return 1;
        }
        return Math.min(1.0, (double) cliente.getPuntos() / rangos[siguiente].getMinimo());
    }

    public String textoSiguienteRango(Cliente cliente) {
        Rango[] rangos = Rango.values();
        int siguiente = cliente.getRango().ordinal() + 1;
        if (siguiente >= rangos.length) {
            return "Rango máximo alcanzado";
        }
        int faltan = Math.max(0, rangos[siguiente].getMinimo() - cliente.getPuntos());
        return "Te faltan " + faltan + " puntos para " + rangos[siguiente];
    }

    public List<RegistroPuntos> historialPuntos(Cliente cliente) {
        return cliente.getHistorialPuntos().reversed();
    }
}
