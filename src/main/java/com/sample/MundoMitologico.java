
package com.sample;

import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ClassObjectFilter;
import mitologia.modelo.*;

/**
 * Esta clase se encarga de preparar el mundo mitologico
 * a partir de las condiciones que aparecen en cada escenario
 *
 * Aqui creamos los personajes, objetos y lugares y los pasamos al motor Drools para q aplique las reglas
 *
 * Al terminar tambien comprobamos si se han cumplido
 * los objetivos q se pedian en el escenario
 */
public final class MundoMitologico {

    // estoy usando mapas pa localizar cada elemento por su nombre

    // Aqui se guardan heroes, dioses, humanos y criaturas
    private final Map<String, Ser> seres = new LinkedHashMap<>();

    // Lugares q aparecen en el escenario
    private final Map<String, Lugar> lugares = new LinkedHashMap<>();

    //Objetos disponibles durante la historia
    private final Map<String, Objeto> objetos = new LinkedHashMap<>();

    //Capacidades qe pueden proporcionar algunos objetos
    private final Map<String, Capacidad> capacidades = new LinkedHashMap<>();

    // Sesion de Drools donde se ejecutan las reglas
    private final KieSession sesion;

    // Guardamos los hechos para insertarlos despues en Drools
    private final List<Object> hechos = new ArrayList<>();

    /**
     * Construimos el mundo usando la configuracion del escenario
     *Primero creamos todos los elementos y sus relaciones
     *y despuess insertamos los hechos en la sesion
     */
    public MundoMitologico(KieSession sesion, ConfiguracionEscenario config) {
        this.sesion = sesion;

        //Recorrer la lista de personajes del archivo TXT
        for (String fila : config.elementos("Personajes")) {

            // Cada personaje viene con su tipo y su nombre
            String[] t = partes(fila, 2);
            String tipo = t[0], nombre = t[1];

            // Evitamos crear dos personajes con el mismo nombre
            if (seres.containsKey(nombre))
                throw new IllegalArgumentException("Personaje repetido: " + nombre);

            Ser s;

            // Crearr cada personaje segun el tipo indicado
            // Todos se pueden guardar como Ser porque heredan de esa clase
            switch (tipo) {
                case "Heroe": s = new Heroe(nombre); break;
                case "HeroeSemidios": s = new HeroeSemidios(nombre); break;
                case "Heroina": s = new Heroina(nombre); break;
                case "Humano": s = new Humano(nombre); break;
                case "Dios": s = new Dios(nombre); break;
                case "Ninfa": s = new Ninfa(nombre); break;
                case "Gorgona": s = new Gorgona(nombre); break;
                case "CriaturaMarina": s = new CriaturaMarina(nombre); break;
                case "CriaturaTerrestre": s = new CriaturaTerrestre(nombre); break;
                default:
                    throw new IllegalArgumentException("Tipo de personaje desconocido: " + tipo);
            }

            // Guardamos el personaje para poder buscarlo mas adelante
            seres.put(nombre, s);

            // Tambien hay q añadirlo a los hechos del escenario
            hechos.add(s);
        }

        // Ahora crear los lugares definidos en el TXT
        for (String nombre : config.elementos("Lugares")) {

            //Comprobamos q no se repita un lugar
            if (lugares.containsKey(nombre))
                throw new IllegalArgumentException("Lugar repetido: " + nombre);

            Lugar lugar = new Lugar(nombre);

            // Guardar el lugar por su nombre
            lugares.put(nombre, lugar);

            // Drools tambien podra utilizar este lugar
            hechos.add(lugar);
        }

        // Recorremos los objetos que aparecen en el escenario
        for (String fila : config.elementos("Objetos")) {

            // Cada objeto tiene seis datos separados por dos puntos
            String[] t = partes(fila, 6);
            String tipo = t[0], nombre = t[1];

            // No permitimos objetos con nombres repetidos
            if (objetos.containsKey(nombre))
                throw new IllegalArgumentException("Objeto repetido: " + nombre);

            // Si aparece un guion significa que no tiene capacidad
            // Si tiene una capacidad, la buscamos o la creamos
            Capacidad cap = t[2].equals("-") ? null :
                    capacidades.computeIfAbsent(t[2], Capacidad::new);

            Objeto obj;

            // Elegimos la clase correspondiente al objeto
            switch (tipo) {
                case "Arma": obj = new Arma(nombre, cap); break;
                case "ObjetoMagico": obj = new ObjetoMagico(nombre, cap); break;
                case "ObjetoComun": obj = new ObjetoComun(nombre, cap); break;
                default:
                    throw new IllegalArgumentException("Tipo de objeto desconocido: " + tipo);
            }

            // Asignamos el propietario inicial del objeto
            // Si aparece un guion, el objeto empieza sin propietario
            obj.setPropietario(t[3].equals("-") ? null : ser(t[3]));

            // Indicamos si el objeto existe al comenzar
            obj.setExiste(verdadero(t[4]));

            // Indicamos si el objeto empieza roto
            obj.setRoto(verdadero(t[5]));

            // Guardamos el objeto para localizarlo por su nombre
            objetos.put(nombre, obj);

            // Lo añadimos a los hechos que utilizara Drools
            hechos.add(obj);
        }

        // Leemos que dioses favorecen a cada personaje
        for (String fila : config.elementos("Favor")) {
            String[] t = partes(fila, 2);

            // El primer dato es el personaje
            // El segundo es el dios que le favorece
            ((Personaje)ser(t[0])).agregarFavor((Dios)ser(t[1]));
        }

        // Hacemos lo mismo con los dioses que estan enfadados
        for (String fila : config.elementos("Enojo")) {
            String[] t = partes(fila, 2);

            // Guardamos el enojo en el personaje correspondiente
            ((Personaje)ser(t[0])).agregarEnojo((Dios)ser(t[1]));
        }

        // Algunos personajes pueden empezar apresados
        for (String fila : config.elementos("Preso")) {
            String[] t = partes(fila, 2);

            // Buscamos al personaje que esta preso
            Ser s = ser(t[0]);

            // Marcamos que no esta libre
            s.setLibre(false);

            // Guardamos quien lo tiene apresado
            s.setApresadoPor(ser(t[1]));
        }

        // Colocamos a los personajes en sus ubicaciones iniciales
        for (String fila : config.elementos("Ubicacion")) {
            String[] t = partes(fila, 2);

            // Buscamos al ser y le asignamos su lugar
            ser(t[0]).setUbicacion(lugar(t[1]));
        }

        // Algunos escenarios incluyen eventos especiales
        for (String evento : config.elementos("Evento")) {

            // Creamos el evento para que las reglas puedan detectarlo
            hechos.add(new EventoLaberinto(evento));
        }

        // Insertar todos los hechos despues de preparar el mundo
        // De esta forma Drools recibe los estados iniciales completos
        for (Object hecho : hechos) sesion.insert(hecho);
    }

