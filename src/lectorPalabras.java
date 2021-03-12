import java.io.FileNotFoundException;
import java.util.HashMap;
import java.io.File;
import java.util.Scanner;

public class lectorPalabras {

    String directorio_palabras="../listaPalabras";

    /**Funcion del modulo que se va a utilizar. Simplemente crearDiccionario() devuelve un
 diccionario por cada categoria y el tipo de cada categoria. El tipo de cada
 categoria determinara la manera de parsear, ya que no es lo mismo una categoria
 que otra**/
    HashMap<String, Object>[] crearDiccionario(){
        HashMap<String, Object>  diccionario = new HashMap<>();  //Diccionario para almacenar los ficheros
        HashMap<String, Object> tipo_categorias = new HashMap<>();  //Diccionario para almacenar el tipo de cada categoria, incluido en fichero
        HashMap<String, Object>[] tupla = new HashMap[2];  //Diccionario para almacenar el tipo de cada categoria, incluido en fichero

        File dir = new File(directorio_palabras);
        File[] files = dir.listFiles();

        for (File file : files) {
            if (!file.isDirectory()) {
                String path = file.getAbsolutePath();
                leerFicheroPalabras(diccionario, tipo_categorias, new File(path)); // Leer palabras del fichero
            }
        }
        tupla[0] = diccionario;
        tupla[1] = tipo_categorias;
        return tupla;
    }

// En tipo_categorias almacenamos el tipo de la categoria de este fichero
// segun el tipo el parseo sera diferente aunque todas usaran palabras.


    void leerFicheroPalabras(HashMap<String, Object> diccionario, HashMap<String, Object> tipo_categorias, File fichero) {
        Scanner myReader = null;
        String categoria_actual = "";
        int n_linea = 1;
        try {
            myReader = new Scanner(fichero);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (myReader.hasNextLine()) {
            String primeraLinea = myReader.nextLine();
            if (n_linea == 1){
                String[] categoria_tipo_splited = primeraLinea.split(",");
                if (categoria_tipo_splited.length != 2) {
                    System.out.println("Error");
                } else {
                    String[] categoria_line = categoria_tipo_splited[0].split("=");
                    if ((categoria_line.length != 2) || (categoria_line[0] != "categoria" || categoria_line[1] == "")){
                        System.out.println("Error");
                    } else {
                        String[] tipo_line = categoria_tipo_splited[1].split("=");
                        if ((tipo_line.length != 2) || (tipo_line[0] != "tipo" || tipo_line[1] == "")) {
                            System.out.println("Error");
                        } else {
                            //Parseo del tipo correcto, transformandolo en entero. (PYTHON)
                            // Esto en Java no vamos a poder hacerlo, comento la línea.

                            // int tipo = Integer.parseInt(tipo_line[1]);

                            // Parseo de la primera linea correcto, guardamos en diccionario
                            categoria_actual = categoria_line[1];
                            //diccionario.put(categoria_actual,new HashMap<String, String>());
                            tipo_categorias.put(categoria_actual, tipo_line[1]);
                            }
                        }
                    }
                }else {
                    /*
                     * Buscando palabras si las hay. Si no las hay querra decir que esta categoria es del tipo Sexo: Femenino
                     *  es decir, que no hay que hacer busqueda, en esta categoria, si no simplemente parsearla.
                     */
                    // Cogemos todos los sinonimos, que estaran separados por el caracter ':'
                    String[] sinonimos = primeraLinea.split(":");
                    /*
                       Metemos la primera palabra en el diccionario, que no tiene sinonimos por ser el padre
                       Notese que como palabra ponemos la primera de la linea, sinonimos[0] sin sinonimo
                       asociado ("") ya que el mismo es el padre.
                     */
                    ((HashMap<String, String>) diccionario.get(categoria_actual)).put(sinonimos[0], "");
                    // Ahora recorremos el resto de sinonimos si los tiene.
                    for(int i=0;i<sinonimos.length; i++){
                        ((HashMap<String, String>) diccionario.get(categoria_actual)).put(sinonimos[i], sinonimos[0]);
                    }

                }
            n_linea++;
        }
        myReader.close();
    }

}



