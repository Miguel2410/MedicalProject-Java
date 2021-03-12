import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class parser {

    /*
        Tipos de parseo:
        -1: No printable
        0: Tipo nombre, edad, etc
        1: Tipo motivo de consulta: buscar palarbas
     */

    /**
     *
     * @param diccionario_palabras
     * @param texto
     */
    public List parsearTextoTipo_1(HashMap<String, Object> diccionario_palabras, String texto){
        /*
          Este tipo es del tipo motivo de consulta, es decir consiste en buscar las palabras
          diccionario_palabras que existen en el texto. El resultado se guardara en un string
          y las palabras iran separadas por ;
         */
        List variables_encontradas = new ArrayList();
        List sinonimo_encontrado = new ArrayList();
        List coordenadas = new ArrayList();

        /*
         El codigo de parseo es muy sencillo. Recorremos todas las palabras y vemos si estan
         en el texto. Esto es mas sencillo que ver que palabras del texto estan en diccionario
         ya que hay espacios en blanco y no se sabe que trozos de palabras habria que buscar
         Notese que los caracteres como el salto de linea no los borramos en el formateo
         principal ya que estos caracteres podrian ser utiles en otras funciones con otras formas de parsear
         */

        //texto_clean = formatText(texto.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ')) # Eliminamos mayusculas y caracteres raros
        String texto_clean = texto;
        Iterator it = diccionario_palabras.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();

            Pattern p = Pattern.compile((String) pair.getKey());
            Matcher m = p.matcher(texto_clean);
            if (m.find()){ // Si la palabra esta en el texto
                if(!variables_encontradas.contains((String) pair.getKey())){
                    //Dos casos, o bien tiene sinonimo, o bien simplemente esa es la palabra a poner
                    if(diccionario_palabras.get(pair.getKey()) == ""){ // pair.getValue() no es mejor???
                        variables_encontradas.add(pair.getKey());
                        sinonimo_encontrado.add(""); // Metemos cadena vacia
                    }else{
                        // Si la palabra tiene un sinonimo padre, ponemos ese, para poner siempre lo mismo
                        variables_encontradas.add(pair.getValue());
                        sinonimo_encontrado.add(pair.getKey());
                    }
                   // Finalmente, calculamos las coordenadas de todas las instancias para luego poder pintarlo
                   // Como puede haber varias instancias, un array de coordenadas sera un array de arrays
                    List coordenadas_actuales = new ArrayList();
                    while ( m.find() ){
                        List coor = new ArrayList();
                        coor.add(m.start());
                        coor.add(m.end());
                        coordenadas_actuales.add(coor);
                    }
                    coordenadas.add(coordenadas_actuales);
                }
            }
            it.remove(); // para evitar ConcurrentModificationException
            // Incluimos las variables en el texto
        }
        List returnear = new ArrayList();
        returnear.add(variables_encontradas);
        returnear.add(sinonimo_encontrado);
        returnear.add(coordenadas);

        return returnear;
    }

    /*
        Finalmente, incluimos las palabras nuevas, si las hay, que empezaran por *
         Para ello buscamos todas las palabras que empiecen por * en el texto
    patron = re.compile("^\*")
    texto_clean_splited=texto_clean.split(' ') # Hemos quitado los saltos de linea por lo que
                                               # siempre funcionara!
    for palabra in texto_clean_splited:
        if(patron.search(palabra)): # Buscamos si la palabra tiene asterisco !
            # Si es asi, quitamos el * y reemplazamos los caracteres _ por espacios
            variable_nueva=palabra.replace('*', '').replace('_', ' ')
            texto_parseado+=variable_nueva+";"
     */


    /*
     Recibe:
        fichero_pdf: una ruta al fichero pdf a parsear,
        diccionario: un diccionario de palabras para ser utilizadas durante el parsing.
               # formato: diccionario[categoria][palabra]=sinonimo padre
        tipo_categorias: indica para cada categoria el tipo. Esto determinara la manera en la que se
               # parsea la seccion
     */

    public HashMap<String, String> extraer_categorias(String fichero_pdf, HashMap<String, Object> tipo_categorias) {
        //Inicializando los datos de salida, que sea una lista de categoria: string

        HashMap<String, String> diccionario_texto_categorias = new HashMap<>();
        diccionario_texto_categorias.put("","");
        String categoria = "";
        //Empezamos a leer el pdf
        // Tratamiento del fichero pdf y lectura
        PDDocument document = null;
        File file = new File(fichero_pdf);
        try {
            document = PDDocument.load(file);
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);

            String[] lines = text.split(System.getProperty("line.separator"));
            for(String line : lines){
                String[] line_splitted = line.split(":");
                // String posible_categoria = formatText.   Esto formatea pero aqui quizá no hace falta, idk

                /*
                 Habra que tratar la categoria segun el tipo de variable. Si por ejemplo es la edad
                 pues simplemente basta con guardarlo, pero si es historia clinica habra que recorrerlo e ir
                 buscando los elementos de la lista. Este tipo de parseo es determinado por tipo_categorias
                 la variable anteriormente encontrada
                 */
                if(tipo_categorias.containsKey(line_splitted[0])){
                    categoria = line_splitted[0];
                    if((Integer)tipo_categorias.get(categoria) == 0){ //Este casteo está feisimo, pero es solucion rapida.
                        // Si la categoria anterior es una categoria existente, guardamos su resultado
                        String parte_derecha = line_splitted[1];
                        diccionario_texto_categorias.put(categoria,parte_derecha);

                    }
                }else{
                    diccionario_texto_categorias.put(categoria,line);
                }
            }
            document.close();
            return diccionario_texto_categorias;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (document != null) {
                try {//cierra el flujo principal
                    document.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}
