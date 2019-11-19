package com.p3.bartheway.Database;

import java.sql.Timestamp;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("save.php")
    Call<Item> saveItem(
            @Field("item_title") String title,
            @Field("item_language") String language,
            @Field("item_description") String description,
            @Field("item_minplayers") int minPlayers,
            @Field("item_maxplayers") int maxPlayers,
            @Field("item_duration") String duration,
            @Field("item_year") int year
    );

    @FormUrlEncoded
    @POST("loan.php")
    Call<Loan> saveLoan(
            @Field("title") String title,
            @Field("card_uid") int card_uid,
            @Field("timestamp") Timestamp timestamp,
            @Field("returned") byte returned
    );

    @GET("items.php")
    Call<List<Item>> getItems();

    @GET("student.php")
    Call<List<Student>> getStudent(@Query("card_uid") int card_uid);

}
