package com.example.ged;

import static java.lang.Math.*;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class WaveFrontTest  extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new CameraView(this));
    }

    class CameraView extends View {
        Camera camera=new Camera(200,200,1000,500);
        int frameDelay = 50;                          // time between frames in milli sec

        WaveFrontElement wf=new WaveFrontElement("/data/data/com.example.WaveFrontTest/files/Helicopter.obj");
        double phi=PI/100;                          // Rotation angle in each simulation step
        M3 Rz=new M3(cos(phi), -sin(phi), 0,        // Rotation matrix around z-axiz
                sin(phi),  cos(phi), 0,
                0,         0, 1);

        Paint paint = new Paint();

        CameraView(Context context) {
            super(context);
            camera.moveTo(new V3(10,5,2));
            camera.focus(wf.center());

            // Paint
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(10);     // Set line thickness
            paint.setTextSize(50);

            postInvalidate();             // start the animation by indirectly invoking onDraw()
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawText("WaveFrontTest +", 0, paint.getTextSize(), paint);
            camera.drawAxis(canvas, 1);
            wf.draw(camera, canvas);
            for (int i=0; i<wf.vertices.length; i++)
                wf.vertices[i]=Rz.mul(wf.vertices[i]); // Rotate

            postInvalidateDelayed(frameDelay);                      // re-invoke onDraw()
        }
    }
}