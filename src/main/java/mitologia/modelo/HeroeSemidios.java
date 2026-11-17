
package mitologia.modelo;

/**
 * Representa a los heroes que tambien son semidioses
 * Como Perseo o Heracles
 */
public class HeroeSemidios extends Heroe
        implements Semidios {

    // Crear un heroe semidios con su nombre
    public HeroeSemidios(String nombre) {
        super(nombre);
    }
}
