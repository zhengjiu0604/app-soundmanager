/**
 * Copyright 2009 Daniel Roozen
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 * http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed 
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language 
 * governing permissions and limitations under the License. 
 */
package com.roozen.SoundManager;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.roozen.SoundManager.provider.ScheduleProvider;
import com.roozen.SoundManager.schedule.ScheduleList;
import com.roozen.SoundManager.services.BootupService;
import com.roozen.SoundManager.utils.SQLiteDatabaseHelper;
import com.roozen.SoundManager.utils.Util;

public class MainSettings extends Activity {

    private Context gui;

    public final static int ACTIVITY_LIST = 0;
    public final static int ACTIVITY_MUTE = 1;
    public final static int ACTIVITY_RINGMODE = 2;

    public static HashMap<Integer, Integer> mActiveCount;
    private boolean hasShownVolumeCouplingWarning;
    private Boolean isVolumeCoupled = null;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        gui = this;

        // boolean hasShownStartup = Util.getBooleanPref(this, getString(R.string.ShownStartup), false);
        hasShownVolumeCouplingWarning = Util.getBooleanPref(this, getString(R.string.ShownVolumeCouplingWarning),
                false);

        setupSeekbars();
        setupButtons();
        setStatusText();


        Intent i = new Intent(gui, TutorialActivity.class);
        startActivityForResult(i, ACTIVITY_LIST);
    }

    private void setupSeekbars() {
        final AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        final int setVolFlags = AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE |
                AudioManager.FLAG_SHOW_UI | AudioManager.FLAG_VIBRATE;

        SeekBar systemSeek = (SeekBar) findViewById(R.id.system_seekbar);
        systemSeek.setMax(audio.getStreamMaxVolume(AudioManager.STREAM_SYSTEM));
        systemSeek.setProgress(audio.getStreamVolume(AudioManager.STREAM_SYSTEM));
        systemSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                //ignore
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                //ignore
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                audio.setStreamVolume(AudioManager.STREAM_SYSTEM, seekBar.getProgress(), setVolFlags);
                updateSeekBars();
            }

        });

        SeekBar ringerSeek = (SeekBar) findViewById(R.id.ringer_seekbar);
        ringerSeek.setMax(audio.getStreamMaxVolume(AudioManager.STREAM_RING));
        ringerSeek.setProgress(audio.getStreamVolume(AudioManager.STREAM_RING));
        ringerSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                //ignore
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                //ignore
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                audio.setStreamVolume(AudioManager.STREAM_RING, seekBar.getProgress(), setVolFlags);

                if (isRingerNotifVolumeCoupled() && !hasShownVolumeCouplingWarning) {
                    showVolumeCouplingWarning();
                }

                updateSeekBars();
            }

        });

        SeekBar notifSeek = (SeekBar) findViewById(R.id.notif_seekbar);
        notifSeek.setMax(audio.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION));
        notifSeek.setProgress(audio.getStreamVolume(AudioManager.STREAM_NOTIFICATION));
        notifSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                //ignore
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                //ignore
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                audio.setStreamVolume(AudioManager.STREAM_NOTIFICATION, seekBar.getProgress(), setVolFlags);

                if (isRingerNotifVolumeCoupled() && !hasShownVolumeCouplingWarning) {
                    showVolumeCouplingWarning();
                }

                updateSeekBars();
            }

        });

        SeekBar mediaSeek = (SeekBar) findViewById(R.id.media_seekbar);
        mediaSeek.setMax(audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        mediaSeek.setProgress(audio.getStreamVolume(AudioManager.STREAM_MUSIC));
        mediaSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                //ignore
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                //ignore
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                audio.setStreamVolume(AudioManager.STREAM_MUSIC, seekBar.getProgress(), setVolFlags);
            }

        });


    }

    private void setupButtons() {

        Button systemTimer = (Button) findViewById(R.id.system_timer_button);
        systemTimer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(gui, ScheduleList.class);
                i.putExtra(ScheduleList.VOLUME_TYPE, String.valueOf(AudioManager.STREAM_SYSTEM));
                startActivityForResult(i, ACTIVITY_LIST);
            }
        });

        Button ringerTimer = (Button) findViewById(R.id.ringer_timer_button);
        ringerTimer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(gui, ScheduleList.class);
                i.putExtra(ScheduleList.VOLUME_TYPE, String.valueOf(AudioManager.STREAM_RING));
                startActivityForResult(i, ACTIVITY_LIST);
            }
        });

        Button notifTimer = (Button) findViewById(R.id.notif_timer_button);
        notifTimer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(gui, ScheduleList.class);
                i.putExtra(ScheduleList.VOLUME_TYPE, String.valueOf(AudioManager.STREAM_NOTIFICATION));
                startActivityForResult(i, ACTIVITY_LIST);
            }
        });

        Button mediaTimer = (Button) findViewById(R.id.media_timer_button);
        mediaTimer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(gui, ScheduleList.class);
                i.putExtra(ScheduleList.VOLUME_TYPE, String.valueOf(AudioManager.STREAM_MUSIC));
                startActivityForResult(i, ACTIVITY_LIST);
            }
        });

        Button moreSettings = (Button) findViewById(R.id.more_settings_button);
        moreSettings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(gui, MoreSettings.class);
                startActivityForResult(i, ACTIVITY_LIST);
            }
        });

        Button refresh = (Button) findViewById(R.id.refresh_button);
        refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                updateSeekBars();
            }
        });
    }

    private void setStatusText() {

        countActiveSchedules();

        TextView systemText = (TextView) findViewById(R.id.system_timer_text);
        systemText.setText(getScheduleCountText(AudioManager.STREAM_SYSTEM));

        TextView ringerText = (TextView) findViewById(R.id.ringer_timer_text);
        ringerText.setText(getScheduleCountText(AudioManager.STREAM_RING));

        TextView notifText = (TextView) findViewById(R.id.notif_timer_text);
        notifText.setText(getScheduleCountText(AudioManager.STREAM_NOTIFICATION));

        TextView mediaText = (TextView) findViewById(R.id.media_timer_text);
        mediaText.setText(getScheduleCountText(AudioManager.STREAM_MUSIC));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.just_mute:
                Intent mute = new Intent(this, MuteActivity.class);
                startActivityForResult(mute, ACTIVITY_MUTE);
                return true;
            case R.id.create_mute_shortcut:
                Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
                shortcutIntent.setClassName(this, MuteActivity.class.getName());

                Intent intent = new Intent();
                intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
                intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Mute/Unmute");
                Parcelable iconResource = Intent.ShortcutIconResource.fromContext(this, R.drawable.mute);
                intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);

                intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
                sendBroadcast(intent);

                // Inform the user that the shortcut has been created
                Toast.makeText(this, "Shortcut Created", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.vibrate_settings:
                Intent vibrate = new Intent(this, VibrateSettings.class);
                startActivity(vibrate);
                return true;
            case R.id.apply_all_settings:
                Intent bootupService = new Intent(this, BootupService.class);
                startService(bootupService);
                return true;
            case R.id.toggle_ringmode:
                Intent toggle = new Intent(this, RingmodeToggle.class);
                startActivityForResult(toggle, ACTIVITY_RINGMODE);
                return true;
            case R.id.faq:
                Uri uri = Uri.parse("http://code.google.com/p/app-soundmanager/wiki/FAQ");
                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void countActiveSchedules() {

        mActiveCount = new HashMap<Integer, Integer>();

        /*
        * get all active schedules, count them by type
        */
        Uri schedulesUri = Uri.withAppendedPath(ScheduleProvider.CONTENT_URI, "active");
        Cursor scheduleCursor = managedQuery(schedulesUri, null, null, null, null);

        if (scheduleCursor.moveToFirst()) {

            int typeIndex = scheduleCursor.getColumnIndex(SQLiteDatabaseHelper.SCHEDULE_TYPE);

            do {

                if (!mActiveCount.containsKey(scheduleCursor.getInt(typeIndex))) {
                    mActiveCount.put(scheduleCursor.getInt(typeIndex), 0);
                }

                mActiveCount.put(scheduleCursor.getInt(typeIndex), mActiveCount.get(scheduleCursor.getInt(typeIndex)).intValue() + 1);

            } while (scheduleCursor.moveToNext());

        }
    }

    public static String getScheduleCountText(int volumeType) {
        String result = "";

        if (mActiveCount.containsKey(volumeType) &&
                mActiveCount.get(volumeType) > 0) {
            result = mActiveCount.get(volumeType).toString() + " active schedule";
            result += mActiveCount.get(volumeType) > 1 ? "s" : "";
        }

        return result;
    }

    /* (non-Javadoc)
    * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTIVITY_LIST) {
            setStatusText();
        }

        updateSeekBars();
    }

    private void updateSeekBars() {
        final AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        SeekBar systemSeek = (SeekBar) findViewById(R.id.system_seekbar);
        systemSeek.setProgress(audio.getStreamVolume(AudioManager.STREAM_SYSTEM));

        SeekBar ringerSeek = (SeekBar) findViewById(R.id.ringer_seekbar);
        ringerSeek.setProgress(audio.getStreamVolume(AudioManager.STREAM_RING));

        SeekBar notifSeek = (SeekBar) findViewById(R.id.notif_seekbar);
        notifSeek.setProgress(audio.getStreamVolume(AudioManager.STREAM_NOTIFICATION));

        SeekBar mediaSeek = (SeekBar) findViewById(R.id.media_seekbar);
        mediaSeek.setProgress(audio.getStreamVolume(AudioManager.STREAM_MUSIC));

        Toast.makeText(this, getString(R.string.VolumeRefreshed), Toast.LENGTH_SHORT).show();
    }

    private void showVolumeCouplingWarning() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("By default, ringer and notification volume are linked such that any changes to " +
                "ringer volume affects notification volume, although not the other way around. " +
                "To unlink them, go to Menu > Settings > Sound & Display > Ringer volume, " +
                "and uncheck \"Use incoming call volume for notifications\".");
        builder.show();

        Util.putBooleanPref(this, getString(R.string.ShownVolumeCouplingWarning), true);
        hasShownVolumeCouplingWarning = true;

    }

    /**
     * temporarily change the ringer volume and check if the notif volume changed with it
     *
     * @return result
     */
    private boolean isRingerNotifVolumeCoupled() {

        if (isVolumeCoupled != null) {
            return isVolumeCoupled;
        }

        isVolumeCoupled = true;

        final AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int ringMax = audio.getStreamMaxVolume(AudioManager.STREAM_RING);

        //get current volumes
        int ringVol = audio.getStreamVolume(AudioManager.STREAM_RING);
        int notifVol = audio.getStreamVolume(AudioManager.STREAM_NOTIFICATION);

        //check if they're the same now
        boolean wereSame = false;
        if (notifVol == ringVol) {
            wereSame = true;
        }

        int tmpRingVol = (ringVol == ringMax) ? ringVol - 1 : ringVol + 1;
        audio.setStreamVolume(AudioManager.STREAM_RING, tmpRingVol, 0);

        int ringCheck = audio.getStreamVolume(AudioManager.STREAM_RING);
        int notifCheck = audio.getStreamVolume(AudioManager.STREAM_NOTIFICATION);

        /*
        * expanded logic:
        * 1. were same, still same        => coupled
        * 2. were same, not same          => not coupled
        * 3. weren't same, still not same => not coupled
        * 4. weren't same, now same       => need to change again and recheck
        * 4a. same again                  => coupled
        * 4b. no longer same              => not coupled
        */
        if (!wereSame && notifCheck == ringCheck) {

            audio.setStreamVolume(AudioManager.STREAM_RING, ringVol, 0);
            ringCheck = audio.getStreamVolume(AudioManager.STREAM_RING);
            notifCheck = audio.getStreamVolume(AudioManager.STREAM_NOTIFICATION);

            if (notifCheck != ringCheck) {
                isVolumeCoupled = false;
            }

        } else if (notifCheck != ringCheck) {
            isVolumeCoupled = false;
        }

        //put everything back to their previous values
        audio.setStreamVolume(AudioManager.STREAM_RING, ringVol, 0);
        audio.setStreamVolume(AudioManager.STREAM_NOTIFICATION, notifVol, 0);

        return isVolumeCoupled;
    }

}