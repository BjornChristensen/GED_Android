// Bjørn Christensen, 1/5 2024
package com.example.ged;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TwinView extends AppCompatActivity {
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
        TextView leftView = new TextView(this);
        leftView.setText("Left View \n");
        leftView.setLayoutParams(new LinearLayout.LayoutParams(
                0, // Width set to 0 to distribute equally
                ViewGroup.LayoutParams.MATCH_PARENT,
                1.0f)); // Weight to occupy half of the screen

        TextView rightView = new TextView(this);
        rightView.setText("Right View");
        rightView.setLayoutParams(new LinearLayout.LayoutParams(
                0, // Width set to 0 to distribute equally
                ViewGroup.LayoutParams.MATCH_PARENT,
                1.0f)); // Weight to occupy half of the screen

        // Add views to the main layout
        mainLayout.addView(leftView);
        mainLayout.addView(rightView);

        // Set the main layout as the content view
        setContentView(mainLayout);
    }
}
