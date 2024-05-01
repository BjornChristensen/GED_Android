package com.example.ged;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class VR_Cube_360 extends AppCompatActivity {
    SensorManager mSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        setContentView(new PaintWiew(this));
    }

    public class PaintWiew extends View implements SensorEventListener {
        // Animation & Simulating time
        double t0 = System.currentTimeMillis();     // Timestamp of simulation start in ms
        int frameRate = 25;                         // No of frames/second
        int frameDelay = 1000 / frameRate;          // time between frames in milli sec

        // VR parameters. Figure out where on the screen the two cameras should paint
        double eye_dist_mm = 64;                    // Distance of my (Bjørn) pupils in mm
//        double width_mm = 113;                      // Width of Samsung Galaxy S6 in mm
//        int width_px = 2560;                        // Screen width in px in landscape mode of S6
//        int height_px = 1440;                       // Screen height in px in landscape mode of S6
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
        V3[] cube = new V3[8];
        V3 center = new V3(0, 0, 0);

        double theta = Math.PI / 1000;            // Rotation angle step size
        M3 I = new M3(1, 0, 0,                    // Identity matrix
                      0, 1, 0,
                      0, 0, 1);
        M3 Sz = new M3(0,-1, 0,                  // xy-plane rotation around z-axis
                       1, 0, 0,
                       0, 0, 0);
        M3 Rz = I.add(Sz.mul(Math.sin(theta))).add(Sz.mul(Sz).mul((1 - Math.cos(theta)))); // Eq (2.19)

        // Paint tool
        Paint paint = new Paint();

        public PaintWiew(Context context) {
            super(context);
            postInvalidate();             // start the animation by indirectly invoking onDraw()

            cube[0] = new V3(1, 4, 1);
            cube[1] = new V3(1, 4, 3);
            cube[2] = new V3(1, 6, 1);
            cube[3] = new V3(1, 6, 3);
            cube[4] = new V3(3, 4, 1);
            cube[5] = new V3(3, 4, 3);
            cube[6] = new V3(3, 6, 1);
            cube[7] = new V3(3, 6, 3);
            for (int i = 0; i < cube.length; i++) {
                center = center.add(cube[i]);
            }
            center = center.mul(1.0 / 8);

            // VR: Place left and right camera in virtual world
            double d = 0.8;       // Distance between left and right camera in virtual world
            left.moveTo(new V3(10, 3 - d / 2, 2.2));
            left.focus(center);
            right.moveTo(new V3(10, 3 + d / 2, 2.2));
            right.focus(center);

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

            // Draw application
            canvas.drawText("VR_Cube_360 xx "+getWidth()+" "+getHeight(), 0, paint.getTextSize(), paint);
            left.drawAxis(canvas, 1);
            left.drawLine(canvas, cube[0], cube[1]);
            left.drawLine(canvas, cube[1], cube[3]);
            left.drawLine(canvas, cube[3], cube[2]);
            left.drawLine(canvas, cube[2], cube[0]);
            left.drawLine(canvas, cube[4], cube[5]);
            left.drawLine(canvas, cube[5], cube[7]);
            left.drawLine(canvas, cube[7], cube[6]);
            left.drawLine(canvas, cube[6], cube[4]);
            left.drawLine(canvas, cube[0], cube[4]);
            left.drawLine(canvas, cube[1], cube[5]);
            left.drawLine(canvas, cube[3], cube[7]);
            left.drawLine(canvas, cube[2], cube[6]);

            right.drawAxis(canvas, 1);
            right.drawLine(canvas, cube[0], cube[1]);
            right.drawLine(canvas, cube[1], cube[3]);
            right.drawLine(canvas, cube[3], cube[2]);
            right.drawLine(canvas, cube[2], cube[0]);
            right.drawLine(canvas, cube[4], cube[5]);
            right.drawLine(canvas, cube[5], cube[7]);
            right.drawLine(canvas, cube[7], cube[6]);
            right.drawLine(canvas, cube[6], cube[4]);
            right.drawLine(canvas, cube[0], cube[4]);
            right.drawLine(canvas, cube[1], cube[5]);
            right.drawLine(canvas, cube[3], cube[7]);
            right.drawLine(canvas, cube[2], cube[6]);

            // Rotate cube
            for (int i = 0; i < cube.length; i++) {
                cube[i] = cube[i].sub(center);
                cube[i] = Rz.mul(cube[i]);
                cube[i] = cube[i].add(center);
            }
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
                SensorManager.getRotationMatrixFromVector(mRotationMatrix ,event.values);

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