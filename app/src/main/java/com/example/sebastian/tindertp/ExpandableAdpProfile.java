package com.example.sebastian.tindertp;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.sebastian.tindertp.commonTools.MultiHashMap;

import java.util.ArrayList;
import java.util.List;


public class ExpandableAdpProfile extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listDataHeader;
    private MultiHashMap listDataChild;

    public ExpandableAdpProfile( Context context, MultiHashMap listChildData, List<String> listDataHeader) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition)).get(childPosition);

    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    static class ViewHolder {
        TextView text;
    }

    public void addHeaders(List<String> newData) {
        Log.i("   adddd 32-", "Dispara el agregado de un nuevo Item");
        List<String> aux = new ArrayList<>(newData);
        listDataHeader.clear();
        listDataHeader.addAll(aux);
        notifyDataSetChanged();
    }
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String category = (String) getGroup(groupPosition);

        ViewHolder holder = null;

        int positionID = Integer.parseInt(""+(groupPosition+1)+""+childPosition);

        if (convertView == null) {

            holder = new ViewHolder();

            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.profile_interest_item, null);

            convertView.setId(positionID);

            holder.text = (TextView) convertView.findViewById(R.id.textView6);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text.setId(positionID);
        holder.text.setText((String)listDataChild.get(category).get(childPosition));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
