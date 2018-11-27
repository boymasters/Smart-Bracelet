package com.sanzitech.szitechwisebrave.chartview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.List;

/**
 * 图表控件
 * Created by lwh on 2015/9/21.
 */
public class ChartView extends View {
    private static final String TAG = "ChartView";
    private Context mContext;
    private int mDensity;
    //Scroller用于手势滚动和弹性
    private Scroller mScroller;
    //当前停留的位置
    private int mScreenIndex;

    //图表配置
    private ChartViewConfig chartViewConfig;
    //格子画笔
    private Paint mPaintGrid;
    private Path[] mPathGridHorizontal;
    private Path[] mPathGridVerical;
    //横线刻度线画笔
    private Paint mPaintHorizontalKedu;

    //竖向刻度画笔 字体大的
    private Paint mPaintLable;
    //竖向刻度画笔 字体小的
    private Paint mPaintLableSub;
    //竖向刻度单位画笔
    private Paint mPaintLableUnit;
    //竖向刻度线画笔
    private Paint mPaintVericalKedu;

    //竖向刻度额外填充x值
    private int verical_unit_extral_x_space;

    //横向向刻度单位画笔 字体大的
    private Paint mPaintHorizontalLable;
    //横向向刻度单位画笔 字体小的
    private Paint mPaintHorizontalLableSub;

    //线的path
    private Path[] mPathSet;
    //画点线与X轴的区域
    private Path[] mPathSetRegion;
    private Path mPath;
    //线的画笔
    private Paint mPaintPath;
    private Path mPathRegion;
    //画电线连接与X轴形成的区域Path
    private Path mPathConnectRegion;
    private Paint mPaintPathConnectRegion;
    private List<PointValue> listRegionTemp = new ArrayList<>();
    //线的画笔
    private Paint mPaintPathRegion;
    //点的内圆和外圆 画笔
    private Paint mPaintCircle, mPaintCircleOutSide;
    //游标画笔
    private Paint mPaintIndicator;
    private Paint mPaintIndicatorOutside;
    //游标标题画笔
    private Paint mPaintIndicatorTitle;
    private Paint mPaintIndicatorSubTitle;
    private Paint mPaintIndicatorTitleUnit;
    //游标画笔上部分连线
    private Paint mPaintIndicatorLineTop;
    //游标画笔下部分连线
    private Paint mPaintIndicatorLineBottom;
    //线性渐变画笔
    private Paint mPaintLinearGradient;
    private GradientDrawable mGradientDrawableLeft;
    private GradientDrawable mGradientDrawableRight;

    //手势参数
    private VelocityTracker mVelocityTracker = null;
    private static final int TOUCH_STATE_REST = 0;
    public static int SNAP_VELOCITY = 600;
    private float mLastionMotionX = 0;
    private float mLastionMotionY = 0;
    private int mLastScrollX = 0;
    protected boolean mIsPressd = false;
    //游标资源
    private Bitmap mBitmapIndicator;
    //游标在随线滚动的时候 与线的距离
    private int mIndicatorExtraSpace = 100;
    //当前点的索引
    private int mCurrentIndex = 0;
    //游标圆圈的范围，用于判断点击区域
    private float mIndicatorX;
    private float mIndicatroY;
    //游标字体大小
    private float mIndicatorTitleTextSize = 20;
    private float mIndicatorTitleSubTextSize = 18;
    private float mIndicatorUnitTextSize = 20;
    //    private float mIndicatorTitleTextSize=14;
//    private float mIndicatorTitleTextSize=14;
    //是否计算值
    private boolean mIsCaculateValue = true;

    //滚动监听
    private OnChartViewChangeListener mListener;

    public void setOnChartViewChangeListener(OnChartViewChangeListener listener) {
        mListener = listener;
    }

    public ChartView(Context context) {
        super(context);
        mContext = context;
    }

    public ChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public ChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {

    }

