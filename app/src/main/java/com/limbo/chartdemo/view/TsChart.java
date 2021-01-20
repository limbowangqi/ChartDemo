package com.limbo.chartdemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.limbo.chartdemo.R;
import com.limbo.chartdemo.bean.LineBean;
import com.limbo.chartdemo.fragment.BaseLazyFragment;
import com.limbo.chartdemo.fragment.TSChartFragment;
import com.limbo.chartdemo.utils.MyUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by wangqi on 2018/10/11.
 */

public class TsChart extends BaseChart<LineBean> {
    //点的宽度
    private float pointWidth = MyUtils.dp2px(0.8f);
    private float crossTextSize = f10;
    //两点之间  线的距离
    private double lineLength;
    //时间占用比例
    private double mScaleTime;
    // 线颜色
    private int color_shadow;
    private LineBean timeBean;
    private LineBean avgBean;

    public TsChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init() {
        for (LineBean lineBean : mData) {
            if (TSChartFragment.TIME_TAG.equals(lineBean.getTag())) {
                timeBean = lineBean;
            } else if (TSChartFragment.AVG_TAG.equals(lineBean.getTag())) {
                avgBean = lineBean;
            }
        }

        //下面这些计算代码 其实可以放到help中 方便管理
        // TODO
        mMax = mMin = (float) BaseLazyFragment.mYesterdayClose;
        for (int i = 0; i < timeBean.getLineData().size(); i++) {
            double price = timeBean.getLineData().get(i).getPrice();
            double avg = avgBean.getLineData().get(i).getPrice();

            if (price <= 0)
                break;
            if (price > mMax) {
                mMax = price;
                mMax = Math.max(mMax, avg);
            } else if (price < mMin) {
                mMin = price;
                mMin = Math.min(mMin, avg);
            }
        }
        //计算Y轴数据
        if (mMax - BaseLazyFragment.mYesterdayClose > BaseLazyFragment.mYesterdayClose - mMin) {
            mMin = (float) (BaseLazyFragment.mYesterdayClose - (mMax - BaseLazyFragment.mYesterdayClose));
        } else if (mMax - BaseLazyFragment.mYesterdayClose < BaseLazyFragment.mYesterdayClose - mMin) {
            mMax = (float) (BaseLazyFragment.mYesterdayClose + (BaseLazyFragment.mYesterdayClose - mMin));
        }
        if (Double.compare(mMax, mMin) == 0) {
            mMax = BaseLazyFragment.mYesterdayClose * 1.1;
            mMin = BaseLazyFragment.mYesterdayClose * 1.1;
        }
        BigDecimal b = new BigDecimal(Double.toString(mMax - mMin));
        BigDecimal one = new BigDecimal("1");
        if (b.divide(one, BaseLazyFragment.mDec, BigDecimal.ROUND_HALF_UP).doubleValue() == 0) {
            mMax += 1;
            mMin -= 1;
        }
        //得到最后最大值 最小值
        double d;
        if (Math.abs(mMax - BaseLazyFragment.mYesterdayClose) > Math.abs(BaseLazyFragment.mYesterdayClose - mMin)) {
            d = Math.abs(mMax - BaseLazyFragment.mYesterdayClose);
            d = d + d * 0.08;
            mMin = (float) (BaseLazyFragment.mYesterdayClose - d);
        } else {
            d = Math.abs(BaseLazyFragment.mYesterdayClose - mMin);
            d = d + d * 0.08;
            mMax = (float) (BaseLazyFragment.mYesterdayClose + d);
        }
        //计算左边数值
        double leftTitleValue[] = new double[]{BaseLazyFragment.mYesterdayClose + d,
                BaseLazyFragment.mYesterdayClose + d / 2, BaseLazyFragment.mYesterdayClose, BaseLazyFragment.mYesterdayClose - d / 2,
                BaseLazyFragment.mYesterdayClose - d};
        YLeftValues = new ArrayList<>();
        for (Double value : leftTitleValue) {
            YLeftValues.add(MyUtils.formatDouble(value, BaseLazyFragment.mDec));
        }
        //计算右边数值
        String[] rightValue = new String[5];
        if (Double.compare(BaseLazyFragment.mYesterdayClose, 0.0) == 0) {
            // 昨收为0特殊处理
            for (int i = 0; i < rightValue.length; i++) {
                rightValue[i] = "0.00%";
            }
        } else {
            for (int i = 0; i < rightValue.length; i++) {
                if (i == 2) {
                    //中间那个固定为0.00%
                    rightValue[2] = "0.00%";
                } else {
                    rightValue[i] = MyUtils.formatDouble((leftTitleValue[i] - BaseLazyFragment.mYesterdayClose) / BaseLazyFragment.mYesterdayClose * 100,
                            BaseLazyFragment.mDec) + "%";
                }
            }
        }
        YRightValues = new ArrayList<>();
        YRightValues.addAll(Arrays.asList(rightValue));

        //计算X轴时间
        XValues = new ArrayList<>();
        long startTime = timeBean.getLineData().get(0).getDate();
        XValues.add(startTime);

        List<int[]> ints = JSON.parseArray(getResources().getString(R.string.json_config), int[].class);

        int week = MyUtils.getWeekOfDate(MyUtils.long2Date(startTime));
        int add = 0;
        long hour_temp = 3600000;
        for (int[] n : ints) {
            if (n[0] == week) {
                if (n[1] == n[3] && n[2] == n[4]) {
                    //没有中途休市的节点
                    Date startDate = TSChartFragment.getRealDate(n[3]);
                    Date endDate = TSChartFragment.getRealDate(n[4]);

                    long allTime = endDate.getTime() - startDate.getTime();
                    if (allTime <= (hour_temp * 2))//两小时以内
                        add = 2;
                    else if ((hour_temp * 2) < allTime && allTime <= (hour_temp * 4))//2<X<4小时
                        add = 4;
                    else if ((hour_temp * 4) < allTime && allTime <= (hour_temp * 8))//4<X<8小时
                        add = 8;
                    else if ((hour_temp * 8) < allTime && allTime <= (hour_temp * 12))//8<X<12小时
                        add = 12;
                    else if ((hour_temp * 12) < allTime && allTime <= (hour_temp * 24))//12<X<24小时
                        add = 24;
                    double allHour;
                    if (add * hour_temp >= allTime) {
                        allHour = allTime / hour_temp;
                        XValues.add(endDate.getTime());
                    } else {
                        allHour = add;
                        XValues.add(startTime);
                    }
                    //获得占用时间比
                    mScaleTime = timeBean.getLineData().size() / 60 / allHour;
                }
            }
        }

        //总宽度 - 点宽度 = 线宽度
        lineLength = (chartWidth * mScaleTime - pointWidth * (mData.get(0).getLineData().size() - 1)) / (mData.get(0).getLineData().size() - 1);
    }

