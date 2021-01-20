package com.limbo.chartdemo.bean;

import java.util.List;

/**
 * Created by wangqi on 2018/10/16.
 */

public class LineBean extends ChartBean{
    /**
     * 实际数据
     */
    private List<Line> lineData;

    public List<Line> getLineData() {
        return lineData;
    }


    public void setLineData(List<Line> lineData) {
        this.lineData = lineData;
    }

    public static class Line{
        public long date;//date为0即为空数据
        public double price;

        public long getDate() {
            return date;
        }

        public double getPrice() {
            return price;
        }

        public void setDate(long date) {
            this.date = date;
        }

        public void setPrice(double price) {
            this.price = price;
        }
    }
}
