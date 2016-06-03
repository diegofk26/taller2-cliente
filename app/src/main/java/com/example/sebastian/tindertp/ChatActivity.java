package com.example.sebastian.tindertp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sebastian.tindertp.application.TinderTP;
import com.example.sebastian.tindertp.chatTools.ChatArrayAdapter;
import com.example.sebastian.tindertp.chatTools.ChatMessage;
import com.example.sebastian.tindertp.chatTools.ChatTextBuilder;
import com.example.sebastian.tindertp.chatTools.ClientBuilder;
import com.example.sebastian.tindertp.internetTools.RequestResponseClient;

public class ChatActivity extends AppCompatActivity {

    private ChatArrayAdapter adp;
    private ListView mssgList;
    private EditText chatText;

    private BroadcastReceiver onNewMessage = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String message = intent.getStringExtra("message");
            String user = intent.getStringExtra("user");

            if(message!=null && user != null) {
                adp.add(new ChatMessage(true, message));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final String chatName = this.getIntent().getStringExtra("from");

        setTitle(chatName);

        TinderTP.updateChatName(chatName);

        int orientation = getResources().getConfiguration().orientation;

        setBackgroundOnOrientation(orientation);

        Button send = (Button) findViewById(R.id.btn);
        mssgList = (ListView) findViewById(R.id.listview);

        chatText = (EditText) findViewById(R.id.chat_text);
        ChatTextBuilder.chatEditor(chatText,this);

        adp = new ChatArrayAdapter(getApplicationContext(), R.layout.chat);
        mssgList.setAdapter(adp);

        chatText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return (event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER) && sendChatMessage();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendChatMessage();
            }
        });

        mssgList.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        mssgList.setAdapter(adp);

        final Activity ctx = this;

        mssgList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                if (adp.isFailureMessage(position)) {
                    TextView chatMssg = (TextView) view.findViewById(R.id.SingleMessage);
                    String text = chatMssg.getText().toString();
                    adp.remove(adp.getItem(position), position);
                    ChatMessage item = new ChatMessage(false, text);
                    adp.add(item);
                    Log.i("aaaaa", "mensjaeee " + text );
                    RequestResponseClient sendMessage = ClientBuilder.build(ctx, text, mssgList, item);
                    sendMessage.addBody(text);
                    sendMessage.runInBackground();
                }
            }
        });

        adp.registerDataSetObserver(new DataSetObserver() {

            public void OnChanged() {
                super.onChanged();
                mssgList.setSelection(adp.getCount() - 1);
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(onNewMessage,
                new IntentFilter("CHAT"));

        RequestResponseClient getHistory = ClientBuilder.build(this,adp);
        getHistory.runInBackground();
    }

    private boolean sendChatMessage(){
        String text =  chatText.getText().toString();
        if ( !text.isEmpty()) {
            ChatMessage item = new ChatMessage(false, text);
            adp.add(item);

            RequestResponseClient sendMessage = ClientBuilder.build(this, text, mssgList, item);
            chatText.setText("");
            sendMessage.addBody(text);
            sendMessage.runInBackground();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat, menu);
        MenuItem item = menu.findItem(R.id.reload);
        MenuItemCompat.setActionView(item, R.layout.reload_icon);
        RelativeLayout notifCount = (RelativeLayout) MenuItemCompat.getActionView(item);
        ImageView icon = (ImageView)notifCount.findViewById(R.id.row);
        ImageView icon2 = (ImageView)notifCount.findViewById(R.id.center);
        return true;
    }

    public void moreMssg(View v) {
        RequestResponseClient getMoreHistory = ClientBuilder.build(this,adp,mssgList);
        getMoreHistory.runInBackground();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.reload) {
            Log.i("acccaa", "reload");
            return true;
        }
        return true;
    }

    private void setBackgroundOnOrientation(int orientation){
        RelativeLayout rLayout = (RelativeLayout) findViewById (R.id.relative_chat);
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            rLayout.setBackgroundResource(R.drawable.landscape);
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT){
            rLayout.setBackgroundResource(R.drawable.land);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setBackgroundOnOrientation(newConfig.orientation);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TinderTP.chatResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        TinderTP.chatPaused();
    }
}
