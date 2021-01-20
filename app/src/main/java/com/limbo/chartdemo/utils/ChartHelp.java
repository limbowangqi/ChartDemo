package com.limbo.chartdemo.utils;


import com.limbo.chartdemo.bean.KBean;
import com.limbo.chartdemo.fragment.BaseLazyFragment;
import com.limbo.chartdemo.view.KChart;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangqi on 2018/10/18.
 * chart帮助类
 */

public class ChartHelp {

    private static ChartHelp help;

    public static ChartHelp getInstance() {
        synchronized (ChartHelp.class) {
            if (help == null) {
                help = new ChartHelp();
            }
        }
        return help;
    }

    private ChartHelp() {
    }

    public void initKChart(KChart kChart) {
        KBean kBean = kChart.getkBean();
        int start = kChart.getStart();
        int end = kChart.getEnd();
        int zoom = kChart.getZoom();

        int DEFAULT = zoom * 10;
        double priceHigh = 0;
        double priceLow = 0;
        double mMax = 0;
        double mMin = 0;
        List<String> YLeftValues = new ArrayList<>();
        if (kBean != null && kBean.getLineData().size() != 0) {
            //初始化 start end
            if (kBean.getLineData().size() > DEFAULT) {
                if (start == 0 && end == 0) {
                    start = kBean.getLineData().size() - DEFAULT;
                    end = kBean.getLineData().size();
                } else {
                    start = end - DEFAULT;
                    if (start<0){
                        start = 0;
                        end = DEFAULT;
                    }
                }
            } else {
                start = 0;
                end = DEFAULT;
            }

            //初始化 最高值和最低值
            priceHigh = Double.MIN_VALUE;
            priceLow = Double.MAX_VALUE;
            for (int i = start; i < end; i++) {
                if (kBean.getLineData().size() > i) {
                    KBean.KLine bean = kBean.getLineData().get(i);
                    priceHigh = Math.max(bean.getHigh(), priceHigh);
                    priceLow = Math.min(bean.getLow(), priceLow);
                }
            }
            double n = priceHigh - priceLow;
            mMax = priceHigh + n * 0.06f;
            mMin = priceLow - n * 0.06f;

            //Y轴指标
            double d = (mMax - mMin) / 4;
            for (int i = 0; i < 4 + 1; i++) {
                YLeftValues.add(MyUtils.formatDouble(mMax - d * i, BaseLazyFragment.mDec));
            }
        }
        //赋值
        kChart.setStart(start);
        kChart.setEnd(end);
        kChart.setDEFAULT(DEFAULT);
        kChart.setPriceLow(priceLow);
        kChart.setPriceHigh(priceHigh);
        kChart.setMin(mMin);
        kChart.setMax(mMax);
        kChart.setYLeftValues(YLeftValues);
    }

}
