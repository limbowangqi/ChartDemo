package com.limbo.chartdemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.limbo.chartdemo.R;
import com.limbo.chartdemo.utils.ChartHelp;
import com.limbo.chartdemo.utils.MyUtils;

import java.util.List;

/**
 * Created by wangqi on 2018/10/11.
 */

public abstract class BaseChart<T> extends View implements GestureDetector.OnGestureListener {
    //高度
    protected int chartHeight;
    //宽度
    protected int chartWidth;
    //最大值 最小值
    protected double mMax = Double.MIN_VALUE;
    protected double mMin = Double.MAX_VALUE;
    //Y轴左边的值
    protected List<String> YLeftValues;
    //Y轴右边的值  百分比
    protected List<String> YRightValues;
    //X轴上的值
    protected List<Long> XValues;

    //背景颜色
    protected int color_bg = getResources().getColor(R.color.color_white);
    //网格颜色
    protected int color_grid = getResources().getColor(R.color.color_f0f0f0);
    //十字标颜色
    protected int color_cross = getResources().getColor(R.color.color_76819f);
    //坐标轴 文本颜色
    protected int color_text_999999 = getResources().getColor(R.color.color_999999);
    protected int color_text_333333 = getResources().getColor(R.color.color_333333);

    protected int colorRed = getResources().getColor(R.color.color_ff4c4f);
    protected int colorGreen = getResources().getColor(R.color.color_1dbf69);
    // 文字大小
    protected float f12 = MyUtils.dp2px(12f);
    protected float f10 = MyUtils.dp2px(10f);

    //通用画笔
    protected Paint mLinePaint;

    protected Paint mTextPaint;
    //手势
    protected GestureDetector gestureDetector;
    //十字标 是否展示
    protected boolean isShowCross;
    //十字标 坐标
    protected float crossX, crossY;
    //十字线 宽度
    protected float crossLineWidth = 2.5f;
    //图表帮助类
    protected ChartHelp help;
    //总数据
    protected List<T> mData;
    //图表类型
    protected Type type;

    public enum Type {
        TYPE_K,
        TYPE_TS,
    }

    /**
     * 初始化需要的数据
     */
    protected abstract void init();

    /**
     * 画网格
     */
    protected abstract void drawGrid(Canvas canvas);

    /**
     * 画线
     */
    protected abstract void drawLine(Canvas canvas);

    /**
     * 画XY轴上的值
     */
    protected abstract void drawXYText(Canvas canvas);

    public BaseChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //关闭硬件加速，不然虚线显示为实线了
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        //图表帮助类
        help = ChartHelp.getInstance();
        //画笔
        mLinePaint = new Paint();
        mTextPaint = new Paint();
        //手势监听
        gestureDetector = new GestureDetector(getContext(), this);
        //设置背景
        setBackgroundColor(color_bg);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            if (mData == null)
            return;
            //1.初始化数据
            initWidthAndHeight();
            //2.画网格
            drawGrid(canvas);
            //3.画线
            drawLine(canvas);
            //4.画XY轴上的值
            drawXYText(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }

    private void initWidthAndHeight() {
        chartHeight = (int) (getMeasuredHeight() - f12) - getPaddingTop() - getPaddingBottom();
        chartWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        //初始化
        init();
    }

    /**
     * X方向
     *
     * @param canvas
     */
    protected void drawXGrid(Canvas canvas) {
        //横向坐标线
        if (YLeftValues == null || YLeftValues.size() == 0)
            return;

        int h = chartHeight / (YLeftValues.size() - 1);
        for (int i = 0; i < YLeftValues.size(); i++) {
            canvas.drawLine(0, h * i, chartWidth, h * i, mLinePaint);
        }
    }

    /**
     * Y方向
     *
     * @param canvas
     */
    protected void drawYGrid(Canvas canvas) {
        //2条纵向坐标线
        float[] floats = {
                0, 0, 0, chartHeight,
                chartWidth, 0, chartWidth, chartHeight
        };
        canvas.drawLines(floats, mLinePaint);
    }

