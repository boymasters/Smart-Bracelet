package com.sanzitech.szitechwisebrave.Activity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sanzitech.szitechwisebrave.Application.MyApplication;
import com.sanzitech.szitechwisebrave.R;
import com.sanzitech.szitechwisebrave.ToolUtils.SharedPreferencesUtils;
import com.sanzitech.szitechwisebrave.chartview.ChartView;
import com.sanzitech.szitechwisebrave.chartview.ChartViewConfig;
import com.sanzitech.szitechwisebrave.chartview.KeduValue;
import com.sanzitech.szitechwisebrave.chartview.OnChartViewChangeListener;
import com.sanzitech.szitechwisebrave.chartview.PointValue;
import com.sanzitech.szitechwisebrave.chartview.ScrollDirection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr Q on 2018/01/09.
 */

public class HistoryDataActivity extends BaseActivity {

    private ChartView chartView;
    private ImageView back;
    private TextView title;
    private float Verical_unit_start;
    private float Verical_unit_end;
    private Object Verical_lable_value_type;
    private String Indicator_title_unit = "";
    private TextView content;
    private float Verical_unit_incremetal;
    private float police_line;

    @Override
    protected int setLayoutId() {
        return R.layout.history_activity_main;
    }

    @Override
    protected void initView() {
        back = (ImageView) findViewById(R.id.history_main_back);
        chartView = (ChartView) findViewById(R.id.chartview);
        title = (TextView) findViewById(R.id.history_main_title);
        View statusBar = findViewById(R.id.statusBarView);
        ViewGroup.LayoutParams params = statusBar.getLayoutParams();
        params.height = getStatusBarHeight();
        statusBar.setLayoutParams(params);
        linearLayout = (LinearLayout) findViewById(R.id.history_bg);
        content = (TextView) findViewById(R.id.history_main_time);
    }

    @Override
    protected void initData() {
        if (getIntent().getBooleanExtra("istemp", true)) {
            Verical_unit_start = 35.0f;
            Verical_unit_end = 42.0f;
            Verical_lable_value_type = 1f;
            Verical_unit_incremetal = 1f;
            Indicator_title_unit = "℃";
            police_line=getMyApplication().getTemperature_police_line();
        } else {
            Verical_unit_start = 50;
            Verical_unit_end = 160;
            Verical_lable_value_type = 1;
            Verical_unit_incremetal = 15f;
            Indicator_title_unit = "次/分";
            police_line=getMyApplication().getSphygmus_police_line();
        }
        content.setText(temperature[0]);
        title.setText(getIntent().getStringExtra("title"));

        int mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        //横向刻度
        List<KeduValue> listHorizontal = getHorizontalKedu();
        //构造绘制点
        List<PointValue> listPoint = getPoint();
        //构造绘制区域范围的点
        List<PointValue> listRegion = getRegion();

        config(mScreenWidth, listHorizontal, listPoint, listRegion);
    }

    private List<KeduValue> getHorizontalKedu() {
        List<KeduValue> list = new ArrayList<>();
        for (int i = 0; i < time.length; i++) {
            KeduValue pointValue = new KeduValue();
            pointValue.display_value = time[i];//显示的文本
            pointValue.value = i + "";//具体的横刻度值
            list.add(pointValue);
        }
        return list;
    }

    String[] temperature = new String[]{"36.1", "37.0", "35.9", "100", "37.5", "36.9", "36.5", "35.5", "37.5", "37.8", "37.9", "37.9", "37.9", "37.9", "37.9", "37.9", "37.9", "37.9", "37.9", "37.9", "40.9", "28.3", "36.4"};
    String[] time = new String[]{"10:00", "10:01", "10:03", "10:04", "10:05", "10:06", "10:07", "10:08", "10:09", "10:10", "10:11", "10:12", "10:13", "10:14", "10:15", "10:16", "10:17", "10:18", "10:19", "10:20", "10:21", "10:22", "10:23"};

    private List<PointValue> getPoint() {
        List<PointValue> list = new ArrayList<>();
        for (int i = 0; i < time.length; i++) {
            PointValue pointValue = new PointValue();
            //横刻度值，必须在横刻度范围内，否则无法显示
            pointValue.horizontal_value = i + "";
            //竖刻度值，必须在竖刻度范围内，否则会取边界值
            pointValue.verical_value = temperature[i];
            //显示在游标的标题
            pointValue.title = pointValue.verical_value + "";
            //显示在游标的子标题
            pointValue.title_sub = "";
            list.add(pointValue);
        }
        return list;
    }

    private List<PointValue> getRegion() {
        List<PointValue> list = new ArrayList<>();
        //正向
        for (int i = -10; i < 20; i++) {
            PointValue pointValue = new PointValue();
            //横刻度值，必须在横刻度范围内，否则无法显示
            pointValue.horizontal_value = i + "";
            //竖刻度值，必须在竖刻度范围内，否则会取边界值
            pointValue.verical_value = 10 + "";
            list.add(pointValue);
        }

        for (int i = 19; i >= -10; i--) {
            PointValue pointValue = new PointValue();
            //横刻度值，必须在横刻度范围内，否则无法显示
            pointValue.horizontal_value = i + "";
            //竖刻度值，必须在竖刻度范围内，否则会取边界值
            pointValue.verical_value = 5 + "";
            list.add(pointValue);
        }
        return list;
    }

