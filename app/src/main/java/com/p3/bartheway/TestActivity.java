package com.p3.bartheway;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class TestActivity extends AppCompatActivity {

    private TextView mTextView;
    private String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


        mTextView = findViewById(R.id.textView_uid);
        UID = getIntent().getStringExtra("UID");
        mTextView.setText(UID);

    }
}
