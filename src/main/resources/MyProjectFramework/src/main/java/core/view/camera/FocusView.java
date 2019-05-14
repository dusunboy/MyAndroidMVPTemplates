package $Package.core.view.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import $Package.R;

/**
 * 相机聚焦view
 * Created by Vincent on 2019-05-10 11:33:28.
 */
public class FocusView extends SurfaceView implements SurfaceHolder.Callback {

    private Context context;
    protected SurfaceHolder surfaceHolder;
    private Bitmap bitmap;

    private Paint paint = new Paint();

    private float rectWidth;

    public void setRectWidth(int rectDpiWidth) {
        float scale = context.getResources().getDisplayMetrics().density;
        synchronized (this) {
            rectWidth = (int) (rectDpiWidth * scale + 0.5f);
        }
    }

    public FocusView(Context context) {
        super(context);
    }

    public FocusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        float scale = context.getResources().getDisplayMetrics().density;
        rectWidth = (int) (25 * scale + 0.5f);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        setZOrderOnTop(true);
        paint.setColor(Color.TRANSPARENT);
    }

    public void setBitmap(Bitmap bitmap) {
        synchronized (this) {
            this.bitmap = bitmap;
        }
    }

    public void setBitmap(int res) {
        synchronized (this) {
            bitmap = BitmapFactory.decodeResource(context.getResources(), res);
        }
    }

    public void surfaceChanged(SurfaceHolder arg0, int arg1, int w, int h) {
    }

    public void surfaceCreated(SurfaceHolder arg0) {

    }

    public void surfaceDestroyed(SurfaceHolder arg0) {

    }

    synchronized void clearDraw() {
        Canvas canvas = surfaceHolder.lockCanvas();
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        canvas.drawColor(Color.TRANSPARENT);
        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    public synchronized void drawLine(float x, float y) {
        Canvas canvas = surfaceHolder.lockCanvas();
        canvas.drawColor(Color.TRANSPARENT);
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(paint);
        if (bitmap != null) {
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
            canvas.drawBitmap(bitmap, x - bitmap.getWidth() / (float) 2, y - bitmap.getHeight() / (float) 2, paint);
        } else {
            Paint paints = new Paint();
            paints.setStrokeWidth(3f);
            paints.setStyle(Paint.Style.STROKE);
            paints.setColor(context.getResources().getColor(R.color.light_blue_500));
            paints.clearShadowLayer();
            paint.setAntiAlias(true);
            canvas.drawCircle(x, y, rectWidth, paints);
        }
        surfaceHolder.unlockCanvasAndPost(canvas);
    }
}