    /**
     * 初始化
     *
     * @param config
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void init(ChartViewConfig config) {

        mDensity = (int) getResources().getDisplayMetrics().density;

        mScroller = new Scroller(mContext);
        //global config
        chartViewConfig = config;
        //格子线和颜色
        mPaintGrid = new Paint();
        //横线刻度线画笔
        mPaintHorizontalKedu = new Paint();

        verical_unit_extral_x_space = 20;
        //竖向刻度线画笔
        mPaintVericalKedu = new Paint();
        //竖向大刻度文本画笔
        mPaintLable = new Paint();
        if (config.getVerical_unit_lable_color() > 0)
            mPaintLable.setColor(getResources().getColor(config.getVerical_unit_lable_color()));
        mPaintLable.setTextSize(12 * mDensity);
        mPaintLable.setAntiAlias(true);
        //竖向小刻度文本画笔
        mPaintLableSub = new Paint();
        if (config.getVerical_unit_lable_sub_color() > 0)
            mPaintLableSub.setColor(getResources().getColor(config.getVerical_unit_lable_sub_color()));
        mPaintLableSub.setTextSize(10 * mDensity);
        mPaintLableSub.setAntiAlias(true);
        //竖向单位文本画笔
        mPaintLableUnit = new Paint();
        if (config.getVerical_unit_color() > 0)
            mPaintLableUnit.setColor(getResources().getColor(config.getVerical_unit_color()));
        mPaintLableUnit.setTextSize(15 * mDensity);
        mPaintLableUnit.setAntiAlias(true);
        //横向向刻度单位画笔 字体大的
        mPaintHorizontalLable = new Paint();
        if (config.getVerical_unit_lable_color() > 0)
            mPaintHorizontalLable.setColor(getResources().getColor(config.getVerical_unit_lable_color()));
        mPaintHorizontalLable.setTextSize(14 * mDensity);
        mPaintHorizontalLable.setAntiAlias(true);
        //横向向刻度单位画笔 字体小的
        mPaintHorizontalLableSub = new Paint();
        if (config.getVerical_unit_lable_sub_color() > 0)
            mPaintHorizontalLableSub.setColor(getResources().getColor(config.getVerical_unit_lable_sub_color()));
        mPaintHorizontalLableSub.setTextSize(12 * mDensity);
        mPaintHorizontalLableSub.setAntiAlias(true);

        //渐变画笔
        mPaintLinearGradient = new Paint();
        mPaintLinearGradient.setStyle(Paint.Style.FILL);
        mPaintLinearGradient.setAntiAlias(true);
        mGradientDrawableLeft = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, config.getGridViewGradientColorLeft());
        mGradientDrawableRight = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, config.getGridViewGradientColorRight());

        //画线
        mPaintPath = new Paint();
        mPaintPath.setStyle(Paint.Style.STROKE);
        mPaintPath.setStrokeWidth(mDensity * 1.5f);
        if (config.getPath_line_color() > 0)
            mPaintPath.setColor(getResources().getColor(config.getPath_line_color()));
        mPaintPath.setAntiAlias(true);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO)
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        //区域画笔
        mPaintPathRegion = new Paint();
        mPaintPathRegion.setStyle(Paint.Style.FILL);
        //mPaintPathRegion.setStrokeWidth(mDensity * 1.5f);
        mPaintPathRegion.setAntiAlias(true);
        if (chartViewConfig.getRegion_color() > 0) {
            mPaintPathRegion.setColor(getResources().getColor(config.getRegion_color()));
        }
        mPaintPathRegion.setAlpha(120);

        mPaintPathConnectRegion = new Paint();
        mPaintPathConnectRegion.setStyle(Paint.Style.FILL);
        mPaintPathConnectRegion.setAntiAlias(true);
        if (chartViewConfig.getRegion_connect_color() > 0) {
            mPaintPathConnectRegion.setColor(getResources().getColor(config.getRegion_connect_color()));
        }
        mPaintPathConnectRegion.setAlpha(80);

        //点的内圆
        mPaintCircle = new Paint();
        mPaintCircle.setStyle(Paint.Style.FILL);
        mPaintCircle.setStrokeWidth(mDensity * 1f);
        if (config.getPoint_circle_color_interval() > 0)
            mPaintCircle.setColor(getResources().getColor(config.getPoint_circle_color_interval()));
//        mPaintCircle.setTextSize(4 * mDensity);
        mPaintCircle.setAntiAlias(true);
        mPaintCircle.setAlpha(0);

        //点的外圆
        mPaintCircleOutSide = new Paint();
        mPaintCircleOutSide.setStyle(Paint.Style.FILL);
        mPaintCircleOutSide.setStrokeWidth(mDensity * 2f);
        if (config.getPoint_circle_color_outside() > 0)
            mPaintCircleOutSide.setColor(getResources().getColor(config.getPoint_circle_color_outside()));
        mPaintCircleOutSide.setAntiAlias(true);
        mPaintCircleOutSide.setAlpha(0);

        //游标圆的画笔
        mPaintIndicator = new Paint();
        mPaintIndicator.setStyle(Paint.Style.FILL);
        mPaintIndicator.setStrokeWidth(mDensity * 2.0f);
        if (config.getIndicatorLinecolor() > 0)
            mPaintIndicator.setColor(getResources().getColor(config.getIndicatorLinecolor()));
        mPaintIndicator.setAntiAlias(true);
        mPaintIndicator.setAlpha(0);

        mPaintIndicatorOutside = new Paint();
        mPaintIndicatorOutside.setStyle(Paint.Style.FILL);
        mPaintIndicatorOutside.setStrokeWidth(mDensity * 2.0f);
        if (config.getIndicator_outside_circle_color() > 0)
            mPaintIndicatorOutside.setColor(getResources().getColor(config.getIndicator_outside_circle_color()));
        mPaintIndicatorOutside.setAntiAlias(true);
        mPaintIndicatorOutside.setAlpha(0);

        //游标连线
        mPaintIndicatorLineTop = new Paint();
        mPaintIndicatorLineTop.setStyle(Paint.Style.FILL);
        mPaintIndicatorLineTop.setStrokeWidth(mDensity * 1.0f);
        if (config.getIndicatorLinecolor() > 0)
            mPaintIndicatorLineTop.setColor(getResources().getColor(config.getIndicatorLinecolor()));
        mPaintIndicatorLineTop.setAntiAlias(true);
        //游标连线
        mPaintIndicatorLineBottom = new Paint();
        mPaintIndicatorLineBottom.setStyle(Paint.Style.FILL);
        mPaintIndicatorLineBottom.setStrokeWidth(mDensity * 1.0f);
        if (config.getIndicatorLinecolor() > 0)
            mPaintIndicatorLineBottom.setColor(getResources().getColor(config.getIndicatorLinecolor()));
        mPaintIndicatorLineBottom.setAntiAlias(true);
        mPaintIndicatorLineBottom.setAlpha(0);
        //游标标题
        mPaintIndicatorTitle = new Paint();
        if (config.getIndicator_title_TextSize() > 0) {
            mPaintIndicatorTitle.setTextSize(config.getIndicator_title_TextSize() * mDensity);
        } else {
            mPaintIndicatorTitle.setTextSize(mIndicatorTitleTextSize * mDensity);
        }
        if (config.getIndicator_title_color() > 0)
            mPaintIndicatorTitle.setColor(getResources().getColor(chartViewConfig.getIndicator_title_color()));
        mPaintIndicatorTitle.setAlpha(0);
        //游标标题单位
        mPaintIndicatorTitleUnit = new Paint();
        mPaintIndicatorTitleUnit.setTextSize(mIndicatorUnitTextSize * mDensity);
        if (config.getIndicator_title_color() > 0)
            mPaintIndicatorTitleUnit.setColor(getResources().getColor(chartViewConfig.getIndicator_title_color()));
        mPaintIndicatorTitleUnit.setAlpha(0);
        //游标子标题
        mPaintIndicatorSubTitle = new Paint();
        mPaintIndicatorSubTitle.setTextSize(mIndicatorTitleSubTextSize * mDensity);
        if (config.getIndicator_title_color() > 0)
            mPaintIndicatorSubTitle.setColor(getResources().getColor(chartViewConfig.getIndicator_title_color()));
        mPaintIndicatorSubTitle.setAlpha(0);
        update();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //画格子
        drawGrid(canvas);
        //画横向刻度和单位
        drawHorizontalUnit(canvas);
        //计算点线坐标
        caculatePointValue();
        //点线区域是否需要闭合渲染
        drawFillPointConnectRegion(canvas);
        //画区域
        drawPointRegion(canvas);
        //画点和线
        drawPointAndPath(canvas);
        //画线性渐变
        drawLinearGradient(canvas);
        //画顶部游标
        drawIndicator(canvas);

        //画竖向刻度和单位,放在最后是为了避免画区域部分覆盖了这个刻度
        drawVericalUnit(canvas);
        //选中初始位置
        setSelection();
        //当前x刻度
        mScreenIndex = getScrollX() / chartViewConfig.getItem_width();

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void drawLinearGradient(Canvas canvas) {
        if (!chartViewConfig.isShowGridViewGradient())
            return;
        //第一个渐变
        int left = getScrollX();
        int top = 0;
        int right = getScrollX() + 20 * mDensity;
        int bottom = chartViewConfig.getRow() * chartViewConfig.getItem_height();
        mGradientDrawableLeft.setBounds(left, top, right, bottom);
        mGradientDrawableLeft.draw(canvas);


        //第二个渐变
        left = getScrollX() + getWidth() - 20 * mDensity;
        top = 0;
        right = getScrollX() + getWidth();
        bottom = chartViewConfig.getRow() * chartViewConfig.getItem_height();
        mGradientDrawableRight.setBounds(left, top, right, bottom);
        mGradientDrawableRight.draw(canvas);

    }

    /**
     * 画指示器
     *
     * @param canvas
     */
    protected void drawIndicator(Canvas canvas) {
        if (chartViewConfig.getListPoint() == null || chartViewConfig.getListPoint().size() == 0)
            return;
        if (!chartViewConfig.isShowIndicator())
            return;
        //indicator坐标
        //x轴中间值
        int indicator_x = getWidth() / 2 + getScrollX();
        //游标默认y轴位置
        int indicator_y = chartViewConfig.getIndicator_radius() * 3 / 2;
        //游标半径
        //------计算上部分连线的Y方式的结束值
        int radius = 50;
        if (chartViewConfig.getIndicatorBgRes() > 0) {
            if (mBitmapIndicator == null) {
                mBitmapIndicator = BitmapFactory.decodeResource(getResources(), chartViewConfig.getIndicatorBgRes());
            }
            if (mBitmapIndicator != null)
                radius = mBitmapIndicator.getWidth() / 2;
        } else {
            radius = chartViewConfig.getIndicator_radius();
        }
        //上部分连接线坐标
        //上部分连线x开始和结束
        float line_top_x_start = indicator_x;
        float line_top_x_end = indicator_x;
        //上部分连线y 默认开始值
        float line_top_y_start = indicator_y + radius;
        //上部分连线y 默认结束值
        float line_top_y_end = indicator_y + radius;

        //当前点位置
        int index = 0;
        boolean isAtPoint = false;
        for (int i = chartViewConfig.getListPoint().size() - 1; i >= 0; i--) {
            Log.d(TAG, "-->i:" + i + "--point x:" + chartViewConfig.getListPoint().get(i).x + "-->indicator_x:" + indicator_x);
            float x = chartViewConfig.getListPoint().get(i).x;
            if (indicator_x + 5 >= (int) x) {
                index = i;
                if (indicator_x + 5 >= (int) x && (int) x > indicator_x - 5) {
                    isAtPoint = true;
                } else {
                    isAtPoint = false;
                }
                break;
            } else {
                isAtPoint = false;
            }
        }
        //------ 计算上部分连线的Y方式的结束值 start
        //计算左右边界x值，防止画超出范围
        getMinAndManScrollerValue();
        //超过最小，取最小
        if (getScrollX() <= minX) {
            line_top_y_end = chartViewConfig.getListPoint().get(0).y;
            //超过最大，取最大
        } else if (getScrollX() >= maxX) {
            line_top_y_end = chartViewConfig.getListPoint().get(chartViewConfig.getListPoint().size() - 1).y;
            //中间按比例取值
        } else {
            //防止越界
            if (index + 1 < chartViewConfig.getListPoint().size()) {
                //与下一个点的x和y方向的差值
                int width_x = (int) (chartViewConfig.getListPoint().get(index + 1).x - chartViewConfig.getListPoint().get(index).x);
                int width_y = (int) (chartViewConfig.getListPoint().get(index + 1).y - chartViewConfig.getListPoint().get(index).y);
                //计算出比例
                int cha_x = indicator_x - (int) chartViewConfig.getListPoint().get(index).x;
                float progress = cha_x / (width_x * 1.0f);
                float cha_y = progress * width_y;
                //最终值
                line_top_y_end = chartViewConfig.getListPoint().get(index).y + cha_y;
                Log.d(TAG, "progress:" + progress + "-->cha_y:" + cha_y + "--chax:" + cha_x + "-->line_top_y_end:" + line_top_y_end + "-->index:" + index);
            } else {
                line_top_y_end = chartViewConfig.getListPoint().get(chartViewConfig.getListPoint().size() - 1).y;
            }
        }
        //------ 计算上部分连线的Y方式的结束值 end

        //绘制上半部分的连线
//        if (!chartViewConfig.isIndicatorMoveWithPoint()) {
//            canvas.drawLine(line_top_x_start, line_top_y_start, line_top_x_end, line_top_y_end, mPaintIndicatorLineTop);
//        } else {
//            line_top_y_start = line_top_y_end - radius - mIndicatorExtraSpace;
//            canvas.drawLine(line_top_x_start, line_top_y_start, line_top_x_end, line_top_y_end, mPaintIndicatorLineTop);
//        }

        //绘制游标,自定义游标背景
        if (chartViewConfig.getIndicatorBgRes() > 0) {
            float left = indicator_x - radius;
            if (mBitmapIndicator != null) {
                if (!chartViewConfig.isIndicatorMoveWithPoint()) {
                    float top = line_top_y_start - radius;
                    mIndicatorX = left;
                    mIndicatroY = top;
                    canvas.drawBitmap(mBitmapIndicator, left, top, mPaintIndicator);
                } else {
                    float top = line_top_y_start - radius;
                    mIndicatorX = left;
                    mIndicatroY = top;
                    canvas.drawBitmap(mBitmapIndicator, left, top, mPaintIndicator);
                }
            }
            //默认游标，圆形
        } else {
            //外圆
            if (!isAtPoint) {
                if (!chartViewConfig.isIndicatorMoveWithPoint()) {
                    if (chartViewConfig.getIndicator_outside_circle_color() > 0) {
                        canvas.drawCircle(indicator_x, line_top_y_start - radius, radius + 6 * mDensity, mPaintIndicatorOutside);
                    }
                } else {
                    if (chartViewConfig.getIndicator_outside_circle_color() > 0) {
                        canvas.drawCircle(indicator_x, line_top_y_start, radius + 6 * mDensity, mPaintIndicatorOutside);
                    }
                }
            }
            //内圆

            if (!chartViewConfig.isIndicatorMoveWithPoint()) {
                line_top_y_start -= radius;
                mIndicatorX = indicator_x - radius;
                mIndicatroY = line_top_y_start - radius;
                canvas.drawCircle(indicator_x, line_top_y_start, radius, mPaintIndicator);
            } else {
                mIndicatorX = indicator_x - radius;
                mIndicatroY = line_top_y_start - radius;
                canvas.drawCircle(indicator_x, line_top_y_start, radius, mPaintIndicator);
            }

        }


        //下部分连线坐标
        int line_bottom_x_end = indicator_x;
        int line_bottom_y_end = chartViewConfig.getRow() * chartViewConfig.getItem_height();
        canvas.drawLine(line_top_x_end, line_top_y_end, line_bottom_x_end, line_bottom_y_end, mPaintIndicatorLineBottom);

        //绘制上半部分连线和下半部分连线的小圆圈,是节点就大圆，非节点就小圆
        //canvas.drawCircle(line_top_x_end, line_top_y_end,mDensity*3, mPaintIndicatorLineTop);
        if (isAtPoint) {
            canvas.drawCircle(line_top_x_end, line_top_y_end, mDensity * 4, mPaintIndicatorLineTop);
        } else {
            canvas.drawCircle(line_top_x_end, line_top_y_end, mDensity * 2, mPaintIndicatorLineTop);
        }

        //绘制游标标题
        String tilte = "无";
        if (index < chartViewConfig.getListPoint().size() && index >= 0) {
            tilte = chartViewConfig.getListPoint().get(index).title;
        }
        if (TextUtils.isEmpty(tilte)) {
            tilte = "无";
        }
        Rect rectTitle = new Rect();
        mPaintIndicatorTitle.getTextBounds(tilte, 0, tilte.length(), rectTitle);
        String title_unit = chartViewConfig.getIndicator_title_unit();
        Rect rectTitleUnit = new Rect();
        mPaintIndicatorTitleUnit.getTextBounds(title_unit, 0, title_unit.length(), rectTitleUnit);

        //绘制游标标题
        //canvas.drawText(tilte, indicator_x - (rectTitle.width() / 2), line_top_y_start, mPaintIndicatorTitle);
        canvas.drawText(tilte, indicator_x - ((rectTitle.width() + rectTitleUnit.width() + 2 * mDensity) / 2), line_top_y_start, mPaintIndicatorTitle);
        //绘制游标标题单位
        canvas.drawText(title_unit, indicator_x - ((rectTitle.width() + rectTitleUnit.width() + 2 * mDensity) / 2) + rectTitle.width() + 2 * mDensity, line_top_y_start, mPaintIndicatorTitleUnit);

        //绘制游标子标题
        String tilteSub = "";
        if (index < chartViewConfig.getListPoint().size() && index >= 0) {
            tilteSub = chartViewConfig.getListPoint().get(index).title_sub;
        }
        if (TextUtils.isEmpty(tilteSub)) {
            tilteSub = "";
        }
        Rect rectTitleSub = new Rect();
        mPaintIndicatorSubTitle.getTextBounds(tilteSub, 0, tilteSub.length(), rectTitleSub);
        canvas.drawText(tilteSub, indicator_x - (rectTitleSub.width() / 2), line_top_y_start + rectTitle.height() + 3 * mDensity, mPaintIndicatorSubTitle);

    }

