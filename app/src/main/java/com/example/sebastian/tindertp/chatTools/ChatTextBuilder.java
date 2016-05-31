package com.example.sebastian.tindertp.chatTools;

import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.widget.EditText;
import android.widget.Scroller;
/**Builder que prepara un ChatText programaticamente.*/
public class ChatTextBuilder {

    public static void chatEditor(EditText chatText, Context ctx) {
        chatText.setScroller(new Scroller(ctx));
        chatText.setMaxLines(2);
        chatText.setVerticalScrollBarEnabled(true);
        chatText.setMovementMethod(new ScrollingMovementMethod());

    }

}
