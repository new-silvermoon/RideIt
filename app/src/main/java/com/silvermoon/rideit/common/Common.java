package com.silvermoon.rideit.common;

import com.silvermoon.rideit.retrofit.IGoogleApi;
import com.silvermoon.rideit.retrofit.RetrofitClient;

/**
 * Created by faith on 3/24/2018.
 */

public class Common {
    private static final String baseURL = "https://maps.googleapis.com";
    public static IGoogleApi getGoogleAPI(){
        return RetrofitClient.getClient(baseURL).create(IGoogleApi.class);
    }
}