    /**
     * 画格子
     *
     * @param canvas
     */
    protected void drawGrid(Canvas canvas) {
        if (chartViewConfig.isShowGridLine()) {
            if (chartViewConfig.getGrid_line_color() > 0)
                mPaintGrid.setColor(getResources().getColor(chartViewConfig.getGrid_line_color()));
            mPaintGrid.setStrokeWidth(mDensity * 0.5f);
            //画横线 ,左边预留getCloumn/2个网格
            int len = 16;
            len = chartViewConfig.getCloumn() * 2 + chartViewConfig.getListHorizontalKedu().size() - 1;
            int duan = 10;//为了避免虚线过长导致path绘制不了，每条虚线分为10段
            if (chartViewConfig.isShowGridHorizontalLine()) {
                //虚线
                if (chartViewConfig.isGridLinePathEffect()) {
                    if (mPathGridHorizontal == null) {
                        PathEffect effects = new DashPathEffect(new float[]{5, 5, 5, 5}, 1);
                        mPaintGrid.setAntiAlias(true);
                        mPaintGrid.setStyle(Paint.Style.STROKE);
                        mPaintGrid.setPathEffect(effects);
                        mPathGridHorizontal = new Path[chartViewConfig.getRow() * duan];
                        /*for (int i = 0; i < chartViewConfig.getRow(); i++) {
                            float startX = getScrollX()-chartViewConfig.getItem_width() * chartViewConfig.getCloumn();
                            float startY = i * chartViewConfig.getItem_height();
                            float stopX =  getScrollX()+ (chartViewConfig.getListHorizontalKedu().size()+chartViewConfig.getCloumn())* chartViewConfig.getItem_width();
                            float stopY = i * chartViewConfig.getItem_height();
                            Path path = new Path();
                            path.moveTo(startX, startY);
                            path.lineTo(stopX, stopY);
                            mPathGridHorizontal[i]=path;
                        }*/
                        int index = 0;
                        for (int i = 0; i < chartViewConfig.getRow(); i++) {
                            //每一条的开始和结束X值
                            float start_X = getScrollX() - chartViewConfig.getItem_width() * chartViewConfig.getCloumn();
                            float stop_X = getScrollX() + (chartViewConfig.getListHorizontalKedu().size() + chartViewConfig.getCloumn()) * chartViewConfig.getItem_width();
                            //分段的间隔
                            float jiange = (stop_X - start_X) / duan;
                            //每一条分段的x开始和x结束
                            for (int j = 0; j < duan; j++) {
                                Path path = new Path();
                                float startX = start_X + j * jiange;
                                float startY = i * chartViewConfig.getItem_height();
                                float stopX = start_X + (j + 1) * jiange;//getScrollX()+ (chartViewConfig.getListHorizontalKedu().size()+chartViewConfig.getCloumn())* chartViewConfig.getItem_width();
                                float stopY = i * chartViewConfig.getItem_height();
                                path.moveTo(startX, startY);
                                path.lineTo(stopX, stopY);
                                mPathGridHorizontal[index + j] = path;
                            }
                            index = (index + duan);
                        }

                    }
                    final int count = (int) (chartViewConfig.getVerical_unit_end() - chartViewConfig.getVerical_unit_start()) / (int) chartViewConfig.getVerical_unit_incremetal();
                    int start = chartViewConfig.getRow() - count;
                    if (start >= 0 && start <= chartViewConfig.getRow() - 1) {
                    } else {
                        start = 0;
                    }
                    for (int i = start; i < chartViewConfig.getRow() * duan; i++) {
                        if (getScrollX() >= (mScreenIndex - chartViewConfig.getCloumn() / 2) * chartViewConfig.getItem_width()
                                && getScrollX() <= (mScreenIndex + chartViewConfig.getCloumn() * 2) * chartViewConfig.getItem_width()) {
                            canvas.drawPath(mPathGridHorizontal[i], mPaintGrid);
                        }
                    }
                    //实线
                } else {
                    for (int i = 0; i < chartViewConfig.getRow(); i++) {
                        float startX = -chartViewConfig.getItem_width() * (chartViewConfig.getCloumn() / 2);
                        float startY = i * chartViewConfig.getItem_height();
                        float stopX = (len - chartViewConfig.getCloumn() / 2) * chartViewConfig.getItem_width();//(chartViewConfig.getListHorizontalKeduValue().size() +chartViewConfig.getCloumn())* chartViewConfig.getItem_width();
                        float stopY = i * chartViewConfig.getItem_height();
                        if (getScrollX() >= (mScreenIndex - chartViewConfig.getCloumn()) * chartViewConfig.getItem_width()
                                && getScrollX() <= (mScreenIndex + chartViewConfig.getCloumn() * 2) * chartViewConfig.getItem_width())
                            canvas.drawLine(startX, startY, stopX, stopY, mPaintGrid);
                    }
                }
            }
            //画竖线
            if (chartViewConfig.isShowGridVericalLine()) {
                //虚线
                if (chartViewConfig.isGridLinePathEffect()) {
                    if (mPathGridVerical == null) {
                        PathEffect effects = new DashPathEffect(new float[]{5, 5, 5, 5}, 1);
                        mPaintGrid.setAntiAlias(true);
                        mPaintGrid.setStyle(Paint.Style.STROKE);
                        mPaintGrid.setPathEffect(effects);
                        mPathGridVerical = new Path[len + 1];
                        for (int i = 0; i < len + 1; i++) {
                            float startX = (i - chartViewConfig.getCloumn() / 2) * chartViewConfig.getItem_width();
                            float startY = 0;
                            float stopX = (i - chartViewConfig.getCloumn() / 2) * chartViewConfig.getItem_width();
                            float stopY = chartViewConfig.getRow() * chartViewConfig.getItem_height();
                            Path path = new Path();
                            path.moveTo(startX, startY);
                            path.lineTo(stopX, stopY);
                            mPathGridVerical[i] = path;
                            //canvas.drawLine(startX, startY, stopX, stopY, mPaintGrid);
                        }
                    }
                    for (int i = 0; i < len + 1; i++) {
                        if (getScrollX() >= (mScreenIndex - chartViewConfig.getCloumn()) * chartViewConfig.getItem_width()
                                && getScrollX() <= (mScreenIndex + chartViewConfig.getCloumn() * 2) * chartViewConfig.getItem_width())
                            canvas.drawPath(mPathGridVerical[i], mPaintGrid);
                    }
                    //实线
                } else {
                    for (int i = 0; i < len + 1; i++) {
                        float startX = (i - chartViewConfig.getCloumn() / 2) * chartViewConfig.getItem_width();
                        float startY = 0;
                        float stopX = (i - chartViewConfig.getCloumn() / 2) * chartViewConfig.getItem_width();
                        float stopY = chartViewConfig.getRow() * chartViewConfig.getItem_height();
                        if (getScrollX() >= (mScreenIndex - chartViewConfig.getCloumn()) * chartViewConfig.getItem_width() && getScrollX() <= (mScreenIndex + chartViewConfig.getCloumn() * 2) * chartViewConfig.getItem_width())
                            canvas.drawLine(startX, startY, stopX, stopY, mPaintGrid);
                    }
                }
            }
        }
    }

