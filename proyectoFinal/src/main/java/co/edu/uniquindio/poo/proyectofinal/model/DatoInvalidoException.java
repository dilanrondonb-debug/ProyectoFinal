package co.edu.uniquindio.poo.proyectofinal.model;

//Validación de datos de entrada (campos vacíos, correo mal escrito, cédula repetida...)
public class DatoInvalidoException extends MonederoException {
  public DatoInvalidoException(String mensaje) {
    super(mensaje);
  }
}
