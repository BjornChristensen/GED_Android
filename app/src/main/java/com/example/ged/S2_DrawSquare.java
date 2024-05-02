// Bjørn Christensen, 1/5 2024
package com.example.ged;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import static java.lang.Math.*;

public class S2_DrawSquare extends View {
  Paint paint=new Paint();

  S2 S=new S2(100,100, 500,800);
  V2 A=new V2(2,2);
  V2 B=new V2(4,2);
  V2 C=new V2(4,4);
  V2 D=new V2(2,4);
  V2 P=A.add(B).add(C).add(D).mul(1.0/4);     // Rotations punkt
  double phi=PI/3;                            // Rotations vinkel
  M2 M=new M2(cos(phi), -sin(phi),
              sin(phi), cos(phi));
  V2 Ar=M.mul(A.sub(P)).add(P);
  V2 Br=M.mul(B.sub(P)).add(P);
  V2 Cr=M.mul(C.sub(P)).add(P);
  V2 Dr=M.mul(D.sub(P)).add(P);

  public S2_DrawSquare(Context context) {
    super(context);
    paint.setColor(Color.BLACK);
    paint.setStrokeWidth(10); // Set line thickness
    paint.setTextSize(100);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.drawText("S2_DrawSquare", 0,100, paint);
    S.drawAxis(canvas);
    S.drawLine(canvas,A,B);
    S.drawLine(canvas,B,C);
    S.drawLine(canvas,C,D);
    S.drawLine(canvas,D,A);
    S.drawPoint(canvas,P);
    S.drawLine(canvas,Ar,Br);
    S.drawLine(canvas,Br,Cr);
    S.drawLine(canvas,Cr,Dr);
    S.drawLine(canvas,Dr,Ar);
  }
}
