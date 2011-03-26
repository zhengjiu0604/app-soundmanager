package com.roozen.SoundManager.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.roozen.SoundManager.services.BootupService;

public class Reinstall extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getData().getPath().contains("com.roozen.SoundManager")) {
            Intent i = new Intent(context, BootupService.class);
            context.startService(i);
        }
    }
}
