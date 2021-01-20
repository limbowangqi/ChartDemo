package com.limbo.chartdemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;


import androidx.annotation.Nullable;

import com.limbo.chartdemo.R;
import com.limbo.chartdemo.bean.ChartBean;
import com.limbo.chartdemo.bean.KBean;
import com.limbo.chartdemo.fragment.BaseLazyFragment;
import com.limbo.chartdemo.fragment.KChartFragment;
import com.limbo.chartdemo.utils.MyUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangqi on 2018/10/16.
 */

public class KChart extends BaseChart<ChartBean> {

    //最高价 最低价
    private double priceHigh;
    private double priceLow;

    //间距
    private float spaceWidth;
    //蜡烛棒 宽度
    private double stickWidth;
    // start end
    private int start, end;
    //缩放等级  10 - 1;
    private int zoom = 5;
    //初始size
    private int DEFAULT;
    //K线数据
    private KBean kBean;
    //X坐标集合
    private List<Float> XList = new ArrayList<>();
    private float downX;
    //缩放 两指间的距离
    private float downPointDX;

    public KChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init() {
        for (ChartBean chartBean : mData) {
            if (KChartFragment.K_CHART.equals(chartBean.getTag())) {
                kBean = (KBean) chartBean;
            }
        }

        help.initKChart(this);

        spaceWidth = MyUtils.dp2px(1f);
        stickWidth = (chartWidth - (getSize() - 1) * spaceWidth) / getSize();
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
        for (ChartBean chartBean : mData) {
            if (!chartBean.isDisplay())
                return;
            if (KChartFragment.K_CHART.equals(chartBean.getTag())) {
                //绘制K线图
                drawKLine(canvas);
            }
        }
    }

