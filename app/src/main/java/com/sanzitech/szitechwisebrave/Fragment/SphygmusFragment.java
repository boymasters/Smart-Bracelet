package com.sanzitech.szitechwisebrave.Fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sanzitech.szitechwisebrave.R;

/**
 * Created by android on 2018/01/18.
 */

public class SphygmusFragment extends Fragment {
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sphygmus_fragment, container, false);
        initView();
        initData();
        return view;
    }

    private void initView() {

    }

    private void initData() {

    }
}
