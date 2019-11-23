package com.p3.bartheway.Login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;

import com.p3.bartheway.Browse.BrowseActivity;
import com.p3.bartheway.R;

import static android.view.KeyEvent.KEYCODE_NUMPAD_ENTER;

public class BartenderLoginActivity extends AppCompatActivity {

    private int password = 9999;
    int KEYCODE_ENTER = 66;

    private EditText editTextPassword;

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case (66):
                if (!editTextPassword.getText().toString().equals("")) {
                    if (Integer.parseInt(editTextPassword.getText().toString()) == (password)){
                        Intent intent = new Intent(getApplicationContext(), BrowseActivity.class);
                        intent.putExtra("Connect", "false");
                        startActivity(intent);
                    } else {
                        editTextPassword.setError("Incorrect password");
                    }

                } else {
                    editTextPassword.setError("Please enter the password");
                }
            default:
                return super.onKeyUp(keyCode,event);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bartender_login);

        editTextPassword = findViewById(R.id.editTextBartenderPassword);
    }
}
