package com.roozen.SoundManagerv2;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;
import com.roozen.SoundManagerv2.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class TutorialActivity extends Activity {

    private int page;
    private List<String> pageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tutorial);

        boolean doNotShowAgainTrue = Util.getBooleanPref(this, getString(R.string.DoNotShowAgain),true);
        boolean doNotShowAgainFalse = Util.getBooleanPref(this, getString(R.string.DoNotShowAgain),false);
        if(doNotShowAgainTrue && !doNotShowAgainFalse) {
            setupCheckBox(doNotShowAgainTrue);
        } else {
            setupCheckBox(doNotShowAgainFalse);
        }

        //if the activity is being resumed...
        boolean mIntentFromMainMenu = this.getIntent().getExtras() != null ? this.getIntent().getExtras().getBoolean("fromMenu", false) : false;

        if(doNotShowAgainFalse && !mIntentFromMainMenu) {
            finish();
        }

        setupButtons();
        setupPages();
    }

    private void setupPages() {
        pageText = new ArrayList<String>();
        page = 0;

        pageText.add(getString(R.string.tutorial_firstpage));
        pageText.add(getString(R.string.tutorial_secondpage));
        pageText.add(getString(R.string.tutorial_thirdpage));
        pageText.add(getString(R.string.tutorial_fourthpage));
        pageText.add(getString(R.string.tutorial_fifthpage));
        pageText.add(getString(R.string.tutorial_sixthpage));

        setCurrentPage();
    }

    private void setCurrentPage() {
        final ScrollView scroll = (ScrollView) findViewById(R.id.tutorial_scroll);
        TextView text = (TextView) findViewById(R.id.tutorial_text);
        text.setText(pageText.get(page));
        scroll.scrollTo(0,0);
    }

    private void setupButtons() {
        final Button back = (Button) findViewById(R.id.tutorial_back);
        final Button next = (Button) findViewById(R.id.tutorial_next);
        final Button close = (Button) findViewById(R.id.tutorial_close);

        back.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view) {
                if(page <= 0) {
                    back.setClickable(false);
                    return;
                }

                page--;
                if(page <= 0) {
                    back.setClickable(false);
                }

                next.setClickable(true);
                setCurrentPage();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                finish();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (page >= pageText.size() - 1) {
                    next.setClickable(false);
                    return;
                }

                page++;
                if (page >= pageText.size()) {
                    next.setClickable(false);
                }

                back.setClickable(true);
                setCurrentPage();
            }
        });
    }

    private void setupCheckBox(boolean doNotShowAgain) {
        CheckBox doNotShowAgainCheck = (CheckBox) findViewById(R.id.doNotShowAgainCheck);
        doNotShowAgainCheck.setChecked(doNotShowAgain);
    }

    @Override
    protected void onDestroy() {
        CheckBox doNotShowAgainCheck = (CheckBox) findViewById(R.id.doNotShowAgainCheck);
        boolean checked = doNotShowAgainCheck.isChecked();
        Util.putBooleanPref(this, getString(R.string.DoNotShowAgain), checked);

        super.onDestroy();
    }
}