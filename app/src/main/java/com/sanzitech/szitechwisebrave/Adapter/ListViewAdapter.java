package com.sanzitech.szitechwisebrave.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sanzitech.szitechwisebrave.Bean.DataBean;
import com.sanzitech.szitechwisebrave.R;

import java.util.List;

/**
 * Created by Mr Q on 2018/01/18.
 */

public class ListViewAdapter extends BaseAdapter {
    private Context context;
    private List<DataBean> list;

    public ListViewAdapter(Context context, List<DataBean> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.history_listview_item, null);
            holder.linearLayout= (LinearLayout) convertView.findViewById(R.id.history_list_item_linear);
            holder.year = (TextView) convertView.findViewById(R.id.history_list_item_year);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.year.setText(list.get(position).getTime());
        return convertView;
    }

    static class ViewHolder {
        LinearLayout linearLayout;
        TextView year;
        TextView info;
    }
}
