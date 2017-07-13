package $Package.core.view.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * 相机SurfaceView
 * Created by Vincent on $Time.
 */
public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Camera.AutoFocusCallback {

//    private static final String TAG = "CameraSurfaceView";

    private Context context;
    private SurfaceHolder surfaceHolder;
    private Camera camera;

    private int screenWidth;
    private int screenHeight;
    private onPermissionListener onPermissionListener;


    public CameraSurfaceView(Context context) {
        this(context, null);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        getScreenMetrics(context);
        initView();
    }

    private void getScreenMetrics(Context context) {
        WindowManager WM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        WM.getDefaultDisplay().getMetrics(outMetrics);
        screenWidth = outMetrics.widthPixels;
        screenHeight = outMetrics.heightPixels;
    }

    public static Point getScreenMetrics2(Context context) {
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
        return new Point(displayMetrics.widthPixels, displayMetrics.heightPixels);
    }

    private void initView() {
        surfaceHolder = getHolder();//获得surfaceHolder引用
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//设置类型
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (camera == null) {
            try {
                if (onPermissionListener != null) {
                    onPermissionListener.granted(true);
                }
                camera = Camera.open();//开启相机
                camera.setPreviewDisplay(holder);//摄像头画面显示在Surface上
            } catch (Exception e) {
                e.printStackTrace();
                if (onPermissionListener != null) {
                    onPermissionListener.granted(false);
                }
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//        Log.i(TAG, "surfaceChanged");
        if (camera == null) {
        } else {
            //设置参数并开始预览
//            setCameraParams(camera, screenWidth, screenHeight);
            focus();
            setAutoFocus();
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
//        Log.i(TAG, "surfaceDestroyed");
        if (camera != null) {
            camera.stopPreview();//停止预览
            camera.release();//释放相机资源
            camera = null;
        }
    }

    @Override
    public void onAutoFocus(boolean success, Camera Camera) {
        if(success){
            focus();
        }
    }

    // 拍照瞬间调用
//    private Camera.ShutterCallback shutter = new Camera.ShutterCallback() {
//        @Override
//        public void onShutter() {
////            Log.i(TAG,"shutter");
//        }
//    };

    // 获得没有压缩过的图片数据
//    private Camera.PictureCallback raw = new Camera.PictureCallback() {
//
//        @Override
//        public void onPictureTaken(byte[] data, Camera Camera) {
////            Log.i(TAG, "raw");
//
//        }
//    };

    //创建jpeg图片回调数据对象
    private Camera.PictureCallback jpeg = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera Camera) {
            BufferedOutputStream bos = null;
            Bitmap bm = null;
            try {
                // 获得图片
                bm = BitmapFactory.decodeByteArray(data, 0, data.length);
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//                    Log.i(TAG, "Environment.getExternalStorageDirectory()="+Environment.getExternalStorageDirectory());
                    String filePath = "/sdcard/dyk" + System.currentTimeMillis() + ".jpg";//照片保存路径
                    File file = new File(filePath);
                    if (!file.exists()) {
                        if (!file.createNewFile()) {
                            throw new IOException("Unable to create file");
                        }
                    }
                    bos = new BufferedOutputStream(new FileOutputStream(file));
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);//将图片压缩到流中

                } else {
//                    Toast.makeText(context, "没有检测到内存卡", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bos != null) {
                        bos.flush();//输出
                        bos.close();//关闭
                    }
                    if (bm != null) {
                        bm.recycle();// 回收bitmap空间
                    }
                    camera.stopPreview();// 关闭预览
                    camera.startPreview();// 开启预览
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    };

    public Camera getCamera() {
        return camera;
    }

    /**
     * 设置自动聚焦
     */
    public void setAutoFocus() {
        camera.autoFocus(this);
    }

    public void takePicture() {
        //设置参数,并拍照
        setCameraParams(camera, screenWidth, screenHeight);
        // 当调用camera.takePicture方法后，camera关闭了预览，这时需要调用startPreview()来重新开启预览
        camera.takePicture(null, null, jpeg);
    }

    public void setCameraParams() {
//        Log.i(TAG,"setCameraParams  width="+width+"  height="+height);
        Camera.Parameters parameters = camera.getParameters();
        // 获取摄像头支持的PictureSize列表
        List<Camera.Size> pictureSizeList = parameters.getSupportedPictureSizes();
        for (Camera.Size size : pictureSizeList) {
//            Log.i(TAG, "pictureSizeList size.width=" + size.width + "  size.height=" + size.height);
        }
        /**从列表中选取合适的分辨率*/
        Camera.Size picSize = getProperSize(pictureSizeList, ((float) screenHeight / screenWidth));
        if (null == picSize) {
//            Log.i(TAG, "null == picSize");
            picSize = parameters.getPictureSize();
        }
//        Log.i(TAG, "picSize.width=" + picSize.width + "  picSize.height=" + picSize.height);
        // 根据选出的PictureSize重新设置SurfaceView大小
        float w = picSize.width;
        float h = picSize.height;
        parameters.setPictureSize(picSize.width, picSize.height);
        this.setLayoutParams(new FrameLayout.LayoutParams((int) (screenHeight * (h / w)), screenHeight));

        // 获取摄像头支持的PreviewSize列表
        List<Camera.Size> previewSizeList = parameters.getSupportedPreviewSizes();

        for (Camera.Size size : previewSizeList) {
//            Log.i(TAG, "previewSizeList size.width=" + size.width + "  size.height=" + size.height);
        }
        Camera.Size preSize = getProperSize(previewSizeList, ((float) screenHeight) / screenHeight);
        if (null != preSize) {
//            Log.i(TAG, "preSize.width=" + preSize.width + "  preSize.height=" + preSize.height);
            parameters.setPreviewSize(preSize.width, preSize.height);
        }

        parameters.setJpegQuality(100); // 设置照片质量
        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 连续对焦模式
        }

        camera.cancelAutoFocus();//自动对焦。
        camera.setDisplayOrientation(90);// 设置PreviewDisplay的方向，效果就是将捕获的画面旋转多少度显示
        camera.setParameters(parameters);

    }

    private void setCameraParams(Camera camera, int width, int height) {
//        Log.i(TAG,"setCameraParams  width="+width+"  height="+height);
        Camera.Parameters parameters = camera.getParameters();
        // 获取摄像头支持的PictureSize列表
        List<Camera.Size> pictureSizeList = parameters.getSupportedPictureSizes();
        for (Camera.Size size : pictureSizeList) {
//            Log.i(TAG, "pictureSizeList size.width=" + size.width + "  size.height=" + size.height);
        }
        /**从列表中选取合适的分辨率*/
        Camera.Size picSize = getProperSize(pictureSizeList, ((float) height / width));
        if (null == picSize) {
//            Log.i(TAG, "null == picSize");
            picSize = parameters.getPictureSize();
        }
//        Log.i(TAG, "picSize.width=" + picSize.width + "  picSize.height=" + picSize.height);
        // 根据选出的PictureSize重新设置SurfaceView大小
        float w = picSize.width;
        float h = picSize.height;
        parameters.setPictureSize(picSize.width, picSize.height);
        this.setLayoutParams(new FrameLayout.LayoutParams((int) (height * (h / w)), height));

        // 获取摄像头支持的PreviewSize列表
        List<Camera.Size> previewSizeList = parameters.getSupportedPreviewSizes();

        for (Camera.Size size : previewSizeList) {
//            Log.i(TAG, "previewSizeList size.width=" + size.width + "  size.height=" + size.height);
        }
        Camera.Size preSize = getProperSize(previewSizeList, ((float) height) / width);
        if (null != preSize) {
//            Log.i(TAG, "preSize.width=" + preSize.width + "  preSize.height=" + preSize.height);
            parameters.setPreviewSize(preSize.width, preSize.height);
        }

        parameters.setJpegQuality(100); // 设置照片质量
        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 连续对焦模式
        }

        camera.cancelAutoFocus();//自动对焦。
        camera.setDisplayOrientation(90);// 设置PreviewDisplay的方向，效果就是将捕获的画面旋转多少度显示
        camera.setParameters(parameters);

    }

    public void setCameraParams2() {
//        Log.i(TAG,"setCameraParams  width="+width+"  height="+height);
        Camera.Parameters parameters = camera.getParameters();
        // 获取摄像头支持的PictureSize列表
        List<Camera.Size> pictureSizeList = parameters.getSupportedPictureSizes();
        for (Camera.Size size : pictureSizeList) {
//            Log.i(TAG, "pictureSizeList size.width=" + size.width + "  size.height=" + size.height);
        }
        /**从列表中选取合适的分辨率*/
        Camera.Size picSize = getProperSize2(pictureSizeList,
                new Point(screenWidth, screenHeight));
        if (null == picSize) {
//            Log.i(TAG, "null == picSize");
            picSize = parameters.getPictureSize();
        }
//        Log.i(TAG, "picSize.width=" + picSize.width + "  picSize.height=" + picSize.height);
        // 根据选出的PictureSize重新设置SurfaceView大小
//        float w = picSize.width;
//        float h = picSize.height;
        parameters.setPictureSize(picSize.width, picSize.height);
//        this.setLayoutParams(new FrameLayout.LayoutParams((int) (screenHeight * (h / w)), screenHeight));

        // 获取摄像头支持的PreviewSize列表
        List<Camera.Size> previewSizeList = parameters.getSupportedPreviewSizes();

        for (Camera.Size size : previewSizeList) {
//            Log.i(TAG, "previewSizeList size.width=" + size.width + "  size.height=" + size.height);
        }
        Camera.Size preSize = getProperSize2(previewSizeList, new Point(screenWidth, screenHeight));
        if (null != preSize) {
//            Log.i(TAG, "preSize.width=" + preSize.width + "  preSize.height=" + preSize.height);
            parameters.setPreviewSize(preSize.width, preSize.height);
        }

        parameters.setJpegQuality(100); // 设置照片质量
        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 连续对焦模式
        }

