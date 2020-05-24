package com.example.myapplication;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

/**
 * ShakeListener
 *
 * <code>ShakeListener</code> ma na starosti sledovat kedy sa zatriaslo telefonom
 * urcitou silou a urcitim poctom krat
 */
public class ShakeListener implements SensorEventListener {
	private String TAG = ShakeListener.class.getSimpleName();
	private static final int FORCE_THRESHOLD = 800;
	private static final int TIME_THRESHOLD = 100;
	private static final int SHAKE_TIMEOUT = 500;
	private static final int SHAKE_DURATION = 1000;
	private static final int SHAKE_COUNT = 5;

	private SensorManager mSensorMgr;
	private float mLastX = -1.0f, mLastY = -1.0f, mLastZ = -1.0f;
	private long mLastTime;
	private OnShakeListener mShakeListener;
	private Context mContext;
	private int mShakeCount = 0;
	private long mLastShake;
	private long mLastForce;

	/**
	 * Interface, ktory specifikuje spravanie listenera
	 */
	public interface OnShakeListener {
		public void onShake();
	}

	public ShakeListener(Context context) {
		mContext = context;
		resume();
	}

	public void setOnShakeListener(OnShakeListener listener) {
		mShakeListener = listener;
	}

	public void resume() {
		mSensorMgr = (SensorManager) mContext
			.getSystemService(Context.SENSOR_SERVICE);
		if (mSensorMgr == null) {
			throw new UnsupportedOperationException("Sensors not supported");
		}
		boolean supported = false;
		try {
			supported = mSensorMgr.registerListener(this,
				mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_GAME);
		} catch (Exception e) {
			Toast.makeText(mContext, "Shaking not supported", Toast.LENGTH_LONG)
				.show();
		}

		if ((!supported) && (mSensorMgr != null))
			mSensorMgr.unregisterListener(this);
	}

	public void pause() {
		if (mSensorMgr != null) {
			mSensorMgr.unregisterListener(this);
			mSensorMgr = null;
		}
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	/**
	 * Zavola sa ked je novu senzor event
	 * @param event senzor event
	 */
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
			return;
		long now = System.currentTimeMillis();

		if ((now - mLastForce) > SHAKE_TIMEOUT) {
			mShakeCount = 0;
		}

		if ((now - mLastTime) > TIME_THRESHOLD) {
			long diff = now - mLastTime;
			float speed = Math.abs(event.values[SensorManager.DATA_X]
				+ event.values[SensorManager.DATA_Y]
				+ event.values[SensorManager.DATA_Z] - mLastX - mLastY
				- mLastZ)
				/ diff * 10000;
			if (speed > FORCE_THRESHOLD) {
				if ((++mShakeCount >= SHAKE_COUNT)
					&& (now - mLastShake > SHAKE_DURATION)) {
					mLastShake = now;
					mShakeCount = 0;
					Log.d(TAG, "ShakeListener mShakeListener---->" + mShakeListener);
					if (mShakeListener != null) {
						mShakeListener.onShake();
					}
				}
				mLastForce = now;
			}
			mLastTime = now;
			mLastX = event.values[SensorManager.DATA_X];
			mLastY = event.values[SensorManager.DATA_Y];
			mLastZ = event.values[SensorManager.DATA_Z];
		}
	}


}
