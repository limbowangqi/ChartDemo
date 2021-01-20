package com.limbo.chartdemo.fragment;

import com.alibaba.fastjson.JSON;
import com.limbo.chartdemo.R;
import com.limbo.chartdemo.bean.KBean;
import com.limbo.chartdemo.bean.LineBean;
import com.limbo.chartdemo.view.BaseChart;
import com.limbo.chartdemo.view.TsChart;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by wangqi on 2018/10/11.
 * 分时Fragment
 */

public class TSChartFragment extends BaseLazyFragment {

    public static String TIME_TAG = "TIME";
    public static String AVG_TAG = "AVG";

    TsChart tsChart;
    private List<LineBean.Line> timeList;
    private List<LineBean.Line> avgList;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_time_sharing;
    }

    @Override
    protected void loadData() {
        tsChart = rootView.findViewById(R.id.ts_chart);

        initData();
    }

    private void initData() {
        String json = getResources().getString(R.string.json_ts);
        List<KBean.KLine> list = JSON.parseArray(json, KBean.KLine.class);
        initChart(list);

        //合并数据
        List<LineBean> data = new ArrayList<>();
        LineBean timeBean = new LineBean();
        timeBean.setLineData(timeList);
        timeBean.setDisplay(true);
        timeBean.setTag(TIME_TAG);
        timeBean.setLineColor(getResources().getColor(R.color.color_2f74ea));
        data.add(timeBean);
        LineBean avgBean = new LineBean();
        avgBean.setLineData(avgList);
        avgBean.setDisplay(true);
        avgBean.setTag(AVG_TAG);
        avgBean.setLineColor(getResources().getColor(R.color.color_ffcc00));
        data.add(avgBean);

        tsChart.setColorShadow(getResources().getColor(R.color.color_dfecfe));
        tsChart.setType(BaseChart.Type.TYPE_TS);
        tsChart.setData(data);
        tsChart.invalidate();
    }

    private void initChart(List<KBean.KLine> list) {
        if (list == null)
            return;
        timeList = new ArrayList<>();
        avgList = new ArrayList<>();

        float totalValue = 0, totalVol = 0;
        for (KBean.KLine bean : list) {
            //时价线
            LineBean.Line timeLine = new LineBean.Line();
            timeLine.setDate(bean.getTime());
            timeLine.setPrice(bean.getClose());
            timeList.add(timeLine);
            //均价线
            LineBean.Line avgLine = new LineBean.Line();
            avgLine.setDate(bean.getTime());
            totalValue += bean.getHolding();
            totalVol += bean.getVolume();
            if (totalVol == 0) {
                avgLine.setPrice(bean.getClose());
            } else {
                avgLine.setPrice(totalValue / totalVol / (float) mContractSize);
            }
            avgList.add(avgLine);
        }
    }

    /**
     * 相对于当日00:00的时间的节点时间
     *
     * @param segment_time time
     * @return time
     */
    public static Date getRealDate(int segment_time) {
        String segment = String.valueOf(Math.abs(segment_time));
        if (segment.length() == 4) {
            //5200
            char qian = segment.charAt(0);//5
            char bai = segment.charAt(1);//2
            char shi = segment.charAt(2);//0
            char ge = segment.charAt(3);//0
            int one = Integer.valueOf(("" + qian)) * 10;
            int two = Integer.valueOf(("" + bai));
            int three = Integer.valueOf(("" + shi)) * 10;
            int four = Integer.valueOf(("" + ge));
            int addOrPlus_hour = one + two;
            int addOrPlus_min = three + four;
            Calendar toady = Calendar.getInstance();
            toady.set(Calendar.HOUR_OF_DAY, 0);
            toady.set(Calendar.MINUTE, 0);
            toady.set(Calendar.SECOND, 0);
            toady.set(Calendar.MILLISECOND, 0);
            if (segment_time < 0) {
                toady.add(Calendar.HOUR_OF_DAY, -addOrPlus_hour);
                toady.add(Calendar.MINUTE, -addOrPlus_min);
            } else {
                if (addOrPlus_hour == 24) {
                    toady.set(Calendar.HOUR_OF_DAY, 0);
                    toady.set(Calendar.MINUTE, 0);
                    toady.set(Calendar.SECOND, 0);
                    toady.add(Calendar.DAY_OF_MONTH, 1);
//                    toady.set(Calendar.HOUR_OF_DAY, addOrPlus_hour);
                } else
                    toady.add(Calendar.HOUR_OF_DAY, addOrPlus_hour);
                toady.add(Calendar.MINUTE, addOrPlus_min);
            }
            return toady.getTime();
        } else if (segment.length() == 3) {
            //900
            char bai = segment.charAt(0);//9
            char shi = segment.charAt(1);//0
            char ge = segment.charAt(2);//0
            int one = Integer.valueOf(("" + bai));
            int two = Integer.valueOf(("" + shi)) * 10;
            int three = Integer.valueOf(("" + ge));
            int addOrPlus_min = two + three;
            Calendar toady = Calendar.getInstance();
            toady.set(Calendar.HOUR_OF_DAY, 0);
            toady.set(Calendar.MINUTE, 0);
            toady.set(Calendar.SECOND, 0);
            toady.set(Calendar.MILLISECOND, 0);
            if (segment_time < 0) {
                toady.add(Calendar.HOUR_OF_DAY, -one);
                toady.add(Calendar.MINUTE, -addOrPlus_min);
            } else {
                toady.add(Calendar.HOUR_OF_DAY, one);
                toady.add(Calendar.MINUTE, addOrPlus_min);
            }
            return toady.getTime();
        } else if (segment.length() == 2) {
            //30
            char shi = segment.charAt(0);//0
            char ge = segment.charAt(1);//0
            int two = Integer.valueOf(("" + shi)) * 10;
            int three = Integer.valueOf(("" + ge));
            int addOrPlus_min = two + three;
            Calendar toady = Calendar.getInstance();
            toady.set(Calendar.HOUR_OF_DAY, 0);
            toady.set(Calendar.MINUTE, 0);
            toady.set(Calendar.SECOND, 0);
            toady.set(Calendar.MILLISECOND, 0);
            if (segment_time < 0) {
//                toady.add(Calendar.HOUR_OF_DAY, -addOrPlus_min);
                toady.add(Calendar.MINUTE, -addOrPlus_min);
            } else {
//                toady.add(Calendar.HOUR_OF_DAY, addOrPlus_min);
                toady.add(Calendar.MINUTE, addOrPlus_min);
            }
            return toady.getTime();
        } else if (segment_time == 0) {
            Calendar toady = Calendar.getInstance();
            toady.set(Calendar.HOUR_OF_DAY, 0);
            toady.set(Calendar.MINUTE, 0);
            toady.set(Calendar.SECOND, 0);
            toady.set(Calendar.MILLISECOND, 0);
            return toady.getTime();
        }
        return new Date();
    }
}
