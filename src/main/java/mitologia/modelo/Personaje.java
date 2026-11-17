
package mitologia.modelo;

import java.util.HashSet;
import java.util.Set;

/**
 *  para los personajes de la mitologia
 * Hereda los datos comunes de Ser
 */
public abstract class Personaje extends Ser {

    // Dioses que favorecen al personaje
    private Set<Dios> favores = new HashSet<>();

    // Dioses que estan enfadados con el personaje
    private Set<Dios> enojos = new HashSet<>();

    // creamos el personaje usando el constructor de Ser
    public Personaje(String nombre) {
        super(nombre);
    }

    // Consultar los dioses que le favorecen
    public Set<Dios> getFavores() {
        return favores;
    }

    // Consultar los dioses que estan enojados con el
    public Set<Dios> getEnojos() {
        return enojos;
    }

    // Anadir un dios a sus favores
    public void agregarFavor(Dios dios) {
        favores.add(dios);
    }

    // este es para regitrar el enojo de un dios
    public void agregarEnojo(Dios dios) {
        enojos.add(dios);
    }
}
