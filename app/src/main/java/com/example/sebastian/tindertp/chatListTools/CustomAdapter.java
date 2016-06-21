package com.example.sebastian.tindertp.chatListTools;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sebastian.tindertp.ProfileActivity;
import com.example.sebastian.tindertp.R;
import com.example.sebastian.tindertp.commonTools.ActivityStarter;

import java.util.ArrayList;
import java.util.List;
/** Adaptador para ChatList, se encarga de preparar las Views para el ChatList*/
public class CustomAdapter extends BaseAdapter {

    Context context;
    List<RowItem> rowItems;     /**< Lista de todos los items del ChatList*/
    private List<Boolean> isBoldList;

    private final static String ADAPTER_TAG = "CustomAdapter CHATLIST";

    public CustomAdapter(Context context, List<RowItem> rowItems) {
        this.context = context;
        this.rowItems = rowItems;

        isBoldList = new ArrayList<>();
        for (int i = 0; i < rowItems.size(); i++) {
            isBoldList.add(false);
        }
    }

    public void addRowItem(List<RowItem> newRows) {
        Log.i(ADAPTER_TAG, "Dispara el agregado de un nuevo Item");
        List<RowItem> aux = new ArrayList<>(newRows);
        rowItems.clear();
        rowItems.addAll(aux);
        notifyDataSetChanged();
    }

    /**Actualiza a Negrita o Normal el mensaje.*/
    public void updateBold(List<RowItem> newRows, int index, boolean isBold) {
        Log.i(ADAPTER_TAG,"Dispara el cambio en la posicion: " + index + ". isBold: " + isBold);
        List<RowItem> aux = new ArrayList<>(newRows);
        rowItems.clear();
        rowItems.addAll(aux);
        isBoldList.set(index,isBold);
        notifyDataSetChanged();
    }

    /** Restaura la negrita a modo normal*/
    public void restore(int position) {
        Log.i(ADAPTER_TAG,"Dispara el cambio a modo NORMAL de la posicion: " + position);
        isBoldList.set(position,false);
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
            holder.profilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityStarter.start(context, ProfileActivity.class);
                }
            });


            holder.lastmessage = (TextView) convertView.findViewById(R.id.message);

            if (isBoldList.get(position)) {
                Log.i(ADAPTER_TAG, "BOLD: " + rowItems.get(position).getUserName());
                holder.lastmessage.setTypeface(null, Typeface.BOLD_ITALIC);
            } else {
                Log.i(ADAPTER_TAG, "NORMAL: " + rowItems.get(position).getUserName());
                holder.lastmessage.setTypeface(null, Typeface.NORMAL);
            }

            RowItem rowPos = rowItems.get(position);
            holder.profilePic.setImageBitmap(rowPos.getProfilePic());
            holder.userName.setText(rowPos.getUserName());
            holder.userName.setTextColor(Color.BLACK);
            holder.lastmessage.setText(rowPos.getLastMessage());
            holder.lastmessage.setTextColor(Color.BLACK);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }
}

