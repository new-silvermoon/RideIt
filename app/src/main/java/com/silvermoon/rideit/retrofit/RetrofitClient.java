package com.silvermoon.rideit.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by faith on 3/24/2018.
 */

public class RetrofitClient {

    public static Retrofit retrofit = null;

    public static Retrofit getClient(String baseUrl){

        if(retrofit == null){
            retrofit = new Retrofit.Builder().baseUrl(baseUrl)
                    .addConverterFactory(ScalarsConverterFactory.create()).build();
        }
        return retrofit;
    }
}
