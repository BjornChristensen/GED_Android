package com.example.ged;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class VR_Mode extends View {
  // Animation & Simulating time
  double t0=System.currentTimeMillis();  // Timestamp of simulation start in ms
  int frameRate=25;                      // No of frames/second
  int frameDelay = 1000/frameRate;       // time between frames in milli sec

  // VR parameters
  double eye_dist_mm=64;                 // Distance of my pupils in mm
  double width_mm=113;                   // Width of Samsung Galaxy 6 in mm
  int width_px=2560;                     // Screen width in px in landscape mode
  int height_px=1440;                    // Screen height in px in landscape mode
  double ppm=width_px/width_mm;          // pixels pr. mm
  int eye_dist_px=(int)(eye_dist_mm*ppm);// pixels corresponding to real world eye distance
  int midH=width_px/2;                    // horizontal midpoint of the screen in px
  int midV=height_px/2;                   // vertical midpoint of the screen in px
  int lp=midH-eye_dist_px/2;              // horizontal position of left camera
  int rp=midH+eye_dist_px/2;              // horizontal position of right camera

  // Application
  int scale=200;  // pixels pr. unit in virtual world
  Camera left=new Camera(scale,scale,lp,midV-100);
  Camera right=new Camera(scale,scale,rp,midV-100);
  V3[] cube=new V3[8];
  V3   center=new V3(0,0,0);

  double theta=Math.PI/1000;            // Rotation angle stepsize
  M3 I =new M3( 1, 0, 0,                // Identity matrix
          0, 1, 0,
          0, 0, 1);
  M3 Sz=new M3( 0,-1, 0,                // xy-plane rotation around z-axis
          1, 0, 0,
          0, 0, 0);
  M3 Rz=I.add(Sz.mul(Math.sin(theta))).add(Sz.mul(Sz).mul((1-Math.cos(theta)))); // Eq (2.19)

  Paint paint=new Paint();

  public VR_Mode(Context context) {
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

    paint.setColor(Color.BLACK);
    paint.setStrokeWidth(10);     // Set line thickness
    paint.setTextSize(100);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    // Animation
    double t=(System.currentTimeMillis()-t0)/1000.0;  // Get elapsed time in sec
    postInvalidateDelayed(frameDelay);                // re-invoke onDraw()

    // Try different positions of left and right camera
    int it=(int)t/5;  // increase by 1 every 5 sec
    int mode=it%5;    // mode changes from 0 to 4 every 5 sec
    double d=0;       // Distance between left and right camera in virtual world
    switch (mode){
      case 0: d=0; break;
      case 1: d=0.4; break;
      case 2: d=0.6; break;
      case 3: d=0.8; break;
      case 4: d=1; break;
    }
    left.moveTo(new V3(8, 3-d/2, 2.2));
    left.focus(center);
    right.moveTo(new V3(8, 3+d/2, 2.2));
    right.focus(center);

    // Draw
    canvas.drawText("VR modeX "+mode, 0,100, paint);
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
    for (int i=0; i<cube.length; i++){
      cube[i]=cube[i].sub(center);
      cube[i]=Rz.mul(cube[i]);
      cube[i]=cube[i].add(center);
    }
  }
}
