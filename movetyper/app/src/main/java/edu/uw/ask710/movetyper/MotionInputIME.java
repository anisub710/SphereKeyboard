package edu.uw.ask710.movetyper;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.KeyboardView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

import static android.content.ContentValues.TAG;

/**
 * Created by Anirudh Subramanyam on 11/23/2017.
 */

public class MotionInputIME extends InputMethodService implements SensorEventListener {

    private InputMethodManager inputManager;
    private SensorManager sensorManager;
    private Sensor accelerometer;

    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if(accelerometer == null){
            Log.v(TAG, "No sensor");
        }
        inputManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
    }

    @Override
    public View onCreateInputView() {
        View inputView = getLayoutInflater().inflate(R.layout.keyboard, null);
        Log.v(TAG, "Keyboard called");
        return inputView;
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        if(info.inputType != InputType.TYPE_CLASS_TEXT){
            //CHECK THIS
            inputManager.showSoftInput(getWindow().getCurrentFocus(), 0);
            Log.v(TAG, "Wrong input type");
        }else{
            Log.v(TAG, "Right input type");
        }
        super.onStartInputView(info, restarting);
    }

    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        super.onStartInput(attribute, restarting);
    }

    @Override
    public void onFinishInput() {
        sensorManager.unregisterListener(this, accelerometer);
        super.onFinishInput();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
//        Log.v(TAG, "Raw values: " + Arrays.toString(event.values));
        InputConnection ic = getCurrentInputConnection();
        String thinkingEmoji = new String(new int[]{0x1F914}, 0, 1);
        ic.commitText(thinkingEmoji, 1);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
