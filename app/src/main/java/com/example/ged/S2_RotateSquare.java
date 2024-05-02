// Bjørn Christensen, 1/5 2024
package com.example.ged;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class S2_RotateSquare extends View {
  // Animation & Simulating time
  double t0=System.currentTimeMillis();  // Timestamp of simulation start in ms
  int frameRate=25;                      // No of frames/second
  int frameDelay = 1000/frameRate;       // time between frames in milli sec

  // Application
  S2 S=new S2(100,100, 500,700);
  V2 A=new V2(2,2);
  V2 B=new V2(4,2);
  V2 C=new V2(4,4);
  V2 D=new V2(2,4);
  V2 P=A.add(B).add(C).add(D).mul(1.0/4);     // Rotations centrum
  double f=0.5;                               // Frekvens: antal omdrejninger pr sec
  double omega=2*PI*f;                        // vinkelhastighed

  Paint paint=new Paint();

  public S2_RotateSquare(Context context) {
    super(context);
    postInvalidate();             // start the animation by indirectly invoking onDraw()

    paint.setColor(Color.BLACK);
    paint.setStrokeWidth(10);     // Set line thickness
    paint.setTextSize(100);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    double t=(System.currentTimeMillis()-t0)/1000.0;  // Get elapsed time in sec
    postInvalidateDelayed(frameDelay);                // re-invoke onDraw()

    canvas.drawText("S2_RotateSquare"+t, 0,100, paint);
    double phi=omega*t;                     // Rotations vinkel
    M2 M=new M2(cos(phi), -sin(phi),
                sin(phi), cos(phi));
    V2 Ar=M.mul(A.sub(P)).add(P);
    V2 Br=M.mul(B.sub(P)).add(P);
    V2 Cr=M.mul(C.sub(P)).add(P);
    V2 Dr=M.mul(D.sub(P)).add(P);

    S.drawAxis(canvas);
    S.drawLine(canvas,Ar,Br);
    S.drawLine(canvas,Br,Cr);
    S.drawLine(canvas,Cr,Dr);
    S.drawLine(canvas,Dr,Ar);
    S.drawPoint(canvas,P);
  }
}