    /**
     * 绘制竖向刻度 和单位
     * Draw unit & incremental lables
     *
     * @param canvas
     */
    protected void drawVericalUnit(Canvas canvas) {

        //画lable line 竖线
        if (chartViewConfig.isVerical_line_show()) {
            if (chartViewConfig.getGrid_line_kedu_color() > 0)
                mPaintVericalKedu.setColor(getResources().getColor(chartViewConfig.getGrid_line_kedu_color()));
            mPaintVericalKedu.setStrokeWidth(mDensity);
            int bottomY = chartViewConfig.getRow() * chartViewConfig.getItem_height();
            float x_ = getScrollX() + chartViewConfig.getVerical_kedu_leftmargin();
            canvas.drawLine(x_, 0, x_, bottomY, mPaintVericalKedu);
        }

        //每五个格子为一个大刻度
        int levelCount = 5;
        //刻度文案
        String unit_text = "";
        //格子数目
        final int count = (int) (chartViewConfig.getVerical_unit_end() - chartViewConfig.getVerical_unit_start()) / (int) chartViewConfig.getVerical_unit_incremetal();

        //从下往上画
        for (int i = 0; i < count + 1; i++) {
            //value
            float big_value = chartViewConfig.getVerical_unit_start() + i * chartViewConfig.getVerical_unit_incremetal();
            float value = i * chartViewConfig.getVerical_unit_incremetal();
            //取mode
            if (chartViewConfig.getVerical_unit_incremetal() < 1) {
                value = value % 1;
            } else if (chartViewConfig.getVerical_unit_incremetal() < 10) {
                value = value % 10;
            } else if (chartViewConfig.getVerical_unit_incremetal() < 100) {
                value = value % 100;
            }
            Rect rect = new Rect();
            //需要分大小刻度
            if (chartViewConfig.isVerical_need_to_fragment()) {
                //绘制大刻度
                if (i % levelCount == 0) {
                    //skip first one
                    if (i == 0)
                        continue;
                    if (chartViewConfig.verical_lable_use_integer) {
                        unit_text = String.valueOf((int) big_value);
                    } else if (chartViewConfig.verical_lable_use_integer) {
                        unit_text = String.valueOf(big_value);
                    } else {
                        unit_text = String.valueOf(big_value);
                    }
                    mPaintLable.getTextBounds(unit_text, 0, unit_text.length(), rect);
                    float x = getScrollX() + chartViewConfig.getVerical_kedu_leftmargin() - rect.width() - verical_unit_extral_x_space;
                    float y = (chartViewConfig.getRow() - i) * chartViewConfig.getItem_height();
                    canvas.drawText(unit_text, x, y, mPaintLable);
                    //绘制小刻度
                } else {
                    //skip first value
                    if (value == 0) {
                        continue;
                    } else {
                        if (chartViewConfig.verical_lable_use_integer) {
                            unit_text = String.valueOf((int) value);
                        } else {
                            unit_text = String.valueOf(value);
                        }
                    }
                    mPaintLableSub.getTextBounds(unit_text, 0, unit_text.length(), rect);
                    //draw text
                    float x = getScrollX() + chartViewConfig.getVerical_kedu_leftmargin() - rect.width() - verical_unit_extral_x_space;
                    //1 的宽度太小，造成不协调,在此特殊处理
                    if (unit_text.equals("1")) {
                        x -= (2 * mDensity);
                    }
                    float y = (chartViewConfig.getRow() - i) * chartViewConfig.getItem_height();
                    canvas.drawText(unit_text, x, y, mPaintLableSub);
                }
            } else {
                if (i == 0)
                    continue;
                if (chartViewConfig.verical_lable_use_integer) {
                    unit_text = String.valueOf((int) big_value);
                } else if (chartViewConfig.verical_lable_use_integer) {
                    unit_text = String.valueOf(big_value);
                } else {
                    unit_text = String.valueOf(big_value);
                }
                mPaintLable.getTextBounds(unit_text, 0, unit_text.length(), rect);
                float x = getScrollX() + chartViewConfig.getVerical_kedu_leftmargin() - rect.width() - verical_unit_extral_x_space;
                float y = (chartViewConfig.getRow() - i) * chartViewConfig.getItem_height() + rect.height() / 2;
                canvas.drawText(unit_text, x, y, mPaintLable);

                //是否需要刻度线
                if (chartViewConfig.isVerical_kedu_line_show()) {
                    float startX = getScrollX() + chartViewConfig.getVerical_kedu_leftmargin();
                    float stopX = startX + 15;
                    float startY = (chartViewConfig.getRow() - i) * chartViewConfig.getItem_height();
                    float stopY = (chartViewConfig.getRow() - i) * chartViewConfig.getItem_height();
                    canvas.drawLine(startX, startY, stopX, stopY, mPaintVericalKedu);
                }

            }
        }

        //绘制刻度单位
        Rect rectUnit = new Rect();
        unit_text = chartViewConfig.getVerical_unit_text();
        mPaintLableUnit.getTextBounds(unit_text, 0, unit_text.length(), rectUnit);
        float x = getScrollX() + chartViewConfig.getVerical_kedu_leftmargin() - rectUnit.width() - verical_unit_extral_x_space;
        float y = (chartViewConfig.getRow() - count) * chartViewConfig.getItem_height() - verical_unit_extral_x_space * 2;
        canvas.drawText(unit_text, x, y, mPaintLableUnit);
    }


    /**
     * 绘制横向刻度和单位,以及横刻度的值
     *
     * @param canvas
     */
    private void drawHorizontalUnit(Canvas canvas) {
        //刻度文案
        String unit_text = "";
        //格子数目
        final int count = chartViewConfig.getListHorizontalKedu().size();
        //文案y轴底部
        int bottomY_y = chartViewConfig.getRow() * chartViewConfig.getItem_height();
        //int bottomY = bottomY_y + 40;
        for (int i = 0; i < count; i++) {
            if (i >= mScreenIndex - chartViewConfig.getCloumn() / 2 && i <= mScreenIndex + chartViewConfig.getCloumn()) {
                KeduValue model = chartViewConfig.getListHorizontalKedu().get(i);
                unit_text = model.display_value;
                float kedu_x = (i + chartViewConfig.getCloumn() / 2) * chartViewConfig.getItem_width();
                if (!TextUtils.isEmpty(unit_text)) {
                    Rect rect = new Rect();
                    mPaintHorizontalLable.getTextBounds(unit_text, 0, unit_text.length(), rect);
                    float x = kedu_x - rect.width() / 2;//- getScrollX();
                    model.current_x = x + rect.width() / 2;
                    canvas.drawText(unit_text, x, bottomY_y + rect.height() + 5 * mDensity, mPaintHorizontalLable);
                }
                //显示水平刻度线
                if (chartViewConfig.isHorizontal_kedu_line_show()) {
                    canvas.drawLine(kedu_x, bottomY_y, kedu_x, bottomY_y - 5 * mDensity, mPaintHorizontalKedu);
                }
                unit_text = model.value_unit;
                if (!TextUtils.isEmpty(unit_text)) {
                    Rect rect = new Rect();
                    mPaintHorizontalLableSub.getTextBounds(unit_text, 0, unit_text.length(), rect);
                    float x = (i + chartViewConfig.getCloumn() / 2) * chartViewConfig.getItem_width() - rect.width() / 2;//- getScrollX();
                    canvas.drawText(unit_text, x, bottomY_y + rect.height() + 5 * mDensity, mPaintHorizontalLableSub);
                }
            }
        }

        if (chartViewConfig.getGrid_line_kedu_color() > 0)
            mPaintHorizontalKedu.setColor(getResources().getColor(chartViewConfig.getGrid_line_kedu_color()));
        mPaintHorizontalKedu.setStrokeWidth(mDensity);
        bottomY_y = chartViewConfig.getRow() * chartViewConfig.getItem_height();
        //画lable line 横线
        canvas.drawLine(getScrollX(), bottomY_y, getScrollX() + getWidth(), bottomY_y, mPaintHorizontalKedu);

    }

