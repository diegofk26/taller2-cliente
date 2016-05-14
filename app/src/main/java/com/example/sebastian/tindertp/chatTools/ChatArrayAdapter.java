package com.example.sebastian.tindertp.chatTools;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sebastian.tindertp.R;

public class ChatArrayAdapter extends ArrayAdapter<ChatMessage>{
    private TextView chatText;
    private List<ChatMessage> MessageList = new ArrayList<ChatMessage>();
    private LinearLayout layout;
    private Context context;


    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }


    public void add(ChatMessage object) {
        MessageList.add(object);
        super.add(object);
    }

    public int getCount() {
        return this.MessageList.size();
    }

    public ChatMessage getItem(int index) {
        return this.MessageList.get(index);
    }

    public View getView(int position,View ConvertView, ViewGroup parent) {

        View v = ConvertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.chat, parent, false);
        }

        layout = (LinearLayout) v.findViewById(R.id.Message1);
        ChatMessage messageObj = getItem(position);
        chatText = (TextView) v.findViewById(R.id.SingleMessage);

        chatText.setText(messageObj.message);

        chatText.setBackgroundResource(messageObj.left ? R.drawable.blue_bubble : R.drawable.green_bubble);

        layout.setGravity(messageObj.left ? Gravity.START : Gravity.END);

        return v;


    }

}