    @Override
    protected void drawGrid(Canvas canvas) {
        mLinePaint.reset();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(color_grid);
        //X方向
        drawXGrid(canvas);
        //Y方向
        drawYGrid(canvas);
    }

    @Override
    protected void drawLine(Canvas canvas) {
        mLinePaint.reset();
        mLinePaint.setStyle(Paint.Style.STROKE);    // 填充模式 - 描边
        mLinePaint.setStrokeWidth(pointWidth);
        mLinePaint.setAntiAlias(true);

        for (LineBean lineBean : mData) {
            if (!lineBean.isDisplay())
                return;

            float x = 0f;
            PointF pricePoint = null;
            Path shadowPaht = new Path();
            shadowPaht.moveTo(x, chartHeight);
            for (int i = 0; i < lineBean.getLineData().size(); i++) {
                LineBean.Line bean = lineBean.getLineData().get(i);
                //绘制时价
                mLinePaint.setColor(lineBean.getLineColor());
                float y = doubleToY(bean.getPrice());
                if (pricePoint == null) {
                    pricePoint = new PointF(x, y);
                }
                canvas.drawLine(pricePoint.x, pricePoint.y, x, y, mLinePaint);

                //绘制阴影
                shadowPaht.lineTo(x, y);
                if (i == lineBean.getLineData().size() - 1 && TSChartFragment.TIME_TAG.equals(lineBean.getTag())) {
                    shadowPaht.lineTo(x, chartHeight);
                    shadowPaht.close();
                    mLinePaint.setColor(color_shadow);
                    mLinePaint.setStyle(Paint.Style.FILL);
                    mLinePaint.setAlpha(100);
                    canvas.drawPath(shadowPaht, mLinePaint);
                }

                pricePoint.x = x;
                pricePoint.y = y;

                x = (float) (x + pointWidth + lineLength);
            }
        }
    }

