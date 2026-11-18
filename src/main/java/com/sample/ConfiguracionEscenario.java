
package com.sample;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * esta clase es pa leer los archivos TXT de los escenarios
 * yguarda los datos que necesitamos pa crear el mundo
 *
 * Tamb revisa q las claves y los valores sean correctos
 * pa evitar errores al ejecutar las reglas
 */
public final class ConfiguracionEscenario {

    // Guardar los datos del escenario agrupados por su claveç
	
    // Una clave puede tener varios valores
    private final Map<String, List<String>> campos = new LinkedHashMap<>();

    //Leer el archivo del escenario linea por linea  y comprobamos q tenga el formato esperado
     
    public static ConfiguracionEscenario leer(Path archivo) throws IOException {

        // Crea una configuracion vacia
        ConfiguracionEscenario c = new ConfiguracionEscenario();

        // Contador pa localizar las lineas con errores
        int numero = 0;

        // Leer todo el archivo usando UTF-8
        for (String original : Files.readAllLines(archivo, StandardCharsets.UTF_8)) {
            numero++;

            // Quita los espacios del principio y del final
            String linea = original.trim();

            // Ignora las lineas vacias y los comentarios
            if (linea.isEmpty() || linea.startsWith("#")) continue;

            // Busca el signo que separa la clave y su valor
            int i = linea.indexOf('=');

            // Si no existe una clave correcta lanzamos un error
            if (i < 1)
                throw new IOException(archivo + ": linea " + numero + " sin clave=valor");

            // Saca la clave qe aparece antes del igual
            String clave = linea.substring(0, i).trim();

            // Saca el valor que aparece despues del igual
            String valor = linea.substring(i + 1).trim();

            // Solo acepta las claves que hemos definido
            if (!esValida(clave))
                throw new IOException(archivo + ": clave desconocida: " + clave);

            // Evita guardar valores vacios
            if (valor.isEmpty())
                throw new IOException(archivo + ": valor vacio: " + clave);

            // Guardamos el valor dentro de su clave
            //Si la clave no existe todavia se crea su lista
            c.campos.computeIfAbsent(clave, k -> new ArrayList<>()).add(valor);
        }

        // Cada escenario debe indicar una unica sesion
        c.unico("Sesion");

        // Tamb debe contener una unica pregunta
        c.unico("Pregunta");

        // Comprobamos que exista al menos un objetivo
        if (c.elementos("Objetivo").isEmpty())
            throw new IOException("Falta Objetivo en " + archivo);

        return c;
    }

    /**
     * Comproba que la clave sea una de las permitidas
     * para no aceptar datos desconocidos
     */
    private static boolean esValida(String k) {
        return k.equals("Sesion") || k.equals("Pregunta") || k.equals("Personajes") ||
                k.equals("Lugares") || k.equals("Objetos") || k.equals("Favor") ||
                k.equals("Enojo") || k.equals("Preso") || k.equals("Ubicacion") ||
                k.equals("Evento") || k.equals("Objetivo");
    }

    //Obtenemos el valor de una clave que solo debe aparecer una vez
     
    public String unico(String clave) throws IOException {

        // Busca los valores asociados a esa clave
        List<String> vs = campos.get(clave);

        // Si falta o aparece repetida avisamos del error
        if (vs == null || vs.size() != 1)
            throw new IOException("Debe haber exactamente un " + clave);

        return vs.get(0);
    }

    /**
     * Obtener todos los elementos asociados a una clave
     * Algunos valores vienen separados por punto y coma
     */
    public List<String> elementos(String clave) {

        // Lista donde guardar los elementos separados
        List<String> res = new ArrayList<>();

        // Recorre todos los grupos de valores de la clave
        for (String grupo : campos.getOrDefault(clave, java.util.Collections.emptyList())) {

            // Separa cada elemento usando el punto y coma
            for (String elemento : grupo.split(";")) {

                // Ignora los elementos vacios
                if (!elemento.trim().isEmpty())
                    res.add(elemento.trim());
            }
        }

        // Devuelve la lista preparada
        return res;
    }
}
