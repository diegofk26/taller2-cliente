package com.example.sebastian.tindertp;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.sebastian.tindertp.application.TinderTP;
import com.example.sebastian.tindertp.chatListTools.CustomAdapter;
import com.example.sebastian.tindertp.chatListTools.RowItem;
import com.example.sebastian.tindertp.chatTools.ClientBuilder;
import com.example.sebastian.tindertp.commonTools.ArraySerialization;
import com.example.sebastian.tindertp.commonTools.Common;
import com.example.sebastian.tindertp.commonTools.ConnectionStruct;
import com.example.sebastian.tindertp.commonTools.DataThroughActivities;
import com.example.sebastian.tindertp.commonTools.Messages;
import java.util.ArrayList;
import java.util.List;

public class ChatListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,DataTransfer, ViewUpdater,ConectivityManagerInterface {

    private List<RowItem> rowItems;
    private List<String> userNames;
    private List<Integer> profilePics;
    private List<String> lastMessages;
    private ListView chatList;
    private boolean[] haveToUpdate;
    private int lastItemSelected;
    private boolean paused;

    private final static String CHAT_LIST_TAG = "ChatList";

    private String user;

    private CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        user = ((TinderTP) this.getApplication()).getUser();

        int orientation = getResources().getConfiguration().orientation;

        setBackgroundOnOrientation(orientation);

        rowItems = new ArrayList<>();
        paused = false;

        //pedir usuarios al server
        userNames = getUserNames();
        haveToUpdate = new boolean[userNames.size()];
        profilePics = getProfilePics();
        initMessages();
        //hasta aca

        buildRowItems();

        adapter = new CustomAdapter(getApplicationContext(), rowItems);

        if (DataThroughActivities.getInstance().hasMessages() ) {
            Log.i(CHAT_LIST_TAG,"Tiene mensajes nuevos del Pending Intent.");
            ArrayList<String> recentUsers = DataThroughActivities.getInstance().getUsers();
            ArrayList<String> recentMessages = DataThroughActivities.getInstance().getMessages();

            int indexLastUser = 0;
            for(int i = 0; i < recentUsers.size(); i++) {
                indexLastUser = updateUserLastMessage(recentUsers.get(i),recentMessages.get(i));
                haveToUpdate[indexLastUser] = true;
                adapter.updateBold(rowItems, indexLastUser, true);
            }

            if(!DataThroughActivities.getInstance().areTwoDifferntUsers()) {
                Log.i(CHAT_LIST_TAG,"Mensajes Pending Intent de un solo usuario.");
                adapter.restore(indexLastUser);
                adapter.notifyDataSetInvalidated();
                updatePriorActivities(userNames.get(indexLastUser));
                DataThroughActivities.getInstance().deleteMssg();
                ArraySerialization.deleteStringFromArray(this, userNames.get(indexLastUser));
                startChat(userNames.get(indexLastUser));
            }
        }

        chatList = (ListView) findViewById(R.id.list);
        chatList.setAdapter(adapter);

        chatList.setOnItemClickListener(this);

        getLastMessages();

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(onNotice, new IntentFilter("CHAT_LIST"));
    }

    private void initMessages() {
        lastMessages = new ArrayList<>();
        for (int i = 0; i < userNames.size(); i++) {
            lastMessages.add("");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RelativeLayout rLayout = (RelativeLayout) findViewById (R.id.relative_chat_list);
        rLayout.setBackgroundResource(0);

    }

    private void getLastMessages() {

        ArrayList<String> recentUsers = null;

        if (DataThroughActivities.getInstance().hasMessages() ) {
            recentUsers = DataThroughActivities.getInstance().getUsers();
        }

        ClientBuilder client = new ClientBuilder(this);

        for(int i = 0; i < userNames.size(); i++) {
            if (recentUsers == null || !recentUsers.contains(userNames.get(i))) {

                client.build(this, userNames, userNames.get(i));
            }
        }
    }

    public void buildRowItems() {
        for (int i = 0; i < userNames.size(); i++) {
            RowItem item = new RowItem(userNames.get(i),
                    profilePics.get(i), lastMessages.get(i));
            rowItems.add(item);
        }
    }

    private List<String > getUserNames() {
        List<String> users = new ArrayList<>();
        users.add("Aldana");
        users.add("Rocio");
        return users;
    }

    private List<Integer> getProfilePics() {
        List<Integer> profilePics = new ArrayList<>();
        profilePics.add(R.drawable.aldana);
        profilePics.add(R.drawable.aldana);
        return profilePics;

    }

    private void setBackgroundOnOrientation(int orientation){
        RelativeLayout rLayout = (RelativeLayout) findViewById (R.id.relative_chat_list);
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            rLayout.setBackgroundResource(R.drawable.beach_horizontal);
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT){
            rLayout.setBackgroundResource(R.drawable.beach_vertical);
        }
    }

    private void restoreFonts(int position) {
        if( haveToUpdate[position] ) {
            NotificationManager notificationManager = (NotificationManager)getSystemService
                    (Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(0);

            Messages.getInstance().clear();

            adapter.restore(position);
            adapter.notifyDataSetInvalidated();
            chatList.setAdapter(adapter);

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
            paused = false;
            Log.i(CHAT_LIST_TAG,"Obtengo mensajes al resumir ChatListActivity.");
            ClientBuilder client = new ClientBuilder(this);
            client.build(this, userNames, userNames.get(lastItemSelected));
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
        }
        if (ArraySerialization.hasPersistedMssg(getApplicationContext())) {
            ArraySerialization.deleteStringFromArray(getApplicationContext(),userName);
        }
        startChat(userName);
    }

    private void updatePriorActivities(String user) {
        Intent activityMsg = new Intent("PRIOR");
        activityMsg.putExtra("user", user);
        LocalBroadcastManager.getInstance(this).sendBroadcast(activityMsg);
    }

    public void updateListView(final int index, final boolean isBold) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.updateBold(rowItems, index, isBold);
                adapter.notifyDataSetInvalidated();
                chatList.setAdapter(adapter);
            }
        });
    }

    @Override
    public void removeExtra(String key) {
        getIntent().removeExtra(key);
    }

    @Override
    public boolean hasExtra(String key) {
        return getIntent().hasExtra(Common.MSSG_KEY);
    }

    @Override
    public ArrayList<String> getStringArrayExtra(String key) {
        return getIntent().getStringArrayListExtra(Common.USER_MSG_KEY);
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

    public void addTransmitterToMssg(int index, String transmitter, String message) {
        if (transmitter.equals(user)) {
            lastMessages.set(index, "Tú: " + message);
        } else {
            lastMessages.set(index, transmitter + ": " + message);
        }
    }

    public void clearRows() {
        rowItems.clear();
    }

    public void haveToUpdate(int index) {
        haveToUpdate[index]=true;
    }

    @Override
    public String getUser() {
        return ((TinderTP) this.getApplication()).getUser();
    }

    @Override
    public String getURL() {
        return ((TinderTP) this.getApplication()).getUrl();
    }

    @Override
    public String getToken() {
        return ((TinderTP) this.getApplication()).getToken();
    }

    @Override
    public String getChatName() {
        return getIntent().getStringExtra("from");
    }

    @Override
    public ConnectivityManager getConectivityManager() {
        return (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    public View findView(int id) {
        return findViewById(id);
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }
}