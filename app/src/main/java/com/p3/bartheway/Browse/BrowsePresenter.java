package com.p3.bartheway.Browse;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.p3.bartheway.Database.ApiClient;
import com.p3.bartheway.Database.ApiInterface;
import com.p3.bartheway.Database.Item;
import com.p3.bartheway.Database.Loan;
import com.p3.bartheway.Database.Student;

import java.sql.Timestamp;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BrowsePresenter {

    private BrowseView view;
    private ApiInterface apiInterface;

    public BrowsePresenter(BrowseView view) {
        this.view = view;
        apiInterface = ApiClient
                .getApiClient()
                .create(ApiInterface.class);
    }

    void getData() {

        view.showLoading();

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

    public void saveLoan(Context context, final int card_uid,
                          final String title,
                          final Timestamp timestampBorrow,
                          final byte returned) {


            apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
            Call<Loan> callLoan =  apiInterface.saveLoan(card_uid, title, timestampBorrow, returned);

            callLoan.enqueue(new Callback<Loan>() {
                @Override
                public void onResponse(@NonNull Call<Loan> call, @NonNull Response<Loan> response) {

                    Log.i("onResponse", "try Loan");
                    if (response.isSuccessful() && response.body()!= null) {
                        Boolean success = response.body().isSuccess();
                        if (success) {
                            Log.i("onResponse", "success Loan");
                            Toast.makeText(context,
                                    response.body().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.i("onResponse", "loan" + response.body().getMessage());
                            Toast.makeText(context,
                                    response.body().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Loan> call, @NonNull Throwable t) {
                    Log.i("onFailure", "loan" + t.getLocalizedMessage());
                    Toast.makeText(context,
                            t.getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();

                }
            });
            Call<Student> callStudent =  apiInterface.updateStudent(title, card_uid);

            callStudent.enqueue(new Callback<Student>() {
                @Override
                public void onResponse(@NonNull Call<Student> call, @NonNull Response<Student> response) {

                    Log.i("onResponse", "try Student");
                    if (response.isSuccessful() && response.body()!= null) {
                        Boolean success = response.body().getSuccess();
                        if (success) {
                            Log.i("onResponse", "success student");
                            Toast.makeText(context,
                                    response.body().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.i("onResponse", "fail student");
                            Toast.makeText(context,
                                    response.body().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Student> call, @NonNull Throwable t) {
                    Log.i("onFailure", "failure student");
                    Toast.makeText(context,
                            t.getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();

                }
            });
            Call<Item> callItem =  apiInterface.updateItem(title, card_uid);

            callItem.enqueue(new Callback<Item>() {
                @Override
                public void onResponse(@NonNull Call<Item> call, @NonNull Response<Item> response) {

                    Log.i("onResponse", "try item");
                    if (response.isSuccessful() && response.body()!= null) {
                        Boolean success = response.body().getSuccess();
                        if (success) {
                            Log.i("onResponse", "success item");
                            Toast.makeText(context,
                                    response.body().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.i("onResponse", response.body().getMessage());
                            Toast.makeText(context,
                                    response.body().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Item> call, @NonNull Throwable t) {
                    Log.i("onFailure", t.getLocalizedMessage());
                    Toast.makeText(context,
                            t.getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();

                }
            });
        }

    public void returnItem(Context context, final int card_uid,
                            final String title,
                            final Timestamp timestampReturn,
                            final byte returned) {


        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<Loan> callReturnLoan =  apiInterface.returnLoan(card_uid, timestampReturn, returned);

        callReturnLoan.enqueue(new Callback<Loan>() {
            @Override
            public void onResponse(@NonNull Call<Loan> call, @NonNull Response<Loan> response) {

                Log.i("onResponse", "try return loan");
                if (response.isSuccessful() && response.body()!= null) {
                    Boolean success = response.body().isSuccess();
                    if (success) {
                        Log.i("onResponse", "success return loan");
                     //   Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i("onResponse", "return loan " + response.body().getMessage());
                        Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Loan> call, @NonNull Throwable t) {
                Log.i("onFailure", "return loan" + t.getLocalizedMessage());
                Toast.makeText(context, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();

            }
        });
        Call<Student> callStudent =  apiInterface.updateStudent("", card_uid);

        callStudent.enqueue(new Callback<Student>() {
            @Override
            public void onResponse(@NonNull Call<Student> call, @NonNull Response<Student> response) {

                Log.i("onResponse", "try update student");
                if (response.isSuccessful() && response.body()!= null) {
                    Boolean success = response.body().getSuccess();
                    if (success) {
                        Log.i("onResponse", "update student success");
                        Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i("onResponse", "fail update student");
                        Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Student> call, @NonNull Throwable t) {
                Log.i("onFailure", "update student");
                Toast.makeText(context, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }
    }

