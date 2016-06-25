package com.example.sebastian.tindertp;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
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

import com.example.sebastian.tindertp.Interfaces.ConectivityManagerInterface;
import com.example.sebastian.tindertp.Interfaces.DataTransfer;
import com.example.sebastian.tindertp.Interfaces.ViewUpdater;
import com.example.sebastian.tindertp.application.TinderTP;
import com.example.sebastian.tindertp.chatListTools.CustomAdapter;
import com.example.sebastian.tindertp.chatListTools.RowItem;
import com.example.sebastian.tindertp.chatTools.ClientBuilder;
import com.example.sebastian.tindertp.commonTools.ArraySerialization;
import com.example.sebastian.tindertp.commonTools.Common;
import com.example.sebastian.tindertp.commonTools.ConnectionStruct;
import com.example.sebastian.tindertp.commonTools.DataThroughActivities;
import com.example.sebastian.tindertp.commonTools.HeaderBuilder;
import com.example.sebastian.tindertp.commonTools.Messages;
import com.example.sebastian.tindertp.internetTools.NewUserDownloaderClient;
import com.example.sebastian.tindertp.services.ReceiverOnInfoIncome;
import com.example.sebastian.tindertp.services.ReceiverOnNewUserMatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,DataTransfer, ViewUpdater,ConectivityManagerInterface {

    private List<RowItem> rowItems;

    private List<String> usersEmails;
    private List<String> usersNames;
    private List<Bitmap> profilePics;
    private List<String> lastMessages;

    private ListView chatList;
    private boolean[] haveToUpdate;
    private int lastItemSelected;
    private boolean paused;

    private final static String CHAT_LIST_TAG = "ChatList";

    private String user;

    private CustomAdapter adapter;
    private ReceiverOnNewUserMatch onMatch;
    private ReceiverOnInfoIncome onProfileInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        user = ((TinderTP) this.getApplication()).getUser();

        rowItems = new ArrayList<>();
        paused = false;

        getUsersEmailsAndNames();
        haveToUpdate = new boolean[usersEmails.size()];
        getProfilePicsDefault();
        initMessages();

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
                updatePriorActivities(usersEmails.get(indexLastUser));
                DataThroughActivities.getInstance().deleteMssg();
                ArraySerialization.deleteStringFromArray(this, user, usersEmails.get(indexLastUser));
                startChat(usersNames.get(indexLastUser), usersEmails.get(indexLastUser));
            }
        }

        chatList = (ListView) findViewById(R.id.list);
        chatList.setAdapter(adapter);

        chatList.setOnItemClickListener(this);

        getLastMessages();

        onMatch = new ReceiverOnNewUserMatch(this,findViewById(R.id.relative_chat_list));
        onProfileInfo = new ReceiverOnInfoIncome(adapter,rowItems, usersEmails,profilePics,lastMessages);

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(onNotice,
                new IntentFilter(Common.CHAT_LIST_MSG_KEY));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(onMatch,
                new IntentFilter(Common.CHAT_LIST_MATCH_KEY));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(onProfileInfo,
                new IntentFilter(Common.SPECIFIC_USER_KEY));

        getRealProfilePics();
    }

    private void initMessages() {
        lastMessages = new ArrayList<>();
        for (int i = 0; i < usersEmails.size(); i++) {
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

        for(int i = 0; i < usersEmails.size(); i++) {
            if (recentUsers == null || !recentUsers.contains(usersEmails.get(i))) {
                client.build(this, usersEmails, usersEmails.get(i));
            }
        }
    }

    public void buildRowItems() {
        for (int i = 0; i < usersNames.size(); i++) {
            RowItem item = new RowItem(usersNames.get(i), usersEmails.get(i), profilePics.get(i), lastMessages.get(i));
            rowItems.add(item);
        }
    }

    private void getUsersEmailsAndNames() {
        usersEmails = new ArrayList<>();
        if (ArraySerialization.hasPersistedMatches(getApplicationContext(),user)) {
            usersEmails = ArraySerialization.getPersistedArray(getApplicationContext(),user,Common.MATCH_KEY);
        }

        usersNames = new ArrayList<>();

        for(int i = 0; i < usersEmails.size(); i++) {
            Log.i(CHAT_LIST_TAG, "Tengo "+usersEmails.get(i));
            usersNames.add(ArraySerialization.getUserName( getApplicationContext(), usersEmails.get(i)));
        }

    }

    private void getProfilePicsDefault() {
        profilePics = new ArrayList<>();
        for (int i = 0; i < usersEmails.size(); i++) {
            profilePics.add(null);
        }
    }

    private void getRealProfilePics() {
        String url = getURL();
        String token = getToken();
        ConnectionStruct conn = new ConnectionStruct(Common.INFO,Common.GET,url);

        for (int i = 0; i < usersEmails.size(); i++) {
            Map<String,String> values = HeaderBuilder.forUserInfo(user, token, usersEmails.get(i));
            NewUserDownloaderClient client = new NewUserDownloaderClient(this, findViewById(R.id.relative_chat_list),
                    Common.SPECIFIC_USER_KEY, conn, values);
            client.runInBackground();
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

            updatePriorActivities(usersEmails.get(position));
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
            client.build(this, usersEmails, usersEmails.get(lastItemSelected));
        }
    }

    private void startChat(String userName, String userEmial) {
        Intent chat = new Intent(this, ChatActivity.class);
        chat.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        chat.putExtra("fromName", userName);
        chat.putExtra("fromEmail",userEmial);
        this.startActivity(chat);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        lastItemSelected = position;
        restoreFonts(position);
        String userName = rowItems.get(position).getUserName();
        String userEmial = usersEmails.get(position);
        if (DataThroughActivities.getInstance().hasMessages()) {
            DataThroughActivities.getInstance().deleteMssg(userEmial);
        }
        if (ArraySerialization.hasPersistedMssg(getApplicationContext(),user)) {
            ArraySerialization.deleteStringFromArray(getApplicationContext(),user,userEmial);
        }
        startChat(userName,userEmial);
    }

    private void updatePriorActivities(String userEmail) {
        Intent activityMsg = new Intent(Common.MSSG_READED_KEY);
        activityMsg.putExtra("user", userEmail);
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
        int index = usersEmails.indexOf(user);
        addTransmitterToMssg(index,user,message);
        rowItems.clear();
        buildRowItems();
        return index;
    }

    public void addTransmitterToMssg(int index, String transmitter, String message) {
        if (transmitter.equals(user)) {
            lastMessages.set(index, "Tú: " + message);
        } else {
            lastMessages.set(index, usersNames.get(index) + ": " + message);
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
        return null;
    }

    @Override
    public String getChatEmail() {
        return null;
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