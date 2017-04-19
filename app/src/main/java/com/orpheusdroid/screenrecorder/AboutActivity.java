package com.orpheusdroid.screenrecorder;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up arrow to close the activity
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_about);

        //Let's set the copyright and app version dynamically
        TextView iconCredit = (TextView) findViewById(R.id.icon_credit_tv);
        TextView videoEditorCredit = (TextView) findViewById(R.id.video_editor_lib_credit_tv);
        TextView github_link = (TextView) findViewById(R.id.my_github);

        iconCredit.setText("Praful Ranjan, Sirisha Nayak");
        videoEditorCredit.setText(getString(R.string.video_editor_library_credit, "knowledge4life",
                "https://github.com/knowledge4life/k4l-video-trimmer",
                "MIT Opensource License"));
        github_link.setText(getString(R.string.source_code, "https://github.com/Dex1019/MinorApp"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //finish this activity and return to parent activity
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

