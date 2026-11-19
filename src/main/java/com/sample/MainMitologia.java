
package com.sample;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

/**
 *esta es laa Clase principal desde la que se ejectan todos los escenarios
 *
 * Aqui se leen  los archivos TXT y pongo en marcha las reglas Drools
 * Tambien comproba los objetivos y guardalas respuestas
 *se puede  utilizar las carpetas por defecto o indicar otras al ejecutar
 */
public class MainMitologia {

    public static void main(String[] args) throws Exception {

        // Estas son las carpetas q utilizapor defecto
        // En escenarios estan los archivos con las historias
        // En soluciones guardare las respuestas que genere el programa
        String escenarios = "escenarios";
        String soluciones = "soluciones";

        // Si el usuario indica una carpeta de entrada la utiliza
        if (args.length >= 1) escenarios = args[0];

        // Tambien puede indicar una carpeta diferente para las respuestas
        if (args.length >= 2) soluciones = args[1];

        // Convertimos los nombres de las carpetas en rutas de Java
        Path origen = Paths.get(escenarios);
        Path destino = Paths.get(soluciones);

        // Crea la carpeta de soluciones si todavia no existe
        Files.createDirectories(destino);

        // Antes de empezar comprobamos qu exista la carpeta de escenarios
        // Si no existe no podemos continuar con la ejecucion
        
        if (!Files.isDirectory(origen))
            throw new IOException("No existe carpeta de escenarios: " + origen.toAbsolutePath());

        // Obtener los servicios de Drools
        KieServices servicios = KieServices.Factory.get();

        // Cargar las bases de reglas configuradas en kmodule.xml
        KieContainer contenedor = servicios.getKieClasspathContainer();

        // Cuenta cuantos escenarios hemos podido procesar
        int procesados = 0;

        // Recorrer los ocho escenarios del proyecto
        for (int n = 1; n <= 8; n++) {

            // Construimos el nombre del archivo que toca leer
            // como:EscenarioF2-1.txt o EscenarioF2-2.txt
            Path archivo = origen.resolve("EscenarioF2-" + n + ".txt");

            // Si falta un escenario lo indicamos y pasar al siguiente
            // Asi no se detiene todo el programa por un archivo ausente
            if (!Files.isRegularFile(archivo)) {
                System.out.println("No encontrado, se omite: " + archivo);
                continue;
            }

            // Leer los datos del escenario con la clase que ya creamos
            ConfiguracionEscenario c = ConfiguracionEscenario.leer(archivo);

            // Ejecuta las reglas y obtenemos la respuesta completa
            String respuesta = ejecutar(contenedor, c, n);

            //Preparamos la ruta donde guardaremos el resultado
            
            // Cada escenario tiene su propio archivo de respuesta
            Path salida = destino.resolve("RespuestaF2_" + n + ".txt");

            // Guardamos el resultado utilizando UTF-8
            Files.write(salida, respuesta.getBytes(StandardCharsets.UTF_8));

            // Mostrar en consola que escenario estamos ejecutando
            System.out.println("\n====== ESCENARIO F2-" + n + " ======");

            // Mostrar el contenido original del escenario
            // Esto permite ver las condiciones iniciales de la historia
            System.out.println(new String(Files.readAllBytes(archivo), StandardCharsets.UTF_8));

            // Separarr la descripcion de los resultados
            System.out.println("\n*** FIN DESCRIPCION ESCENARIO ***\n");

            // Mostramos lo que ha deducido Drools y la respuesta final
            System.out.println(respuesta);

            // Indicamos que hemos terminado este escenario
            System.out.println("*** FIN ESCENARIO F2-" + n + " ***\n");

            // Suma uno al contador de escenarios procesados
            procesados++;
        }

        // Al terminar mostramos cuantos archivos hemos ejecutado
        System.out.println("Archivos procesados: " + procesados);

        //Indica donde se han guardado todas las respuestas
        System.out.println("Carpeta de respuestas: " + destino.toAbsolutePath());
    }

