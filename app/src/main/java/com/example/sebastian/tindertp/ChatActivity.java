package com.example.sebastian.tindertp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import com.example.sebastian.tindertp.commonTools.HeaderBuilder;
import com.example.sebastian.tindertp.internetTools.RequestResponseClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private String chatName;
    private String user;

    private ChatArrayAdapter adp;
    private ListView list;
    private EditText chatText;
    private Button send;

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

        send = (Button) findViewById(R.id.btn);
        list = (ListView) findViewById(R.id.listview);
        chatText = (EditText) findViewById(R.id.chat_text);

        adp = new ChatArrayAdapter(getApplicationContext(), R.layout.chat);
        list.setAdapter(adp);

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

        list.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        list.setAdapter(adp);

        adp.registerDataSetObserver(new DataSetObserver() {

            public void OnChanged() {
                super.onChanged();
                list.setSelection(adp.getCount() - 1);
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(onNewMessage,
                new IntentFilter("CHAT"));

        String url = ((TinderTP) this.getApplication()).getUrl();
        String token = ((TinderTP) this.getApplication()).getToken();
        Conn_struct conn = new Conn_struct(Common.MESSAGES, Common.GET, url);
        Map<String, String> headers = HeaderBuilder.forLoadMessages(token,user,chatName,1);
        RequestResponseClient client = new RequestResponseClient(this,conn,headers){

            @Override
            protected void getJson() throws IOException {
                jsonString = readIt();
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
                    }catch (JSONException e) {showText("No se pudo enviar el mensaje.");}
                }else {
                    showText("No se pudo enviar el mensaje.");
                }
            }

            @Override
            protected void showText(String message) {
                Snackbar.make(findViewById(R.id.listview), message, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        };
        client.runInBackground();
    }

    private boolean sendChatMessage(){
        String url = ((TinderTP) this.getApplication()).getUrl();
        String token = ((TinderTP) this.getApplication()).getToken();
        Conn_struct conn = new Conn_struct(Common.CHAT, Common.POST, url);
        Map<String,String> headers = HeaderBuilder.forSendMessage(token,user,chatName);

        RequestResponseClient client = new RequestResponseClient(this,conn,headers){

            @Override
            protected void getJson() throws IOException {}

            private void updatePriorActivities(String user, String message) {
                Intent activityMsg = new Intent("CHAT_LIST");
                activityMsg.putExtra("user", user);
                activityMsg.putExtra("message", message);
                LocalBroadcastManager.getInstance(ctx).sendBroadcast(activityMsg);
            }

            @Override
            protected void onPostExec() {
                if(!badResponse && isConnected) {
                    adp.add(new ChatMessage(false, chatText.getText().toString()));
                    updatePriorActivities(chatName, chatText.getText().toString());
                    chatText.setText("");
                } else {
                    showText("No se pudo enviar el mensaje.");
                }
            }

            @Override
            protected void showText(String message) {
                Snackbar.make(findViewById(R.id.listview), message, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        };
        client.addBody(chatText.getText().toString());
        client.runInBackground();
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
