package com.example.ged;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class VR_Helicopter2 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a horizontal LinearLayout
//       getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.HORIZONTAL);
        mainLayout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        // Create two TextViews (you can replace with any other views)
        CameraView leftView = new CameraView(this);
        leftView.setText("Left View");
        leftView.setLayoutParams(new LinearLayout.LayoutParams(0, -1, 1.0f)); // Weight to occupy half of the screen

        TextView rightView = new TextView(this);
        rightView.setText("Right View");
        leftView.setLayoutParams(new LinearLayout.LayoutParams(0, -1, 1.0f)); // Weight to occupy half of the screen

        // Add views to the main layout
        mainLayout.addView(leftView);
        mainLayout.addView(rightView);

        // Set the main layout as the content view
        setContentView(mainLayout);
    }

    class CameraView extends TextView {
        Camera camera=new Camera(200,200,800,500);
        int frameDelay = 50;                          // time between frames in milli sec

        WaveFrontElement wf=new WaveFrontElement("/data/data/com.example.VR_Helicopter2/files/Helicopter.obj");
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
            canvas.drawText("WaveFrontTest ++", 0, paint.getTextSize(), paint);
            camera.drawAxis(canvas, 1);
            wf.draw(camera, canvas);
            for (int i=0; i<wf.vertices.length; i++)
                wf.vertices[i]=Rz.mul(wf.vertices[i]); // Rotate

            postInvalidateDelayed(frameDelay);                      // re-invoke onDraw()
        }
    }

}
