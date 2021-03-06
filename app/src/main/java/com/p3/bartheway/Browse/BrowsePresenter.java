package com.p3.bartheway.Browse;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;
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

    void getItemData() {

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
    void getStudentData(long card_uid){

        view.showLoading();

        Call<List<Student>> call = apiInterface.getStudent(card_uid);
        call.enqueue(new Callback<List<Student>>() {
            @Override
            public void onResponse(@NonNull Call<List<Student>> call, @NonNull Response<List<Student>> response) {
                view.hideLoading();

                if (response.isSuccessful() && response.body() != null) {
                    view.onGetStudent(response.body());
                    Log.i("getStudentDataSuccess", response.body().toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Student>> call, @NonNull Throwable t) {
                view.hideLoading();
                view.onErrorLoading(t.getLocalizedMessage());
                Log.i("getStudentDataFail", t.toString());
            }
        });
    }
    void getCurrentBorrowers(){

        view.showLoading();

        Call<List<Student>> callStudents = apiInterface.getBorrowers();
        callStudents.enqueue(new Callback<List<Student>>() {
            @Override
            public void onResponse(@NonNull Call<List<Student>> call, @NonNull Response<List<Student>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    view.onGetStudent(response.body());
                    Log.i("CurrentBorrowersSuccess", response.body().toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Student>> call, @NonNull Throwable t) {
                Log.i("CurrentBorrowersFail", t.toString());
            }
        });
    }
    void getLoans(byte returned) {
        Call<List<Loan>> callLoans = apiInterface.getLoans(returned);
        callLoans.enqueue(new Callback<List<Loan>>() {
            @Override
            public void onResponse(@NonNull Call<List<Loan>> call, @NonNull Response<List<Loan>> response) {
                view.hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    view.onGetLoans(response.body());
                    Log.i("getLoansSuccess", response.body().toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Loan>> call, @NonNull Throwable t) {
                Log.i("getLoansFailure", t.toString());
            }
        });
    }
    void getPreviousLoans(String title) {
        Call<List<Loan>> callLoans = apiInterface.getPreviousLoans(title);
        callLoans.enqueue(new Callback<List<Loan>>() {
            @Override
            public void onResponse(@NonNull Call<List<Loan>> call, @NonNull Response<List<Loan>> response) {
                view.hideLoading();
                String success;
                if (response.isSuccessful()) {
                    success = "success";
                } else {
                    success = "not success";
                }
                if (response.isSuccessful() && response.body() != null) {
                    view.onGetLoans(response.body());
                    Log.i("getPreviousLoansSuccess", response.body().toString());
                } else {
                    Log.i("hello", success);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Loan>> call, @NonNull Throwable t) {
                Log.i("getPreviousLoansFailure", t.toString());
                view.onErrorLoading("");
            }
        });
    }
    /**
     * Method that does everything in the database when a loan is made by calling the methods
     * saveLoan, updateStudentBorrow, and updateItemBorrow in ApiInterface
     * @param title
     * @param card_uid
     * @param timestampBorrow
     * @param returned
     */
    void saveLoan(Context context,
                          final long card_uid,
                          final String title,
                          final String timestampBorrow,
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
        /**
     * Method that does everything in the database when an item is returned, by calling the methods
     * updateLoan, updateStudent, and updateItem in ApiInterface
     * @param title
     * @param card_uid
     * @param timestampReturn
     * @param returned
     */
    void returnItem(Context context,
                    final long card_uid,
                    final String title,
                    final String timestampReturn,
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
        Call<Item> callReturnItem=  apiInterface.updateItem(title, -1);

        callReturnItem.enqueue(new Callback<Item>() {
            @Override
            public void onResponse(@NonNull Call<Item> call, @NonNull Response<Item> response) {

                Log.i("onResponse", "try return item");
                if (response.isSuccessful() && response.body()!= null) {
                    Boolean success = response.body().getSuccess();
                    if (success) {
                        Log.i("onResponse", "success return item");
                        //   Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i("onResponse", "return item " + response.body().getMessage());
                        Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Item> call, @NonNull Throwable t) {
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
        // Fix here later with Toasts
        // To Jonas from Jonas

    }
    void deleteItem(String title) {
        // put call to the ApiInterface method also called deleteItem()

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<Item> call = apiInterface.deleteItem(title);
        call.enqueue(new Callback<Item>() {
            @Override
            public void onResponse(@NonNull Call<Item> call, @NonNull Response<Item> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean success = response.body().getSuccess();
                    if (success){
                        Log.i("delete item", response.body().toString());
                    } else {
                        Log.i("delete item", response.body().toString());

                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Item> call, @NonNull Throwable t) {
                Log.i("delete item", t.getLocalizedMessage());

            }
        });
    }

}