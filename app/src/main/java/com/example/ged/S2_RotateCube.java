// Bjørn Christensen, 1/5 2024
package com.example.ged;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class S2_RotateCube extends View {
  // Animation & Simulating time
  double t0=System.currentTimeMillis();  // Timestamp of simulation start in ms
  int frameRate=25;                      // No of frames/second
  int frameDelay = 1000/frameRate;       // time between frames in milli sec

  // Application
  Camera S=new Camera(300,300,800,800);
  V3[] cube=new V3[8];
  V3   center=new V3(0,0,0);

  // Rotation matrices for rotating the cube
  double theta=Math.PI/1000;           // Rotation angle stepsize
  M3 I =new M3( 1, 0, 0,              // Identity matrix
          0, 1, 0,
          0, 0, 1);
  M3 Sz=new M3( 0,-1, 0,              // xy-plane rotation around z-axis
          1, 0, 0,
          0, 0, 0);
  M3 Rz=I.add(Sz.mul(Math.sin(theta)))
          .add(Sz.mul(Sz).mul((1-Math.cos(theta)))); // Eq (2.19)

  Paint paint=new Paint();

  public S2_RotateCube(Context context) {
    super(context);
    postInvalidate();             // start the animation by indirectly invoking onDraw()

    cube[0]=new V3(1,4,1);
    cube[1]=new V3(1,4,3);
    cube[2]=new V3(1,6,1);
    cube[3]=new V3(1,6,3);
    cube[4]=new V3(3,4,1);
    cube[5]=new V3(3,4,3);
    cube[6]=new V3(3,6,1);
    cube[7]=new V3(3,6,3);
    for (int i=0; i<cube.length; i++){ center=center.add(cube[i]); }
    center=center.mul(1.0/8);
    S.moveTo(new V3(10,5,2));
    S.focus(center);

    paint.setColor(Color.BLACK);
    paint.setStrokeWidth(10);     // Set line thickness
    paint.setTextSize(100);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    double t=(System.currentTimeMillis()-t0)/1000.0;  // Get elapsed time in sec
    postInvalidateDelayed(frameDelay);                // re-invoke onDraw()

    canvas.drawText("S2_RotateCube", 0,100, paint);
    S.drawAxis(canvas, 1);
    S.drawLine(canvas, cube[0], cube[1]);
    S.drawLine(canvas, cube[1], cube[3]);
    S.drawLine(canvas, cube[3], cube[2]);
    S.drawLine(canvas, cube[2], cube[0]);
    S.drawLine(canvas, cube[4], cube[5]);
    S.drawLine(canvas, cube[5], cube[7]);
    S.drawLine(canvas, cube[7], cube[6]);
    S.drawLine(canvas, cube[6], cube[4]);
    S.drawLine(canvas, cube[0], cube[4]);
    S.drawLine(canvas, cube[1], cube[5]);
    S.drawLine(canvas, cube[3], cube[7]);
    S.drawLine(canvas, cube[2], cube[6]);

    // Rotate cube
    for (int i=0; i<cube.length; i++){
      cube[i]=cube[i].sub(center);
      cube[i]=Rz.mul(cube[i]);
      cube[i]=cube[i].add(center);
    }
  }
}
