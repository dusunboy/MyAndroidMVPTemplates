package $Package.core.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.widget.ImageView;

/**
 * 矩形框
 * Created by Vincent on $Time.
 */
public class RectFrameView extends ImageView {

    /**
     * 矩形边框值
     */
    private static final int RECT_VALUE = 350;
    private final Paint paint;

    public RectFrameView(Context context) {
        super(context);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.5f);//设置线宽
        paint.setAlpha(100);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制矩形
        canvas.drawRect(new Rect(RECT_VALUE, RECT_VALUE, RECT_VALUE, RECT_VALUE), paint);

    }
}
