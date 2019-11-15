package com.p3.bartheway;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.p3.bartheway.Database.ApiClient;
import com.p3.bartheway.Database.ApiInterface;
import com.p3.bartheway.Database.Item;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddItemActivity extends AppCompatActivity {

    EditText editTextTitle, editTextMaxPlayers, editTextLanguage, editTextMinPlayers, editTextDescription, editTextDuration, editTextYear;

    ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        editTextTitle = findViewById(R.id.title);
        editTextLanguage = findViewById(R.id.language);
        editTextDescription = findViewById(R.id.description);
        editTextMinPlayers = findViewById(R.id.minPlayers);
        editTextMaxPlayers = findViewById(R.id.maxPlayers);
        editTextDuration = findViewById(R.id.duration);
        editTextYear = findViewById(R.id.year);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:

                Log.i("onOptionsItemSelected", "save is clicked");

                String minPlayersString = editTextMinPlayers.getText().toString().trim();
                String maxPlayersString = editTextMaxPlayers.getText().toString().trim();
                String yearString = editTextYear.getText().toString().trim();

                String title = editTextTitle.getText().toString().trim();
                String language = editTextLanguage.getText().toString().trim();
                String description = editTextDescription.getText().toString().trim();
                int minPlayers = Integer.parseInt(minPlayersString);
                int maxPlayers = Integer.parseInt(maxPlayersString);
                String duration = editTextDuration.getText().toString().trim();
                int year = Integer.parseInt(yearString);

                if (title.isEmpty()) {
                    editTextTitle.setError("Please enter a title");
                } else if (language.isEmpty()) {
                    editTextLanguage.setError("Please enter a language");
                } else if (description.isEmpty()) {
                    editTextDescription.setError("Please enter a description");
                } else if (minPlayersString.isEmpty()) {
                    editTextMinPlayers.setError("Please enter a minimum amount of players");
                } else if (maxPlayersString.isEmpty()) {
                    editTextMaxPlayers.setError("Please enter a maximum amount of players");
                } else if (minPlayers>maxPlayers) {
                    Toast.makeText(this,
                            "Minimum players cannot be greater than maximum players",
                            Toast.LENGTH_SHORT).show();
                } else if (minPlayers < 1) {
                    Toast.makeText(this,
                            "Amount of players cannot be less than 1",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    saveItem(title, language, description, minPlayers, maxPlayers, duration, year);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveItem(final String title,
                          final String language,
                          final String description,
                          final int minPlayers,
                          final int maxPlayers,
                          final String duration,
                          final int year) {


        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<Item> call =  apiInterface.saveItem(title, language, description, minPlayers, maxPlayers, duration, year);

        call.enqueue(new Callback<Item>() {
            @Override
            public void onResponse(@NonNull Call<Item> call, @NonNull Response<Item> response) {

                Log.i("onResponse", "try");
                if (response.isSuccessful() && response.body()!= null) {
                    Boolean success = response.body().getSuccess();
                    if (success) {
                        Log.i("onResponse", "success");
                        Toast.makeText(AddItemActivity.this,
                                response.body().getMessage(),
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Log.i("onResponse", "fail");
                        Toast.makeText(AddItemActivity.this,
                                response.body().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.i("onResponse", "is null or not successful");
                    Log.i("onResponse", response.toString());
                    Log.i("onResponse", response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Item> call, @NonNull Throwable t) {
                Log.i("onFailure", "dang");
                Toast.makeText(AddItemActivity.this,
                        t.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });
    }
}