    /**
     * 绘制点和线
     *
     * @param canvas
     */
    protected void drawPointAndPath(Canvas canvas) {
        if (chartViewConfig.getListPoint() == null || chartViewConfig.getListPoint().size() == 0)
            return;
        if (chartViewConfig.getListPoint().size() >= 4 && chartViewConfig.isSmoothPoint()) {
            if (mPathSet == null) {
                int size = chartViewConfig.getListPoint().size();
                PointValue[] mBezierControls = new PointValue[4];
                mPathSet = new Path[size];
                for (int i = 0; i < size; i++) {
                    Path mPathTrends = new Path();
                    //构造控制点的四个点；
                    PointValue l = null;
                    PointValue a = chartViewConfig.getListPoint().get(i);
                    int nextIndex = i + 1 <= size - 1 ? i + 1 : size - 1;
                    int nextNextIndex = i + 2 <= size - 1 ? i + 2 : size - 1;
                    PointValue b = chartViewConfig.getListPoint().get(nextIndex);
                    PointValue n = chartViewConfig.getListPoint().get(nextNextIndex);
                    //超过限制，break;
                    if (i + 1 > size - 1) {
                        mPathSet[i] = mPathTrends;
                        break;
                    }
                    //构造最后一个，为了最后一个点能平滑过渡
                    if (i + 1 == size - 1) {
                        PointValue nn = new PointValue(b.x + 50, b.y);
                        n = nn;
                    }
                    //构造第一个的前一个，为了第一个点和第二个点能够平滑过渡
                    if (i == 0) {
                        PointValue ll = new PointValue(a.x - 50, a.y);
                        l = ll;
                        mPathTrends.moveTo(a.x, b.y);
                    } else {
                        l = chartViewConfig.getListPoint().get(i - 1);
                    }
                    //构造控制点
                    ChartViewHelper.caculateController(a, b, l, n, mBezierControls);
                    mPathTrends.moveTo(a.x, a.y);
                    mPathTrends.cubicTo(mBezierControls[1].x, mBezierControls[1].y, mBezierControls[2].x, mBezierControls[2].y, b.x, b.y);
                    mPathSet[i] = mPathTrends;
                }
            }
        } else {
            //画线
            if (mPathSet == null) {
                mPathSet = new Path[chartViewConfig.getListPoint().size()];
                for (int i = 0; i < chartViewConfig.getListPoint().size(); i++) {
                    PointValue point = chartViewConfig.getListPoint().get(i);
                    PointValue pointLast = null;
                    if (i > 0) {
                        pointLast = chartViewConfig.getListPoint().get(i - 1);
                    }
                    Path mPathTrends = new Path();
                    if (i == 0) {
                        mPathTrends.moveTo(point.x, point.y);

                    } else {
                        mPathTrends.moveTo(pointLast.x, pointLast.y);
                        mPathTrends.lineTo(point.x, point.y);
                    }
                    mPathSet[i] = mPathTrends;
                }
            }
        }
        int indicator_x = getWidth() / 2 + getScrollX();

        //画线
        for (int i = 0; i < chartViewConfig.getListPoint().size(); i++) {
            PointValue point = chartViewConfig.getListPoint().get(i);
            //if (point.x >= (mScreenIndex - chartViewConfig.getCloumn()) * chartViewConfig.getItem_width() && point.x <= (mScreenIndex + chartViewConfig.getCloumn() * 2) * chartViewConfig.getItem_width()) {
            canvas.drawPath(mPathSet[i], mPaintPath);
            //}
        }
        //画圆
        for (int i = 0; i < chartViewConfig.getListPoint().size(); i++) {
            PointValue point = chartViewConfig.getListPoint().get(i);
            if (point.x >= (mScreenIndex - chartViewConfig.getCloumn() / 2) * chartViewConfig.getItem_width() && point.x <= (mScreenIndex + chartViewConfig.getCloumn() * 2) * chartViewConfig.getItem_width()) {
                //画外圆
                canvas.drawCircle(point.x, point.y, mDensity * 7f, mPaintCircleOutSide);
                //画内圆
                if (indicator_x + 5 >= point.x && indicator_x - 5 <= point.x) {
                    //canvas.drawCircle(point.x, point.y, mDensity * 4f, mPaintCircle);
                } else {
                    //内圆空心
                    if (chartViewConfig.isPointCircleIntervalStoke()) {
                        mPaintCircle.setStyle(Paint.Style.STROKE);
                        canvas.drawCircle(point.x, point.y, mDensity * 4f, mPaintCircle);
                        //内圆实心
                    } else {
                        mPaintCircle.setStyle(Paint.Style.FILL);
                        canvas.drawCircle(point.x, point.y, mDensity * 4f, mPaintCircle);
                    }
                }
            }
        }
    }


    /**
     * 画区域
     *
     * @param canvas
     */
    protected void drawPointRegion(Canvas canvas) {
        if (chartViewConfig.getListPointRegion() == null) {
            return;
        }

        //画笔重置
        if (mPathRegion == null) {
            mPathRegion = new Path();
        }
        mPathRegion.reset();
        //可见范围内，线的链接
        boolean bFirst = true;
        for (int i = 0; i < chartViewConfig.getListPointRegion().size(); i++) {
            PointValue point = chartViewConfig.getListPointRegion().get(i);
            if (point.x >= (mScreenIndex - chartViewConfig.getCloumn()) * chartViewConfig.getItem_width() && point.x <= (mScreenIndex + chartViewConfig.getCloumn() * 2) * chartViewConfig.getItem_width()) {
                PointValue value = chartViewConfig.getListPointRegion().get(i);
                if (bFirst) {
                    mPathRegion.moveTo(value.x, value.y);
                    bFirst = false;
                } else {
                    mPathRegion.lineTo(value.x, value.y);
                }
            }
        }
        //path闭合，填充
        mPathRegion.close();
        canvas.drawPath(mPathRegion, mPaintPathRegion);
    }

