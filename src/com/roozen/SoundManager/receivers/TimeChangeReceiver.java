package com.roozen.SoundManager.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.roozen.SoundManager.MainSettings;
import com.roozen.SoundManager.services.BootupService;

import java.util.Date;

public class TimeChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, BootupService.class);
        context.startService(i);
    }
}
