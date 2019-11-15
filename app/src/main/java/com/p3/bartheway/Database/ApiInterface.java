package com.p3.bartheway.Database;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

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

    @GET("items.php")
    Call<List<Item>> getItems();

    @GET("student.php")
    Call<Student> getStudent();

}
