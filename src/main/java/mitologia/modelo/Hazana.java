
package mitologia.modelo;

/**
 * Guarda las hazanas realizadas por los heroes
 * Como rescatar a alguien o derrotar una criatura
 */
public class Hazana {

    // Tipo de hazana realizada
    private String tipo;

    // Heroe q realiza la hazana
    private Heroe actor;

    // Ser sobre el q se realiza la accion
    private Ser objetivo;

    // Crear una hazana con sus datos
    public Hazana(String tipo, Heroe actor, Ser objetivo) {
        this.tipo = tipo;
        this.actor = actor;
        this.objetivo = objetivo;
    }

    // Consultarr el tipo de hazana
    public String getTipo() {
        return tipo;
    }

    // Consultar quien la ha realizado
    public Heroe getActor() {
        return actor;
    }

    // Consultar el objetivo de la hazana
    public Ser getObjetivo() {
        return objetivo;
    }
}
