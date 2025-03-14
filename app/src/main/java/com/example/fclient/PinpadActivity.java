package com.example.fclient;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.text.DecimalFormat;

public class PinpadActivity extends AppCompatActivity {

    TextView tvPin, tvAmount, tvAttempts;
    String pin = "";
    final int MAX_KEYS = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pinpad);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        tvPin = findViewById(R.id.txtPin);
        tvAmount = findViewById(R.id.txtAmount);
        tvAttempts= findViewById(R.id.txtLastAttempts);
        String amt = String.valueOf(getIntent().getStringExtra("amount"));
        Long f = Long.valueOf(amt);
        DecimalFormat df = new DecimalFormat("#,###,###,##0.00");
        String s = df.format(f);
        tvAmount.setText("Сумма:" + s);

        int attempts = getIntent().getIntExtra("attempts", 0);
        if (attempts == 2)
            tvAttempts.setText("Осталось две попытки");
        else if (attempts == 1)
            tvAttempts.setText("Осталась одна попытка");
        ShuffleKeys();

        findViewById(R.id.btnOK).setOnClickListener((View) -> {
            Intent it = new Intent();
            it.putExtra("pin", pin);
            setResult(RESULT_OK, it);
            finish();
        });

        findViewById(R.id.btnReset).setOnClickListener((View) -> {
            pin = "";
            tvPin.setText("");
        });
    }

    public void keyClick(View v)
    {
        String key = ((TextView)v).getText().toString();
        int sz = pin.length();
        if (sz < 4)
        {
            pin += key;
            tvPin.setText("****".substring(3 - sz));
        }
    }

    protected void ShuffleKeys()
    {
        Button[] keys = new Button[] {
                findViewById(R.id.btnKey0),
                findViewById(R.id.btnKey1),
                findViewById(R.id.btnKey2),
                findViewById(R.id.btnKey3),
                findViewById(R.id.btnKey4),
                findViewById(R.id.btnKey5),
                findViewById(R.id.btnKey6),
                findViewById(R.id.btnKey7),
                findViewById(R.id.btnKey8),
                findViewById(R.id.btnKey9),
        };

        byte[] rnd = MainActivity.randomBytes(MAX_KEYS);
        for(int i = 0; i < MAX_KEYS; i++)
        {
            int idx = (rnd[i] & 0xFF) % 10;
            CharSequence txt = keys[idx].getText();
            keys[idx].setText(keys[i].getText());
            keys[i].setText(txt);
        }
    }



}