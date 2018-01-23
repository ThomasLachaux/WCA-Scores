package com.adrastel.niviel.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.adrastel.niviel.R;
import com.adrastel.niviel.assets.Assets;

public class NotificationActivity extends AppCompatActivity {

    public static final String CONTENT = "content";
    public static final String NAME = "name";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notification);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView viewContent = (TextView) findViewById(R.id.content);

        String content = getIntent().getStringExtra(CONTENT);
        String name = getIntent().getStringExtra(NAME);

        setTitle(name);

        if(content != null) {
            viewContent.setText(Assets.fromHtml(content));
        }
    }
}
