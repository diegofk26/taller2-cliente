package com.example.sebastian.tindertp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.sebastian.tindertp.chatListTools.CustomAdapter;
import com.example.sebastian.tindertp.chatListTools.RowItem;
import com.example.sebastian.tindertp.commonTools.Common;
import com.example.sebastian.tindertp.commonTools.DataThroughActivities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChatListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private List<RowItem> rowItems;
    private List<String> userNames;
    private List<Integer> profilePics;
    private List<String> lastMessages;
    private ListView mylistview;
    private boolean[] updated;

    private CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rowItems = new ArrayList<>();

        //pedir usuarios al server
        userNames = getUserNames();
        updated = new boolean[userNames.size()];
        profilePics = getProfilePics();
        getLastMessages();
        //hasta aca

        buildRowItems();

        adapter = new CustomAdapter(this, rowItems);

        if (DataThroughActivities.getInstance().hasMessages() ){
            ArrayList<String> recentUsers = DataThroughActivities.getInstance().getUsers();
            ArrayList<String> recentMessages= DataThroughActivities.getInstance().getMessages();

            for(int i = 0; i < recentUsers.size(); i++) {
                int index = updateUserLastMessage(recentUsers.get(i),recentMessages.get(i));
                updated[index] = true;
                adapter.update(rowItems,index);

            }
            if(!DataThroughActivities.getInstance().areTwoDifferntUsers()) {
                Common.startActivity(this, ChatActivity.class);
            }
        }

        mylistview = (ListView) findViewById(R.id.list);
        mylistview.setAdapter(adapter);

        mylistview.setOnItemClickListener(this);

        getNewMsg();

        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("CHAT_LIST"));

    }

    private void getNewMsg() {
        if(getIntent().hasExtra(Common.MSSG_KEY)) {
            List<String> messages = getIntent().getStringArrayListExtra(Common.MSSG_KEY);
            List<String> users = getIntent().getStringArrayListExtra(Common.USER_MSG_KEY);

            for (int i = 0; i < userNames.size(); i++) {
                int index = users.lastIndexOf(userNames.get(i));
                if(index >= 0) {
                    lastMessages.set(i, messages.get(index));
                    rowItems.clear();
                    buildRowItems();
                    updated[i]=true;
                    updateListView(i);
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
        users.add("Aldana");users.add("Rocio");users.add("asddd");
        return users;
    }

    private List<Integer> getProfilePics(){
        List<Integer> profilePics = new ArrayList<>();
        profilePics.add(R.drawable.aldana);
        profilePics.add(R.drawable.aldana);
        profilePics.add(R.drawable.aldana);
        return profilePics;

    }

    private void getLastMessages() {
        lastMessages = new ArrayList<>();
        lastMessages.add("Hola sdads  skakkjj asdadsasd");
        lastMessages.add("sdasdd asdasdas");
        lastMessages.add("");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        if( updated[position]) {
            adapter.restore();
            adapter.notifyDataSetInvalidated();
            mylistview.setAdapter(adapter);
            updatePriorActivities(userNames.get(position));
        }

        String userName = rowItems.get(position).getUserName();
        Common.startActivity(this, ChatActivity.class);

    }

    private void updatePriorActivities(String user) {
        Intent activityMsg = new Intent("PRIOR");
        activityMsg.putExtra("user", user);

        LocalBroadcastManager.getInstance(this).sendBroadcast(activityMsg);
    }

    private void updateListView(final int index ) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.update(rowItems, index);
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
                updated[index] = true;
                updateListView(index);
            }
        }
    };


    private int updateUserLastMessage(String user, String message) {
        int index = userNames.indexOf(user);
        lastMessages.set(index, message);
        rowItems.clear();
        buildRowItems();
        return index;
    }
}