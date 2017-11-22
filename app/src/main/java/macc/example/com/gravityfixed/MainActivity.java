package macc.example.com.gravityfixed;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sm=null;
    private Sensor sensor = null;
    private float [] gravity = new float[3];
    private float [] magnetic = new float[3];
    private float [] v = new float[3];
    private float [] Rot = new float[9];
    private boolean gravityOk=false;
    private boolean magneticOk=false;
    private TextView log;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        log = (TextView) findViewById(R.id.log);
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        super.onResume();
        sensor = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sm.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);
        sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);

    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int type = sensorEvent.sensor.getType();
        if (type == Sensor.TYPE_ACCELEROMETER) {
            gravityOk = true;
            gravity = sensorEvent.values.clone();
        }
        if (type == Sensor.TYPE_MAGNETIC_FIELD) {
            magneticOk = true;
            magnetic = sensorEvent.values.clone();
        }
        if (magneticOk && gravityOk) {
            //Express the gravity vector in the fixed frame
            v=gravity;
            sm.getRotationMatrix(Rot, null, gravity, magnetic);
            float absx = Rot[0]*v[0]+Rot[1]*v[1]+Rot[2]*v[2];
            float absy=  Rot[3]*v[0]+Rot[4]*v[1]+Rot[5]*v[2];
            float absz=  Rot[6]*v[0]+Rot[7]*v[1]+Rot[8]*v[2];

            String msg= "\nX: "+String.format("%.02f",absx)+"\nY: "+String.format("%.02f",absy)+"\nZ: "+String.format("%.02f",absz);
            log.setText(msg);

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
