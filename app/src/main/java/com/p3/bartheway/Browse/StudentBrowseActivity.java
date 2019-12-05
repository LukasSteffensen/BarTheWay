package com.p3.bartheway.Browse;

import android.app.Dialog;
import android.content.res.Resources;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.p3.bartheway.Database.Item;
import com.p3.bartheway.Database.Loan;
import com.p3.bartheway.Database.Student;
import com.p3.bartheway.R;

import org.w3c.dom.Text;

import java.util.List;

public class StudentBrowseActivity extends AppCompatActivity implements ItemRecyclerAdapter.OnClickListener, BrowseView {

    SwipeRefreshLayout swipeRefresh;
    BrowsePresenter presenter;

    List<Item> items;

    SearchView mSearchView;
    TextView textViewTitle;
    TextView textViewDuration;
    TextView textViewPlayers;
    TextView textViewYear;
    TextView textViewLanguage;
    TextView textViewDescription;
    ImageView imageView;
    private RecyclerView mRecyclerView;
    private ItemRecyclerAdapter mAdapter;

    Dialog itemDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_browse);

        itemDialog = new Dialog(this);

        itemDialog.setContentView(R.layout.pop_up_item);
        textViewTitle = itemDialog.findViewById(R.id.textViewPopUpTitle);
        textViewDuration = itemDialog.findViewById(R.id.textViewPopUpDuration);
        textViewPlayers = itemDialog.findViewById(R.id.textViewPopUpPlayers);
        textViewYear = itemDialog.findViewById(R.id.textViewPopUpYear);
        textViewLanguage = itemDialog.findViewById(R.id.textViewPopUpLanguage);
        textViewDescription = itemDialog.findViewById(R.id.textViewPopUpDescription);
        imageView = itemDialog.findViewById(R.id.imageViewItem);

        mRecyclerView = findViewById(R.id.item_recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setHasFixedSize(true);
        mSearchView = findViewById(R.id.search_view_browse);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        swipeRefresh = findViewById(R.id.swipeRefresh);

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
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showPopUp(String title,
                          String duration,
                          String players,
                          String year,
                          String language,
                          String description) {

        String titleForImage = title;
        titleForImage = titleForImage.replaceAll(" ", "_");
        titleForImage = titleForImage.replaceAll("'", "");
        titleForImage = titleForImage.toLowerCase();
        Resources res = getApplicationContext().getResources();
        String mDrawableName = titleForImage;
        int resID = res.getIdentifier(mDrawableName , "drawable", getApplicationContext().getPackageName());
        imageView.setImageResource(resID);
        textViewTitle.setText("Title: " + title);
        textViewDuration.setText("Playing time: " + duration);
        textViewPlayers.setText("Players: " + players);
        textViewYear.setText("Year: " + year);
        textViewLanguage.setText("Language: " + language);
        textViewDescription.setText("Description: " + description);
        itemDialog.show();
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
        mAdapter = new ItemRecyclerAdapter(items, this, getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        this.items = items;
    }

    @Override
    public void onErrorLoading(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetLoans(List<Loan> loans) {

    }

    @Override
    public void onGetStudent(List<Student> student) {

    }

    @Override
    public void onItemClick(int position) {
        String title = items.get(position).getTitle();
        String duration = items.get(position).getDuration();
        String players = items.get(position).getMinPlayers() + "-" + items.get(position).getMaxPlayers();
        String year = items.get(position).getYear() + "";
        String language = items.get(position).getLanguage();
        String description = items.get(position).getDescription();
        showPopUp(title,duration,players,year,language,description);
    }
}
