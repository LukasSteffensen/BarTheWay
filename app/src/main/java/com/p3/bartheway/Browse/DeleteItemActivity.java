package com.p3.bartheway.Browse;

import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.p3.bartheway.Browse.BrowsePresenter;
import com.p3.bartheway.Browse.BrowseView;
import com.p3.bartheway.Browse.ItemRecyclerAdapter;
import com.p3.bartheway.Database.ApiClient;
import com.p3.bartheway.Database.ApiInterface;
import com.p3.bartheway.Database.Item;
import com.p3.bartheway.Database.Student;
import com.p3.bartheway.R;

import java.sql.Timestamp;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeleteItemActivity extends AppCompatActivity implements ItemRecyclerAdapter.OnClickListener, BrowseView {

    SwipeRefreshLayout swipeRefresh;

    BrowsePresenter presenter;

    List<Item> items;

    private RecyclerView mRecyclerView;
    private ItemRecyclerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_item);

        mRecyclerView = findViewById(R.id.deleteItem_recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setHasFixedSize(true);

        swipeRefresh = findViewById(R.id.deleteItem_swipeRefresh);

        presenter = new BrowsePresenter(this);
        presenter.getData();

        swipeRefresh.setOnRefreshListener(
                () -> presenter.getData()
        );

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
        mAdapter = new ItemRecyclerAdapter(items, this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        this.items = items;
    }

    @Override
    public void onErrorLoading(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetStudent(List<Student> student) {
    }

    @Override
    public void onItemClick(int position) {

        String title = items.get(position).getTitle();

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);

        builder.setMessage("Are you sure you want to delete " + title + "from the database?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> {
                    deleteItem(title);
                }).setNegativeButton("No", ((dialog, which) -> {
            dialog.cancel();
        }));
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteItem(String title) {
        presenter.deleteItem(title);
    }
}

