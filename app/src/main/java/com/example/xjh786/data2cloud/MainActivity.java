package com.example.xjh786.data2cloud;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity implements SensorEventListener {
    private  String LongT,LatT,HearB,tempS,accelS;
    private TextView timeV,AccelTV,temperature,HBTV;
    private SensorManager mSensorManager = null;
    private Sensor mTempSensor= null;
    private Sensor mAccSensor= null;
    SeekBar HBSB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temperature=((TextView)findViewById(R.id.tempTV));
        timeV = ((TextView)findViewById(R.id.timeTV));
        AccelTV =((TextView)findViewById(R.id.accelTV));
        HBTV =((TextView)findViewById(R.id.heartTV));
        LocationManager mlocation = (LocationManager)getSystemService(LOCATION_SERVICE);
        HBSB = (findViewById(R.id.seekBar3));
        HBSB.setMax(150);

        sensorData();
    }

    public void onClickSubmit(View view) {


        Spinner longSP = ((findViewById(R.id.longDD)));
        LongT = longSP.getSelectedItem().toString();
        Spinner latSP =(findViewById(R.id.latDD));
        LatT=latSP.getSelectedItem().toString();

        int seekBarValue = HBSB.getProgress();
        HearB = String.valueOf(seekBarValue);
        //String accel, String Lat, String Long, String Heart, String Temp, String timeD
        final String accel = accelS;
        final String Lat = LatT;//etCoreId.getText().toString();
        final String Long =LongT;
        final String Heart = HearB;//etEmail.getText().toString();
        final String Temp = tempS;
        final String timeD = currentTimeStr();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //hidePDialog();
                Log.d(TAG, "onRegisterSubmit: response received :" + response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean b_success = jsonResponse.getBoolean("success");

                    Log.d(TAG, "onRegisterSubmit: try to decode content");

                    if (b_success) {
                        Log.d(TAG, "onRegisterSubmit: success");

                        //Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        // intent.putExtra("registerSuccess", true);

                        // RegisterActivity.this.startActivity(intent);
                    } else {
                        Log.d(TAG, "onRegisterSubmit: fail");
                        //AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        //builder.setMessage("Login Failed")
                        //      .setNegativeButton("Retry", null)
                        //      .create()
                        //     .show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        add registerRequest = new add(accel, Lat, Long, Heart, Temp, timeD, responseListener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(registerRequest);
    }
    private void sensorData(){
        mSensorManager = ((SensorManager)getSystemService(SENSOR_SERVICE));
        mAccSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mTempSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

        mSensorManager.registerListener(this, mAccSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mTempSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    public void onSensorChanged(SensorEvent event) {
        timeV.setText("Time: " +currentTimeStr());
        HBTV.setText("Heart Beat: " + String.valueOf(HBSB.getProgress()));
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelS=String.valueOf(event.values[0]);
            String msg = "Accel Data: " + (int)event.values[0];
            AccelTV.setText(msg);
        }
        else if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            tempS=String.valueOf(event.values[0]);
            String msg = "Temperature: "+(int)event.values[0] + "C";
            temperature.setText(msg);
        }

    }
    private String currentTimeStr() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        return df.format(c.getTime());
    }
}
