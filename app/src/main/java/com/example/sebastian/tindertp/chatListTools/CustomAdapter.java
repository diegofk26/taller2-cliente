package com.example.sebastian.tindertp.chatListTools;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sebastian.tindertp.R;

import java.util.List;

public class CustomAdapter extends BaseAdapter {

    Context context;
    List<RowItem> rowItems;

    public CustomAdapter(Context context, List<RowItem> rowItems) {
        this.context = context;
        this.rowItems = rowItems;
    }

    @Override
    public int getCount() {
        return rowItems.size();
    }

    @Override
    public Object getItem(int position) {
        return rowItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return rowItems.indexOf(getItem(position));
    }

    /* private view holder class */
    private class ViewHolder {
        ImageView profilePic;
        TextView userName;
        TextView lastmessage;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();

            holder.userName = (TextView) convertView
                    .findViewById(R.id.userName);
            holder.profilePic = (ImageView) convertView
                    .findViewById(R.id.profilePic);
            holder.lastmessage = (TextView) convertView.findViewById(R.id.message);

            RowItem rowPos = rowItems.get(position);

            holder.profilePic.setImageResource(rowPos.getProfilePic());
            holder.userName.setText(rowPos.getUserName());
            holder.lastmessage.setText(rowPos.getLastMessage());

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

}

