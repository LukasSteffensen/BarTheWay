package com.p3.bartheway.Browse;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.p3.bartheway.Database.ApiInterface;
import com.p3.bartheway.Database.Loan;
import com.p3.bartheway.Database.Student;
import com.p3.bartheway.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CurrentBorrowersActivity extends AppCompatActivity implements LoanRecyclerAdapter.OnClickListener{

    ApiInterface apiInterface;

    private RecyclerView mRecyclerView;
    private LoanRecyclerAdapter mAdapter;

    List<Loan> loanList;
    List<Student> studentList;

    boolean hasLoans, hasStudents = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_borrowers);

        mRecyclerView = findViewById(R.id.loan_recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setHasFixedSize(true);

        getCurrentLoans();

        if (hasLoans && hasStudents) {
            mAdapter = new LoanRecyclerAdapter(loanList, studentList, this);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }

    }

    private void getCurrentLoans(){

        Call<List<Student>> callStudents = apiInterface.getBorrowers();
        callStudents.enqueue(new Callback<List<Student>>() {
            @Override
            public void onResponse(@NonNull Call<List<Student>> call, @NonNull Response<List<Student>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    studentList = response.body();
                    hasStudents = true;
                    Log.i("onResponse", response.body().toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Student>> call, @NonNull Throwable t) {
                Log.i("onFailure", "Fail");
            }
        });

        Call<List<Loan>> callLoans = apiInterface.getLoans();
        callLoans.enqueue(new Callback<List<Loan>>() {
            @Override
            public void onResponse(@NonNull Call<List<Loan>> call, @NonNull Response<List<Loan>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    loanList = response.body();
                    hasLoans = true;
                    Log.i("onResponse", response.body().toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Loan>> call, @NonNull Throwable t) {
                Log.i("onFailure", "Fail");
            }
        });
    }

    @Override
    public void onItemClick(int position) {

    }
}
