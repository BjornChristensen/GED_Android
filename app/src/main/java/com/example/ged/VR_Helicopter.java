// Bjørn Christensen, 1/5 2024
package com.example.ged;

import static java.lang.Math.*;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class VR_Helicopter extends AppCompatActivity {
    SensorManager mSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        setContentView(new PaintWiew(this));
    }

    public class PaintWiew extends View implements SensorEventListener {
        // Animation & Simulating time (not used in ths simulation)
        double t0 = System.currentTimeMillis();     // Timestamp of simulation start in ms
        int frameRate = 10;                         // No of frames/second
        int frameDelay = 1000 / frameRate;          // time between frames in milli sec

        // VR parameters. Figure out where on the screen the two cameras should paint
        double eye_dist_mm = 64;                    // Distance of my (Bjørn) pupils in mm
        double width_mm = 142;                      // Width of Samsung Galaxy S24 in mm
        int width_px = 2400;                        // Screen width in px in landscape mode of S24
        int height_px = 1080;                       // Screen height in px in landscape mode of S24
        double ppm = width_px / width_mm;           // pixels pr. mm
        int eye_dist_px = (int) (eye_dist_mm * ppm);// pixels corresponding to real world eye distance
        int midH = width_px / 2;                    // horizontal midpoint of the screen in px
        int midV = height_px / 2;                   // vertical midpoint of the screen in px
        int lp = midH - eye_dist_px / 2;            // horizontal position of left camera
        int rp = midH + eye_dist_px / 2;            // horizontal position of right camera
        int scale = 200;                            // pixels pr. unit in virtual world
        Camera left = new Camera(scale, scale, lp, midV - 100);
        Camera right = new Camera(scale, scale, rp, midV - 100);

        // Sensor to receive physical orientation of the device (phone)
        Sensor mRotationVectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        // Application
        WaveFrontElement wf=new WaveFrontElement("/data/data/com.example.VR_Helicopter/files/Helicopter.obj");
        double phi=PI/100;                          // Rotation angle in each simulation step
        M3 Rz=new M3(cos(phi), -sin(phi), 0,        // Rotation matrix around z-axiz
                     sin(phi),  cos(phi), 0,
                            0,         0, 1);

        // Paint tool
        Paint paint = new Paint();

        public PaintWiew(Context context) {
            super(context);
            postInvalidate();             // start the animation by indirectly invoking onDraw()

            // VR: Place left and right camera in virtual world
            double d = 0.8;       // Distance between left and right camera in virtual world
            left.moveTo(new V3(10, 5 - d / 2, 2));
            right.moveTo(new V3(10, 5 + d / 2, 2));

            // Sensor: Start the rotation sensor, and get call back approx. every 10 ms
            mSensorManager.registerListener(this, mRotationVectorSensor, 10000);

            // Paint
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(10);     // Set line thickness
            paint.setTextSize(50);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            // Animation
            double t = (System.currentTimeMillis() - t0) / 1000.0;  // Get elapsed time in sec
            postInvalidateDelayed(frameDelay);                      // re-invoke onDraw()

            // Application
            canvas.drawText("VR_Helicopter", 0, paint.getTextSize(), paint);
            left.drawAxis(canvas, 1);
            right.drawAxis(canvas, 1);
            for (int i=0; i<wf.vertices.length; i++) wf.vertices[i]=Rz.mul(wf.vertices[i]); // Rotate
            wf.draw(left, canvas);
            wf.draw(right, canvas);
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                // The rotation matrix holds the coordinates of the (X,Y,Z) axis also known as
                // the (i,j,k) vectors relative to earth coordinate system.
                // First column is i, fixed to the phone and points right
                // Second column is j, fixed to the phone and points up
                // Third column is k, fixed to the phone and points out of the screen towards user
                float[] mRotationMatrix = new float[9];
                SensorManager.getRotationMatrixFromVector(mRotationMatrix , event.values);

                V3 dir=new V3(0,0,0);   // Direction of camera is -k vector
                dir.x=-mRotationMatrix[2];
                dir.y=-mRotationMatrix[5];
                dir.z=-mRotationMatrix[8];

                // Turn camera from eye point (E) in direction of -k vector
                left.focus(dir.add(left.E));
                right.focus(dir.add(right.E));
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    } // PaintView
} // main class