    @Override
    protected void drawXYText(Canvas canvas) {
        drawXValue(canvas);
        drawYValue(canvas);
        //绘制 十字标
        if (isShowCross) {
            double v = (crossX + pointWidth) / (mScaleTime * chartWidth);
            //判断点击区域是否在分时图里面
            if (v >= 1) {
                isShowCross = false;
                return;
            }
            int index = (int) (v * mData.get(0).getLineData().size());
            LineBean.Line timeLine = timeBean.getLineData().get(index);
            LineBean.Line avgLine = avgBean.getLineData().get(index);
            crossY = doubleToY(timeLine.getPrice());

            mLinePaint.reset();
            mLinePaint.setAntiAlias(true);
            mLinePaint.setColor(color_cross);
            mLinePaint.setStyle(Paint.Style.FILL);    // 填充模式 - 描边
            mLinePaint.setStrokeWidth(crossLineWidth);
            //画线
            canvas.drawLine(0, crossY, chartWidth, crossY, mLinePaint);
            canvas.drawLine(crossX, 0, crossX, chartHeight, mLinePaint);
            //画点
            mLinePaint.setColor(mData.get(0).getLineColor());
            canvas.drawCircle(crossX, crossY, pointWidth * 2, mLinePaint);
            mLinePaint.setColor(mData.get(1).getLineColor());
            float avgY = doubleToY(avgLine.getPrice());
            ;
            canvas.drawCircle(crossX, avgY, pointWidth * 2, mLinePaint);

            mTextPaint.reset();
            mTextPaint.setAntiAlias(true);
            mTextPaint.setColor(color_cross);
            mTextPaint.setTextSize(crossTextSize);

            int textPadding = MyUtils.dp2px(3);// 间距
            String scale = MyUtils.formatDouble((timeLine.getPrice() - BaseLazyFragment.mYesterdayClose) / TSChartFragment.mYesterdayClose * 100, BaseLazyFragment.mDec) + "%"; //右侧百分比
            String time = MyUtils.formatTime(timeLine.getDate(), "HH:mm");
            //矩形
            canvas.drawRect(0, crossY - crossTextSize / 2,
                    mTextPaint.measureText(MyUtils.formatDouble(timeLine.getPrice(), BaseLazyFragment.mDec)) + textPadding * 2, crossY + crossTextSize / 2 + textPadding, mTextPaint);
            canvas.drawRect(chartWidth - mTextPaint.measureText(scale) - textPadding * 2,
                    crossY - crossTextSize / 2,
                    chartWidth, crossY + crossTextSize / 2 + textPadding, mTextPaint);
            canvas.drawRect(crossX - mTextPaint.measureText(time) / 2 - textPadding, chartHeight - crossTextSize - textPadding,
                    crossX + mTextPaint.measureText(time) / 2 + textPadding, chartHeight, mTextPaint);
            //数值
            mTextPaint.setColor(getResources().getColor(R.color.color_white));
            canvas.drawText(MyUtils.formatDouble(timeLine.getPrice(), BaseLazyFragment.mDec), textPadding, crossY + crossTextSize / 2, mTextPaint);
            canvas.drawText(scale,
                    chartWidth - mTextPaint.measureText(scale) - textPadding,
                    crossY + crossTextSize / 2, mTextPaint);
            canvas.drawText(time, crossX - mTextPaint.measureText(time) / 2, chartHeight - textPadding, mTextPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                if (isShowCross) {
                    double v = (event.getX() + pointWidth) / (mScaleTime * chartWidth);
                    if (v >= 1) {
                        //超过分时图范围
                        crossX = (float) (mScaleTime * chartWidth - pointWidth);
                    } else {
                        crossX = event.getX();
                    }
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onTouchEvent(event);
    }

    public void setColorShadow(int colorShadow) {
        this.color_shadow = colorShadow;
    }

    public void setScaleTime(double ScaleTime) {
        this.mScaleTime = ScaleTime;
    }
}
