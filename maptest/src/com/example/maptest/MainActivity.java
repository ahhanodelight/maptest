package com.example.maptest;

import weather.ChooseAreaActivity;

import com.example.maptest.HistoryActivity;
import com.example.maptest.RunningActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button historyButton = (Button) findViewById(R.id.history_record);
        Button startButton = (Button) findViewById(R.id.start_running);
        Button weatherButton = (Button) findViewById(R.id.weather);
        historyButton.setOnClickListener(this);
        startButton.setOnClickListener(this);
        weatherButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.history_record:
                Intent intent = new Intent(this, HistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.start_running:
                intent = new Intent(this, RunningActivity.class);
                startActivity(intent);
                break;
            case R.id.set_plan:
            	intent = new Intent(this,SetPlansActivity.class);
            	startActivity(intent);
            case R.id.weather:
            	intent = new Intent(this,ChooseAreaActivity.class);
            	startActivity(intent);
            default:
                break;
        }
    }
}
