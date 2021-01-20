package com.limbo.chartdemo.bean;

import java.util.List;

/**
 * Created by wangqi on 2018/10/11.
 * 分时图数据
 */

public class KBean  extends ChartBean{
    /**
     * 实际数据
     */
    private List<KLine> lineData;

    public List<KLine> getLineData() {
        return lineData;
    }

    public void setLineData(List<KLine> lineData) {
        this.lineData = lineData;
    }

    public static class KLine{
        private double close;
        private double high;
        private double holding;
        private double low;
        private double open;
        private long time;
        private double volume;

        public double getClose() {
            return close;
        }

        public void setClose(double close) {
            this.close = close;
        }

        public double getHigh() {
            return high;
        }

        public void setHigh(double high) {
            this.high = high;
        }

        public double getHolding() {
            return holding;
        }

        public void setHolding(double holding) {
            this.holding = holding;
        }

        public double getLow() {
            return low;
        }

        public void setLow(double low) {
            this.low = low;
        }

        public double getOpen() {
            return open;
        }

        public void setOpen(double open) {
            this.open = open;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public double getVolume() {
            return volume;
        }

        public void setVolume(double volume) {
            this.volume = volume;
        }
    }
}
