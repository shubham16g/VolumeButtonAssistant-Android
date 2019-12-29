package com.shubham16.volumeassistant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class SpeechReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            Toast.makeText(context, "Hello", Toast.LENGTH_SHORT).show();
        }
    }
}
