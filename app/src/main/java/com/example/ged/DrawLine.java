package com.example.ged;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class DrawLine extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(new DrawView(this));
  }

  class DrawView extends View {
    private Paint paint= new Paint();
    public DrawView(Context context) {
      super(context);
      paint.setStrokeWidth(10); // Set line thickness
      paint.setTextSize(100);
    }

    @Override
    protected void onDraw(Canvas canvas) {
      super.onDraw(canvas);
      canvas.drawText("Hello World", 0,100, paint);
      canvas.drawLine(0, 0, 500, 700, paint);
    }
  } // DrawView
} // DrawLine