    //画点与点链接，与X轴行程的区域
    protected void drawFillPointConnectRegion(Canvas canvas) {
        if (!chartViewConfig.isFillPointRegion())
            return;
        if (chartViewConfig.getListPoint().size() >= 4 && chartViewConfig.isSmoothPoint()) {
            int bottomY = chartViewConfig.getRow() * chartViewConfig.getItem_height();
            if (mPathSetRegion == null) {
                int size = chartViewConfig.getListPoint().size();
                PointValue[] mBezierControls = new PointValue[4];
                mPathSetRegion = new Path[size];
                for (int i = 0; i < size; i++) {
                    Path mPathTrends = new Path();
                    //构造控制点的四个点；
                    PointValue l = null;
                    PointValue a = chartViewConfig.getListPoint().get(i);
                    int nextIndex = i + 1 <= size - 1 ? i + 1 : size - 1;
                    int nextNextIndex = i + 2 <= size - 1 ? i + 2 : size - 1;
                    PointValue b = chartViewConfig.getListPoint().get(nextIndex);
                    PointValue n = chartViewConfig.getListPoint().get(nextNextIndex);
                    //超过限制，break;
                    if (i + 1 > size - 1) {
                        mPathSetRegion[i] = mPathTrends;
                        break;
                    }
                    //构造最后一个，为了最后一个点能平滑过渡
                    if (i + 1 == size - 1) {
                        PointValue nn = new PointValue(b.x + 50, b.y);
                        n = nn;
                    }
                    //构造第一个的前一个，为了第一个点和第二个点能够平滑过渡
                    if (i == 0) {
                        PointValue ll = new PointValue(a.x - 50, a.y);
                        l = ll;
                        mPathTrends.moveTo(a.x, b.y);
                    } else {
                        l = chartViewConfig.getListPoint().get(i - 1);
                    }
                    //构造控制点
                    ChartViewHelper.caculateController(a, b, l, n, mBezierControls);
                    mPathTrends.moveTo(a.x, a.y);
                    mPathTrends.cubicTo(mBezierControls[1].x, mBezierControls[1].y, mBezierControls[2].x, mBezierControls[2].y, b.x, b.y);
                    //闭合区域
                    if (nextIndex >= 1 && nextIndex <= size - 1) {
                        PointValue pointValueBottomRight = new PointValue(b.x, bottomY);
                        PointValue pointValueBottomLeft = new PointValue(a.x, bottomY);
                        mPathTrends.lineTo(pointValueBottomRight.x, pointValueBottomRight.y);
                        mPathTrends.lineTo(pointValueBottomLeft.x, pointValueBottomLeft.y);
                        mPathTrends.close();
                    }
                    mPathSetRegion[i] = mPathTrends;
                }
            }
            for (int i = 0; i < chartViewConfig.getListPoint().size(); i++) {
                PointValue point = chartViewConfig.getListPoint().get(i);
                if (point.x >= (mScreenIndex - chartViewConfig.getCloumn() / 2) * chartViewConfig.getItem_width() && point.x <= (mScreenIndex + chartViewConfig.getCloumn() * 2) * chartViewConfig.getItem_width()) {
                    //画线
                    canvas.drawPath(mPathSetRegion[i], mPaintPathConnectRegion);
                }
            }

        } else {
            //画笔重置
            if (mPathConnectRegion == null) {
                mPathConnectRegion = new Path();
            }
            mPathConnectRegion.reset();

            listRegionTemp.clear();
            listRegionTemp.addAll(chartViewConfig.getListPoint());
            //加入第一点
            PointValue firstValue = chartViewConfig.getListPoint().get(0);
            int bottomY = chartViewConfig.getRow() * chartViewConfig.getItem_height();
            PointValue firstBottomValue = new PointValue(firstValue.x, bottomY);
            listRegionTemp.add(0, firstBottomValue);

            PointValue lastValue = chartViewConfig.getListPoint().get(chartViewConfig.getListPoint().size() - 1);
            PointValue lastBottomValue = new PointValue(lastValue.x, bottomY);
            listRegionTemp.add(lastBottomValue);
            //可见范围内，线的链接
            boolean bFirst = true;
            for (int i = 0; i < listRegionTemp.size(); i++) {
                PointValue point = listRegionTemp.get(i);
                if (point.x >= (mScreenIndex - chartViewConfig.getCloumn() / 2) * chartViewConfig.getItem_width() && point.x <= (mScreenIndex + chartViewConfig.getCloumn() * 2) * chartViewConfig.getItem_width()) {
                    PointValue value = listRegionTemp.get(i);
                    if (bFirst) {
                        mPathConnectRegion.moveTo(value.x, value.y);
                        bFirst = false;
                    } else {
                        mPathConnectRegion.lineTo(value.x, value.y);
                    }
                }
            }
            //path闭合，填充
            mPathConnectRegion.close();
            canvas.drawPath(mPathConnectRegion, mPaintPathConnectRegion);
        }

    }

    /**
     * 计算控制点
     *
     * @param result
     * @param value1
     * @param value2
     * @param multiplier
     */
    private void calc(PointValue result, PointValue value1, PointValue value2, final float multiplier) {
        float diffX = value2.x - value1.x;
        float diffY = value2.y - value2.y;
        result.x = value1.x + (diffX * multiplier);
        result.y = value1.y + (diffY * multiplier);
    }

    //通过刻度值获取横向刻度值的索引
    private int getHorizontalKeduIndex(String value) {
        int size = chartViewConfig.getListHorizontalKedu().size();
        for (int i = 0; i < size; i++) {
            KeduValue keduValue = chartViewConfig.getListHorizontalKedu().get(i);
            if (chartViewConfig.horizontal_lable_use_integer) {
                int valueSrc = Integer.valueOf(keduValue.value);
                if (valueSrc == Integer.valueOf(value)) {
                    return i;
                    /*
                    //第一点返回-1说明没有间隔,从第二点开始才有间隔的概念
                    if(i==0){
                        return -1;
                    }else {
                        return i-1;
                    }
                    */
                }
            }
            if (chartViewConfig.horizontal_lable_use_float) {
                float valueSrc = Float.valueOf(keduValue.value);
                if (valueSrc == Float.valueOf(value)) {
                   /* //第一点返回-1说明没有间隔,从第二点开始才有间隔的概念
                    if(i==0){
                        return -1;
                    }else {
                        return i-1;
                    }*/
                    return i;
                }
            }
           /* if(chartViewConfig.horizontal_lable_use_calendar){

               *//* int valueSrc = Integer.valueOf(keduValue.value);
                if(valueSrc==Integer.valueOf(value)){
                    //第一点返回-1说明没有间隔,从第二点开始才有间隔的概念
                    if(i==0){
                        return -1;
                    }else {
                        return i-1;
                    }
                }*//*
            }*/
        }
        return -1;
    }

    /**
     * 计算每个点的坐标值
     */
    private void caculatePointValue() {
        if (mIsCaculateValue) {
            //计算区域坐标
            if (chartViewConfig.getListPointRegion() != null && chartViewConfig.getListPointRegion().size() > 0) {
                caculatePointRegionValue(chartViewConfig.getListPointRegion(), true);
            }
            //计算点的坐标
            caculatePointRegionValue(chartViewConfig.getListPoint(), false);
            mIsCaculateValue = false;
        }
    }

    /**
     * 更新视图
     */
    public void update() {
        mScreenIndex = 0;
        mIsCaculateValue = true;
        mPathGridHorizontal = null;
        mPathGridVerical = null;
        mPathSet = null;
        mPathSetRegion = null;
        isFirst = true;
        mScroller.setFinalX(0);
        invalidate();
    }

