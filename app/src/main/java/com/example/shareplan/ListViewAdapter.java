package com.example.shareplan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<ListItem> listItems = new ArrayList<ListItem>();

    public ListViewAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int i) {
        return listItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.lec_item, parent, false);
        }

        TextView txt_lec_name = (TextView) convertView.findViewById(R.id.lec_item_name);
        TextView txt_lec_info = (TextView) convertView.findViewById(R.id.lec_item_info);

        ListItem listItem = listItems.get(position);

        txt_lec_info.setText(listItem.getName());
        txt_lec_info.setText(listItem.getInfo());

        return convertView;
    }

    public void addItem(String name, String info) {
        ListItem listItem = new ListItem();

        listItem.setName(name);
        listItem.setInfo(info);

        listItems.add(listItem);
    }


}