    LinearLayout linearLayout;

    ChartViewConfig config = new ChartViewConfig();

    private void config(int mScreenWidth, List<KeduValue> listHorizontal, List<PointValue> listPoint, List<PointValue> listPointRegion) {
        config
                //设置列数
                .setCloumn(7)
                //设置行数
                .setRow(8)
                //设置每一个格子的高度
                .setItem_height(mScreenWidth / 8)
                //设置每一个格子的宽度
                .setItem_width(mScreenWidth / 8)
                //设置格子线的颜色
                .setGrid_line_color(R.color.color_white)
                //设置横竖刻度线的颜色
                .setGrid_line_kedu_color(R.color.color_white)
                //设置是否显示格子线
                .setIsShowGridLine(true)
                //横向网格显示
                .setIsShowGridHorizontalLine(true)
                //竖向网格显示
                .setIsShowGridVericalLine(true)
                //网格空心
                .setIsGridLinePathEffect(true)
                //设置竖向刻度左边距
                .setVerical_kedu_leftmargin(mScreenWidth / 8)
                //设置竖向刻度单位文案
                .setVerical_unit_text("")
                //设置竖向刻度开始值
                .setVerical_unit_start(Verical_unit_start)
                //设置竖向刻度结束值
                .setVerical_unit_end(Verical_unit_end)
                //设置竖向刻度增量
                .setVerical_unit_incremetal(Verical_unit_incremetal)
                //设置竖向刻度值的类型，支持int和float
                .setVerical_lable_value_type(Verical_lable_value_type)
                //竖向刻度是否分段
                .setVerical_need_to_fragment(false)
                //竖向刻度值颜色
                .setVerical_unit_color(R.color.color_white)
                //竖向刻度 文案 颜色
                .setVerical_unit_lable_color(R.color.color_white)
                //竖向刻度 文案 颜色
                .setVerical_unit_lable_sub_color(R.color.color_white)
                //竖向刻度 凸出线是否显示
                .setVerical_kedu_line_show(false)
                //竖向刻度线 不显示
                .setVerical_line_show(true)
                //设置水平刻度
                .setListHorizontalKeduAndValueType(listHorizontal, 0, "1")
                //设置水平刻度凸出线
                .setHorizontal_kedu_line_show(true)
                //设置贝塞尔区域
                .setListPointRegion(listPointRegion)
                //设置区域颜色
                .setRegion_color(R.color.chart_view_region)
                //设置点的内容
                .setListPoint(listPoint)
                //是否平滑过渡，即贝塞尔曲线过度
                .setIsSmoothPoint(true)
                //是否点线闭合,闭合则需要设置闭合区域颜色
                .setIsFillPointRegion(true)
                //点线闭合区域颜色
                .setRegion_connect_color(R.color.color_white)
                //设置点和线的颜色
                .setPath_line_color(R.color.color_white)
                //点的内圆
                .setPoint_circle_color_interval(R.color.xiyou_white)
                //点的外援
                .setPoint_circle_color_outside(R.color.xiyou_white)
                //内圆是否空心
                .setIsPointCircleIntervalStoke(true)
                //游标的单位文本
                .setIndicator_title_unit(Indicator_title_unit)
                //游标颜色
                .setIndicator_Linecolor(R.color.color_white)
                //游标外圈颜色
                .setIndicator_outside_circle_color(R.color.xiyou_touming)
                //游标标题颜色
                .setIndicator_title_color(R.color.bg_nor_start)
                //游标是否跟着曲线一起上下移动
                .setIsIndicatorMoveWithPoint(false)
                //自定义游标背景
//                 .setIndicatorBgRes(R.drawable.tree50)
                //设置游标半径，默认游标是圆形的
                .setIndicator_radius(100)
                //默认选中位置
                .setItemSelection(0);
        chartView.init(config);
    }


    @Override
    protected void setListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        chartView.setOnChartViewChangeListener(new OnChartViewChangeListener() {
            @Override
            public void onChartViewScrolled(int offset) {
            }

            @Override
            public void onChartViewScrollDirection(ScrollDirection scrollDirection) {
            }

            @Override
            public void onItemSelected(int position, Object object) {
                if (Float.parseFloat(temperature[position]) >= police_line) {
                    linearLayout.setBackgroundResource(R.drawable.shape_gradient_ala);
                } else {
                    linearLayout.setBackgroundResource(R.drawable.shape_gradient_nor);
                }
                content.setText(temperature[position]);
                chartView.invalidate();
            }

            @Override
            public void onIndicatorClick(int position) {

            }
        });
    }

}
