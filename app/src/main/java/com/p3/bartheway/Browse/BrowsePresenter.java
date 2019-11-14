package com.p3.bartheway.Browse;

import android.support.annotation.NonNull;
import android.util.Log;

import com.p3.bartheway.ApiClient;
import com.p3.bartheway.ApiInterface;
import com.p3.bartheway.Item;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BrowsePresenter {

    private BrowseView view;

    public BrowsePresenter(BrowseView view) {
        this.view = view;
    }

    void getData() {

        view.showLoading();

        ApiInterface apiInterface = ApiClient
                .getApiClient()
                .create(ApiInterface.class);
        Call<List<Item>> call = apiInterface.getItems();
        call.enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(@NonNull Call<List<Item>> call, @NonNull Response<List<Item>> response) {
                view.hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    view.onGetResult(response.body());
                    Log.i("onResponse", response.body().toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Item>> call, @NonNull Throwable t) {
                view.hideLoading();
                view.onErrorLoading(t.getLocalizedMessage());

            }
        });
    }

}
