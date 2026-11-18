
package mitologia.modelo;

/**
 * Guarda q heroe ha conseguido derrotar a Medusa
 */
public class VictoriaMedusa {

    // Heroe q ha ganado el combate
    private Heroe heroe;

    // Gorgona q ha sido derrotada
    private Gorgona medusa;

    // Guardar los participantes de la victoria
    public VictoriaMedusa(Heroe heroe, Gorgona medusa) {
        this.heroe = heroe;
        this.medusa = medusa;
    }

    // Consultar el heroe vencedor
    public Heroe getHeroe() {
        return heroe;
    }

    // Consultar la gorgona derrotada
    public Gorgona getMedusa() {
        return medusa;
    }
}
