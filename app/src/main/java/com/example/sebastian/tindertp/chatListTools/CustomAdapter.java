package com.example.sebastian.tindertp.chatListTools;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sebastian.tindertp.R;

import java.util.ArrayList;
import java.util.List;
/** Adaptador para ChatList, se encarga de preparar las Views para el ChatList*/
public class CustomAdapter extends BaseAdapter {

    Context context;
    List<RowItem> rowItems;     /**< Lista de todos los items del ChatList*/
    private int updatedIndex;   /**< Indece de uina posicion actualizada*/
    private boolean updated;

    public CustomAdapter(Context context, List<RowItem> rowItems) {
        this.context = context;
        updatedIndex = 0;
        this.rowItems = rowItems;
        updated = false;
    }

    /**Actualiza a Negrita el nuevo mensaje.*/
    public void updateBold(List<RowItem> newRows, int index, boolean isBold) {
        List<RowItem> aux = new ArrayList<>( newRows);
        this.updatedIndex = index;
        rowItems.clear();
        rowItems.addAll(aux);
        updated = isBold;
        notifyDataSetChanged();
    }

    /** Restaura la negrita a modo normal*/
    public void restore() {
        updated = false;
        notifyDataSetChanged();
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

            if (updated && updatedIndex == position) {
                Log.i("BOLDDDD", rowItems.get(position).getUserName());
                holder.lastmessage.setTypeface(null, Typeface.BOLD_ITALIC);
            } else {
                Log.i("NORMAALLL", rowItems.get(position).getUserName());
                holder.lastmessage.setTypeface(null, Typeface.NORMAL);
            }

            RowItem rowPos = rowItems.get(position);
            holder.profilePic.setImageResource(rowPos.getProfilePic());
            holder.userName.setText(rowPos.getUserName());
            holder.lastmessage.setText(rowPos.getLastMessage());
            Log.i("en VIEW", holder.lastmessage.getText().toString());

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }
}

