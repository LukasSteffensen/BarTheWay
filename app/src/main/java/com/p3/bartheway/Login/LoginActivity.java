package com.p3.bartheway.Login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.p3.bartheway.Browse.StudentBrowseActivity;
import com.p3.bartheway.R;

public class LoginActivity extends AppCompatActivity {

    private Button btnStudent;
    private Button btnBartender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnBartender = findViewById(R.id.btnBartender);
        btnStudent = findViewById(R.id.btnStudent);

        btnBartender.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), BartenderLoginActivity.class);
            i.putExtra("Connect", "false");
            startActivity(i);
        });

        btnStudent.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), StudentBrowseActivity.class));
        });
    }

    @Override
    public void onBackPressed() {
        // this will always exit the app
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}
