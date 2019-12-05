package com.p3.bartheway.Login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;

import com.p3.bartheway.Browse.BartenderBrowseActivity;
import com.p3.bartheway.R;

import static android.view.KeyEvent.KEYCODE_ENTER;

public class BartenderLoginActivity extends AppCompatActivity {

    private EditText editTextPassword;

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case (KEYCODE_ENTER):
                if (!editTextPassword.getText().toString().equals("")) {
                    int password = 9999;
                    if (Integer.parseInt(editTextPassword.getText().toString()) == (password)){
                        Intent intent = new Intent(getApplicationContext(), BartenderBrowseActivity.class);
                        intent.putExtra("Connect", "false");
                        startActivity(intent);
                    } else {
                        editTextPassword.setText("");
                        editTextPassword.setError("Incorrect password");
                    }

                } else {
                    editTextPassword.setError("Please enter the password");
                }
            default:
                if (editTextPassword.length() == 10) {
                    editTextPassword.setText("");
                    editTextPassword.setError("Password too large");
                }
                return super.onKeyUp(keyCode,event);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bartender_login);

        editTextPassword = findViewById(R.id.editTextBartenderPassword);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
