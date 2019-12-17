package com.mikuta0407.myrepo04a;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import static com.mikuta0407.myrepo04a.R.id.disp_time;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView disp_time = findViewById(R.id.disp_time);
    }
}
