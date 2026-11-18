
package mitologia.modelo;

/**
 * pa representar los lugares donde pueden estar los seres
 */
public class Lugar {

    // Nombre del lugar
    private String nombre;

    // crear un lugar con su nombre
    public Lugar(String nombre) {
        this.nombre = nombre;
    }

    //Consultar el nombre del lugar
    public String getNombre() {
        return nombre;
    }
}
