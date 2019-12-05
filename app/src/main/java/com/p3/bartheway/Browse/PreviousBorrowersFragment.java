package com.p3.bartheway.Browse;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.p3.bartheway.Database.Item;
import com.p3.bartheway.Database.Loan;
import com.p3.bartheway.Database.Student;
import com.p3.bartheway.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PreviousBorrowersFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PreviousBorrowersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PreviousBorrowersFragment extends Fragment implements BrowseView, ItemRecyclerAdapter.OnClickListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    SwipeRefreshLayout swipeRefresh;
    BrowsePresenter presenter;

    List<Item> itemList;
    List<Loan> loanList;

    SearchView mSearchView;

    private RecyclerView mRecyclerView;
    private ItemRecyclerAdapter itemRecyclerAdapter;

    private OnFragmentInteractionListener mListener;

    ArrayList<String> studentNames = new ArrayList<>();
    ArrayList<String> title = new ArrayList<>();
    ArrayList<String> timestampBorrow = new ArrayList<>();
    ArrayList<String> timestampReturn = new ArrayList<>();


    public PreviousBorrowersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PreviousBorrowersFragment.
     */
    public static PreviousBorrowersFragment newInstance(String param1, String param2) {
        PreviousBorrowersFragment fragment = new PreviousBorrowersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_previous_borrowers, container, false);

        mRecyclerView = view.findViewById(R.id.item_recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mRecyclerView.setHasFixedSize(true);
        mSearchView = view.findViewById(R.id.search_view_browse);

        swipeRefresh = view.findViewById(R.id.swipeRefresh);

        presenter = new BrowsePresenter(this);
        presenter.getItemData();

        swipeRefresh.setOnRefreshListener(
                () -> presenter.getItemData()
        );

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                itemRecyclerAdapter.getFilter().filter(newText);
                return false;
            }
        });

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
    public void showLoading() {
        swipeRefresh.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        swipeRefresh.setRefreshing(false);

    }

    @Override
    public void onGetResult(List<Item> items) {
        itemRecyclerAdapter = new ItemRecyclerAdapter(items, this, getContext());
        mRecyclerView.setAdapter(itemRecyclerAdapter);
        itemRecyclerAdapter.notifyDataSetChanged();
        this.itemList = items;
    }

    @Override
    public void onErrorLoading(String message) {
        Toast.makeText(getContext(), "There are no previous loans for this item", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetLoans(List<Loan> loans) {
        this.loanList = loans;
        studentNames = new ArrayList<>();
        title = new ArrayList<>();
        timestampBorrow = new ArrayList<>();
        timestampReturn = new ArrayList<>();
        for (Loan l : loanList) {
            presenter.getStudentData(l.getCard_uid());
            title.add(l.getTitle());
            timestampBorrow.add(l.getTimestampBorrow());
            timestampReturn.add(l.getTimestampReturn());
        }
    }

    @Override
    public void onGetStudent(List<Student> student) {
        studentNames.add(student.get(0).getStudentName());
        if (studentNames.size() == loanList.size()) {
            Log.i("Hello", "What");
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("studentNames", studentNames);
            bundle.putStringArrayList("title", title);
            bundle.putStringArrayList("timestampBorrow", timestampBorrow);
            bundle.putStringArrayList("timestampReturn", timestampReturn);
            loanList = new ArrayList<>();
            Fragment fragment = new PreviousLoansFragment();
            fragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onItemClick(int position) {
        String title = itemList.get(position).getTitle();
        presenter.getPreviousLoans(title);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}
