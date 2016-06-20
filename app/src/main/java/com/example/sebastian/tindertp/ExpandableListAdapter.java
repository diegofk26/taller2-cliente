package com.example.sebastian.tindertp;


import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.TextKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sebastian.tindertp.commonTools.MultiHashIntStr;
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
    private Map<Integer,ArrayAdapter<String>> adapters;

    public void addHeaders(List<String> newData) {
        Log.i("   adddd 32-", "Dispara el agregado de un nuevo Item");
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

    public void setSuggestions(MultiHashIntStr suggestions) {

        adapters = new HashMap<>();

        for(int i = 0; i < listDataHeader.size(); i++ ) {
            List<Object> childs = listDataChild.get(listDataHeader.get(i));
            for (int j = 0; j < childs.size(); j++) {
                Integer position =  Integer.parseInt(""+(i+1)+""+j);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                        android.R.layout.simple_list_item_1, suggestions.get(i));
                adapters.put(position, adapter);
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
        AutoCompleteTextView autoCompText;
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
            holder.autoCompText = (AutoCompleteTextView) convertView.findViewById(R.id.autoCompleteTextView);
            holder.autoCompText.setAdapter(adapters.get(positionID));
            holder.autoCompText.addTextChangedListener(new GenericTextWatcher(holder.autoCompText));

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

        holder.autoCompText.setId(positionID);

        Log.i("EXP adap", "" + positionID + " - " + savedTextMap.get(positionID));

        if (holder.autoCompText.getText().length() != 0) {
            Log.i("asd", "BORRRROOOOO");
            holder.autoCompText.setAdapter(null);
            holder.autoCompText.getText().clear();
            Log.i("asd", "ESTOY SETEARRR");
            holder.autoCompText.setText(savedTextMap.get(positionID));
            Log.i("asd", "SETEOOOO");
            holder.autoCompText.setAdapter(adapters.get(positionID));
        }

        Log.i("adap", "adp ID" + positionID + " "+ adapters.get(positionID));

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
    public View getGroupView(int groupPosition, boolean isExpanded,
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
            final AutoCompleteTextView editText = (AutoCompleteTextView) view;

            Log.i("ADAPTER", editText.getText().toString());

            savedTextMap.put(position, editText.getText().toString());

            int categoryPosition = Integer.parseInt((""+position).substring(1));

            listDataChild.set(positionCategoryMapper.get(position), categoryPosition, editText);

        }
    }
}
