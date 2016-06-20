package com.example.sebastian.tindertp.chatTools;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sebastian.tindertp.DataTransfer;
import com.example.sebastian.tindertp.R;

public class OnItemClickCustom implements AdapterView.OnItemClickListener {

    private Context act;
    private ListView mssgList;

    public OnItemClickCustom(Context act, ListView mssgList){
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
            ClientBuilder client = new ClientBuilder((DataTransfer)act);
            client.build(text, mssgList, item, (DataTransfer) act);
        }
    }

}
