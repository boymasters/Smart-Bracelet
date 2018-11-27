package com.sanzitech.szitechwisebrave.chartview;

import android.graphics.PointF;

/**
 * 贝塞尔辅助类
 * Created by lwh on 2015/10/10.
 */
public class ChartViewHelper {

    /**
     *  求A点到B点的三次贝塞尔曲线的两个控制点
     * @param a  a点
     * @param b  b点
     * @param l  a的前一点
     * @param n  b的后一点
     * @param controls
     */
    /*public static void curve3(PointF a, PointF b, PointF l, PointF n, PointF[] controls) {
        // 点a前一个点和点a的中点
        PointF cLA = center(l, a);
        // 点a和点b的中点
        PointF cAB =  center(a, b);
        // 点b和点b后一个点的中点
        PointF cBN =  center(b, n);

        // 点a前一个点到点a的距离
        float lenLA =  distance(l, a);
        // 点a到点b的距离
        float lenAB =  distance(a, b);
        // 点b到点b后一个点的距离
        float lenBN =  distance(b, n);

        // cLA和cAB连线的比例点
        PointF cLAB =  percent(cLA, cAB, lenLA / (lenLA + lenAB));
        PointF cABN =  percent(cAB, cBN, lenAB / (lenAB + lenBN));

        // 顶点a和顶点b的控制点1
        controls[0] =  translate(cAB, a.x - cLAB.x, a.y - cLAB.y);
        // 顶点a和顶点b的控制点2
        controls[1] =  translate(cAB, b.x - cABN.x, b.y - cABN.y);

    }*/


    /**
     *  求A点到B点的三次贝塞尔曲线的两个控制点
     * @param a  a点
     * @param b  b点
     * @param l  a的前一点
     * @param n  b的后一点
     * @param controls
     */
    public static void caculateController(PointValue a, PointValue b, PointValue l,PointValue n, PointValue[] controls) {
        /*// 点a前一个点和点a的中点
        PointF cLA = center(l, a);
        // 点a和点b的中点
        PointF cAB =  center(a, b);
        // 点b和点b后一个点的中点
        PointF cBN =  center(b, n);

        // 点a前一个点到点a的距离
        float lenLA =  distance(l, a);
        // 点a到点b的距离
        float lenAB =  distance(a, b);
        // 点b到点b后一个点的距离
        float lenBN =  distance(b, n);

        // cLA和cAB连线的比例点
        PointF cLAB =  percent(cLA, cAB, lenLA / (lenLA + lenAB));
        PointF cABN =  percent(cAB, cBN, lenAB / (lenAB + lenBN));

        // 顶点a和顶点b的控制点1
        controls[0] =  translate(cAB, a.x - cLAB.x, a.y - cLAB.y);
        // 顶点a和顶点b的控制点2.
        controls[1] =  translate(cAB, b.x - cABN.x, b.y - cABN.y);*/

        // 点l和a的重点
        PointF cLA = center(l, a);
        // 点a和点b的中点
        PointF cAB =  center(a, b);
        // 点b和点b后一个点的中点
        PointF cBN =  center(b, n);

        //取la ab中点连线的中点
        PointValue pointValue = new PointValue(cLA.x,cLA.y);
        PointValue pointValue1 = new PointValue(cAB.x,cAB.y);
        PointF cLAB = center(pointValue,pointValue1);
        //计算连线中点平移到a点，计算出偏移量
        float deltaX = (a.x-cLAB.x);
        float deltaY = (a.y-cLAB.y);
        //计算第一对控制点的比例
        PointF percentOne = percent(cLA, cLAB, 0.4f);
        PointF percentTwo = percent(cAB, cLAB, 0.4f);

        //按照比例，算出第一对控制点
        PointValue value = new PointValue(percentOne.x+deltaX,percentOne.y+deltaY);
        controls[0] = new PointValue(value.x,value.y);
        PointValue value1 = new PointValue(percentTwo.x+deltaX,percentTwo.y+deltaY);
        controls[1] = new PointValue(value1.x,value1.y);


        //取ab bn中点连线的中点
        PointValue pointValue2 = new PointValue(cBN.x,cBN.y);
        PointF cABN = center(pointValue1,pointValue2);
        //计算连线中点平移到b点，计算出偏移量
        deltaX = (b.x-cABN.x);
        deltaY = (b.y-cABN.y);
        //计算第二对控制点的比例
        PointF percentThree = percent(cLAB, cABN, 0.4f);
        PointF percentFour = percent(cABN, cABN, 0.4f);

        //按照比例，算出第二对控制点
        value = new PointValue(percentThree.x+deltaX,percentThree.y+deltaY);
        controls[2] = new PointValue(value.x,value.y);
         value1 = new PointValue(percentFour.x+deltaX,percentFour.y+deltaY);
        controls[3] = new PointValue(value1.x,value1.y);

    /*
        // 点b和点b后一个点的中点
        PointF cBN =  center(b, n);

        // 点a前一个点到点a的距离
        float lenLA =  distance(l, a);
        // 点a到点b的距离
        float lenAB =  distance(a, b);
        // 点b到点b后一个点的距离
        float lenBN =  distance(b, n);

        // cLA和cAB连线的比例点
        PointF cLAB =  percent(cLA, cAB, lenLA / (lenLA + lenAB));
        PointF cABN =  percent(cAB, cBN, lenAB / (lenAB + lenBN));

        // 顶点a和顶点b的控制点1
        controls[0] =  translate(cAB, a.x - cLAB.x, a.y - cLAB.y);
        // 顶点a和顶点b的控制点2
        controls[1] =  translate(cAB, b.x - cABN.x, b.y - cABN.y);*/


    }

