package com.example.sebastian.tindertp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.DataSetObserver;
import android.net.ConnectivityManager;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.sebastian.tindertp.Interfaces.ConectivityManagerInterface;
import com.example.sebastian.tindertp.Interfaces.DataTransfer;
import com.example.sebastian.tindertp.application.TinderTP;
import com.example.sebastian.tindertp.chatTools.ChatArrayAdapter;
import com.example.sebastian.tindertp.chatTools.ChatMessage;
import com.example.sebastian.tindertp.chatTools.ChatTextBuilder;
import com.example.sebastian.tindertp.chatTools.ClientBuilder;
import com.example.sebastian.tindertp.chatTools.OnItemClickCustom;
import com.example.sebastian.tindertp.commonTools.Common;

public class ChatActivity extends AppCompatActivity implements DataTransfer, ConectivityManagerInterface {

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

        final String chatName = this.getIntent().getStringExtra("fromName");
        final String chatEmail = this.getIntent().getStringExtra("fromEmail");

        setTitle(chatName);

        TinderTP.updateChatName(chatEmail);

        int orientation = getResources().getConfiguration().orientation;

        setBackgroundOnOrientation(orientation);

        Button send = (Button) findViewById(R.id.btn);
        mssgList = (ListView) findViewById(R.id.listview);

        chatText = (EditText) findViewById(R.id.chat_text);
        ChatTextBuilder.chatEditor(chatText, getApplicationContext());

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

        mssgList.setOnItemClickListener(new OnItemClickCustom(getApplicationContext(), mssgList));

        adp.registerDataSetObserver(new DataSetObserver() {

            public void OnChanged() {
                super.onChanged();
                mssgList.setSelection(adp.getCount() - 1);
            }
        });

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(onNewMessage,
                new IntentFilter(Common.CHAT_KEY));
        ClientBuilder client = new ClientBuilder(this);
        client.build(adp, this);
    }

    private boolean sendChatMessage(){
        String text =  chatText.getText().toString();
        if ( !text.isEmpty()) {
            ChatMessage item = new ChatMessage(false, text);
            adp.add(item);
            ClientBuilder client = new ClientBuilder(this);
            client.build(text, mssgList, item, this);
            chatText.setText("");
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RelativeLayout rLayout = (RelativeLayout) findViewById (R.id.relative_chat);
        rLayout.setBackgroundResource(0);

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
        ClientBuilder client = new ClientBuilder(this);
        client.build(getApplicationContext(), adp, mssgList, this);
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
    protected void onResume() {
        super.onResume();
        TinderTP.chatResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        TinderTP.chatPaused();
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
        return getIntent().getStringExtra("fromName");
    }

    @Override
    public String getChatEmail() {
        return getIntent().getStringExtra("fromEmail");
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
