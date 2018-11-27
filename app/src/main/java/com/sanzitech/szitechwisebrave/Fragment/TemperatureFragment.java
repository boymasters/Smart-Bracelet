package com.sanzitech.szitechwisebrave.Fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sanzitech.szitechwisebrave.Activity.HistoryDataActivity;
import com.sanzitech.szitechwisebrave.Adapter.ListViewAdapter;
import com.sanzitech.szitechwisebrave.Bean.DataBean;
import com.sanzitech.szitechwisebrave.R;
import com.sanzitech.szitechwisebrave.ToolUtils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr Q on 2018/01/18.
 */

public class TemperatureFragment extends Fragment implements AdapterView.OnItemClickListener {
    private View view;
    private ListView listview;
    private ListViewAdapter adapter;
    private List<DataBean> list = new ArrayList<>();
    private Context context;
    private Intent intent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.temperature_fragment, container, false);
        context = this.getActivity().getApplicationContext();
        adapter = new ListViewAdapter(context,list);
        initView();
        initData();
        return view;
    }

    private void initView() {
        listview = (ListView) view.findViewById(R.id.temperature_list);
    }

    private void initData() {
        listview.setAdapter(adapter);
        for (int i = 0; i <7 ; i++) {
            DataBean dataBean=new DataBean();
            dataBean.setTime(TimeUtils.formatDate(System.currentTimeMillis(),TimeUtils.FORMAT_YEAR_MONTH_DAY_2));
            list.add(dataBean);
        }
        adapter.notifyDataSetChanged();
        listview.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.temperature_list:
                intent = new Intent(getActivity().getApplicationContext(), HistoryDataActivity.class);
                intent.putExtra("title","体温");
                startActivity(intent);
                break;
        }
    }
}
