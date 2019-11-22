package com.p3.bartheway.Browse;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.p3.bartheway.Database.Loan;
import com.p3.bartheway.Database.Student;
import com.p3.bartheway.R;

import java.util.List;

public class LoanRecyclerAdapter extends RecyclerView.Adapter<LoanRecyclerAdapter.RecyclerViewAdapter> {

    private OnClickListener mOnClickListener;

    List<Loan> loanList;
    List<Student> studentList;

    public LoanRecyclerAdapter(List<Loan> loanList, List<Student> studentList, OnClickListener onClickListener){
        this.loanList = loanList;
        this.studentList = studentList;
        this.mOnClickListener = onClickListener;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.loan_card, viewGroup, false);
        return new RecyclerViewAdapter(view, mOnClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter recyclerViewAdapter, int i) {
        Loan loan = loanList.get(i);
        Student student = studentList.get(i);

        recyclerViewAdapter.mTextViewStudentName.setText(student.getStudentName());
        recyclerViewAdapter.mTextViewTitle.setText(loan.getTitle());
        recyclerViewAdapter.mTextViewTimestamp.setText(loan.getTimestampBorrow().toString());

    }

    public Loan getLoan(int position){
        return loanList.get(position);
    }

    @Override
    public int getItemCount() {
        return loanList.size();
    }

    class RecyclerViewAdapter extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mTextViewTitle, mTextViewStudentName, mTextViewTimestamp;
        OnClickListener mOnClickListener;

        public RecyclerViewAdapter(@NonNull View loanView, OnClickListener onClickListener) {
            super(loanView);

            mTextViewTitle = loanView.findViewById(R.id.item_title);
            mTextViewStudentName = loanView.findViewById(R.id.student_name);
            mTextViewTimestamp = loanView.findViewById(R.id.timestamp);
            this.mOnClickListener = onClickListener;

            loanView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            mOnClickListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnClickListener{
        void onItemClick(int position);
    }

}
