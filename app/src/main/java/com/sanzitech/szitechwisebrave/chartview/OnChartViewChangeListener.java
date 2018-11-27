package com.sanzitech.szitechwisebrave.chartview;

/**
 * Created by lwh on 2015/10/12.
 */
public interface OnChartViewChangeListener {

    /**
     * 滚动
     * @param offset 滚动偏移量  会频繁触发
     */
    public void onChartViewScrolled(int offset);

    /**
     * 滚动方向，滑动几次触发几次
     * @param scrollDirection
     */
    public void onChartViewScrollDirection(ScrollDirection scrollDirection);

    /**
     * 滑动到某个点
     * @param position
     */
    public void onItemSelected(int position, Object object);

    /**
     * 点击了游标
     * @param position
     */
    public void onIndicatorClick(int position);
}
