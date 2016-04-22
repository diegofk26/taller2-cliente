package com.example.sebastian.tindertp;

import java.util.Iterator;
import java.util.List;
//!Adaptador del array de urls.
/**Cambia la funcionalidad del recorrido del array, para que primero descarga las primeras 3 urls
 * y depues descargue la siguiente solo si la imagen actual apunta a la
 * a la anteultima posicion del array.*/
public class UrlArrayAdapter {

    private int end;/**< Numero para descargar las primeras imagenes.*/
    private Iterator<String>it;
    private int i;/**< posicion actual de imagenes descargas en el array.*/

    public UrlArrayAdapter(List<String> urls) {
        it = urls.iterator();
        i = 0; //to iterate
        end = 3;
    }

    public boolean hasNext() {
        boolean has = (it.hasNext() && i < end);

        if (it.hasNext() && !(i < end)){
            end++;
        }
        return has;
    }

    public String next() {
        i++;
        return it.next();
    }

    public boolean downloadComplete(){
        return !it.hasNext();
    }
}
