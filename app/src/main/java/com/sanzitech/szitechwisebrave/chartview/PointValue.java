package com.sanzitech.szitechwisebrave.chartview;

import java.util.Calendar;

/**
 * Created by lwh on 2015/9/23.
 */
public class PointValue {
    //横向数值
    public String horizontal_value;
    //横向 日期 使用
    public Calendar horizontal_calendar;
    //竖向竖直
    public String verical_value;
    //标题
    public String title;
    //子标题
    public String title_sub;
    //x 位置
    public float x;
    //y 位置
    public float y;
    //是否是虚假的
    public boolean bEmpty;

    public PointValue(){

    }

    public PointValue(float x,float y){
        this.x = x;
        this.y = y;
    }
}
