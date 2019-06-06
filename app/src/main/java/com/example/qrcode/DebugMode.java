package com.example.qrcode;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

public class DebugMode extends AppCompatActivity {

  public static boolean DEBUG_MODE = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_debug_mode);

    Switch sw = (Switch) findViewById(R.id.switch1);

    sw.setChecked(DEBUG_MODE);

    sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
          DEBUG_MODE = true;
        } else {
          DEBUG_MODE = false;
        }
      }
    });

  }
}
