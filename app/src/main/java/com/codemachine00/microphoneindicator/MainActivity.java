package com.codemachine00.microphoneindicator;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.codemachine00.micuv.MicroUtils;
import com.codemachine00.micuv.MicrophoneIndicator;

public class MainActivity extends AppCompatActivity {

    Button button;
    MicrophoneIndicator indicator;
    private RecorderHandler recorder;
    final int permissionCode = 94;
    String[] permissions = new String[] {
            Manifest.permission.RECORD_AUDIO,
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED) {
          init();
        } else {
            this.requestPermissions(permissions, permissionCode);
        }
    }

    private void init() {
        // You can use the API that requires the permission.
        setContentView(R.layout.activity_main);
        indicator = findViewById(R.id.indicator);

        button = findViewById(R.id.button);
        recorder = new RecorderHandler((byte[] buffer, int size) -> {
//            int amplitude = MicroUtils.getAmplitudeFromBuffer(buffer, size);
            int amplitude = MicroUtils.getAmplitude(buffer);
            indicator.setAmplitude(amplitude);
        });

        button.setOnClickListener(view -> {
            if (!recorder.IsRunning()) {
                recorder.start();
            } else {
                recorder.stop();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (recorder != null)
            recorder.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (recorder != null)
            recorder.stop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case permissionCode:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init();
                }  else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }

}