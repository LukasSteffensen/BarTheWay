package com.p3.bartheway.Browse;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.p3.bartheway.Database.Item;
import com.p3.bartheway.Database.Loan;
import com.p3.bartheway.Database.Student;
import com.p3.bartheway.R;

import java.util.List;

public class CurrentBorrowersActivity extends AppCompatActivity implements LoanRecyclerAdapter.OnClickListener, BrowseView{

    private TextView textViewCurrentBorrowers;
    private RecyclerView mRecyclerView;
    private LoanRecyclerAdapter mAdapter;
    BrowsePresenter presenter;

    List<Loan> loanList;
    List<Student> studentList;

    boolean hasLoans, hasStudents = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_borrowers);

        presenter = new BrowsePresenter(this);
        presenter.getCurrentLoans();

        textViewCurrentBorrowers = findViewById(R.id.textViewCurrentBorrowers);

        mRecyclerView = findViewById(R.id.loan_recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setHasFixedSize(true);
    }



    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void onGetLoans(List<Loan> loans) {
        loanList = loans;
        presenter.getCurrentBorrowers();
    }


    @Override
    public void onGetResult(List<Item> items) {

    }

    @Override
    public void onErrorLoading(String message) {

    }

    @Override
    public void onGetStudent(List<Student> students) {
        studentList = students;
        mAdapter = new LoanRecyclerAdapter(loanList, studentList, this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }
}
