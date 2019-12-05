package com.p3.bartheway.Browse;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.p3.bartheway.R;


public class PreviousBorrowersActivity extends AppCompatActivity implements PreviousLoansFragment.OnFragmentInteractionListener, PreviousBorrowersFragment.OnFragmentInteractionListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_borrowers);

        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, new PreviousBorrowersFragment()).commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
