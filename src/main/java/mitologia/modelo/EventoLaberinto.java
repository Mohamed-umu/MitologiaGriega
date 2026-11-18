
package mitologia.modelo;

/**
 * pa guardar los eventos que ocurren en el laberinto
 */
public class EventoLaberinto {

    // Tipo de evento que ha ocurrido
    private String tipo;

    // Crear un evento indicando su tipo
    public EventoLaberinto(String tipo) {
        this.tipo = tipo;
    }

    // Consulta el tipo de evento
    public String getTipo() {
        return tipo;
    }
}