    /**
     * 计算点的x y值
     *
     * @param listPoint
     * @param bOverHorizontalKeduRange 是否允许超出横刻度范围，纯属为了排查bug
     */
    private void caculatePointRegionValue(List<PointValue> listPoint, boolean bOverHorizontalKeduRange) {
        if (listPoint == null || listPoint.size() == 0)
            return;

        //遍历在这个刻度范围内的点，计算出x值
        KeduValue firstKeduValue = chartViewConfig.getListHorizontalKedu().get(0);
        int index = 0;
        for (PointValue point : listPoint) {
            if (chartViewConfig.horizontal_lable_use_integer) {
                index++;
                //当前点的横刻度值
                int horizontal_value = Integer.valueOf(String.valueOf(point.horizontal_value));
                //找出当前点在第几个横刻度
                int horizontalKeduIndex = (horizontal_value - Integer.valueOf(firstKeduValue.value)) / (int) chartViewConfig.horizontal_kedu_interval[0];
                //该横刻度的值             //2370(79) 横刻度最后一个2433（92） 点的最后一个2432（4）
                if (horizontalKeduIndex >= 0 && horizontalKeduIndex < chartViewConfig.getListHorizontalKedu().size()) {
                    int horizontalKeduIndex_value = Integer.valueOf(chartViewConfig.getListHorizontalKedu().get(horizontalKeduIndex).value);//horizontalKeduIndex*(int)chartViewConfig.horizontal_kedu_interval[0];
                    //该横刻度的x值
                    int horizontalKeduIndex_x = (horizontalKeduIndex + chartViewConfig.getCloumn() / 2) * chartViewConfig.getItem_width();
                    //计算出当前点与当前横刻度的差
                    int value = horizontal_value - horizontalKeduIndex_value;
                    //计算差值占用间隔的百分比
                    float percent = value / (chartViewConfig.horizontal_kedu_interval[0] * 1.0f);
                    //通过百分比和横刻度的x值算出当前点的x值
                    point.x = horizontalKeduIndex_x + percent * chartViewConfig.getItem_width();
                    //不在横刻度范围内，则自己计算x值，画区域点的时候才会遇到
                } else {
                    //不允许超过横刻度，但此时超过了，打log
                    if (!bOverHorizontalKeduRange) {
                        return;
                    }
                    int horizontalKeduIndex_value = 0;
                    if (horizontalKeduIndex < 0)
                        horizontalKeduIndex_value = Integer.valueOf(chartViewConfig.getListHorizontalKedu().get(0).value) - horizontalKeduIndex * (int) chartViewConfig.horizontal_kedu_interval[0];
                    else {
                        int size = chartViewConfig.getListHorizontalKedu().size();
                        int lastKeduvalue = Integer.valueOf(chartViewConfig.getListHorizontalKedu().get(size - 1).value);
                        horizontalKeduIndex_value = lastKeduvalue + (horizontalKeduIndex - size) * (int) chartViewConfig.horizontal_kedu_interval[0];
                    }
                    //该横刻度的x值
                    int horizontalKeduIndex_x = (horizontalKeduIndex + chartViewConfig.getCloumn() / 2) * chartViewConfig.getItem_width();
                    //计算出当前点与当前横刻度的差
                    int value = horizontal_value - horizontalKeduIndex_value;
                    //计算差值占用间隔的百分比
                    float percent = value / (chartViewConfig.horizontal_kedu_interval[0] * 1.0f);
                    //通过百分比和横刻度的x值算出当前点的x值
                    point.x = horizontalKeduIndex_x + percent * chartViewConfig.getItem_width();
                }

            } else if (chartViewConfig.horizontal_lable_use_float) {

                //当前点的横刻度值
                float horizontal_value = Float.valueOf(String.valueOf(point.horizontal_value));
                //找出当前点在第几个横刻度（非精确，因为是画区域），取余数
                int horizontalKeduIndex = (int) (horizontal_value - Integer.valueOf(firstKeduValue.value)) / (int) chartViewConfig.horizontal_kedu_interval[0];
                if (horizontalKeduIndex >= 0 && horizontalKeduIndex < chartViewConfig.getListHorizontalKedu().size()) {
                    //该横刻度的值
                    float horizontalKeduIndex_value = Float.valueOf(chartViewConfig.getListHorizontalKedu().get(horizontalKeduIndex).value);// horizontalKeduIndex*(int)chartViewConfig.horizontal_kedu_interval[0];
                    //该横刻度的x值
                    float horizontalKeduIndex_x = (horizontalKeduIndex + chartViewConfig.getCloumn() / 2) * chartViewConfig.getItem_width();

                    //计算出当前点与当前横刻度的差
                    float value = horizontal_value - horizontalKeduIndex_value;
                    //计算差值占用间隔的百分比
                    float percent = value / (chartViewConfig.horizontal_kedu_interval[0] * 1.0f);
                    //通过百分比和横刻度的x值算出当前点的x值
                    point.x = horizontalKeduIndex_x + percent * chartViewConfig.getItem_width();
                    //不在横刻度范围内，则自己计算x值，画区域点的时候才会遇到
                } else {
                    float horizontalKeduIndex_value = 0;
                    if (horizontalKeduIndex < 0)
                        horizontalKeduIndex_value = Float.valueOf(chartViewConfig.getListHorizontalKedu().get(0).value) - horizontalKeduIndex * (int) chartViewConfig.horizontal_kedu_interval[0];
                    else {
                        int size = chartViewConfig.getListHorizontalKedu().size();
                        float lastKeduvalue = Float.valueOf(chartViewConfig.getListHorizontalKedu().get(size - 1).value);
                        horizontalKeduIndex_value = lastKeduvalue + (horizontalKeduIndex - size) * (int) chartViewConfig.horizontal_kedu_interval[0];
                    }
                    //该横刻度的x值
                    int horizontalKeduIndex_x = (horizontalKeduIndex + chartViewConfig.getCloumn() / 2) * chartViewConfig.getItem_width();
                    //计算出当前点与当前横刻度的差
                    float value = horizontal_value - horizontalKeduIndex_value;
                    //计算差值占用间隔的百分比
                    float percent = value / (chartViewConfig.horizontal_kedu_interval[0] * 1.0f);
                    //通过百分比和横刻度的x值算出当前点的x值
                    point.x = horizontalKeduIndex_x + percent * chartViewConfig.getItem_width();
                }
            }
        }

        //遍历在这个刻度范围内的点，计算出y值
        final int count_verical = (int) (chartViewConfig.getVerical_unit_end() - chartViewConfig.getVerical_unit_start()) / (int) chartViewConfig.getVerical_unit_incremetal();
        for (PointValue point : listPoint) {
            for (int i = count_verical; i >= 0; i--) {
                //从最大值开始
                float value = chartViewConfig.getVerical_unit_start() + i * chartViewConfig.getVerical_unit_incremetal();
                if (chartViewConfig.verical_lable_use_integer) {
                    float verical_value = Float.valueOf(String.valueOf(point.verical_value));
                    if (verical_value >= (int) value) {
                        //超过最高，取最高
                        if (verical_value >= (int) (chartViewConfig.getVerical_unit_end())) {
                            point.y = (chartViewConfig.getRow() - count_verical) * chartViewConfig.getItem_height();
                            //超过最最低，取最低
                        } else if (verical_value <= (int) (chartViewConfig.getVerical_unit_start())) {
                            point.y = (chartViewConfig.getRow()) * chartViewConfig.getItem_height();
                        } else {
                            float cha = verical_value - (int) value;
                            float percent = cha / (chartViewConfig.getVerical_unit_incremetal() * 1.0f);
                            int y = (chartViewConfig.getRow() - i) * chartViewConfig.getItem_height();
                            point.y = y - percent * chartViewConfig.getItem_height();
                        }
                    }
                } else if (chartViewConfig.verical_lable_use_float) {
                    float verical_value = Float.valueOf(String.valueOf(point.verical_value));
                    if (verical_value >= Float.valueOf(String.valueOf(value))) {
                        //超过最高，取最高
                        if (verical_value >= Float.valueOf(String.valueOf(chartViewConfig.getVerical_unit_end()))) {
                            point.y = (chartViewConfig.getRow() - count_verical) * chartViewConfig.getItem_height();
                            //超过最最低，取最低
                        } else if (verical_value <= Float.valueOf(String.valueOf(chartViewConfig.getVerical_unit_start()))) {
                            point.y = (chartViewConfig.getRow()) * chartViewConfig.getItem_height();
                        } else {
                            float cha = verical_value - Float.valueOf(String.valueOf(value));
                            float percent = cha / (chartViewConfig.getVerical_unit_incremetal() * 1.0f);
                            int y = (chartViewConfig.getRow() - i) * chartViewConfig.getItem_height();
                            point.y = y - percent * chartViewConfig.getItem_height();
                        }
                    }
                }
            }
            if (point.y == 0) {
                float verical_value = Float.valueOf(String.valueOf(point.verical_value));
                if (chartViewConfig.verical_lable_use_integer) {
                    if (verical_value >= (int) (chartViewConfig.getVerical_unit_end())) {
                        point.y = (chartViewConfig.getRow() - count_verical) * chartViewConfig.getItem_height();
                        //超过最最低，取最低
                    } else if (verical_value <= (int) (chartViewConfig.getVerical_unit_start())) {
                        point.y = (chartViewConfig.getRow()) * chartViewConfig.getItem_height();
                    }
                } else if (chartViewConfig.verical_lable_use_float) {
                    if (verical_value >= Float.valueOf(String.valueOf(chartViewConfig.getVerical_unit_end()))) {
                        point.y = (chartViewConfig.getRow() - count_verical) * chartViewConfig.getItem_height();
                        //超过最最低，取最低
                    } else if (verical_value <= Float.valueOf(String.valueOf(chartViewConfig.getVerical_unit_start()))) {
                        point.y = (chartViewConfig.getRow()) * chartViewConfig.getItem_height();
                    }
                }
            }
        }

    }

    private boolean isFirst = true;

    private void setSelection() {
        if (chartViewConfig.getListPoint() == null || chartViewConfig.getListPoint().size() == 0)
            return;
        Log.d(TAG, "----------------isFirst:" + isFirst);
        if (isFirst) {
            isFirst = false;
            mCurrentIndex = chartViewConfig.getItemSelection();
            if (mCurrentIndex <= 0) {
                mScroller.setFinalX((int) minX);
            } else {
                boolean has = false;
                for (int i = 0; i < chartViewConfig.getListPoint().size(); i++) {
                    if (mCurrentIndex == i) {
                        mScroller.setFinalX(getScrollX() + (int) chartViewConfig.getListPoint().get(i).x - getWidth() / 2);
                        has = true;
                        break;
                    }
                }
                if (!has) {
                    mScroller.setFinalX((int) maxX);
                }
            }
            invalidate();
        }
        /*
        //只有一个选中第一个
        if (chartViewConfig.getListPoint().size() == 1) {
            mScroller.setFinalX((int)minX);
            scrollToNearllyPoint();
            //多个则选择最后一个
        } else {
            mScroller.setFinalX((int)maxX);
        }*/


    }