//        camera.cancelAutoFocus();//自动对焦。
        camera.setDisplayOrientation(90);// 设置PreviewDisplay的方向，效果就是将捕获的画面旋转多少度显示
        camera.setParameters(parameters);

    }

    /**
     * 从列表中选取合适的分辨率
     * 默认w:h = 4:3
     * <p>注意：这里的w对应屏幕的height
     * h对应屏幕的width<p/>
     */
    private Camera.Size getProperSize(List<Camera.Size> pictureSizeList, float screenRatio) {
//        Log.i(TAG, "screenRatio=" + screenRatio);
        Camera.Size result = null;
        for (Camera.Size size : pictureSizeList) {
            float currentRatio = ((float) size.width) / size.height;
            if (currentRatio - screenRatio == 0) {
                result = size;
                break;
            }
        }

        if (null == result) {
            for (Camera.Size size : pictureSizeList) {
                float curRatio = ((float) size.width) / size.height;
                if (Math.abs(curRatio - 4f / 3) < .0000001) {// 默认w:h = 4:3
                    result = size;
                    break;
                }
            }
        }

        return result;
    }

    public Camera.Size getProperSize2(List<Camera.Size> list, Point dstSize) {
        //Collections.sort(list, sizeComparator);
        int min_idx = 0, diff, min_diff, i = 0;
        min_diff = Math.abs(list.get(0).height - dstSize.y)
                + Math.abs(list.get(0).width - dstSize.x);
        for (Camera.Size s : list) {    // to find the most-matched view area
            diff = Math.abs(s.height - dstSize.y)
                    + Math.abs(s.width - dstSize.x);
            //System.out.println("diff:"+diff+" min_diff:"+min_diff);
            if (diff < min_diff) {
                min_diff = diff;
                min_idx = i;
            }
            i++;
        }
        return list.get(min_idx);
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    /**
     * 监听是否开启权限
     * @param onPermissionListener
     */
    public void setOnPermissionListener(onPermissionListener onPermissionListener) {
        this.onPermissionListener = onPermissionListener;
    }

    /**
     * 相机聚焦
     */
    public void focus() {
        setCameraParams2();
        camera.startPreview();
        camera.cancelAutoFocus();
    }

    public interface onPermissionListener {

        /**
         * 授权
         * @param granted
         */
        void granted(boolean granted);
    }
}
