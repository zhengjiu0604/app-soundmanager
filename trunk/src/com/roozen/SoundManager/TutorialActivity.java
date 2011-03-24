package com.roozen.SoundManager;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import com.roozen.SoundManager.utils.Util;

import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;

public class TutorialActivity extends Activity {

    private int page = 0;
    private List<String> pageText = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tutorial);

        boolean doNotShowAgain = Util.getBooleanPref(this, getString(R.string.DoNotShowAgain),false);
        if(doNotShowAgain) {
            this.onDestroy();
        }

        setupCheckBox(doNotShowAgain);
        setupButtons();
        setupPages();
    }

    private void setupButtons() {
        Button back = (Button) findViewById(R.id.tutorial_back);
        Button close = (Button) findViewById(R.id.tutorial_close);
        Button next = (Button) findViewById(R.id.tutorial_next);
    }

    private void setupCheckBox(boolean doNotShowAgain) {
        CheckBox doNotShowAgainCheck = (CheckBox) findViewById(R.id.doNotShowAgainCheck);
        doNotShowAgainCheck.setChecked(doNotShowAgain);
    }
}
