package $Package.core.view.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import $Package.R;
import $Package.core.fuction.ScreenUtil;

/**
 * 相机的矩形框
 * Created by Vincent on $Time.
 */
public class RectOnCamera extends View {
    public static final double SCALE_WIDTH = 0.7;
    public static final double SCALE_HEIGHT = 0.6;
    private final Rect centerScreenRect;
    //    private static final String TAG = "CameraSurfaceView";
    private Paint paint;
    private double rectOnCameraWidth;
    private double rectOnCameraHeight;
    private int leftOffset;
    private int topOffset;
//    // 圆
//    private Point centerPoint;
//    private int radio;

    public RectOnCamera(Context context) {
        this(context, null);
    }

    public RectOnCamera(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RectOnCamera(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        getScreenMetrics(context);
        initView(context);
        centerScreenRect = createCenterScreenRect(context);
    }

    private Rect createCenterScreenRect(Context context) {
        WindowManager WM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        WM.getDefaultDisplay().getMetrics(outMetrics);
        int heightPixels = outMetrics.heightPixels - ScreenUtil.getToolbarHeight(context);
        rectOnCameraWidth = SCALE_WIDTH * outMetrics.widthPixels;
        rectOnCameraHeight = SCALE_HEIGHT * heightPixels;
        leftOffset = (int) (outMetrics.widthPixels / (float) 2 - rectOnCameraWidth / (float) 2);
        topOffset = (int) (heightPixels / (float) 2 - rectOnCameraHeight / (float) 2);
        int x2 = (int) (leftOffset + rectOnCameraWidth);
        int y2 = (int) (topOffset + rectOnCameraHeight);
        return new Rect(leftOffset, topOffset, x2, y2);
    }

//    private void getScreenMetrics(Context context) {
//
//        screenWidth = outMetrics.widthPixels;
//        screenHeight = (int) (outMetrics.heightPixels - actionbarSizeHeight);
//    }

    private void initView(Context context) {
        paint = new Paint();
        paint.setAntiAlias(true);// 抗锯齿
        paint.setDither(true);// 防抖动
        paint.setColor(getResources().getColor(R.color.colorPrimaryDark));
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);// 空心
//        double marginLeft = (screenWidth * 0.15);
//        double marginTop =  (screenHeight * 0.25);
//        rectF = new RectF((int) marginLeft, (int) marginTop,
//                (int) (screenWidth - marginLeft), (int) (screenHeight - marginTop));
//        leftOffset = marginLeft + screenWidth * 0.02;
//        topOffset = marginTop + screenHeight * 0.05;
//        rectOnCameraWidth = screenWidth - 2 * leftOffset;
//        rectOnCameraHeight = screenHeight - 2 * topOffset;
//        rectOnCameraWidth = screenWidth;
//        rectOnCameraHeight = screenHeight;
//        centerPoint = new Point(screenWidth / 2, screenHeight / 2);
//        radio = (int) (screenWidth * 0.1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        paint.setColor(Color.RED);
        canvas.drawRect(centerScreenRect, paint);
//        paint.setColor(Color.WHITE);
//        Log.i(TAG, "onDraw");
//        canvas.drawCircle(centerPoint.x, centerPoint.y, radio, paint);// 外圆
//        canvas.drawCircle(centerPoint.x, centerPoint.y, radio - 20, paint); // 内圆
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
//                int x = (int) event.getX();
//                int y = (int) event.getY();
//                centerPoint = new Point(x, y);
                invalidate();
                if (mIAutoFocus != null) {
                    mIAutoFocus.autoFocus();
                }
                return true;
        }
        return true;
    }

    private IAutoFocus mIAutoFocus;

    /**
     * 聚焦的回调接口
     */
    public interface IAutoFocus {
        void autoFocus();
    }

    public void setIAutoFocus(IAutoFocus mIAutoFocus) {
        this.mIAutoFocus = mIAutoFocus;
    }

    public double getRectOnCameraWidth() {
        return rectOnCameraWidth;
    }

    public void setRectOnCameraWidth(double rectOnCameraWidth) {
        this.rectOnCameraWidth = rectOnCameraWidth;
    }

    public double getRectOnCameraHeight() {
        return rectOnCameraHeight;
    }

    public void setRectOnCameraHeight(double rectOnCameraHeight) {
        this.rectOnCameraHeight = rectOnCameraHeight;
    }

    public int getTopOffset() {
        return topOffset;
    }

    public void setTopOffset(int topOffset) {
        this.topOffset = topOffset;
    }

    public int getLeftOffset() {
        return leftOffset;
    }

    public void setLeftOffset(int leftOffset) {
        this.leftOffset = leftOffset;
    }
}
