# ChartDemo
### 股票K线图、分时图


![image]( https://github.com/limbowangqi/ChartDemo/blob/master/demo.gif)

# 实现原理
## 1.分时图
分时图一般有两条线，一条均价线，一条时价线。其实每条线都是由很多点组合而来，通过**canvas.drawLine**方法将每两个点进行连接，最终绘制出分时图。
## 2.K线图
K线图是由一个一个的矩形组合而来，每个矩形代表了一个时间段的相关价格，通过这些价格来确定当前时间段的矩形的位置，这样就绘制出K线图。关于K线图的一些基础知识，如阴线、阳线、高开低收，去百度学习下，这样开发起来会方便很多。
## 3.十字标
首先对View进行手势监听**GestureDetector**，当触发长按**onLongPress**方法时，记录当前手指的X坐标，通过当前X坐标获取当前时间的Y轴坐标，最终绘制出十字标。
## 4.缩放、滑动
分时图是没有缩放和滑动功能的，所以主要说说K线图。
K线图的数据点有很多，但是一般在屏幕上不会展示完，所以我们要确定屏幕可以展示的矩形个数，并且展示的数据源的start、end。 滑动和缩放其实只是不停重新绘制的过程，滑动是改变数据源的start、end，缩放则是改变矩形个数。

# 实现步骤
## 基类BaseChart
BaseChart中除了包含了一些公用的常量和方法外，主要是定义了绘制步骤。
 ```javascript
//1.初始化数据
initWidthAndHeight();
 //2.画网格
drawGrid(canvas);
//3.画线
drawLine(canvas);
//4.画XY轴上的值
drawXYText(canvas);
```
## 画图
 1. 在**initWidthAndHeight**中我们需要来获取当前数据源在Y轴的最大值和最小值**mMax**、**mMin**，来确定Y轴上面的刻度值，在分时图中还需要确定当日的开盘时间和收盘时间；
 2. 有了上面的那些数据，我们就可以愉快的在**drawGrid**中绘制表格；
 ```javascript
  	   mLinePaint.reset();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(color_grid);
        //X方向
        drawXGrid(canvas);
        //Y方向
        drawYGrid(canvas);
```
 3. **drawLine**则是画图的核心方法，循环遍历当前数据源的每条数据，将数据换算成图上的每一个点；
 ```javascript
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
```
 ```javascript
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
```
 4. 最后通过**drawXYText**绘制XY轴上的刻度值，显示在K线、分时线上面，避免被覆盖造成无法看清的问题；
 5. 最后的最后还需要加上十字标；
 ```javascript
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
 ```
# 总结
关于数据基类bean，注释已经写得很清楚了。
 ```javascript
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
 ```
 还有K线和分时线一起绘制的，在demo中没有使用。不过大家可以自己摸索实现下，毕竟整明白了还是挺简单的。
