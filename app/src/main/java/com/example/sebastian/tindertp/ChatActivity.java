package com.example.sebastian.tindertp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.sebastian.tindertp.application.TinderTP;
import com.example.sebastian.tindertp.chatTools.ChatArrayAdapter;
import com.example.sebastian.tindertp.chatTools.ChatMessage;
import com.example.sebastian.tindertp.chatTools.ChatTextBuilder;
import com.example.sebastian.tindertp.chatTools.ClientBuilder;
import com.example.sebastian.tindertp.internetTools.RequestResponseClient;

public class ChatActivity extends AppCompatActivity {

    private String chatName;
    private String user;/**< Nombre de usuario de la aplicacion.*/
    private String url;
    private String token;

    private ChatArrayAdapter adp;
    private ListView mssgList;
    private EditText chatText;
    private Button send;
    private Animation rotate;
    private Animation rotateInverse;

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

        chatName = this.getIntent().getStringExtra("from");

        TinderTP.updateChatName(chatName);
        user = ((TinderTP) this.getApplication()).getUser();
        url = ((TinderTP) this.getApplication()).getUrl();
        token = ((TinderTP) this.getApplication()).getToken();

        int orientation = getResources().getConfiguration().orientation;

        setBackgroundOnOrientation(orientation);

        send = (Button) findViewById(R.id.btn);
        mssgList = (ListView) findViewById(R.id.listview);

        rotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
        rotateInverse = AnimationUtils.loadAnimation(this, R.anim.rotate_inverse);

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
        final String text =  chatText.getText().toString();
        if ( !text.isEmpty()) {
            RequestResponseClient sendMessage = ClientBuilder.build(this,adp,chatText);
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
