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
            @Field("title") String title,
            @Field("language") String language,
            @Field("description") String description,
            @Field("minPlayers") int minPlayers,
            @Field("maxPlayers") int maxPlayers,
            @Field("duration") String duration,
            @Field("year") int year
    );

    @FormUrlEncoded
    @POST("loan.php")
    Call<Loan> saveLoan(
            @Field("title") String title,
            @Field("card_uid") int card_uid,
            @Field("timestampBorrow") Timestamp timestampBorrow,
            @Field("returned") byte returned
    );

    @GET("items.php")
    Call<List<Item>> getItems();

    @GET("student.php")
    Call<List<Student>> getStudent(@Query("card_uid") int card_uid);

}
