package com.example.sebastian.tindertp.chatTools;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.sebastian.tindertp.R;
import com.example.sebastian.tindertp.application.TinderTP;
import com.example.sebastian.tindertp.commonTools.Common;
import com.example.sebastian.tindertp.commonTools.ConnectionStruct;
import com.example.sebastian.tindertp.commonTools.HeaderBuilder;
import com.example.sebastian.tindertp.internetTools.RequestResponseClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;


public class ClientBuilder {

    private static String user;
    private static String url;
    private static String token;
    private static String chatName;

    private static void init(Activity context) {
        user = ((TinderTP) context.getApplication()).getUser();
        url = ((TinderTP) context.getApplication()).getUrl();
        token = ((TinderTP) context.getApplication()).getToken();
        chatName = context.getIntent().getStringExtra("from");
    }

    public static RequestResponseClient build(Activity context, final ChatArrayAdapter adp) {

        init(context);

        ConnectionStruct conn = new ConnectionStruct(Common.MESSAGES, Common.GET, url);
        Map<String, String> headers = HeaderBuilder.forLoadMessages(token,user,chatName,1);

        RequestResponseClient client = new GetMessagesClient(context, conn, headers) {
            @Override
            protected void onPostExec() {
                if(!badResponse && isConnected) {
                    try {
                        JSONArray jsonA = new JSONArray(jsonString);
                        for (int i = jsonA.length() - 1; i >= 0; i--) {
                            JSONObject jsonO = jsonA.getJSONObject(i);
                            boolean side = !jsonO.getString("emisor").equals(user);
                            adp.add(new ChatMessage(side, jsonO.getString("mensaje")));
                        }
                    }catch (JSONException e) {showText("Problemas con los mensajes guardados.");}
                }else {
                    showText("No se pudo conectar con el server.");
                }
            }
        };

        return client;
    }

    public static RequestResponseClient build(Activity context, final ChatArrayAdapter adp, final EditText chatText) {

        init(context);

        final String text =  chatText.getText().toString();

        ConnectionStruct conn = new ConnectionStruct(Common.CHAT, Common.POST, url);
        Map<String, String> headers = HeaderBuilder.forSendMessage(token, user, chatName);

        RequestResponseClient client = new RequestResponseClient(context, conn, headers) {

            @Override
            protected void getJson() throws IOException {
            }

            private void updatePriorActivities(String user, String message) {
                Intent activityMsg = new Intent("CHAT_LIST");
                activityMsg.putExtra("user", user);
                activityMsg.putExtra("message", message);
                LocalBroadcastManager.getInstance(ctx).sendBroadcast(activityMsg);
            }

            @Override
            protected void onPostExec() {
                if (!badResponse && isConnected) {
                    adp.add(new ChatMessage(false, text));
                    updatePriorActivities(chatName, text);
                    chatText.setText("");
                } else {
                    showText("No se pudo enviar el mensaje.");
                }
            }

            @Override
            protected void showText(String message) {
                Snackbar.make(ctx.findViewById(R.id.listview), message, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        };

        return client;
    }

    public static RequestResponseClient build(Activity context, final ChatArrayAdapter adp, final ListView mssgList) {

        init(context);

        final Animation rotate = AnimationUtils.loadAnimation(context, R.anim.rotate);
        final Animation rotateInverse = AnimationUtils.loadAnimation(context, R.anim.rotate_inverse);

        ConnectionStruct conn = new ConnectionStruct(Common.MESSAGES, Common.GET, url);
        Map<String, String> headers = HeaderBuilder.forLoadMessages(token, user, chatName, adp.size());

        RequestResponseClient client = new GetMessagesClient(context, conn, headers) {
            @Override
            protected void onPostExec() {
                if(!badResponse && isConnected) {
                    try {
                        JSONArray jsonA = new JSONArray(jsonString);
                        ImageView rows = (ImageView)ctx.findViewById(R.id.row);
                        ImageView mssg = (ImageView)ctx.findViewById(R.id.center);
                        if (jsonA.length() != 0) {
                            rows.startAnimation(rotate);
                            mssg.startAnimation(rotateInverse);
                        } else {
                            rows.setImageResource(R.drawable.rows_grey);
                            mssg.setImageResource(R.drawable.mssg_grey);
                        }

                        for (int i = 0; i < jsonA.length(); i++) {
                            JSONObject jsonO = jsonA.getJSONObject(i);
                            boolean side = !jsonO.getString("emisor").equals(user);
                            adp.add(0, new ChatMessage(side, jsonO.getString("mensaje")));
                        }

                        int positionBeforeReload = mssgList.getFirstVisiblePosition() + jsonA.length();
                        if(positionBeforeReload == 10){
                            positionBeforeReload = 0;
                        }

                        ScrollListMaintainer.maintainScrollPosition(mssgList, positionBeforeReload);

                    }catch (JSONException e) {showText("Problemas con los mensajes guardados.");}
                }else {
                    showText("No se pudo conectar con el server.");
                }
            }
        };

        return client;
    }

    abstract private static class GetMessagesClient extends RequestResponseClient {

        public GetMessagesClient(Activity ctx, ConnectionStruct conn, Map<String, String> values) {
            super(ctx, conn, values);
        }

        @Override
        protected void getJson() throws IOException {
            jsonString = readIt();
        }

        @Override
        protected void showText(String message) {
            Snackbar.make(ctx.findViewById(R.id.listview), message, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }


}