    /**
     * 调用postInvalidate()方法重新绘制界面，
     * postInvalidate()方法会调用invalidate()方法，
     * invalidate()方法又会调用computeScroll方法，
     * 就这样周而复始的相互调用，直到mScroller.computeScrollOffset() 返回false才会停止界面的重绘动作
     */
    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
        super.computeScroll();
    }


    /**
     * 是否处于游标范围内
     *
     * @param event
     * @return
     */
    private boolean isAtIndicatorRange(MotionEvent event) {
        float left = mIndicatorX;
        float right = mIndicatorX + chartViewConfig.getIndicator_radius() * 2;
        float top = mIndicatroY;
        float bottom = mIndicatroY + chartViewConfig.getIndicator_radius() * 2;
        float x = event.getX() + getScrollX();
        float y = event.getY();
        Log.d(TAG, "left:" + left + "--right:" + right + "--x:" + x + "\\n"
                + "top" + top + "--bottom:" + bottom + "--y:" + y);
        if (x >= left && x <= right && y >= top && y <= bottom) {
            return true;
        }
        return false;
    }

    private boolean isAtInicatorRange = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            if (mVelocityTracker == null) {
                mVelocityTracker = VelocityTracker.obtain();
            }
            mVelocityTracker.addMovement(event);
            float x = event.getX();
            float y = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    //mLastFingerPosition = event.getX();
                    mIsPressd = true;
                    mLastScrollX = 0;
                    mLastionMotionX = x;
                    mLastionMotionY = y;
                    mScroller.forceFinished(true);
                    isAtInicatorRange = isAtIndicatorRange(event);
                    invalidate();
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    int detaX = (int) (mLastionMotionX - x);
                    scrollBy(detaX, 0);
                    int detaY = (int) (mLastionMotionY - y);
                    //纵向消息交给父控件
                    if (Math.abs(detaY) > 50) {
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }
                    //触发监听
                    if (mLastScrollX != 0 && mListener != null) {
                        int delta = getScrollX() - mLastScrollX;
                        mListener.onChartViewScrolled(delta);
                    } else {
                        mLastScrollX = getScrollX();
                    }
                    mLastionMotionX = x;
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    getParent().requestDisallowInterceptTouchEvent(false);
                    final VelocityTracker velocityTracker = mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000);
                    int velocityX = (int) velocityTracker.getXVelocity();
                    int velocityY = (int) velocityTracker.getYVelocity();
                    if (chartViewConfig.getListPoint() == null || chartViewConfig.getListPoint().size() == 0) {
                        mScroller.setFinalX((int) 0);
                        invalidate();
                        break;
                    }
                    if (isAtInicatorRange) {
                        isAtInicatorRange = isAtIndicatorRange(event);
                        if (isAtInicatorRange && mListener != null) {
                            mListener.onIndicatorClick(mCurrentIndex);
                        }
                    }
                    //计算最大最小值
                    getMinAndManScrollerValue();
                    //往右边滑
                    if (velocityX > SNAP_VELOCITY) {
                        handleFling(velocityX, velocityY);
                        if (mListener != null) {
                            mListener.onChartViewScrollDirection(ScrollDirection.RIGHT);
                        }
                    }
                    //往左边滑
                    else if (velocityX < -SNAP_VELOCITY) {
                        handleFling(velocityX, velocityY);
                        if (mListener != null) {
                            mListener.onChartViewScrollDirection(ScrollDirection.LEFT);
                        }
                    } else {
                        snapToDestination();
                    }

                    if (mVelocityTracker != null) {
                        mVelocityTracker.recycle();
                        mVelocityTracker = null;
                    }
                    mIsPressd = false;
                    invalidate();
                    break;
                }
                case MotionEvent.ACTION_CANCEL:
                    snapToDestination();
                    //mTouchState = TOUCH_STATE_REST;
                    break;
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }


    private void scrollToNearby() {
        //当前点位置
        mCurrentIndex = 0;
        int indicator_x = getWidth() / 2 + getScrollX();
        for (int i = chartViewConfig.getListPoint().size() - 1; i >= 0; i--) {
            if (indicator_x > chartViewConfig.getListPoint().get(i).x) {
                mCurrentIndex = i;
                break;
            }
        }
        //与后边的点的差
        int width = 0;
        if (mCurrentIndex + 1 < chartViewConfig.getListPoint().size()) {
            width = (int) (chartViewConfig.getListPoint().get(mCurrentIndex + 1).x - chartViewConfig.getListPoint().get(mCurrentIndex).x);
        }
        int cha = indicator_x - (int) chartViewConfig.getListPoint().get(mCurrentIndex).x;
        //处于右半部分，往右边偏移
        if (Math.abs(cha) >= width / 2) {
            int dx = width - Math.abs(cha);
            mScroller.startScroll(getScrollX(), 0, dx, 0, 250);
            mCurrentIndex ++;
            mCurrentIndex = Math.min(mCurrentIndex, chartViewConfig.getListPoint().size() - 1);//mCurrentIndex++>chartViewConfig.getListPoint().size()-1?mCurrentIndex:mCurrentIndex--;
            invalidate();
        } else {
            int dx = -Math.abs(cha);
            mScroller.startScroll(getScrollX(), 0, dx, 0, 250);
            invalidate();
        }
        if (mListener != null && chartViewConfig.getListPoint() != null && chartViewConfig.getListPoint().size() > 0) {
            mListener.onItemSelected(mCurrentIndex, chartViewConfig.getListPoint().get(mCurrentIndex));
        }
    }

    /**
     * 计算左右x的界限值
     */
    private void getMinAndManScrollerValue() {
        if (chartViewConfig.getListPoint() == null || chartViewConfig.getListPoint().size() == 0) {
            int indicator_X = getWidth() / 2 + getScrollX();
            minX = (int) (chartViewConfig.getListPoint().get(0).x - getWidth() / 2);
            maxX = (int) chartViewConfig.getListPoint().get(chartViewConfig.getListPoint().size() - 1).x - getWidth() / 2;
            return;
        }
        int indicator_X = getWidth() / 2 + getScrollX();
        minX = (int) (chartViewConfig.getListPoint().get(0).x - getWidth() / 2);
        maxX = (int) chartViewConfig.getListPoint().get(chartViewConfig.getListPoint().size() - 1).x - getWidth() / 2;
    }

    private void snapToDestination() {
        if (chartViewConfig.getListPoint() == null || chartViewConfig.getListPoint().size() == 0)
            return;
        int nowScrollX = getScrollX();
        getMinAndManScrollerValue();
        maxX = Math.max(maxX, 0);
        if (nowScrollX < minX) {

            int dx = minX - nowScrollX;
            Log.d(TAG, "-->dx :" + dx);
            mScroller.startScroll(nowScrollX, 0, dx, 0, Math.abs(dx));
            invalidate();
            if (mListener != null && chartViewConfig.getListPoint() != null && chartViewConfig.getListPoint().size() > 0) {
                mCurrentIndex = 0;
                mListener.onItemSelected(mCurrentIndex, chartViewConfig.getListPoint().get(mCurrentIndex));
            }
        } else if (nowScrollX > maxX) {
            int dx = maxX - nowScrollX;
            //数据过少的时候
            if (maxX == 0) {
                dx = -nowScrollX + minX;
            }
            mScroller.startScroll(getScrollX(), 0, dx, 0, Math.abs(dx));
            invalidate();
            if (mListener != null && chartViewConfig.getListPoint() != null && chartViewConfig.getListPoint().size() > 0) {
                mCurrentIndex = chartViewConfig.getListPoint().size() - 1;
                mListener.onItemSelected(mCurrentIndex, chartViewConfig.getListPoint().get(mCurrentIndex));
            }

        } else if ((nowScrollX <= maxX) && (nowScrollX >= minX)) {
            scrollToNearby();
        }


    }


    private void handleFling(int velocityX, int velocityY) {
        int nowScrollX = getScrollX();
        //滑动距离在:
        if ((nowScrollX <= maxX) && (nowScrollX >= minX)) {
            /**
             * 基于甩动手势开始滚动处理。根据甩动的初始速率来决定滚动的距离。
             参数：
             startX  滚动的起始点（X）
             startY  滚动的起始点（Y）
             velocityX 以像素/每秒为单位，测量所得X轴的初始甩动速率
             velocityY以像素/每秒为单位，测量所得Y轴的初始甩动速率
             minX  最小的X轴值，滚动器不能滚动经过这个点
             maxX  最大的X轴值，滚动器不能滚动经过这个点
             minY  最小的Y轴值，滚动器不能滚动经过这个点
             maxY  最大的Y轴值，滚动器不能滚动经过这个点
             */
            mScroller.fling(getScrollX(), getScrollY(), -velocityX, -velocityY, minX, maxX, 0, 0);
            final int mDurationTime = mScroller.getDuration() + 500;
            this.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //滚动完成后，定位
                    if (!mScroller.computeScrollOffset()) {
                        scrollToNearllyPoint();
                    } else {

                    }
                }
            }, mDurationTime);
            invalidate();
        } else {
            snapToDestination();
        }
       /* //超过左边，回滚
        else if (nowScrollX < minX) {
            int dx = minX - nowScrollX;
            int duration = Math.abs(dx);
            mScroller.startScroll(getScrollX(), 0, dx, 0, duration);
            if(mListener!=null && chartViewConfig.getListPoint()!=null && chartViewConfig.getListPoint().size()>0){
                mCurrentIndex = 0;
                mListener.onItemSelected(mCurrentIndex,chartViewConfig.getListPoint().get(mCurrentIndex));
            }
            //超过右边，回滚
        } else if (nowScrollX > maxX) {
            int dx = maxX - nowScrollX;
            int duration = Math.abs(dx);
            mScroller.startScroll(getScrollX(), 0, dx, 0, duration);
            if(mListener!=null && chartViewConfig.getListPoint()!=null && chartViewConfig.getListPoint().size()>0){
                mCurrentIndex = chartViewConfig.getListPoint().size()-1;
                mListener.onItemSelected(mCurrentIndex,chartViewConfig.getListPoint().get(mCurrentIndex));
            }
        }*/
    }

    private void scrollToNearllyPoint() {
        getMinAndManScrollerValue();
        int nowScrollX = getScrollX();
        if ((nowScrollX <= (maxX)) && (nowScrollX >= (minX))) {
            scrollToNearby();
        }
    }

    private int minX;
    private int maxX;

}
