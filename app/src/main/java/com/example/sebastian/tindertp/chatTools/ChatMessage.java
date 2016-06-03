package com.example.sebastian.tindertp.chatTools;

/**Clase chat de un mensaje.*/
public class ChatMessage {

    public boolean left;/** Perteneciente al Usuario con el que se esta chateando,
                            su burbuja esta a la izquierda. */
    public String message;

    public ChatMessage(boolean left , String message) {
        super();
        this.left=left;
        this.message = message;

    }

}