package mitologia.modelo;

/**
 * Clase base para las criaturas mitologicas
 * 
 * De aqui heredan los distintos tipos de criaturas
 */
public abstract class Criatura extends Ser {

    //pa crear una criatura con su nombre
    public Criatura(String nombre) {
        super(nombre);
    }
}
