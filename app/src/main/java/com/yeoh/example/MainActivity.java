package com.yeoh.example;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.yeoh.seeker.annotation.Hide;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Hide
    private void test(String str, Context context) {

    }

    @Hide
    private void test() {

    }
}
