package com.example.administrator.topactivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.administrator.topactivity.service.DaemonService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button mBtnStartService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnStartService = (Button) findViewById(R.id.button);
        mBtnStartService.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                Intent intent = new Intent(this, DaemonService.class);
                startService(intent);
                break;
        }
    }
}
