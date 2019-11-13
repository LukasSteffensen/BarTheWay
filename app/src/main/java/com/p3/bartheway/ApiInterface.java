package com.p3.bartheway;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("save.php")
    Call<Item> saveItem(
            @Field("item_title") String title,
            @Field("item_language") String language,
            @Field("item_description") String description,
            @Field("item_minplayers") int minPlayers,
            @Field("item_maxplayers") int maxPlayers
    );
}
