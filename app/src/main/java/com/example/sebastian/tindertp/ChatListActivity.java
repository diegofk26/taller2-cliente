package com.example.sebastian.tindertp;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.example.sebastian.tindertp.application.TinderTP;
import com.example.sebastian.tindertp.chatListTools.CustomAdapter;
import com.example.sebastian.tindertp.chatListTools.RowItem;
import com.example.sebastian.tindertp.commonTools.ArraySerialization;
import com.example.sebastian.tindertp.commonTools.Common;
import com.example.sebastian.tindertp.commonTools.ConnectionStruct;
import com.example.sebastian.tindertp.commonTools.DataThroughActivities;
import com.example.sebastian.tindertp.commonTools.HeaderBuilder;
import com.example.sebastian.tindertp.commonTools.Messages;
import com.example.sebastian.tindertp.internetTools.RequestResponseClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private List<RowItem> rowItems;
    private List<String> userNames;
    private List<Integer> profilePics;
    private List<String> lastMessages;
    private ListView mylistview;
    private boolean[] haveToUpdate;
    private int lastItemSelected;
    private boolean paused;

    private String url;
    private String user;
    private String token;

    private CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        url = ((TinderTP) this.getApplication()).getUrl();
        user = ((TinderTP) this.getApplication()).getUser();
        token = ((TinderTP) this.getApplication()).getToken();

        rowItems = new ArrayList<>();
        paused = false;

        //pedir usuarios al server
        userNames = getUserNames();
        haveToUpdate = new boolean[userNames.size()];
        profilePics = getProfilePics();
        initMessages();
        //hasta aca

        buildRowItems();

        adapter = new CustomAdapter(this, rowItems);

        if (DataThroughActivities.getInstance().hasMessages() ) {
            ArrayList<String> recentUsers = DataThroughActivities.getInstance().getUsers();
            ArrayList<String> recentMessages= DataThroughActivities.getInstance().getMessages();

            int indexLastUser=0;
            for(int i = 0; i < recentUsers.size(); i++) {
                indexLastUser = updateUserLastMessage(recentUsers.get(i),recentMessages.get(i));
                haveToUpdate[indexLastUser] = true;
                adapter.updateBold(rowItems, indexLastUser, true);

            }
            if(!DataThroughActivities.getInstance().areTwoDifferntUsers()) {
                adapter.restore();
                adapter.notifyDataSetInvalidated();
                updatePriorActivities(userNames.get(indexLastUser));
                DataThroughActivities.getInstance().deleteMssg();
                ArraySerialization.deleteStringFromArray(this, userNames.get(indexLastUser));
                startChat(userNames.get(indexLastUser));
            }
        }

        mylistview = (ListView) findViewById(R.id.list);
        mylistview.setAdapter(adapter);

        mylistview.setOnItemClickListener(this);

        getLastMessages();
        getNewMsg();

        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("CHAT_LIST"));
    }

    private void initMessages() {
        lastMessages = new ArrayList<>();
        for (int i = 0; i < userNames.size(); i++) {
            lastMessages.add("");
        }
    }

    private void getNewMsg() {
        if(getIntent().hasExtra(Common.MSSG_KEY)) {
            List<String> messages = getIntent().getStringArrayListExtra(Common.MSSG_KEY);
            List<String> users = getIntent().getStringArrayListExtra(Common.USER_MSG_KEY);

            for (int i = 0; i < userNames.size(); i++) {
                int index = users.lastIndexOf(userNames.get(i));
                if(index >= 0) {
                    addTransmitterToMssg(i, userNames.get(i),messages.get(index));
                    rowItems.clear();
                    buildRowItems();
                    haveToUpdate[i]=true;
                    updateListView(i, true);
                }
            }
        }
    }

    private void buildRowItems() {
        for (int i = 0; i < userNames.size(); i++) {
            RowItem item = new RowItem(userNames.get(i),
                    profilePics.get(i), lastMessages.get(i));
            rowItems.add(item);
        }
    }

    private List<String > getUserNames() {
        List<String> users = new ArrayList<>();
        users.add("Rocio");
        return users;
    }

    private List<Integer> getProfilePics() {
        List<Integer> profilePics = new ArrayList<>();
        profilePics.add(R.drawable.aldana);
        return profilePics;

    }

    private void getLastMessages() {
        ConnectionStruct conn = new ConnectionStruct(Common.MESSAGES, Common.GET, url);

        for(int i = 0; i < userNames.size(); i++) {
            Log.i("GetMSSG", "obtengo mensajes de " + userNames.get(i));
            Map<String, String> headers = HeaderBuilder.forLoadOneMessage(token, user, userNames.get(i), 1);
            GetOneMessage getOneMessage = new GetOneMessage(this, conn, headers,userNames.get(i));
            getOneMessage.runInBackground();
        }
    }

    private void restoreFonts(int position) {
        if( haveToUpdate[position]) {
            NotificationManager notificationManager = (NotificationManager)getSystemService
                    (Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(0);
            Messages.getInstance().clear();
            adapter.restore();
            adapter.notifyDataSetInvalidated();
            mylistview.setAdapter(adapter);
            updatePriorActivities(userNames.get(position));
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        paused = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (paused) {
            Log.i("GetMSSG", "Resumee de chat");
            paused = false;
            ConnectionStruct conn = new ConnectionStruct(Common.MESSAGES, Common.GET, url);
            Map<String, String> headers = HeaderBuilder.forLoadOneMessage(token, user, userNames.get(lastItemSelected), 1);
            GetOneMessage getOneMessage = new GetOneMessage(this, conn, headers, userNames.get(lastItemSelected));
            getOneMessage.runInBackground();
        }
    }

    private void startChat(String userName) {
        Intent chat = new Intent(this, ChatActivity.class);
        chat.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        chat.putExtra("from",userName);
        this.startActivity(chat);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        lastItemSelected = position;
        restoreFonts(position);
        String userName = rowItems.get(position).getUserName();
        if (DataThroughActivities.getInstance().hasMessages()) {
            DataThroughActivities.getInstance().deleteMssg(userName);
        } else if (ArraySerialization.hasPersistedMssg(this)) {
            Log.i("asd","Borro mesanjes de usuario");
            ArraySerialization.deleteStringFromArray(this,userName);
        }
        startChat(userName);
    }

    private void updatePriorActivities(String user) {
        Intent activityMsg = new Intent("PRIOR");
        activityMsg.putExtra("user", user);
        LocalBroadcastManager.getInstance(this).sendBroadcast(activityMsg);
    }

    private void updateListView(final int index, final boolean isBold) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.updateBold(rowItems, index, isBold);
                adapter.notifyDataSetInvalidated();
                mylistview.setAdapter(adapter);
            }
        });
    }

    private BroadcastReceiver onNotice = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String message = intent.getStringExtra("message");
            String user = intent.getStringExtra("user");

            if(message!=null && user != null) {

                int index = updateUserLastMessage(user,message);
                haveToUpdate[index] = true;
                updateListView(index, true);
            }
        }
    };

    private int updateUserLastMessage(String user, String message) {
        int index = userNames.indexOf(user);
        addTransmitterToMssg(index,user,message);
        rowItems.clear();
        buildRowItems();
        return index;
    }

    private void addTransmitterToMssg(int index, String transmitter, String message) {
        if (transmitter.equals(user)) {
            lastMessages.set(index, "Tú: " + message);
        } else {
            lastMessages.set(index, transmitter + ": " + message);
        }
    }

    private class GetOneMessage extends RequestResponseClient {

        private String userFrom;

        public GetOneMessage(Activity ctx, ConnectionStruct conn, Map<String, String> values, String userFrom) {
            super(ctx, conn, values);
            this.userFrom = userFrom;
        }

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
                       addTransmitterToMssg(index,transmitter,jsonO.getString("mensaje"));
                       rowItems.clear();
                       buildRowItems();
                       haveToUpdate[index]=true;
                       updateListView(index, false);
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
            Snackbar.make(findViewById(R.id.list), message, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }
}