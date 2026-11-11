package co.edu.uniquindio.poo.proyectofinal.model;

public abstract class Persona {
    private String cedula;
    private String nombre;
    private String correo;
    private String telefono;



    protected Persona(String cedula, String nombre, String correo, String telefono)
            throws DatoInvalidoException {
        setCedula(cedula);
        setNombre(nombre);
        setCorreo(correo);
        setTelefono(telefono);
    }

    public String getCedula() { return cedula; }
    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public String getTelefono() { return telefono; }

    public void setCedula(String cedula) throws DatoInvalidoException {
        if (cedula == null || !cedula.matches("\\d{5,12}")) {
            throw new DatoInvalidoException("La cédula debe tener entre 5 y 12 dígitos");
        }
        this.cedula = cedula;
    }

    public void setNombre(String nombre) throws DatoInvalidoException {
        if (nombre == null || nombre.isBlank()) {
            throw new DatoInvalidoException("El nombre no puede estar vacío");
        }
        this.nombre = nombre.trim();
    }

    public void setCorreo(String correo) throws DatoInvalidoException {
        if (correo == null || !correo.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {
            throw new DatoInvalidoException("El correo no tiene un formato válido");
        }
        this.correo = correo;
    }

    public void setTelefono(String telefono) throws DatoInvalidoException {
        if (telefono == null || !telefono.matches("\\d{7,10}")) {
            throw new DatoInvalidoException("El teléfono debe tener entre 7 y 10 dígitos");
        }
        this.telefono = telefono;
    }

    @Override
    public String toString() {
        return nombre + " (" + cedula + ")";
    }
}