    /**
     * 大致思路就是 先算出相邻原始点的中点，在把相邻中点连成的线段平移到对应的原始点，
     * 以平移后的中点作为控制点，相邻原始点为起始点画贝塞尔曲线，
     * 这样就保证了连接处的光滑。而贝塞尔曲线本身是光滑的，所以就把这些原始点用光滑曲线连起来了。
     *
     */

    /**
     *
     * 通过 a b c d四个点 算出两个控制点，画的曲线会经过a b c d四个点；
     * @param a
     * @param b
     * @param c
     * @param d
     * @param controls
     */
    public static void caculateControllerPoint(PointValue a, PointValue b, PointValue c, PointValue d, PointValue[] controls) {
        /*// 点a前一个点和点a的中点
        PointF cLA = center(l, a);
        // 点a和点b的中点
        PointF cAB =  center(a, b);
        // 点b和点b后一个点的中点
        PointF cBN =  center(b, n);

        // 点a前一个点到点a的距离
        float lenLA =  distance(l, a);
        // 点a到点b的距离
        float lenAB =  distance(a, b);
        // 点b到点b后一个点的距离
        float lenBN =  distance(b, n);

        // cLA和cAB连线的比例点
        PointF cLAB =  percent(cLA, cAB, lenLA / (lenLA + lenAB));
        PointF cABN =  percent(cAB, cBN, lenAB / (lenAB + lenBN));

        // 顶点a和顶点b的控制点1
        controls[0] =  translate(cAB, a.x - cLAB.x, a.y - cLAB.y);
        // 顶点a和顶点b的控制点2
        controls[1] =  translate(cAB, b.x - cABN.x, b.y - cABN.y);*/

    }

    // 求两点中点
    public static PointF center(PointValue p1, PointValue p2) {
        return new PointF((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
    }

    // 求两点间距离
    public static float distance(PointValue p1, PointValue p2) {
        float dx = Math.abs(p2.x - p1.x);
        float dy = Math.abs(p2.y - p1.y);
        return (float) Math.hypot(dx, dy);
    }

    // 平移点
    public static PointValue translate(PointF p, float x, float y) {
        return new PointValue(p.x + x, p.y + y);
    }

    // 计算两点连线中的一点，这个点把这条线分成两段，比例是percent
    public static PointF percent(PointF p1, PointF p2, float percent) {
        return percent(p1,percent,p2,percent);
    }

    public static PointF percent(PointF p1, float percent1, PointF p2, float percent2) {
        float x = (p2.x - p1.x) * percent1 + p1.x;
        float y = (p2.y - p1.y) * percent2 + p1.y;
        return new PointF(x, y);
    }
    
}
