package edu.uw.ask710.movetyper;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.KeyboardView;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;
import static java.lang.Math.abs;

/**
 * Created by Anirudh Subramanyam on 11/23/2017.
 */

//Custom input method class
public class MotionInputIME extends InputMethodService implements SensorEventListener {

    private InputMethodManager inputManager;
    private SensorManager sensorManager;
    private Sensor linearAcc;
    private InputConnection ic;

    private View inputView;

    private Vibrator v;

    private Timer timer;
    private TimerTask timerTask;

    private ArrayList<Float> xChange;
    private ArrayList<Float> yChange;
    private ArrayList<Float> zChange;
    private boolean changeX;
    private boolean changeY;
    private boolean changeZ;

    private Handler timerHandler = new Handler();

    private String laughingEmoji = new String(new int[] {0x1F602}, 0, 1);
    private String tiredEmoji = new String(new int[] {0x1F62B}, 0, 1);
    private String thinkingEmoji = new String(new int[]{0x1F914}, 0, 1);

    private DrawingSurfaceView view;
    private SharedPreferences sharedPreferences;

    private static final String SENSITIVITY = "pref_sensitivity";


    @Override
    public void onCreate() {
        super.onCreate();

        //set up sensor manager and get linear acceleration sensor.
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        linearAcc = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if(linearAcc == null){
            Log.v(TAG, "No sensor");
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        inputManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
    }

    @Override
    public View onCreateInputView() {
        //return view with input method that has the ball and the settings button
        inputView = getLayoutInflater().inflate(R.layout.keyboard, null);
        view = (DrawingSurfaceView) inputView.findViewById(R.id.drawingView);
        Button settings = (Button) inputView.findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MotionInputIME.this, SettingsActivity.class));
            }
        });
        Log.v(TAG, "Keyboard called");
        return inputView;
    }


    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        if(info.inputType != InputType.TYPE_CLASS_TEXT) {//check input method type
            inputManager.showInputMethodPicker();
        }
        super.onStartInputView(info, restarting);
    }

    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        //register sensor listener and setup timer for repeatedly getting samples

        sensorManager.registerListener(this, linearAcc, SensorManager.SENSOR_DELAY_GAME);
        ic = getCurrentInputConnection();

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        timer = new Timer();

        xChange = new ArrayList<Float>();
        yChange = new ArrayList<Float>();
        zChange = new ArrayList<Float>();
        changeX = false;
        changeY = false;
        changeZ = false;

        timer.scheduleAtFixedRate(checkSample(), 0, 500);

        super.onStartInput(attribute, restarting);
    }

    @Override
    public void onFinishInput() { //unregister motion sensor
        sensorManager.unregisterListener(this, linearAcc);
        super.onFinishInput();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //add each coordinate to respective lists for sample
        xChange.add(event.values[0]);
        yChange.add(event.values[1]);
        zChange.add(event.values[2]);

        //check if there is acceleration in each case.
        if(changeX){
            view.ball.dx = -1*1000*0.03f;
            changeX = !changeX;
            v.vibrate(500);
            ic.commitText(tiredEmoji, 1);
        }else if(changeY){
            view.ball.dy = -1*1000*0.03f;
            changeY = !changeY;
            v.vibrate(500);
            ic.commitText(laughingEmoji, 1);
        }else if(changeZ){
            ObjectAnimator anim = ObjectAnimator.ofFloat(view.ball, "radius", view.ball.getRadius(), view.ball.getRadius() + 100f);
            anim.setDuration(1000);
            anim.start();
            anim.setRepeatCount(5);
            anim.setRepeatMode(ObjectAnimator.REVERSE);
            changeZ = !changeZ;
            v.vibrate(500);
            ic.commitText(thinkingEmoji, 1);
        }

    }

    //handles result from checking sample for each coordinate.
    public TimerTask checkSample(){
        timerTask = new TimerTask(){
            public void run(){
                timerHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(xChange != null){
                            if(checkChange(xChange)){
                                changeX = true;
                            }
                            xChange.clear();
                        }
                        if(yChange != null){
                            if(checkChange(yChange)){
                                changeY = true;
                            }
                            yChange.clear();
                        }
                        if(zChange != null){
                            if(checkChange(zChange)){
                                changeZ = true;
                            }
                            zChange.clear();
                        }

                    }
                });

            }
        };

        return timerTask;
    }

    //checks each sample for change in acceleration
    public boolean checkChange(ArrayList<Float> values){
        for(int i = 0; i < values.size(); i++){
            if(abs(values.get(i)) > Float.parseFloat(sharedPreferences.getString(SENSITIVITY, "25"))){
                return true;
            }
        }

        return false;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
