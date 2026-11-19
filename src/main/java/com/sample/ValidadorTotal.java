
package com.sample;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase sirve pa comprobar el funcionamiento del proyecto
 *
 * Ejecutaa los 8 escenarios originales, los 8 negativos
 * y los 5 alternativos que hemos preparado
 *
 * En total comprobamos 21 pruebas y muestra los resultados
 * para saber si todas las reglas funcionan correctamente
 */
public class ValidadorTotal {

    public static void main(String[] args) throws Exception {

        // Definir los tres grupos de pruebas
        // Cada grupo indica la carpeta de entrada
        // la carpeta donde guardar las respuestas y el resultado q esperamos obtener
        String[][] lotes = {
            {"escenarios", "soluciones_positivas", "SI"},
            {"escenarios_negativos", "soluciones_negativas", "NO"},
            {"escenarios_alternativos", "soluciones_alternativas", "SI"}
        };

        //Indica q escenarios vamos a ejecutar en cada grupo, los originales y negativos tienen 8 pruebas
        //Los alternativos solo tienen los escenarios 1 3 5 6 y 7
        int[][] numeros = {
            {1, 2, 3, 4, 5, 6, 7, 8},
            {1, 2, 3, 4, 5, 6, 7, 8},
            {1, 3, 5, 6, 7}
        };

        // Guarda los errores que puedan aparecer durante las pruebas
        List<String> errores = new ArrayList<>();

        // Contador de pruebas que han dado el resultado esperado
        int correctos = 0;

        // Contador de todas las pruebas que hemos comprobado
        int comprobados = 0;

        // Recorre los tres grupos de escenarios
        for (int i = 0; i < lotes.length; i++) {

            // Obtienes las carpetas y el resultado esperado de cada grupo
            String carpetaEntrada = lotes[i][0];
            String carpetaSalida = lotes[i][1];
            String resultadoEsperado = lotes[i][2];

            // Convierte los nombres de las carpetas en rutas
            Path entrada = Paths.get(carpetaEntrada);
            Path salida = Paths.get(carpetaSalida);

            // Muestraa el grupo de pruebas que vamos a ejecutar
            System.out.println("\n===== INICIO LOTE: " + carpetaEntrada + " =====");

            // Comprobamos q exista la carpeta de entrada
            //Si falta no se puede ejecutar las pruebas de ese grupo
            if (!Files.isDirectory(entrada)) {
                errores.add("No existe la carpeta: " + entrada.toAbsolutePath());
                comprobados += numeros[i].length;
                continue;
            }

            // Crea la carpeta donde guardaremos las nuevas respuestas
            Files.createDirectories(salida);

            // Borrar las respuestas anteriores de las pruebas esperadas
            // Asi evitar aceptar resultados de ejecuciones antiguas
            for (int n : numeros[i]) {
                Files.deleteIfExists(salida.resolve("RespuestaF2_" + n + ".txt"));
            }

            // Comprobar si estan todos los escenarios que necesitamos
            boolean entradasCompletas = true;

            // Recorrer los numeros correspondientes a este grupo
            for (int n : numeros[i]) {

                // Construir el nombre del archivo de entrada
                Path escenario = entrada.resolve("EscenarioF2-" + n + ".txt");

                //Si falta algun escenario registramos el error
                if (!Files.isRegularFile(escenario)) {
                    errores.add("Falta escenario de prueba: " + escenario);
                    entradasCompletas = false;
                }
            }

            // Si faltan archivos no ejecutamos este grupo
            // Contamos las pruebas como comprobadas pero no correctas
            if (!entradasCompletas) {
                comprobados += numeros[i].length;
                continue;
            }

            // Ejecuta los escenarios utilizando el programa principal
            try {

                // Le pasa las carpetas de entrada y salida
                // MainMitologia se encarga de ejecutar Drools
                MainMitologia.main(new String[]{carpetaEntrada, carpetaSalida});

            } catch (Exception ex) {

                // Si ocurre un error lo guardamos para mostrarlo al final
                errores.add("Error ejecutando " + carpetaEntrada + ": " + ex.getMessage());

                // Mostramos informacion del error en la consola
                ex.printStackTrace();

                // Contamos las pruebas del grupo y pasamos al siguiente
                comprobados += numeros[i].length;
                continue;
            }

            // Ahora comproba las respuestas que ha generado el programa
            for (int n : numeros[i]) {

                //Suma una prueba al total de comprobaciones
                comprobados++;

                // Busca el archivo de respuesta del escenario
                Path ruta = salida.resolve("RespuestaF2_" + n + ".txt");

                // Si no se ha generado el archivo la prueba falla
                if (!Files.isRegularFile(ruta)) {
                    errores.add(carpetaEntrada + " F2-" + n + ": no se genero respuesta");

                    System.out.println("COMPROBACION " + carpetaEntrada + " F2-" + n + " -> FALLO");
                    continue;
                }

                // Leemos el contenido de la respuesta que ha generado Drools
                String contenido = new String(Files.readAllBytes(ruta), StandardCharsets.UTF_8);

                // Preparamos el texto que esperamos encontrar
                // Por ejemplo Respuesta: SI o Respuesta: NO
                String esperado = "Respuesta: " + resultadoEsperado;

                // Comprobamos q la respuesta coincida con la esperada
                // Tamb rechazamos ejecuciones que hayan llegado
                // al limite de reglas porque podrian indicar un bucle
                boolean ok = contenido.matches("(?s).*\\bRespuesta: " + resultadoEsperado + "\\s*(?:\\r?\\n|$).*")
                        && !contenido.contains("AVISO: alcanzado limite de reglas");

                //Si la respuesta es correcta sumamos un acierto
                // Si es incorrecta guardamos el problema
                if (ok) correctos++;
                else errores.add(carpetaEntrada + " F2-" + n + ": se esperaba " + esperado);

                // Mostramos el resultado de esta prueba en la consola
                System.out.println("COMPROBACION " + carpetaEntrada + " F2-" + n
                        + " -> " + (ok ? "OK" : "FALLO")
                        + " (esperado " + resultadoEsperado + ")");
            }
        }

        //Cuando terminan los tres grupos mostramos el resumen final
        System.out.println("\n==================================");
        System.out.println(" RESUMEN FINAL DE VALIDACION");
        System.out.println("==================================");

        // Muestra cuantos escenarios han dado el resultado esperado
        System.out.println("Correctos: " + correctos + "/" + comprobados);

        // Solo damos por superadas las pruebas si las 21 son correctas
        // y no se ha registrado ningun error
        if (errores.isEmpty() && correctos == 21 && comprobados == 21) {
            System.out.println("TODAS LAS 21 PRUEBAS HAN PASADO");
        } else {

            // Si hay fallos mostramos todos los problemas encontrados
            System.out.println("INCIDENCIAS:");

            for (String error : errores) System.out.println(" - " + error);
        }
    }
}
