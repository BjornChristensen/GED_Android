// Bjørn Christensen, 1/5 2024
package com.example.ged;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import java.util.Random;

public class DrawViewAnimation extends View {
  // Animation & Simulating time
  double t0=System.currentTimeMillis(); // Timestamp of simulation start in sec
  double t=0;                           // Time in sec since simulation start
  int frameRate=25;                     // No of frames/second
  int frameDelay = 1000/frameRate;      // time between frames in milli sec

  // Application
  Paint paint;
  Random gen=new Random();
  static int count_ViewObjects=0;  // Every time You rotate the phone a new View object is created !
  int count_onDraw_calls=0;        // count calls to onDraw() for each new View object

  public DrawViewAnimation(Context context) {
    super(context);
    postInvalidate();             // start the animation by indirectly invoking onDraw()

    count_ViewObjects++;
    paint = new Paint();
    paint.setColor(Color.BLACK);
    paint.setStrokeWidth(10);     // Set line thickness
    paint.setTextSize(100);       // Text height in pixels
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    t=System.currentTimeMillis()-t0;        // Get elapsed time
    postInvalidateDelayed(frameDelay);      // re-invoke onDraw()

    canvas.drawText("DrawViewAnimation", 0,100, paint);
    int x1=gen.nextInt(1000);
    int y1=gen.nextInt(1000);
    int x2=gen.nextInt(1000);
    int y2=gen.nextInt(1000);
    canvas.drawLine(x1,y1, x2,y2, paint);
    count_onDraw_calls++;
    canvas.drawText("count_ViewObjects="+count_ViewObjects, 0,200, paint);
    canvas.drawText("count_onDraw_calls="+count_onDraw_calls, 0,300, paint);
  }
}