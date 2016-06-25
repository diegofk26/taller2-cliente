package com.example.sebastian.tindertp.ExpandedListAdapters;


import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sebastian.tindertp.R;
import com.example.sebastian.tindertp.commonTools.MultiHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listDataHeader;                /**< Categorias*/
    private MultiHashMap listDataChild;                 /**< guarda los EditText*/
    private Map<Integer,String> savedTextMap;           /**< guarda el texto ingresado*/
    private Map<Integer,String> positionCategoryMapper; /**< relaciones (groupPosition.concat(childPosition)) con la categoria*/
    private List<String> suggestions;
    private List<String> indexCategoryLink;
    private static final String EXAPANDABLE_TAG = "ExpandableListAdapter";

    public void addHeaders(List<String> newData) {
        Log.i(EXAPANDABLE_TAG, "Dispara el agregado de un nuevo Item");
        List<String> aux = new ArrayList<>(newData);
        listDataHeader.clear();
        listDataHeader.addAll(aux);

        for(int i = 0; i < listDataHeader.size(); i++ ) {
            List<Object> childs = listDataChild.get(listDataHeader.get(i));
            for (int j = 0; j < childs.size(); j++) {
                Integer position =  Integer.parseInt(""+(i+1)+""+j);
                positionCategoryMapper.put(position,listDataHeader.get(i));
                if (!savedTextMap.containsKey(position))
                    savedTextMap.put(position,"");
            }
        }

        notifyDataSetChanged();
    }

    public Map<Integer, String> getSavedTextMap() {
        return savedTextMap;
    }

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 MultiHashMap listChildData) {

        savedTextMap = new HashMap<>();
        positionCategoryMapper = new HashMap<>();
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
    }

    public void setSuggestions(List<String> indexCategoryLink, MultiHashMap suggestions) {

        this.suggestions = new ArrayList<>();
        this.indexCategoryLink = indexCategoryLink;

        for(int i = 0; i < listDataHeader.size(); i++ ) {

            StringBuilder valuesSuggested = new StringBuilder();
            valuesSuggested.append("Sugerencias: ");
            String category = indexCategoryLink.get(i);
            List<Object> values = suggestions.get(category);
            for (int j = 0; j < values.size(); j++) {
                valuesSuggested.append((String) values.get(j));
                if (j == values.size() -1) {
                    valuesSuggested.append(".");
                }else {
                    valuesSuggested.append(", ");
                }
            }

            this.suggestions.add(valuesSuggested.toString());
        }
    }

    public void setAllEditText(Map<Integer,String> mapperID, MultiHashMap newValues) {

        EditText edit = null;

        for(int i = 0; i < listDataHeader.size(); i++ ) {
            String categoryMod = listDataHeader.get(i);
            String category = indexCategoryLink.get(i);
            if (newValues.hasKey(category)) {
                List<Object> values = newValues.get(category);
                for (int j = 0; j < values.size(); j++) {
                    int positionID = Integer.parseInt("" + (i + 1) + "" + j);
                    mapperID.put(positionID, categoryMod);
                    savedTextMap.put(positionID, (String) values.get(j));
                    if ( j != 0 ) {
                        listDataChild.put(categoryMod, edit);
                    }
                }
            }
        }

    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition)).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    static class ViewHolder {
        EditText editText;
        TextView textView;
        ImageView plus;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        String category = (String) getGroup(groupPosition);

        ViewHolder holder = null;

        int positionID = Integer.parseInt(""+(groupPosition+1)+""+childPosition);

        if (convertView == null) {

            holder = new ViewHolder();

            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.interest_item, null);

            convertView.setId(positionID);

            holder.plus = (ImageView) convertView.findViewById(R.id.plus);
            holder.textView = (TextView) convertView.findViewById(R.id.textView10);
            holder.editText = (EditText) convertView.findViewById(R.id.editText4);
            holder.editText.addTextChangedListener(new GenericTextWatcher(holder.editText));

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.plus.setId(positionID);

        if (listDataChild.get(category).size()-1 > childPosition){
            holder.plus.setVisibility(View.GONE);
        }else {
            holder.plus.setVisibility(View.VISIBLE);
        }

        holder.textView.setId(positionID);
        holder.textView.setText(suggestions.get(groupPosition));
        holder.textView.setSelected(true);

        holder.editText.setId(positionID);

        holder.editText.setText(savedTextMap.get(positionID));

        return convertView;
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
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, final boolean isExpanded,
                             View convertView, ViewGroup parent) {
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

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private class GenericTextWatcher implements TextWatcher {

        private View view;
        private GenericTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {  }

        public void afterTextChanged(Editable editable) {
            final int position = view.getId();
            final EditText editText = (EditText) view;

            savedTextMap.put(position, editText.getText().toString());

            int digitSize = String.valueOf(listDataHeader.size()).length();
            int childPosition = Integer.parseInt(("" + position).substring(digitSize));
            int categoryPart = Integer.parseInt(("" + position).substring(0,digitSize));
            int categoryPosition = Integer.parseInt(""+categoryPart+""+0);
            listDataChild.set(positionCategoryMapper.get(categoryPosition), childPosition, editText);

        }
    }
}