    /**
     * 绘制X轴上的值
     *
     * @param canvas
     */
    protected void drawXValue(Canvas canvas) {
        if (XValues != null && XValues.size() == 2) {
            mTextPaint.reset();
            mTextPaint.setColor(color_text_999999);
            mTextPaint.setTextSize(f12);
            mTextPaint.setAntiAlias(true);
            canvas.drawText(MyUtils.formatTime(XValues.get(0), "HH:mm"), 0, chartHeight + f12, mTextPaint);
            canvas.drawText(MyUtils.formatTime(XValues.get(1), "HH:mm"),
                    chartWidth - mTextPaint.measureText(MyUtils.formatTime(XValues.get(1), "HH:mm")), chartHeight + f12, mTextPaint);
        }
    }

    /**
     * 绘制Y轴上的值
     *
     * @param canvas
     */
    protected void drawYValue(Canvas canvas) {
        if (YLeftValues == null || YLeftValues.size() == 0)
            return;

        mTextPaint.reset();
        mTextPaint.setTextSize(f12);
        mTextPaint.setAntiAlias(true);
        for (int i = 0; i < YLeftValues.size(); i++) {
            String text = YLeftValues.get(i);
            if (type == Type.TYPE_K) {
                mTextPaint.setColor(color_text_999999);
            } else if (type == Type.TYPE_TS) {
                switch (i) {
                    case 0:
                    case 1:
                        mTextPaint.setColor(colorRed);
                        break;
                    case 2:
                        mTextPaint.setColor(color_text_333333);
                        break;
                    case 3:
                    case 4:
                        mTextPaint.setColor(colorGreen);
                        break;
                }
            } else {
                return;
            }

            if (i == YLeftValues.size() - 1) {
                canvas.drawText(text, 0, chartHeight, mTextPaint);
            } else {
                canvas.drawText(text, 0, chartHeight / (YLeftValues.size() - 1) * i + f12, mTextPaint);
            }
        }

        //绘制百分比
        if (YRightValues == null || YRightValues.size() == 0)
            return;

        for (int i = 0; i < YRightValues.size(); i++) {
            String text = YRightValues.get(i);
            switch (i) {
                case 0:
                case 1:
                    mTextPaint.setColor(colorRed);
                    break;
                case 2:
                    mTextPaint.setColor(color_text_333333);
                    break;
                case 3:
                case 4:
                    mTextPaint.setColor(colorGreen);
                    break;
            }
            if (i == YRightValues.size() - 1) {
                canvas.drawText(text, chartWidth - mTextPaint.measureText(text), chartHeight, mTextPaint);
            } else {
                canvas.drawText(text, chartWidth - mTextPaint.measureText(text), chartHeight / (YLeftValues.size() - 1) * i + f12, mTextPaint);
            }
        }
    }

    protected float doubleToY(double f) {
        return (float) ((mMax - f) / (mMax - mMin) * chartHeight);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (isShowCross) {
            isShowCross = false;
            invalidate();
        }
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        if (!isShowCross) {
            isShowCross = true;
            crossX = e.getX();
            invalidate();
        }
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
////            Log.e("wangqi", "velocityX = " + velocityX);
//        int a = 7000;
//        if (Math.abs(velocityX) > 3000) {
//            //惯性 加速
//            float t = velocityX / a;
//            float s = velocityX * t - a * t * t / 2;
//
//        }
        return false;
    }

    //get set 相关
    public void setColorCross(int colorCross) {
        this.color_cross = colorCross;
    }

    public void setColorBg(int colorBg) {
        this.color_bg = colorBg;
    }

    public void setColorGrid(int colorGrid) {
        this.color_grid = colorGrid;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setYLeftValues(List<String> YLeftValues) {
        this.YLeftValues = YLeftValues;
    }

    public void setYRightValues(List<String> YRightValues) {
        this.YRightValues = YRightValues;
    }

    public void setMax(double mMax) {
        this.mMax = mMax;
    }

    public void setMin(double mMin) {
        this.mMin = mMin;
    }

    public void setXValues(List<Long> XValues) {
        this.XValues = XValues;
    }

    public double getmMax() {
        return mMax;
    }

    public double getmMin() {
        return mMin;
    }

    public List<String> getYLeftValues() {
        return YLeftValues;
    }

    public List<String> getYRightValues() {
        return YRightValues;
    }

    public List<Long> getXValues() {
        return XValues;
    }

    public List<T> getData() {
        return mData;
    }

    public void setData(List<T> mData) {
        this.mData = mData;
    }
}
