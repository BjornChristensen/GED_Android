package com.example.ged;
// 2D coordinate system class
// Bjørn Christensen, 3/3-2024

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import static java.lang.Math.*;

public class S2 {
    static final V2 Origo=new V2(0,0);
    V2 O;
    M2 S;
    M2 F;
    M2 T;

    Paint paint=new Paint();
    TextPaint textPaint=new TextPaint();

    S2(int sx, int sy, int ox, int oy){
        O=new V2(ox,oy);        // Position in pixels of coordinate system on screen
        F=new M2(1, 0,          // Matrix to flip y-axis
                 0,-1);
        S=new M2(sx,  0,        // Scaling matrix
                  0, sy);       // sx: number of pixels for 1 unit in x-axis of virtual world coordinate system
        T=F.mul(S);             // Combined transformation matrix

        textPaint.setTextSize(25);  // Text height in pixels
    }

    V2 transform(V2 v){         // Transform from 2D virtual world coordinates to pixels
        return T.mul(v).add(O);
    }
    void moveTo(V2 p){   // Move origo to new position p
        O=transform(p);
    }
    void rotate(double phi){        // Rotate axis
        M2 R=new M2(cos(phi), -sin(phi),
                    sin(phi), cos(phi));
        T=T.mul(R);
    }

    void setStrokeWidth(float w){
        paint.setStrokeWidth(w);
    }

    public void drawAxis(Canvas canvas){
        drawLine(canvas, new V2(0,0), new V2(1,0));              // X-axis
        drawLine(canvas, new V2(0.9,0.1), new V2(1,0));
        drawLine(canvas, new V2(0.9,-0.1), new V2(1,0));
        drawString(canvas, new V2(1.1,0), "x");

        drawLine(canvas, new V2(0,0), new V2(0,1));              // Y-axis
        drawLine(canvas, new V2(-0.1,0.9), new V2(0,1));
        drawLine(canvas, new V2(0.1,0.9), new V2(0,1));
        drawString(canvas, new V2(0,1.1), "y");
    }

    public void drawLine(Canvas canvas, V2 v1, V2 v2){
        V2 p1=transform(v1);              // point in pixels
        V2 p2=transform(v2);              // point in pixels
        canvas.drawLine((float)p1.x, (float)p1.y, (float)p2.x, (float)p2.y, paint);
    }

    public void drawPoint(Canvas canvas, V2 v){
        V2 p=transform(v);                // p in pixels
        canvas.drawPoint((float)p.x, (float)p.y, paint);
    }

    void drawString(Canvas canvas, V2 v, String text){
        V2 p=transform(v);   // point in pixels
        canvas.drawText(text, (float) p.x, (float) p.y, textPaint);
    }
}