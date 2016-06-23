package com.example.sebastian.tindertp.chatTools;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.sebastian.tindertp.Interfaces.ConectivityManagerInterface;
import com.example.sebastian.tindertp.Interfaces.DataTransfer;
import com.example.sebastian.tindertp.R;
import com.example.sebastian.tindertp.Interfaces.ViewUpdater;
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

    private String user; /**< Usuario al que pertenece la app */
    private String url;  /**< Url del server*/
    private String token;/**< Token que devuelve el server.*/
    private String chatEmail; /**< Usuario de la persona con la que se esta chateando*/

    public ClientBuilder(DataTransfer transfer) {
        user = transfer.getUser();
        url = transfer.getURL();
        token = transfer.getToken();
        chatEmail = transfer.getChatEmail();
    }

    /**Builder de un cliente para ChatListActivity, y luego ChatList se encarga de
     * recargar su vista.
     * */
    public void build(final ViewUpdater updater, final List<String> usersEmails,
                                              final String userFrom) {

        ConnectionStruct conn = new ConnectionStruct(Common.MESSAGES, Common.GET, url);
        Map<String, String> headers = HeaderBuilder.forLoadOneMessage(token, user, userFrom, 1);

        List<String> users = null;
        final boolean hasExtra = updater.hasExtra(Common.MSSG_KEY);

        if(hasExtra) {
            users = updater.getStringArrayExtra(Common.USER_MSG_KEY);
        }

        final List<String> finalUsers = users;
        RequestResponseClient client = new RequestResponseClient((ConectivityManagerInterface)updater, conn, headers) {

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
                            int index = usersEmails.indexOf(userFrom);
                            JSONObject jsonO = jsonA.getJSONObject(0);
                            String transmitter = jsonO.getString("emisor");
                            updater.addTransmitterToMssg(index, transmitter, jsonO.getString("mensaje"));
                            updater.clearRows();
                            updater.buildRowItems();
                            updater.haveToUpdate(index);
                            if (hasExtra && finalUsers.contains(userFrom))
                                updater.updateListView(index, true);
                            else
                                updater.updateListView(index, false);

                            if(hasExtra && index == usersEmails.size() -1 ) {
                                updater.removeExtra(Common.USER_MSG_KEY);
                                updater.removeExtra(Common.MSSG_KEY);
                            }
                        }
                    } catch (JSONException e) {
                        showText("Problemas con los mensajes guardados.");
                    }
                } else {
                    showText("No se pudo conectar con el server.");
                }
            }

            @Override
            protected void showText(String message) {
                Snackbar.make(((DataTransfer) updater).findView(R.id.list), message, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        };

        client.runInBackground();
    }

    /** Builder de un cliente para chat, en este caso el adapter se encarga de
     * actualizar la vista de ChatActivity. */
    public void build(final ChatArrayAdapter adp,final DataTransfer transfer) {

        ConnectionStruct conn = new ConnectionStruct(Common.MESSAGES, Common.GET, url);
        Map<String, String> headers = HeaderBuilder.forLoadMessages(token,user, chatEmail,1);

        RequestResponseClient client = new RequestResponseClient((ConectivityManagerInterface)transfer, conn, headers) {

            @Override
            protected void getJson() throws IOException {
                jsonString = readIt();
            }

            @Override
            protected void showText(String message) {
                Snackbar.make(transfer.findView(R.id.listview), message, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

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

        client.runInBackground();
    }

    /**Builder de un cliente que descarga y luego avisa a las vistas de las actividades anteriores
     * se actualicen.*/
    public void build(final String text, final ListView list,
                                              final ChatMessage chat, final DataTransfer transfer) {


        ConnectionStruct conn = new ConnectionStruct(Common.CHAT, Common.POST, url);
        Map<String, String> headers = HeaderBuilder.forSendMessage(token, user, chatEmail);

        RequestResponseClient client = new RequestResponseClient((ConectivityManagerInterface)transfer, conn, headers) {

            @Override
            protected void getJson() throws IOException {
            }

            private void updatePriorActivities(String user, String message) {
                Intent activityMsg = new Intent("CHAT_LIST");
                activityMsg.putExtra("user", user);
                activityMsg.putExtra("message", message);
                LocalBroadcastManager.getInstance(transfer.getContext()).sendBroadcast(activityMsg);
            }

            @Override
            protected void onPostExec() {
                if (!badResponse && isConnected) {
                    updatePriorActivities(chatEmail, text);

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
                }
            }

            @Override
            protected void showText(String message) {
                Snackbar.make(transfer.findView(R.id.listview), message, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        };

        client.addBody(text);
        client.runInBackground();
    }
    /**Builder de un cliente que luego de la descarga,
     *      - si no tiene mas mensajes actualiza la vista de ChatActivity, que como resultado
     *        cambie el icono de barra.
     *      - si tiene mensajes genera una animacion con el icono de barra.*/
    public void build(Context context, final ChatArrayAdapter adp, final ListView mssgList, final DataTransfer transfer) {

        final Animation rotate = AnimationUtils.loadAnimation(context, R.anim.rotate);
        final Animation rotateInverse = AnimationUtils.loadAnimation(context, R.anim.rotate_inverse);

        ConnectionStruct conn = new ConnectionStruct(Common.MESSAGES, Common.GET, url);
        Map<String, String> headers = HeaderBuilder.forLoadMessages(token, user, chatEmail, adp.size());

        RequestResponseClient client = new RequestResponseClient((ConectivityManagerInterface)transfer, conn, headers) {

            @Override
            protected void getJson() throws IOException {
                jsonString = readIt();
            }

            @Override
            protected void showText(String message) {
                Snackbar.make(transfer.findView(R.id.listview), message, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

            @Override
            protected void onPostExec() {
                if(!badResponse && isConnected) {
                    try {
                        JSONArray jsonA = new JSONArray(jsonString);
                        ImageView rows = (ImageView)transfer.findView(R.id.row);
                        ImageView mssg = (ImageView)transfer.findView(R.id.center);
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

        client.runInBackground();
    }
}
