package com.example.fclient;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fclient.databinding.ActivityMainBinding;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {


    ActivityResultLauncher activityResultLauncher;
    // Used to load the 'fclient' library on application startup.
    static {
        System.loadLibrary("fclient");
        System.loadLibrary("mbedcrypto");
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activityResultLauncher  = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback() {
                    @Override
                    public void onActivityResult(Object o) {
                        ActivityResult result = (ActivityResult) o;
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            // обработка результата
                            String pin = data.getStringExtra("pin");
                            Log.d("PIN", pin);
                            Toast.makeText(MainActivity.this, pin, Toast.LENGTH_SHORT).show();
                            binding.sampleText2.setTextSize(20);
                            binding.sampleText2.setText(R.string.insert_pin_str);
                            binding.sampleText3.setTextSize(16);
                            binding.sampleText3.setText("Пин-код:" + pin);
                        }
                    }
                });

        initRng();
        byte[] byteArr = randomBytes(20);
        // Example of a call to a native method
        TextView tv = binding.sampleText;
        tv.setText(stringFromJNI());
        byte[] KEY = randomBytes(16);
        Log.d("fclient_ndk", Arrays.toString(byteArr));
        byteArr = encrypt(KEY, byteArr);
        Log.d("fclient_ndk", Arrays.toString(byteArr));
        byteArr = decrypt(KEY, byteArr);
        Log.d("fclient_ndk", Arrays.toString(byteArr));
        byte[] encryptedTextBytes = encrypt(KEY, tv.getText().toString().getBytes(StandardCharsets.UTF_8));
        String decryptedText = new String(decrypt(KEY, encryptedTextBytes), StandardCharsets.UTF_8);
        binding.sampleText2.setText("Шифрованный текст:" + Arrays.toString(encryptedTextBytes));
        binding.sampleText3.setText("Расшифрованный текст:" + decryptedText);
        Log.d("fclient_ndk", Arrays.toString(tv.getText().toString().getBytes(StandardCharsets.UTF_8)));
        Log.d("fclient_ndk", Arrays.toString(decrypt(KEY, encryptedTextBytes)));
    }

    public static byte[] stringToHex(String s) {
        byte[] hex;
        try {
            hex = Hex.decodeHex(s.toCharArray());
        } catch (DecoderException e) {
            hex = null;
        }
        return hex;
    }

    public void onButtonClick(View v)
    {
        Intent it = new Intent(this, PinpadActivity.class);
        activityResultLauncher.launch(it);
    }



    /**
     * A native method that is implemented by the 'fclient' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
    public static native int initRng();
    public static native byte[] randomBytes(int no);
    public static native byte[] encrypt(byte[] key, byte[] data);
    public static native byte[] decrypt(byte[] key, byte[] data);
}