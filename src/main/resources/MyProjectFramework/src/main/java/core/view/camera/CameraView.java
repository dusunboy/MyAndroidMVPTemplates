package $Package.core.view.camera;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 自定义相机View
 * Created by Vincent on $Time.
 */
public class CameraView implements SurfaceHolder.Callback,
        Camera.AutoFocusCallback, View.OnTouchListener, ShakeListener.OnShakeListener {

    /**
     * camera flash mode
     */
    public final static int FLASH_AUTO = 2;
    public final static int FLASH_OFF = 0;
    public final static int FLASH_ON = 1;

    /**
     * camera preview size
     */
    public static final int MODE4T3 = 43;
    public static final int MODE16T9 = 169;

    private int currentMODE = MODE4T3;

    private SurfaceHolder holder;
    private Camera camera;
    private CameraSizeComparator sizeComparator = new CameraSizeComparator();
    private FocusView focusView;
    // 0 close , 1 open , 2 auto
    private int flash_type = FLASH_AUTO;
    // 0 back camera , 1 front camera
    private int cameraPosition = Camera.CameraInfo.CAMERA_FACING_BACK;
    private int takePhotoOrientation = 90;

    private int topDistance;
    private int zoomFlag = 0;

    private SurfaceView surfaceView;
    private int screenDpi;
    /**
     * touch focus area size
     */
    private float focusAreaSize = 300;
    private OnCameraSelectListener onCameraSelectListener;

    private int picQuality = 100;
    private onPermissionListener onPermissionListener;
    private Context context;

    public CameraView(Context context, FocusView focusView, SurfaceView cameraSurfaceView) {
        this.context = context;
        this.focusView = focusView;
        setCameraView(cameraSurfaceView);
    }

    private void setCameraView(SurfaceView surfaceView) throws NullPointerException, ClassCastException {
        this.setCameraView(surfaceView, MODE4T3);
    }

    /**
     * @param surfaceView the camera view you should give it
     * @param cameraMode  set the camera preview proportion ,default is MODE4T3; {@link #MODE4T3}
     * @throws Exception
     */
    public void setCameraView(SurfaceView surfaceView, int cameraMode) throws NullPointerException, ClassCastException {
        this.surfaceView = surfaceView;
        this.currentMODE = cameraMode;
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        if (currentMODE == MODE4T3) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) this.surfaceView.getLayoutParams();
            layoutParams.width = screenWidth;
            layoutParams.height = screenWidth * 4 / 3;
            this.surfaceView.setLayoutParams(layoutParams);
        } else if (currentMODE == MODE16T9) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) this.surfaceView.getLayoutParams();
            layoutParams.width = screenWidth;
            layoutParams.height = screenWidth * 16 / 9;
            this.surfaceView.setLayoutParams(layoutParams);
        }
        ShakeListener.newInstance().setOnShakeListener(this);
        screenDpi = context.getResources().getDisplayMetrics().densityDpi;
        holder = surfaceView.getHolder();
        surfaceView.setOnTouchListener(this);
        holder.addCallback(this);
        holder.setKeepScreenOn(true);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    /**
     * set camera top distance
     *
     * @param topDistance
     */
    public void setTopDistance(int topDistance) {
        this.topDistance = topDistance;
    }

    public void setFocusView(FocusView focusView) {
        this.focusView = focusView;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (screenDpi == DisplayMetrics.DENSITY_HIGH) {
            zoomFlag = 10;
        }
        if (camera == null) {
            try {
                if (onPermissionListener != null) {
                    onPermissionListener.granted(true);
                }
                onResume();
            } catch (Exception e) {
                e.printStackTrace();
                if (onPermissionListener != null) {
                    onPermissionListener.granted(false);
                }
            }
        } else {
            if (onPermissionListener != null) {
                onPermissionListener.granted(false);
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (camera == null) {
        } else {
            this.holder = holder;
            this.holder.setKeepScreenOn(true);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        closeCamera();
        ShakeListener.newInstance().close();
    }

    public void openCamera() {
        try {
            if (onPermissionListener != null) {
                onPermissionListener.granted(true);
            }
            closeCamera();
            camera = Camera.open(cameraPosition);
            camera.setDisplayOrientation(takePhotoOrientation);
            camera.setPreviewDisplay(holder);
            setCameraPictureSize();
            setCameraPreviewSize();
            changeFlash(flash_type);
            if (camera == null) {
                if (onPermissionListener != null) {
                    onPermissionListener.granted(false);
                }
            } else {
                camera.startPreview();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (onPermissionListener != null) {
                onPermissionListener.granted(false);
            }
        }
    }

    public void closeCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
        }
        flash_type = FLASH_AUTO;
        camera = null;
    }

    public void resetCamera() {
        if (onCameraSelectListener != null) {
            onCameraSelectListener.onChangeCameraPosition(cameraPosition);
        }
        closeCamera();
        openCamera();
    }

    private void setCameraPreviewSize() {
        Camera.Parameters params = camera.getParameters();
        List<Camera.Size> sizes = params.getSupportedPreviewSizes();
        Collections.sort(sizes, sizeComparator);
        for (Camera.Size size : sizes) {
            params.setPreviewSize(size.width, size.height);
//            if (size.width * 1.0 / size.height * 1.0 == 4.0 / 3.0 && currentMODE == MODE4T3) {
            if (Math.abs(size.width * 1.0 / size.height * 1.0 - 4.0 / 3.0) < .0000001 && currentMODE == MODE4T3) {
                break;
//            } else if (size.width * 1.0 / size.height * 1.0 == 16.0 / 9.0 && currentMODE == MODE16T9) {
            } else if (Math.abs(size.width * 1.0 / size.height * 1.0 - 16.0 / 9.0) < .0000001 && currentMODE == MODE16T9) {
                break;
            }
        }
        camera.setParameters(params);
    }

    private void setCameraPictureSize() {
        Camera.Parameters params = camera.getParameters();
        List<Camera.Size> sizes = params.getSupportedPictureSizes();
        Collections.sort(sizes, sizeComparator);
        for (Camera.Size size : sizes) {
            params.setPictureSize(size.width, size.height);
//            if (size.width * 1.0 / size.height * 1.0 == 4.0 / 3.0
//                    && currentMODE == MODE4T3 && size.height < 2000) {
            if (Math.abs(size.width * 1.0 / size.height * 1.0 - 4.0 / 3.0) < .0000001
                    && currentMODE == MODE4T3 && size.height < 2000) {
                break;
//            } else if (size.width * 1.0 / size.height * 1.0 == 16.0 / 9.0
//                    && currentMODE == MODE16T9 && size.height < 2000) {
            } else if (Math.abs(size.width * 1.0 / size.height * 1.0 - 16.0 / 9.0) < .0000001
                    && currentMODE == MODE16T9 && size.height < 2000) {
                break;
            }
        }
        params.setJpegQuality(picQuality);
        params.setPictureFormat(ImageFormat.JPEG);
        camera.setParameters(params);
    }

    /**
     * use with activity or fragment life circle
     */
    public final void onResume() {
        if (surfaceView == null)
            throw new NullPointerException("not init surfaceView for camera view");
        openCamera();
        ShakeListener.newInstance().start(context);
    }

    /**
     * seem to onResume
     * {@link #onResume()}
     */
    public final void onPause() {
        closeCamera();
        ShakeListener.newInstance().stop();
    }

    public final int changeFlash(int flash_type) {
        this.flash_type = flash_type;
        return changeFlash();
    }

    /**
     * change camera flash mode
     */
    public final int changeFlash() {
        if (camera == null) {
            return -1;
        }
        Camera.Parameters parameters = camera.getParameters();
        List<String> flashModes = parameters.getSupportedFlashModes();
        if (flashModes == null || flashModes.size() <= 1) {
            return 0;
        }
        if (onCameraSelectListener != null) {
            onCameraSelectListener.onChangeFlashMode((flash_type) % 3);
        }
        switch (flash_type % 3) {
            case FLASH_ON:
                if (flashModes.contains(Camera.Parameters.FLASH_MODE_ON)) {
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                    flash_type++;
                    camera.setParameters(parameters);
                }
                break;
            case FLASH_OFF:
                if (flashModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    flash_type++;
                    camera.setParameters(parameters);
                }
                break;
            case FLASH_AUTO:
                if (flashModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                    flash_type++;
                    camera.setParameters(parameters);
                }
                break;
            default:
                if (flashModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    flash_type++;
                    camera.setParameters(parameters);
                }
                break;
        }
        return flash_type;
    }

    /**
     * change camera facing
     */
    public final int changeCamera() {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int cameraCount = Camera.getNumberOfCameras();
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraPosition == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraPosition = Camera.CameraInfo.CAMERA_FACING_FRONT;
                resetCamera();
                return cameraPosition;
            } else if (cameraPosition == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraPosition = Camera.CameraInfo.CAMERA_FACING_BACK;
                resetCamera();
                return cameraPosition;
            }
        }
        return cameraPosition;
    }


    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        if (focusView != null) {
            focusView.clearDraw();
        }
    }

    /**
     * Convert touch position x:y in (-1000~1000)
     */
    private Rect calculateTapArea(float x, float y, float coefficient) {
        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();
        x = x / surfaceView.getWidth();
        y = y / surfaceView.getHeight();

        float cameraX = y;
        float cameraY = 1 - x;

        int centerX = (int) (cameraX * 2000 - 1000);
        int centerY = (int) (cameraY * 2000 - 1000);
        int left = clamp(centerX - areaSize / 2, -1000, 1000);
        int top = clamp(centerY - areaSize / 2, -1000, 1000);
        int right = clamp(left + areaSize, -1000, 1000);
        int bottom = clamp(top + areaSize, -1000, 1000);

        return new Rect(left, top, right, bottom);
    }

    private int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

    private float mDist;

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (camera == null) {
            return false;
        }
        Camera.Parameters params = camera.getParameters();
        int action = event.getAction();

        if (event.getPointerCount() > 1) {
            if (action == MotionEvent.ACTION_POINTER_DOWN) {
                mDist = getFingerSpacing(event);
            } else if (action == MotionEvent.ACTION_MOVE && params.isZoomSupported()) {
                camera.cancelAutoFocus();
                handleZoom(event, params);
            }
            if (focusView != null) {
                focusView.clearDraw();
            }
        } else {
            if (action == MotionEvent.ACTION_DOWN) {
                if (focusView != null) {
                    focusView.clearDraw();
                    focusView.drawLine(event.getRawX(), event.getRawY() - topDistance);
                }
            }
            if (action == MotionEvent.ACTION_UP) {
                handleFocus(event);
            }
        }
        return true;
    }

    private void handleZoom(MotionEvent event, Camera.Parameters params) {
        int maxZoom = params.getMaxZoom();
        int zoom = params.getZoom();
        float newDist = getFingerSpacing(event);
        if (newDist > mDist && newDist - mDist > zoomFlag) {
            if (zoom < maxZoom)
                zoom++;
        } else if (newDist < mDist && mDist - newDist > zoomFlag) {
            if (zoom > 0)
                zoom--;
        }
        mDist = newDist;
        params.setZoom(zoom);
        camera.setParameters(params);
    }

    public void handleFocus(MotionEvent event) {
        if (camera != null) {
            camera.cancelAutoFocus();

            Rect focusRect = calculateTapArea(event.getRawX(), event.getRawY() - topDistance, 1f);
            Rect meteringRect = calculateTapArea(event.getRawX(), event.getRawY() - topDistance, 2f);

            Camera.Parameters parameters = camera.getParameters();
            if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_AUTO))
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

            if (parameters.getMaxNumFocusAreas() > 0) {
                List<Camera.Area> areaList = new ArrayList<Camera.Area>();
                areaList.add(new Camera.Area(focusRect, 1000));
                parameters.setFocusAreas(areaList);
            }

            if (parameters.getMaxNumMeteringAreas() > 0) {
                List<Camera.Area> meteringList = new ArrayList<Camera.Area>();
                meteringList.add(new Camera.Area(meteringRect, 1000));
                parameters.setMeteringAreas(meteringList);
            }

            camera.setParameters(parameters);
            camera.autoFocus(this);
        }
    }

    private float getFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    @Override
    public void onShake(int orientation) {
        if (onCameraSelectListener != null) {
            onCameraSelectListener.onShake(orientation);
        }
        this.takePhotoOrientation = orientation;
    }

    public interface OnCameraSelectListener {

        void onChangeFlashMode(int flashMode);

        void onChangeCameraPosition(int cameraPosition);

        void onShake(int orientation);
    }

    public static class CameraSizeComparator implements Comparator<Camera.Size> {
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            if (lhs.width == rhs.width) {
                return 0;
            } else if (lhs.width < rhs.width) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    public Camera getCamera() {
        return camera;
    }

    /**
     * 监听是否开启权限
     * @param onPermissionListener
     */
    public void setOnPermissionListener(onPermissionListener onPermissionListener) {
        this.onPermissionListener = onPermissionListener;
    }

    public interface onPermissionListener {

        /**
         * 授权
         * @param granted
         */
        void granted(boolean granted);
    }

    public void setPicQuality(int picQuality) {
        if (picQuality > 0 && picQuality < 101)
            this.picQuality = picQuality;
    }

    public void setOnCameraSelectListener(OnCameraSelectListener onCameraSelectListener) {
        this.onCameraSelectListener = onCameraSelectListener;
    }

    public void setFocusAreaSize(float focusAreaSize) {
        this.focusAreaSize = focusAreaSize;
    }

}