    @Override
    protected void drawXYText(Canvas canvas) {
        drawXValue(canvas);
        drawYValue(canvas);

        if (isShowCross) {
            int index = (int) (crossX / chartWidth * getSize());
            KBean.KLine bean = kBean.getLineData().get(start + index);

            crossX = XList.get(index);
            crossY = doubleToY(bean.getClose());

            mLinePaint.reset();
            mLinePaint.setAntiAlias(true);
            mLinePaint.setColor(color_cross);
            mLinePaint.setStyle(Paint.Style.FILL);    // 填充模式 - 描边
            mLinePaint.setStrokeWidth(crossLineWidth);
            //画线
            canvas.drawLine(0, crossY, chartWidth, crossY, mLinePaint);
            canvas.drawLine(crossX, 0, crossX, chartHeight, mLinePaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float MIN_LENGTH = (super.getWidth() / 40) < 5 ? 5 : (super.getWidth() / 50);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                if (isShowCross) {
                    // 十字标 滑动
                    crossX = event.getX();
                } else {
                    if (event.getPointerCount() == 1) {
                        //K线图滑动
                        float dx = event.getX() - downX;
                        int count = (int) (-dx / (stickWidth + spaceWidth * 2));
                        if (Math.abs(count) >= 1) { //说明滑动距离超过一个蜡烛距离
                            start += count;
                            end += count;
                            downX = event.getX();
                            if (start <= 0) {
                                start = 0;
                                end = start + DEFAULT;
                            }
                            if (end >= kBean.getLineData().size()) {
                                end = kBean.getLineData().size();
                                start = end - DEFAULT;
                            }
                        }
                    } else if (event.getPointerCount() == 2) {
                        //K线图缩放
                        float dX = spacing(event);
                        if (dX > MIN_LENGTH && Math.abs(dX - downPointDX) > MIN_LENGTH) {
                            if (dX > downPointDX) {
                                //放大
                                zoom--;
                                if (zoom < 1) {
//                                    Toast.makeText(MyApp.getInstance(), "K线图已达到最大尺寸", Toast.LENGTH_LONG).show();
                                    zoom = 1;
                                    break;
                                }
                            } else if (dX < downPointDX) {
                                zoom++;
                                if (zoom > 10) {
//                                    Toast.makeText(MyApp.getInstance(), "无法容纳更多的K线", Toast.LENGTH_LONG).show();
                                    zoom = 10;
                                    break;
                                }
                            }
                            Log.e("wangqi", zoom + "");
                            downPointDX = dX;
                        }
                    }
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                //缩放
                if (!isShowCross) {
                    if (event.getPointerCount() == 2) {
                        downPointDX = spacing(event);
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
        }
        return super.onTouchEvent(event);
    }

    // 计算移动距离
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, final float velocityX, float velocityY) {
        if (isShowCross)
            return false;
        if (Math.abs(velocityX) > 3000) {
            //惯性 加速
            int a = 20;
            if (Math.abs(velocityX) < 5000)
                a = 30;
            else if (Math.abs(velocityX) >= 5000 && Math.abs(velocityX) < 8000)
                a = 20;
            else if (Math.abs(velocityX) >= 8000)
                a = 10;
            float t = 600;
            //执行动画
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (velocityX > 0) { // 向左滑
                        start--;
                        end--;
                    } else {
                        start++;
                        end++;
                    }
                    if (start <= 0) {
                        start = 0;
                        end = start + DEFAULT;
                        removeCallbacks(this);
                    }
                    if (end >= kBean.getLineData().size()) {
                        end = kBean.getLineData().size();
                        start = end - DEFAULT;
                        removeCallbacks(this);
                    }
                    postInvalidate();
                }
            };
            removeCallbacks(runnable);
            for (int i = 0; i < t; i += a) {
                postDelayed(runnable, i);
            }
            return true;
        }
        return false;
    }

    private void drawKLine(Canvas canvas) {
        mLinePaint.reset();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStrokeWidth(crossLineWidth);

        mTextPaint.reset();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(f10);
        mTextPaint.setColor(getResources().getColor(R.color.color_white));

        XList.clear();
        float x = (float) (stickWidth / 2);
        for (int i = start; i < end; i++) {
            if (kBean.getLineData().size() <= i) {
                continue;
            }
            KBean.KLine bean = kBean.getLineData().get(i);

            if (bean.getClose() > bean.getOpen()) { //阳线
                mLinePaint.setColor(colorRed);
                mLinePaint.setStyle(Paint.Style.STROKE);
            } else {
                mLinePaint.setColor(colorGreen);
                mLinePaint.setStyle(Paint.Style.FILL);
            }
            //绘制蜡烛矩形
            canvas.drawRect((float) (x - stickWidth / 2), doubleToY(bean.getClose()),
                    (float) (x + stickWidth / 2), doubleToY(bean.getOpen()), mLinePaint);

            canvas.drawLine(x, doubleToY(bean.getHigh()), x, doubleToY(Math.max(bean.getClose(), bean.getOpen())), mLinePaint);
            canvas.drawLine(x, doubleToY(bean.getLow()), x, doubleToY(Math.min(bean.getClose(), bean.getOpen())), mLinePaint);

            //绘制最高价和最低价
            if (bean.getHigh() == priceHigh) {
                drawHighLow(canvas, x, priceHigh);
            } else if (bean.getLow() == priceLow) {
                drawHighLow(canvas, x, priceLow);
            }

            XList.add(x);
            x += stickWidth + spaceWidth;
        }


    }

    private void drawHighLow(Canvas canvas, float x, double price) {
        mLinePaint.setColor(color_text_999999);
        mLinePaint.setStyle(Paint.Style.FILL);
        int textPadding = MyUtils.dp2px(3);
        String text = MyUtils.formatDouble(price, BaseLazyFragment.mDec);
        float h = mTextPaint.measureText(text);
        if (chartWidth - x > h + f10 + textPadding * 2) {
            //右边 f10指线的距离
            canvas.drawLine(x, doubleToY(price), x + f10, doubleToY(price), mLinePaint);
            canvas.drawRect(x + f10, doubleToY(price) - f10 / 2,
                    x + f10 + h + textPadding * 2, doubleToY(price) + f10 / 2 + textPadding, mLinePaint);
            canvas.drawText(text, x + f10 + textPadding,
                    doubleToY(price) + f10 / 2, mTextPaint);
        } else {
            canvas.drawLine(x, doubleToY(price), x - f10, doubleToY(price), mLinePaint);
            canvas.drawRect(x - f10 - h - textPadding * 2, doubleToY(price) - f10 / 2,
                    x - f10, doubleToY(price) + f10 / 2 + textPadding, mLinePaint);
            canvas.drawText(text, x - f10 - h - textPadding,
                    doubleToY(price) + f10 / 2, mTextPaint);
        }
    }

    private int getSize() {
        int size = end - start;
        if (size < 0)
            size = 0;
        return size;
    }

    //get set 相关
    public double getPriceHigh() {
        return priceHigh;
    }

    public double getPriceLow() {
        return priceLow;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public KBean getkBean() {
        return kBean;
    }

    public void setPriceHigh(double priceHigh) {
        this.priceHigh = priceHigh;
    }

    public void setPriceLow(double priceLow) {
        this.priceLow = priceLow;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
    }

    public void setDEFAULT(int DEFAULT) {
        this.DEFAULT = DEFAULT;
    }
}
