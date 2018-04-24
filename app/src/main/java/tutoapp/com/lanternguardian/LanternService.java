package tutoapp.com.lanternguardian;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;

public class LanternService extends Service implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mPressure;
    private Sensor mProximity;
    private boolean flashLightStatus;
    double lecturaLuz=10;
    double lecturaProximidad=50;
    BatteryManager myBatteryManager;
    public LanternService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Log.i("TAG","se creo");
        mPressure = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mProximity=mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mPressure, SensorManager.SENSOR_DELAY_UI);
        myBatteryManager = (BatteryManager) getApplicationContext().getSystemService(Context.BATTERY_SERVICE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onSensorChanged(SensorEvent event) {


        switch (event.sensor.getType()){

            case Sensor.TYPE_LIGHT :

                lecturaLuz = event.values[0];
                Log.i("lecturaL",lecturaLuz+"");

                if(lecturaProximidad!=0){//quiere decir que esta tapado

                    if(lecturaLuz==0){
                        Log.i("Luz","1");
                        if(flashLightStatus==false){
                            if (isUSBCharging() == false) {
                                flashLightOn();
                            }

                        }
                        flashLightStatus=true;
                    }else if(lecturaLuz>20){
                        flashLightStatus=false;
                        flashLightOff();

                    }
                }
            break;
            case Sensor.TYPE_PROXIMITY :

                lecturaProximidad=event.values[0];
                Log.i("LecturaP",lecturaProximidad+"");

                break;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onDestroy() {
        super.onDestroy();
        flashLightOff();
        mSensorManager.unregisterListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void flashLightOn() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, true);
            flashLightStatus = true;
            //imageFlashlight.setImageResource(R.drawable.btn_switch_on);
        } catch (CameraAccessException e) {
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void flashLightOff() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, false);
            flashLightStatus = false;
            //imageFlashlight.setImageResource(R.drawable.btn_switch_off);
        } catch (CameraAccessException e) {
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean isUSBCharging(){
        return  myBatteryManager.isCharging();
    }
}
