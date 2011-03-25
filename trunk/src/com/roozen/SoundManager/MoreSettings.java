package com.roozen.SoundManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import com.roozen.SoundManager.provider.ScheduleProvider;
import com.roozen.SoundManager.schedule.ScheduleList;
import com.roozen.SoundManager.utils.SQLiteDatabaseHelper;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Danny
 * Date: 3/22/11
 * Time: 4:05 AM
 * To change this template use File | Settings | File Templates.
 */
public class MoreSettings extends Activity {

    private AudioManager audio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_more);

        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        setupSeekbars();
        setupButtons();
        setStatusText();
    }


    @Override
    protected void onResume() {
        super.onResume();
        updateSeekBars();
    }

    public void countActiveSchedules() {
	    MainSettings.mActiveCount = new HashMap<Integer,Integer>();

        /*
         * get all active schedules, count them by type
         */
        Uri schedulesUri = Uri.withAppendedPath(ScheduleProvider.CONTENT_URI, "active");
        Cursor scheduleCursor = managedQuery(schedulesUri, null, null, null, null);

        if (scheduleCursor.moveToFirst()) {

            int typeIndex = scheduleCursor.getColumnIndex(SQLiteDatabaseHelper.SCHEDULE_TYPE);

            do {

                if (!MainSettings.mActiveCount.containsKey(scheduleCursor.getInt(typeIndex))) {
                    MainSettings.mActiveCount.put(scheduleCursor.getInt(typeIndex), 0);
                }

                MainSettings.mActiveCount.put(scheduleCursor.getInt(typeIndex), MainSettings.mActiveCount.get(scheduleCursor.getInt(typeIndex)).intValue()+1);

            } while(scheduleCursor.moveToNext());

        }
	}

    private void setStatusText() {
        countActiveSchedules();

        TextView alarmText = (TextView) findViewById(R.id.alarm_timer_text);
        alarmText.setText(MainSettings.getScheduleCountText(AudioManager.STREAM_ALARM));

        TextView incallText = (TextView) findViewById(R.id.phonecall_timer_text);
        incallText.setText(MainSettings.getScheduleCountText(AudioManager.STREAM_VOICE_CALL));
    }

    private void setupButtons() {
        Button alarmTimer = (Button) findViewById(R.id.alarm_timer_button);
        alarmTimer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ScheduleList.class);
                i.putExtra(ScheduleList.VOLUME_TYPE, String.valueOf(AudioManager.STREAM_ALARM));
                startActivityForResult(i, MainSettings.ACTIVITY_LIST);
            }
        });

    	Button incallTimer = (Button) findViewById(R.id.phonecall_timer_button);
    	incallTimer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ScheduleList.class);
                i.putExtra(ScheduleList.VOLUME_TYPE, String.valueOf(AudioManager.STREAM_VOICE_CALL));
                startActivityForResult(i, MainSettings.ACTIVITY_LIST);
            }
        });
    }

    private void setupSeekbars() {

    	final int setVolFlags = AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE |
							AudioManager.FLAG_SHOW_UI | AudioManager.FLAG_VIBRATE;

        SeekBar alarmSeek = (SeekBar) findViewById(R.id.alarm_seekbar);
        alarmSeek.setMax(audio.getStreamMaxVolume(AudioManager.STREAM_ALARM));
        alarmSeek.setProgress(audio.getStreamVolume(AudioManager.STREAM_ALARM));
        alarmSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
			  //ignore
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				//ignore
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				audio.setStreamVolume(AudioManager.STREAM_ALARM, seekBar.getProgress(), setVolFlags);
                audio.playSoundEffect(AudioManager.FX_KEY_CLICK, seekBar.getProgress());
			}

        });

        SeekBar phonecallSeek = (SeekBar) findViewById(R.id.phonecall_seekbar);
        phonecallSeek.setMax(audio.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL));
        phonecallSeek.setProgress(audio.getStreamVolume(AudioManager.STREAM_VOICE_CALL));
        phonecallSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
			  //ignore
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				//ignore
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				audio.setStreamVolume(AudioManager.STREAM_VOICE_CALL, seekBar.getProgress(), setVolFlags);
                audio.playSoundEffect(AudioManager.FX_KEY_CLICK, seekBar.getProgress());
			}

        });
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MainSettings.ACTIVITY_LIST) {
            setStatusText();
        }

        updateSeekBars();
    }

    private void updateSeekBars() {

        SeekBar notifSeek = (SeekBar) findViewById(R.id.phonecall_seekbar);
        notifSeek.setProgress(audio.getStreamVolume(AudioManager.STREAM_VOICE_CALL));

        SeekBar mediaSeek = (SeekBar) findViewById(R.id.alarm_seekbar);
        mediaSeek.setProgress(audio.getStreamVolume(AudioManager.STREAM_ALARM));
    }
}
