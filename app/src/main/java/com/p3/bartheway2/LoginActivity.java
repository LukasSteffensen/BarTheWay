package com.p3.bartheway2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity {

    private Button btnStudent;
    private Button btnBartender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnBartender = findViewById(R.id.btnBartender);
        btnStudent = findViewById(R.id.btnStudent);

        btnBartender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MonitoringScreen.class);
                i.putExtra("Connect", "false");
                startActivity(i);
            }
        });

    }
}
