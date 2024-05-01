package com.example.ged;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ReadFile extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv=new TextView(this);
        setContentView(tv);
        tv.setText("ReadFile:\n");
        tv.append(" Hej Bjørn \n");

        String filename="/data/data/com.example.ReadFile/files/cube.obj";
        tv.append(filename+"\n");
        try {
            File file = new File(filename);
            Scanner input=new Scanner(file);
            while (input.hasNextLine()){
                String line=input.nextLine();
                tv.append(line+"\n");
            }
        } catch (FileNotFoundException e){
            tv.append(e.toString());
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }
}