package com.sanzitech.szitechwisebrave.Activity;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.sanzitech.szitechwisebrave.Fragment.SphygmusFragment;
import com.sanzitech.szitechwisebrave.Fragment.TemperatureFragment;
import com.sanzitech.szitechwisebrave.R;
import com.sanzitech.szitechwisebrave.ViewUtils.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

public class HistoryListActivity extends BaseActivity {

    private List<Fragment> mFragments = new ArrayList<>();
    private NoScrollViewPager viewPager;
    private RadioGroup radioGroup;
    private RadioButton radioButton_t;
    private RadioButton radioButton_s;
    private ImageView back;
    private View statusBar;


    @Override
    protected int setLayoutId() {
        return R.layout.history_list_main;
    }

    protected void initView() {
        statusBar = findViewById(R.id.statusBarView);
        mFragments.add(new TemperatureFragment());
        mFragments.add(new SphygmusFragment());
        back= (ImageView) findViewById(R.id.history_list_back);
        viewPager = (NoScrollViewPager) findViewById(R.id.viewpager);
        radioGroup = (RadioGroup) findViewById(R.id.history_list_rg);
        radioButton_t = (RadioButton) findViewById(R.id.history_list_rb_t);
        radioButton_s = (RadioButton) findViewById(R.id.history_list_rb_s);
        FragAdapter adapter = new FragAdapter(getSupportFragmentManager(), mFragments);
        viewPager.setAdapter(adapter);
        viewPager.setNoScroll(true);
    }


    protected void initData() {
        ViewGroup.LayoutParams params = statusBar.getLayoutParams();
        params.height = getStatusBarHeight();
        statusBar.setLayoutParams(params);
    }

    @Override
    protected void setListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.history_list_rb_t:
                        viewPager.setCurrentItem(0, false);
                        radioButton_s.setTextColor(Color.parseColor("#b3b3b3"));
                        radioButton_t.setTextColor(Color.parseColor("#00c896"));
                        break;
                    case R.id.history_list_rb_s:
                        viewPager.setCurrentItem(1, false);
                        radioButton_t.setTextColor(Color.parseColor("#b3b3b3"));
                        radioButton_s.setTextColor(Color.parseColor("#00c896"));
                        break;
                }

            }
        });
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        radioGroup.check(R.id.history_list_rb_t);
                        break;
                    case 1:
                        radioGroup.check(R.id.history_list_rb_s);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    class FragAdapter extends FragmentPagerAdapter {

        private List<Fragment> mFragments;

        public FragAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            mFragments = fragments;
        }

        @Override
        public Fragment getItem(int arg0) {
            return mFragments.get(arg0);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

    }
}