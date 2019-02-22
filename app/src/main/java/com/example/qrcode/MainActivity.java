package com.example.qrcode;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

  public static final String EXTRA_MESSAGE = "com.example.qrcode.MESSAGE";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
  }

  /** Fonction de test de QrRead et QrMessage */
  public void sendMessage(View view) {
      Intent intent = new Intent(this, DisplayTestActivity.class);
      String msg = "Test de l'activité";// TODO
      intent.putExtra(EXTRA_MESSAGE, msg);
      startActivity(intent);

  }


}
