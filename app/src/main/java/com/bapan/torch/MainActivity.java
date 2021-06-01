package com.bapan.torch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
private ConstraintLayout display;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initialize Views
        display = findViewById(R.id.display_id);
      
    }
}