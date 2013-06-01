/**
 * 
 */
package com.magik.mundo.controllers;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

/**
 * @author Kelvin
 *
 */
public class RotationControlService extends Service implements SensorEventListener
{
	private float[] accelValues;
	private float[] magnetValues;
	private float[] orientValues;
	private float[] rotationMatrix;
	private boolean reading;
	private static RotationControlService instance;
	private SensorManager sensorManager;
	
    /*
     * (non-Javadoc)
     * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
     */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {		
		
		accelValues = new float[3];
		
		magnetValues = new float[3];
		
		orientValues = new float[3];
		
		rotationMatrix = new float[9];
		
		sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
		
		instance = this;
		
		setListeners();		
			
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		sensorManager.unregisterListener(this);
		super.onDestroy();
	}
	
    private void setListeners() {
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);
	}
    
    public static RotationControlService getInstance()
    {
    	return instance;
    }
    
    public boolean isReading()
    {
    	return reading;
    }

	/* (non-Javadoc)
     * @see android.app.Service#onBind(android.content.Intent)
     */
    @Override
    public IBinder onBind( Intent arg0 )
    {        
        return null;
    }

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		switch(event.sensor.getType())
		{
			case Sensor.TYPE_ACCELEROMETER:
				System.arraycopy(event.values, 0, accelValues, 0, 3);
				break;
			case Sensor.TYPE_MAGNETIC_FIELD:
				System.arraycopy(event.values, 0, magnetValues, 0, 3);
				break;
		}
		SensorManager.getRotationMatrix(rotationMatrix, null, accelValues, magnetValues);		
        SensorManager.getOrientation(rotationMatrix, orientValues);
        double orientY = orientValues[2]*180/Math.PI;
        if(orientY<0 && orientY>-80)
        {
        	reading = true;
        }
        else
        {
        	reading = false;
        }
        
	}

}
