package com.p3.bartheway.Browse;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.p3.bartheway.Database.Loan;
import com.p3.bartheway.Database.Student;
import com.p3.bartheway.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PreviousLoansFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PreviousLoansFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PreviousLoansFragment extends Fragment implements LoanRecyclerAdapter.OnClickListener {
    private static final String ARG_STUDENT_NAMES = "studentNames";
    private static final String ARG_TITLE = "title";
    private static final String ARG_TIMESTAMP_BORROW = "timestampBorrow";
    private static final String ARG_TIMESTAMP_RETURN = "timestampReturn";

    private List<Loan> loanList;
    private List<Student> studentList;

    TextView textViewTitle;

    ArrayList<Loan> loanArrayList = new ArrayList<>();
    ArrayList<Student> studentArrayList = new ArrayList<>();

    ArrayList<String> studentNames = new ArrayList<>();
    ArrayList<String> title = new ArrayList<>();
    ArrayList<String> timestampBorrow = new ArrayList<>();
    ArrayList<String> timestampReturn = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private LoanRecyclerAdapter loanRecyclerAdapter;

    private OnFragmentInteractionListener mListener;

    public PreviousLoansFragment() {
        // Required empty public constructor
    }

    public static PreviousLoansFragment newInstance(ArrayList<String> studentNames,
                                                    ArrayList<String> title,
                                                    ArrayList<String> timestampBorrow,
                                                    ArrayList<String> timestampReturn) {
        PreviousLoansFragment fragment = new PreviousLoansFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_STUDENT_NAMES, studentNames);
        args.putStringArrayList(ARG_TITLE, title);
        args.putStringArrayList(ARG_TIMESTAMP_BORROW, timestampBorrow);
        args.putStringArrayList(ARG_TIMESTAMP_RETURN, timestampReturn);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            studentNames = getArguments().getStringArrayList(ARG_STUDENT_NAMES);
            title = getArguments().getStringArrayList(ARG_TITLE);
            timestampBorrow = getArguments().getStringArrayList(ARG_TIMESTAMP_BORROW);
            timestampReturn = getArguments().getStringArrayList(ARG_TIMESTAMP_RETURN);
            for (int i = 0; i < timestampBorrow.size(); i++) {
                Loan loan = new Loan();
                loan.setTitle(title.get(i));
                loan.setTimestampBorrow(timestampBorrow.get(i));
                loan.setTimestampReturn(timestampReturn.get(i));
                loanArrayList.add(loan);
                Student student = new Student();
                student.setStudentName(studentNames.get(i));
                studentArrayList.add(student);
            }
            loanList = loanArrayList;
            Collections.reverse(loanList);
            studentList = studentArrayList;
            Collections.reverse(studentList);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_previous_loans, container, false);

        textViewTitle = view.findViewById(R.id.textViewTitle);
        textViewTitle.setText(loanList.get(0).getTitle());
        for (int i = 0; i < loanList.size(); i++) {
            loanList.get(i).setTitle("");
        }
        mRecyclerView = view.findViewById(R.id.previousBorrowersRecycler);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mRecyclerView.setHasFixedSize(true);
        loanRecyclerAdapter = new LoanRecyclerAdapter(loanList, studentList, this);
        mRecyclerView.setAdapter(loanRecyclerAdapter);
        // Inflate the layout for this fragment
        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(int position) {

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
