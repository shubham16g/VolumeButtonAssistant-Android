package com.shubham16.volumeassistant;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {


    private ImageButton jButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Class<AssistantService> t = AssistantService.class;
        final Intent intent = new Intent(this, t);
        jButton = findViewById(R.id.btnX);

        if (isServiceRunningInForeground(MainActivity.this, t)) {
            jButton.setBackgroundResource(R.drawable.button_background_on);
        } else {
            jButton.setBackgroundResource(R.drawable.button_background_off);
        }
        jButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isServiceRunningInForeground(MainActivity.this, t)) {
                    stopService(intent);
                    jButton.setBackgroundResource(R.drawable.button_background_off);
                } else {
                    startService(intent);
                    jButton.setBackgroundResource(R.drawable.button_background_on);

                }
            }
        });
    }

    public static boolean isServiceRunningInForeground(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    return true;
                }

            }
        }
        return false;
    }
    //setup the pocket recogniser
}
