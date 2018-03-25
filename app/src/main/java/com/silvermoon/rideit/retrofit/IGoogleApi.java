package com.silvermoon.rideit.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by faith on 3/24/2018.
 */

public interface IGoogleApi {
    @GET
    Call<String> getPath(@Url String url);
}
