package com.example.sebastian.tindertp.chatTools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.sebastian.tindertp.ChatListActivity;
import com.example.sebastian.tindertp.DataTransfer;
import com.example.sebastian.tindertp.GetMessagesClient;
import com.example.sebastian.tindertp.R;
import com.example.sebastian.tindertp.ViewUpdater;
import com.example.sebastian.tindertp.application.TinderTP;
import com.example.sebastian.tindertp.commonTools.Common;
import com.example.sebastian.tindertp.commonTools.ConnectionStruct;
import com.example.sebastian.tindertp.commonTools.HeaderBuilder;
import com.example.sebastian.tindertp.internetTools.RequestResponseClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/** Construye el cliente para la descarga de Mensajes. */
public class ClientBuilder {

    private static String user; /**< Usuario al que pertenece la app */
    private static String url;  /**< Url del server*/
    private static String token;/**< Token que devuelve el server.*/
    private static String chatName; /**< Usuario de la persona con la que se esta chateando*/

    private static void init(DataTransfer transfer) {
        user = transfer.getUser();
        url = transfer.getURL();
        token = transfer.getToken();
        chatName = transfer.getChatName();
    }

    /**Builder de un cliente para ChatListActivity, y luego ChatList se encarga de
     * recargar su vista.
     * */
    public static RequestResponseClient build(ViewUpdater updater, final List<String> userNames,
                                              final String userFrom) {

        init((DataTransfer)updater);

        ConnectionStruct conn = new ConnectionStruct(Common.MESSAGES, Common.GET, url);
        Map<String, String> headers = HeaderBuilder.forLoadOneMessage(token, user, userFrom, 1);

        List<String> users = null;
        final boolean hasExtra = updater.hasExtra(Common.MSSG_KEY);

        if(hasExtra) {
            users = updater.getStringArrayExtra(Common.USER_MSG_KEY);
        }

        final List<String> finalUsers = users;
        RequestResponseClient client = new RequestResponseClient((DataTransfer)updater, conn, headers) {

            @Override
            protected void getJson() throws IOException {
                jsonString = readIt();
            }

            @Override
            protected void onPostExec() {
                if (!badResponse && isConnected) {
                    try {
                        JSONArray jsonA = new JSONArray(jsonString);
                        if(jsonA.length() != 0) {
                            int index = userNames.indexOf(userFrom);
                            JSONObject jsonO = jsonA.getJSONObject(0);
                            String transmitter = jsonO.getString("emisor");
                            ((ViewUpdater)dTransfer).addTransmitterToMssg(index, transmitter, jsonO.getString("mensaje"));
                            ((ViewUpdater)dTransfer).clearRows();
                            ((ViewUpdater)dTransfer).buildRowItems();
                            ((ViewUpdater)dTransfer).haveToUpdate(index);
                            if (hasExtra && finalUsers.contains(userFrom))
                                ((ViewUpdater)dTransfer).updateListView(index, true);
                            else
                                ((ViewUpdater)dTransfer).updateListView(index, false);

                            if(hasExtra && index == userNames.size() -1 ) {
                                ((ViewUpdater)dTransfer).removeExtra(Common.USER_MSG_KEY);
                                ((ViewUpdater)dTransfer).removeExtra(Common.MSSG_KEY);
                            }
                        }
                    } catch (JSONException e) {
                        showText("Problemas con los mensajes guardados.");
                    }
                } else {
                    showText("No se pudo conectar con el server.");
                }dTransfer = null;
            }

            @Override
            protected void showText(String message) {
                Snackbar.make(dTransfer.findView(R.id.list), message, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        };

        return client;

    }

    /** Builder de un cliente para chat, en este caso el adapter se encarga de
     * actualizar la vista de ChatActivity. */
    public static RequestResponseClient build(final ChatArrayAdapter adp, DataTransfer transfer) {

        init(transfer);

        ConnectionStruct conn = new ConnectionStruct(Common.MESSAGES, Common.GET, url);
        Map<String, String> headers = HeaderBuilder.forLoadMessages(token,user,chatName,1);

        RequestResponseClient client = new GetMessagesClient(transfer, conn, headers) {
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
                }dTransfer = null;
            }
        };

        return client;
    }

    /**Builder de un cliente que descarga y luego avisa a las vistas de las actividades anteriores
     * se actualicen.*/
    public static RequestResponseClient build(final String text, final ListView list,
                                              final ChatMessage chat, DataTransfer transfer) {

        init(transfer);

        ConnectionStruct conn = new ConnectionStruct(Common.CHAT, Common.POST, url);
        Map<String, String> headers = HeaderBuilder.forSendMessage(token, user, chatName);

        RequestResponseClient client = new RequestResponseClient(transfer, conn, headers) {

            @Override
            protected void getJson() throws IOException {
            }

            private void updatePriorActivities(String user, String message) {
                Intent activityMsg = new Intent("CHAT_LIST");
                activityMsg.putExtra("user", user);
                activityMsg.putExtra("message", message);
                LocalBroadcastManager.getInstance(dTransfer.getContext()).sendBroadcast(activityMsg);
            }

            @Override
            protected void onPostExec() {
                if (!badResponse && isConnected) {
                    updatePriorActivities(chatName, text);

                } else {
                    ChatArrayAdapter adp = (ChatArrayAdapter) list.getAdapter();
                    //obtiene la posicion dentro de los items visibles del ListView.
                    int firstPosition = list.getFirstVisiblePosition() - list.getHeaderViewsCount();
                    int position = adp.indexOf(chat);
                    int wantedChild = position - firstPosition;

                    adp.change(position);

                    if (wantedChild >= 0 && wantedChild < list.getChildCount()) {
                        View v = list.getChildAt( wantedChild );
                        adp.getView(position, v, list);
                    }

                    showText("No se envió el mensaje. Click en el mensaje para reintentar.");
                }dTransfer = null;
            }

            @Override
            protected void showText(String message) {
                Snackbar.make(dTransfer.findView(R.id.listview), message, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        };

        return client;
    }
    /**Builder de un cliente que luego de la descarga,
     *      - si no tiene mas mensajes actualiza la vista de ChatActivity, que como resultado
     *        cambie el icono de barra.
     *      - si tiene mensajes genera una animacion con el icono de barra.*/
    public static RequestResponseClient build(Context context, final ChatArrayAdapter adp, final ListView mssgList, DataTransfer transfer) {

        init(transfer);

        final Animation rotate = AnimationUtils.loadAnimation(context, R.anim.rotate);
        final Animation rotateInverse = AnimationUtils.loadAnimation(context, R.anim.rotate_inverse);

        ConnectionStruct conn = new ConnectionStruct(Common.MESSAGES, Common.GET, url);
        Map<String, String> headers = HeaderBuilder.forLoadMessages(token, user, chatName, adp.size());

        RequestResponseClient client = new GetMessagesClient(transfer, conn, headers) {
            @Override
            protected void onPostExec() {
                if(!badResponse && isConnected) {
                    try {
                        JSONArray jsonA = new JSONArray(jsonString);
                        ImageView rows = (ImageView)dTransfer.findView(R.id.row);
                        ImageView mssg = (ImageView)dTransfer.findView(R.id.center);
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
                }dTransfer=null;
            }
        };

        return client;
    }
}
