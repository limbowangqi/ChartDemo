package com.limbo.chartdemo.bean;

/**
 * Created by wangqi on 2018/10/16.
 */

public class ChartBean {
    /**
     * 线标题
     */
    private String tag;

    /**
     * 线表示颜色
     */
    private int lineColor;

    /**
     * 是否显示线
     */
    private boolean display = true;

    public String getTag() {
        return tag;
    }

    public int getLineColor() {
        return lineColor;
    }

    public boolean isDisplay() {
        return display;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }
}
