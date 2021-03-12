import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

public class database {

    private String fichero_database="database.txt";
    /**
     * Inicializa la base de datos. En esta version simplemente lo que hara es comprobar si esta creado el fichero
     * con la base de datos. Si no esta creado se crea y se pone la cabecera
     * Recibe el diccionario con las categorias para ponerlas en la cabecera del fichero
     */

    public void initDatabase(HashMap<String, Object> diccionario){
        // Primero comprobamos que el fichero no existe, si existe perfecto ya que simplemente vamos metiendo datos
        File file = new File(fichero_database);
        FileWriter writer = null;
        if(!file.exists()){
            try{
                writer = new FileWriter(file);
                //crea un buffer o flujo intermedio antes de escribir directamente en el archivo
                BufferedWriter bfwriter = new BufferedWriter(writer);
                Iterator it = diccionario.entrySet().iterator();
                while (it.hasNext()) {
                    HashMap.Entry pair = (HashMap.Entry)it.next();
                    bfwriter.write(pair.getKey() + "," );
                    it.remove(); // para evitar ConcurrentModificationException
                }
                bfwriter.write("\n" );
                bfwriter.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (writer != null) {
                    try {//cierra el flujo principal
                    writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    /**
     * Recibe un diccionario. Cada clave del diccionario es una columna de la tabla y cada valor es el valor de dicha
     * columna
     *
     * Obviamente el diccionario que se usa tiene que ser el mismo y las claves en el mismo orden, pero ya que no se
     * resetea, esto se va a mantener.
     */

    public void guardarEntrada(HashMap<String, Object> diccionario_datos){
        File file = new File(fichero_database);
        FileWriter writer = null;
        if(file.exists()){
            try{
                writer = new FileWriter(file, true);
                //crea un buffer o flujo intermedio antes de escribir directamente en el archivo
                BufferedWriter bfwriter = new BufferedWriter(writer);
                Iterator it = diccionario_datos.entrySet().iterator();
                while (it.hasNext()) {
                    HashMap.Entry pair = (HashMap.Entry)it.next();
                    bfwriter.write(pair.getKey() + "," );
                    it.remove(); // para evitar ConcurrentModificationException
                }
                bfwriter.write("\n" );
                bfwriter.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (writer != null) {
                    try {//cierra el flujo principal
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }
}
