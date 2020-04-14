package com.srise.hotfix;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TestBug testBug = new TestBug();
        findViewById(R.id.parent).setBackgroundColor(testBug.getColor());
    }
}
