package com.example.sebastian.tindertp;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.sebastian.tindertp.chatListTools.CustomAdapter;
import com.example.sebastian.tindertp.chatListTools.RowItem;
import com.example.sebastian.tindertp.commonTools.Common;

import java.util.ArrayList;
import java.util.List;

public class ChatListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private List<RowItem> rowItems;
    private String[] userNames;
    private List<Integer> profilePics;
    private String[] lastMessages;
    private ListView mylistview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rowItems = new ArrayList<>();
        userNames = getUserNames();
        profilePics = getProfilePics();
        lastMessages = getLastMessages();

        for (int i = 0; i < userNames.length; i++) {
            RowItem item = new RowItem(userNames[i],
                    profilePics.get(i), lastMessages[i]);
            rowItems.add(item);
        }

        mylistview = (ListView) findViewById(R.id.list);
        CustomAdapter adapter = new CustomAdapter(this, rowItems);
        mylistview.setAdapter(adapter);

        mylistview.setOnItemClickListener(this);

    }

    private String[] getUserNames() {
        String[] users = new String[3];
        users[0]="Aldana";users[1]="Rocio";users[2]="asd";
        return users;
    }

    private List<Integer> getProfilePics(){
        List<Integer> profilePics = new ArrayList<>();
        profilePics.add(R.drawable.aldana);
        profilePics.add(R.drawable.aldana);
        profilePics.add(R.drawable.aldana);
        return profilePics;

    }

    private String[] getLastMessages() {
        String[] msg = new String[3];
        msg[0]="Hola sdads  skakkjj asdadsasd";msg[1]="sdasdd asdasdas";msg[2]=" asdad asd";
        return msg;

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        String userName = rowItems.get(position).getUserName();
        Common.startActivity(this,ChatActivity.class);

    }

    @Override
    protected void onResume() {
        super.onResume();
        TinderTP.chatListResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        TinderTP.chatListPaused();
    }
}
