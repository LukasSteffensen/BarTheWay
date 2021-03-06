package com.p3.bartheway.Database;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("saveItem.php")
    Call<Item> saveItem(
            @Field("title") String title,
            @Field("language") String language,
            @Field("description") String description,
            @Field("minPlayers") int minPlayers,
            @Field("maxPlayers") int maxPlayers,
            @Field("duration") String duration,
            @Field("year") int year
    );

    @FormUrlEncoded
    @POST("saveLoan.php")
    Call<Loan> saveLoan(
            @Field("card_uid") long card_uid,
            @Field("title") String title,
            @Field("timestampBorrow") String timestampBorrow,
            @Field("returned") byte returned
    );

    @FormUrlEncoded
    @POST("returnLoan.php")
    Call<Loan> returnLoan(
            @Field("card_uid") long card_uid,
            @Field("timestampReturn") String timestampReturn,
            @Field("returned") byte returned
    );

    @FormUrlEncoded
    @POST("updateItem.php")
    Call<Item> updateItem(
            @Field("title") String title,
            @Field("card_uid") long card_uid
    );

    @FormUrlEncoded
    @POST("updateStudent.php")
    Call<Student> updateStudent(
            @Field("title") String title,
            @Field("card_uid") long card_uid
    );

    @GET("getItems.php")
    Call<List<Item>> getItems();

    @GET("getStudent.php")
    Call<List<Student>> getStudent(@Query("card_uid") long card_uid);

    @GET("currentBorrowers.php")
    Call<List<Student>> getBorrowers();

    @GET("getLoans.php")
    Call<List<Loan>> getLoans(@Query("returned") byte returned);

    @GET("getPreviousLoans.php")
    Call<List<Loan>> getPreviousLoans(@Query("title") String title);

    @FormUrlEncoded
    @POST("deleteItem.php")
    Call<Item> deleteItem(@Field("title") String title);
}
