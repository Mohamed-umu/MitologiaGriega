
package mitologia.modelo;

/**
 * Representa las capacidades que pueden tener los seres comoo volar o petrificar
 */
public class Capacidad {

    // Nombre de la capacidad
    private String nombre;

    // Crear una capacidad con su nombre
    public Capacidad(String nombre) {
        this.nombre = nombre;
    }

    // Obtener el nombre de la capacidad
    public String getNombre() {
        return nombre;
    }

    // Mostrar directamente el nombre
    @Override
    public String toString() {
        return nombre;
    }
}
