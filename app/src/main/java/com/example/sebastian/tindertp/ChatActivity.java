package com.example.sebastian.tindertp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.sebastian.tindertp.application.TinderTP;
import com.example.sebastian.tindertp.chatTools.ChatArrayAdapter;
import com.example.sebastian.tindertp.chatTools.ChatMessage;
import com.example.sebastian.tindertp.commonTools.Common;
import com.example.sebastian.tindertp.commonTools.Conn_struct;
import com.example.sebastian.tindertp.internetTools.RequestResponseClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private String chatName;
    private String user = ((TinderTP) this.getApplication()).getUser();

    private static final String TAG = "ChatActivity";

    private ChatArrayAdapter adp;
    private ListView list;
    private EditText chatText;
    private Button send;

    private String jsonString;

    private BroadcastReceiver onNotice = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            jsonString = intent.getStringExtra(Common.JSON);
            try {
                JSONArray jsonA = new JSONArray(jsonString);

                for (int i = 0; i < jsonA.length(); i++) {
                    JSONObject jsonO = jsonA.getJSONObject(i);
                    boolean side = !jsonO.getString("emisor").equals(user);
                    adp.add(new ChatMessage(side, jsonO.getString("mensaje")));
                }

            }catch (JSONException e) {}
        }
    };

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

        send = (Button) findViewById(R.id.btn);

        list = (ListView) findViewById(R.id.listview);

        adp = new ChatArrayAdapter(getApplicationContext(), R.layout.chat);
        list.setAdapter(adp);

        chatText = (EditText) findViewById(R.id.chat_text);
        chatText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return sendChatMessage();
                }
                return false;
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendChatMessage();
            }
        });

        list.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        list.setAdapter(adp);

        adp.registerDataSetObserver(new DataSetObserver() {

            public void OnChanged() {

                super.onChanged();

                list.setSelection(adp.getCount() - 1);
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice,
                new IntentFilter(Common.RESPONSE));
        LocalBroadcastManager.getInstance(this).registerReceiver(onNewMessage,
                new IntentFilter("CHAT"));


        String url = ((TinderTP) this.getApplication()).getUrl();
        Conn_struct conn = new Conn_struct(Common.MESSAGES, Common.GET,url);
        Map<String, String> headers = makeHeaders(1);
        RequestResponseClient client = new RequestResponseClient(this,conn,headers);
        client.runInBackground();

    }

    private Map<String, String> makeHeaders(int desde) {
        Map<String, String> headers = new HashMap<>();
        String token = ((TinderTP) this.getApplication()).getToken();
        headers.put(Common.USER1,user);
        headers.put(Common.USER2,chatName);
        headers.put(Common.TOKEN, token);
        headers.put(Common.DESDE, String.valueOf(desde));
        headers.put(Common.CANT, String.valueOf(Common.MAX_MESSAGES));

        return headers;

    }

    private boolean sendChatMessage(){
        adp.add(new ChatMessage(false, chatText.getText().toString()));
        chatText.setText("");
        return true;
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
