package $Package.core.view.camera;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * 相机传感器监听
 * Created by Vincent on $Time.
 */
public class ShakeListener implements SensorEventListener {

    public static final int LandscapeLeft = 0;
    public static final int LandscapeRight = 180;
    public static final int PORTRAIT = 90;
    private SensorManager sensorManager;
    private static ShakeListener shakeListener;
    private Sensor sensor;
    private OnShakeListener onShakeListener;

    public static ShakeListener newInstance() {
        if (shakeListener == null) {
            shakeListener = new ShakeListener();
        }
        return shakeListener;
    }

    public void start(Context context) {
        if (sensorManager == null) {
            sensorManager = (SensorManager) context
                    .getSystemService(Context.SENSOR_SERVICE);
        }
        if (sensorManager != null && sensor == null) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        if (sensor != null) {
            try {
                sensorManager.registerListener(this, sensor,
                        SensorManager.SENSOR_DELAY_NORMAL);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        if (sensorManager != null)
            sensorManager.unregisterListener(this);
    }

    public void close() {
        shakeListener = null;
    }

    public interface OnShakeListener {
        void onShake(int orientation);
    }

    public void setOnShakeListener(OnShakeListener listener) {
        onShakeListener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float g = 9.8f;
        float hysteresis = g / 5;
        float halfOfg = g * (float) Math.sqrt(2) / 2;
        if (Math.abs(z) > halfOfg) {
            return;
        }

        if (x <= -halfOfg - hysteresis / 2) {
            onShakeListener.onShake(LandscapeRight);
        } else if (y >= halfOfg + hysteresis / 2) {
            onShakeListener.onShake(PORTRAIT);
        } else if (x >= halfOfg + hysteresis / 2) {
            onShakeListener.onShake(LandscapeLeft);
        }

    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}