    /**
     * Ejecuta las reglas de un escenario concreto
     *
     * Primero seleccionamos la sesion que aparece en el TXT
     * Despues construimos el mundo y dejamos trabajar a Drools
     *
     * ffinalmente revisar los objetivos y preparamos la respuesta
     */
    private static String ejecutar(KieContainer contenedor,
                                  ConfiguracionEscenario c, int n) throws Exception {

        // Leemos el nombre de la sesion indicada en el escenario
        String sesionNombre = c.unico("Sesion");

       //Solo permitimos las sesiones q hemos definido en kmodule.xml
        // Asi evitamos utilizar nombres incorrectos o desconocidos
        if (!(sesionNombre.equals("mitologiaSession") ||
              sesionNombre.equals("laberintoSession") ||
              sesionNombre.equals("integradaSession") ||
              sesionNombre.equals("nuevosSession") ||
              sesionNombre.equals("f8Session"))) {
            throw new IOException("Sesion no permitida: " + sesionNombre);
        }

        // Crear una nueva sesion de Drools para este escenario
        // Cada historia se ejecuta por separado
        KieSession kSession = contenedor.newKieSession(sesionNombre);

        try {

            // Construir todos los personajes, objetos y lugares del TXT
            // MundoMitologico tamb inserta los hechos en Drools
            MundoMitologico mundo = new MundoMitologico(kSession, c);

            // Prepara una memoria donde guardar los mensajes de las reglas
            // Asi podemos escribir despues la historia completa en el TXT
            ByteArrayOutputStream memoria = new ByteArrayOutputStream();

            // Guarda la salida normal de la consola
            PrintStream original = System.out;

            // Aqui guardaremos el numero de reglas que se han activado
            int reglas;

            // Creamos una salida temporal que escribe en memoria
            try (PrintStream capturador = new PrintStream(memoria, true, "UTF-8")) {

                try {

                    // Durante la ejecucion capturamos los mensajes de Drools
                    System.setOut(capturador);

                    // Ejecutamos las reglas hasta que ya no haya mas activaciones
                    // Ponemos un limite de 1000 para evitar posibles bucles
                    reglas = kSession.fireAllRules(1000);

                } finally {

                    // Recuperamos la salida normal aunque haya un error
                    System.setOut(original);
                }
            }

            // Vamos construyendo el texto de la respuesta
            StringBuilder s = new StringBuilder();

            // Escribimos el numero del escenario
            s.append("ESCENARIO F2-").append(n).append('\n');

          // Incluimos la pregunta que aparece en el archivo TXT
            s.append("Pregunta: ").append(c.unico("Pregunta")).append("\n\n");

          //Este apartado contiene las acciones deducidas por las reglas
            s.append("ACONTECIMIENTOS INFERIDOS POR DROOLS\n");

            // Recuperamos todos los mensajes que hemos capturado
            String hechos = memoria.toString("UTF-8").trim();

          // Si no se ha ejecutado ninguna regla lo indicamos
            // En caso contrario mostramos los acontecimientos ocurridos
            s.append(hechos.isEmpty() ? "(Ninguna regla activada)" : hechos).append("\n\n");

            // Al principio suponemos que los objetivos se cumplen
            // Despues comprobamos cada uno por separado
            boolean ok = true;

            s.append("COMPROBACIONES DEL OBJETIVO\n");

            // Recorremos los objetivos pedidos en el escenario
            // Puede haber uno o varios objetivos en el mismo TXT
            for (String objetivo : c.elementos("Objetivo")) {

                // Preguntamos a MundoMitologico si este objetivo se ha cumplido
                boolean cumple = mundo.cumple(objetivo);

                // Todos los objetivos deben cumplirse para responder SI
                // Si alguno es falso el resultado final sera NO
                ok &= cumple;

                // Guardamos el resultado individual de este objetivo
                s.append(objetivo).append(" = ").append(cumple).append('\n');
            }

            // Escribimos la respuesta final segun las comprobaciones
            s.append("\nRespuesta: ").append(ok ? "SI" : "NO").append('\n');

            // Mostramos cuantas reglas se han activado
            s.append("Reglas activadas: ").append(reglas).append('\n');

            // Si llega al limite avisamos por si existe un bucle
            if (reglas == 1000) s.append("AVISO: alcanzado limite de reglas. Revisar bucles.\n");

            // Devolvemos todo el texto para guardarlo en el archivo
            return s.toString();

        } finally {

            // Cerrar la sesion para liberar los recursos utilizados
            // Esto se hace incluso si aparece algun error durante la ejecucion
            kSession.dispose();
        }
    }
}
