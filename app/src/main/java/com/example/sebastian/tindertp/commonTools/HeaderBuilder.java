package com.example.sebastian.tindertp.commonTools;

import java.util.HashMap;
import java.util.Map;

public class HeaderBuilder {

    public static Map<String, String> forRegister(String user, String pass, String tokenGCM) {
        Map<String, String> headers = new HashMap<>();
        headers.put(Common.USER_KEY, user);
        headers.put(Common.PASS_KEY, pass);
        headers.put(Common.TOKEN_GCM, tokenGCM);
        return headers;
    }

    public static Map<String, String> forLoadMessages(String token, String user,
                                                         String chatName, int desde) {
        Map<String, String> headers = new HashMap<>();
        headers.put(Common.USER1, user);
        headers.put(Common.USER2, chatName);
        headers.put(Common.TOKEN, token);
        headers.put(Common.DESDE, String.valueOf(desde));
        headers.put(Common.CANT, Common.MAX_MESSAGES);
        return headers;
    }

    public static Map<String, String> forLoadOneMessage(String token, String user,
                                                      String chatName, int desde) {
        Map<String, String> headers = new HashMap<>();
        headers.put(Common.USER1, user);
        headers.put(Common.USER2, chatName);
        headers.put(Common.TOKEN, token);
        headers.put(Common.DESDE, String.valueOf(desde));
        headers.put(Common.CANT, "1");
        return headers;
    }

    public static Map<String, String> forSendMessage(String token, String user, String chatName) {
        Map<String, String> headers = new HashMap<>();
        headers.put(Common.USER_KEY, user);
        headers.put(Common.RECEPTOR, chatName);
        headers.put(Common.TOKEN, token);
        return headers;
    }
}
