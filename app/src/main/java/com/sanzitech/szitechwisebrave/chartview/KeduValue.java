package com.sanzitech.szitechwisebrave.chartview;

import java.util.Calendar;

/**
 * 刻度
 * Created by lwh on 2015/9/21.
 */
public class KeduValue {
    //具体值
    public String value;
    //显示的值
    public String display_value="";
    //日期类型使用
    public Calendar calendar;
    //预留字段，如果有就绘制在第二行
    public String value_unit;
    //是否需要在底部绘制线
    public boolean need_to_draw_line_at_bottom =false;
    //线的颜色
    public int line_color;
    //背景颜色
    public int point_bg;
    //是否需要绘制数据
    public boolean need_to_draw_bg=false;
    //当前x值
    public float current_x;
}
