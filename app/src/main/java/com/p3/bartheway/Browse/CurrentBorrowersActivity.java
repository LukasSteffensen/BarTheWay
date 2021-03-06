package com.p3.bartheway.Browse;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.p3.bartheway.Database.Item;
import com.p3.bartheway.Database.Loan;
import com.p3.bartheway.Database.Student;
import com.p3.bartheway.R;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CurrentBorrowersActivity extends AppCompatActivity implements LoanRecyclerAdapter.OnClickListener, BrowseView {

    private TextView textViewCurrentBorrowers;
    private RecyclerView mRecyclerView;
    private LoanRecyclerAdapter mAdapter;
    BrowsePresenter presenter;

    List<Loan> loanList;
    List<Student> studentList;
    byte returned = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_borrowers);

        presenter = new BrowsePresenter(this);
        presenter.getLoans(returned);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textViewCurrentBorrowers = findViewById(R.id.textViewCurrentBorrowers);

        mRecyclerView = findViewById(R.id.loan_recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setHasFixedSize(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onItemClick(int position) {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);

        builder.setMessage("Is " + studentList.get(position).getStudentName() + " returning " + studentList.get(position).getTitle() + "?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> {
                    Date date = new Date();
                    long card_uid = studentList.get(position).getCard_uid();
                    String title = loanList.get(position).getTitle();
                    title = title.replaceAll("'", "''");
                    String timestampReturn = new Timestamp(date.getTime()).toString();
                    timestampReturn = BartenderBrowseActivity.removeTimestampDecimals(timestampReturn);
                    byte returned = 1;
                    presenter.returnItem(this, card_uid, title, timestampReturn, returned);
                    loanList.remove(position);
                    studentList.remove(position);
                    if (loanList.size() == 0) {
                        textViewCurrentBorrowers.setText("There are no current borrowers");
                    }
                    mAdapter.notifyDataSetChanged();
                }).setNegativeButton("No", ((dialog, which) -> {
            dialog.cancel();
        }));
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void onGetLoans(List<Loan> loans) {
        if (loans.size()== 0) {
            textViewCurrentBorrowers.setText("There are no current borrowers");
        } else {
            loanList = loans;
            presenter.getCurrentBorrowers();
        }
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
        Collections.reverse(loanList);
        Collections.reverse(studentList);
        Student temp;
        for (int i = 0; i < loanList.size(); i++) {
            for (int j = 0; j < students.size(); j++) {
                if (loanList.get(i).getTitle().equals(students.get(j).getTitle())) {
                    temp = studentList.get(i);
                    studentList.set(i, students.get(j));
                    studentList.set(j, temp);
                }
            }
        }
        mAdapter = new LoanRecyclerAdapter(loanList, studentList, this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }
}
