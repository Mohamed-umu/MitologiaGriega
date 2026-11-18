
package mitologia.modelo;

/**
 * Representa los objetos del mundo mitologico
 * Guarda su estado, propietario y capacidad
 */
public class Objeto {

    // Nombre del objeto
    private String nombre;

    // Indica si el objeto existe
    private boolean existe;

    // Paa saber si esta roto
    private boolean roto;

    // Ser que tiene el objeto
    private Ser propietario;

    // Capacidad que puede aportar el objeto
    private Capacidad capacidad;

    // Crear un objeto con su nombre y capacidad
    public Objeto(String nombre, Capacidad capacidad) {
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.existe = true;
        this.roto = false;
    }

    // Consultar el nombre
    public String getNombre() {
        return nombre;
    }

    // Comprobar si existe
    public boolean isExiste() {
        return existe;
    }

    // Cambiar si existe o no
    public void setExiste(boolean existe) {
        this.existe = existe;
    }

    // Comprobar si esta roto
    public boolean isRoto() {
        return roto;
    }

    // Cambiar el estado del objeto
    public void setRoto(boolean roto) {
        this.roto = roto;
    }

    // Consultar quien tiene el objeto
    public Ser getPropietario() {
        return propietario;
    }

    // Asignar el objeto a un ser
    public void setPropietario(Ser propietario) {
        this.propietario = propietario;
    }

    // Consultar su capacidad
    public Capacidad getCapacidad() {
        return capacidad;
    }

    // pa Cambiar la capacidad del objeto
    public void setCapacidad(Capacidad capacidad) {
        this.capacidad = capacidad;
    }
}
