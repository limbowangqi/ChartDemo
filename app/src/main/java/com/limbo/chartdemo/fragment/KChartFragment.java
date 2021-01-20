package com.limbo.chartdemo.fragment;

import com.alibaba.fastjson.JSON;
import com.limbo.chartdemo.R;
import com.limbo.chartdemo.bean.ChartBean;
import com.limbo.chartdemo.bean.KBean;
import com.limbo.chartdemo.view.BaseChart;
import com.limbo.chartdemo.view.KChart;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangqi on 2018/10/16.
 */

public class KChartFragment extends BaseLazyFragment {

    public static final String K_CHART = "K";

    private KChart kChart;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_k_chart;
    }

    @Override
    protected void loadData() {
        kChart = rootView.findViewById(R.id.k_chart);

        String json = getResources().getString(R.string.json_k);
        List<KBean.KLine> list = JSON.parseArray(json, KBean.KLine.class);

        List<ChartBean> data = new ArrayList<>();
        KBean kBean = new KBean();
        kBean.setLineData(list);
        kBean.setDisplay(true);
        kBean.setTag(K_CHART);
        data.add(kBean);

        kChart.setType(BaseChart.Type.TYPE_K);
        kChart.setData(data);
        kChart.invalidate();
    }

    private void initChart(List<KBean.KLine> list, int num) {
        if (list == null || num <= 0)
            return;
    }
}
