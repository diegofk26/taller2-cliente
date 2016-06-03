package com.example.sebastian.tindertp.chatTools;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sebastian.tindertp.R;
import com.example.sebastian.tindertp.internetTools.RequestResponseClient;

public class OnItemClickCustom implements AdapterView.OnItemClickListener {

    private Activity act;
    private ListView mssgList;

    public OnItemClickCustom(Activity act, ListView mssgList){
        this.act = act;
        this.mssgList = mssgList;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        ChatArrayAdapter adp = (ChatArrayAdapter) mssgList.getAdapter();

        if (adp.isFailureMessage(position)) {
            TextView chatMssg = (TextView) view.findViewById(R.id.SingleMessage);
            String text = chatMssg.getText().toString();
            adp.remove(adp.getItem(position), position);
            ChatMessage item = new ChatMessage(false, text);
            adp.add(item);
            RequestResponseClient sendMessage = ClientBuilder.build(act, text, mssgList, item);
            sendMessage.addBody(text);
            sendMessage.runInBackground();
        }
    }

}