    //Separa los datos que vienen en una linea del TXT y comprueba que el formato sea correcto
    private static String[] partes(String fila, int cantidad) {

        // Dividimos la linea usando los dos puntos
        String[] t = fila.split(":", -1);

        // Comprobamos que tenga el numero esperado de campos
        if (t.length != cantidad)
            throw new IllegalArgumentException(
                    "Formato incorrecto (" + cantidad + " campos): " + fila);

        // Evitamos que algun campo este vacio
        for (String s : t)
            if (s.trim().isEmpty())
                throw new IllegalArgumentException("Campo vacio: " + fila);

        return t;
    }

    // Convierte los valores true y false del TXT en valores booleanos de Java
     
    private static boolean verdadero(String s) {

        // Rechazamos cualquier otro valor
        if (!s.equals("true") && !s.equals("false"))
            throw new IllegalArgumentException("Esperaba true/false: " + s);

        return Boolean.parseBoolean(s);
    }

    // Buscar un ser usando su nombre
    private Ser ser(String nombre) {
        Ser s = seres.get(nombre);

        // Si no existe, el escenario tiene una referencia incorrecta
        if (s == null)
            throw new IllegalArgumentException("Ser no declarado: " + nombre);

        return s;
    }

    // Buscar un lugar usando su nombre
    private Lugar lugar(String nombre) {
        Lugar l = lugares.get(nombre);

        // No podemos utilizar lugares que no se hayan declarado
        if (l == null)
            throw new IllegalArgumentException("Lugar no declarado: " + nombre);

        return l;
    }

    // Buscar un objeto usando su nombre
    private Objeto objeto(String nombre) {
        Objeto o = objetos.get(nombre);

        // Comprobamos q el objeto exista en la configuracion
        if (o == null)
            throw new IllegalArgumentException("Objeto no declarado: " + nombre);

        return o;
    }

    /**
     * Comprueba si un objetivo se ha conseguido
     *Se consulta el estado del mundo despues de ejecutar Drools
     * y se devuelve true o false segun el resultado
     */
    public boolean cumple(String objetivo) {

        //Separamos el tipo de objetivo y sus datos
        String[] t = objetivo.split(":", -1);

        //cada tipo de objetivo necesita una comprobacion diferente
        switch (t[0]) {

            case "Libre":
                // Comprobar si el personaje esta libre
                return t.length == 2 && ser(t[1]).isLibre();

            case "Preso":
                // Comprobar si sigue apresado
                return t.length == 2 && !ser(t[1]).isLibre();

            case "Vivo":
                // Comprobar si sigue vivo
                return t.length == 2 && ser(t[1]).isVivo();

            case "Muerto":
                // Comprobar si la criatura o personaje ha muerto
                return t.length == 2 && !ser(t[1]).isVivo();

            case "Posee":
                // Comprobar si el objeto existe y pertenece al personaje
                return t.length == 3 && objeto(t[2]).isExiste() &&
                        objeto(t[2]).getPropietario() == ser(t[1]);

            case "Capacidad":
                // Comprobar si el ser ha conseguido una capacidad
                return t.length == 3 && ser(t[1]).tieneCapacidad(t[2]);

            case "Ubicacion":
                // Comprobar si el ser ha llegado al lugar pedido
                return t.length == 3 && ser(t[1]).getUbicacion() == lugar(t[2]);

            case "Roto":
                // Comprobar si el objeto esta roto
                return t.length == 2 && objeto(t[1]).isRoto();

            case "Hazana":
                //una hazana necesita actor, tipo y objetivo
                if (t.length != 4)
                    throw new IllegalArgumentException("Hazana:Actor:Tipo:Objetivo");

                // Buscamos entre las hazanas que hay en la sesion
                for (Object hecho : sesion.getObjects(new ClassObjectFilter(Hazana.class))) {
                    Hazana h = (Hazana) hecho;

                    // Comprobamos quien la hizo, su tipo y sobre quien
                    if (h.getActor() == ser(t[1]) && h.getTipo().equals(t[2]) &&
                            h.getObjetivo() == ser(t[3]))
                        return true;
                }

                // Si no encontramos esa hazana, no se ha cumplido
                return false;

            default:
                //ya q no podemos comprobar un objetivo desconocido
                throw new IllegalArgumentException("Objetivo desconocido: " + objetivo);
        }
    }
}
