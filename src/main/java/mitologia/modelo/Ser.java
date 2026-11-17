
package mitologia.modelo;

import java.util.HashSet;
import java.util.Set;

/**
 * Clase base pa los seres de la mitologia
 * aqui guardamos: su nombre, estado vital, libertad, ubicacion
 * y capacidades
 */
public class Ser {

    // Nombre del ser
    private String nombre;

    //Para saber si sigue vivo
    private boolean vivo;

    // Indica si esta libre o apresado
    private boolean libre;

    // Quien lo tiene apresado, si es el caso
    private Ser apresadoPor;

    // Lugar donde se encuentra
    private Lugar ubicacion;

    // Capacidades que puede ir consiguiendo
    private Set<String> capacidades = new HashSet<>();

    
    //Al crear un ser empieza vivo y libre
    public Ser(String nombre) {
        this.nombre = nombre;
        this.vivo = true;
        this.libre = true;
        this.apresadoPor = null;
    }

    // obtener su nombre
    public String getNombre() {
        return nombre;
    }

    // comprobar si esta vivo
    public boolean isVivo() {
        return vivo;
    }

    // Cambiar su estado de vida
    public void setVivo(boolean vivo) {
        this.vivo = vivo;
    }

    // Comprobar si esta libre
    public boolean isLibre() {
        return libre;
    }

    // Cambiar su estado de libertad
    public void setLibre(boolean libre) {
        this.libre = libre;
    }

    // Saber quien lo tiene apresado
    public Ser getApresadoPor() {
        return apresadoPor;
    }

    // Guardar quien lo ha apresado
    public void setApresadoPor(Ser apresadoPor) {
        this.apresadoPor = apresadoPor;
    }

    // Consultar donde esta
    public Lugar getUbicacion() {
        return ubicacion;
    }

    // Actualizar su ubicacion
    public void setUbicacion(Lugar ubicacion) {
        this.ubicacion = ubicacion;
    }

    // Obtener todas sus capacidades
    public Set<String> getCapacidades() {
        return capacidades;
    }

    // Comprobar si tiene una capacidad concreta
    public boolean tieneCapacidad(String nombre) {
        return capacidades.contains(nombre);
    }

    // Agregar una capacidad nueva
    public void agregarCapacidad(String nombre) {
        capacidades.add(nombre);
    }

    // pa mostrar el nombre del ser
    @Override
    public String toString() {
        return nombre;
    }
}
