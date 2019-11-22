package com.p3.bartheway.Browse;

import android.support.annotation.NonNull;
import android.util.Log;

import com.p3.bartheway.Database.ApiClient;
import com.p3.bartheway.Database.ApiInterface;
import com.p3.bartheway.Database.Item;
import com.p3.bartheway.Database.Student;

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
    void getStudentData(int card_uid){

        view.showLoading();

        ApiInterface apiInterface = ApiClient
                .getApiClient()
                .create(ApiInterface.class);

        Call<List<Student>> call = apiInterface.getStudent(card_uid);
        call.enqueue(new Callback<List<Student>>() {
            @Override
            public void onResponse(@NonNull Call<List<Student>> call, @NonNull Response<List<Student>> response) {
                view.hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    view.onGetStudent(response.body());
                    Log.i("onResponse", response.body().toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Student>> call, @NonNull Throwable t) {
                view.hideLoading();
                view.onErrorLoading(t.getLocalizedMessage());
                Log.i("onFailure", "Fail");
            }
        });
    }



}
