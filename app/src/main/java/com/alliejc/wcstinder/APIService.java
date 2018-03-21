package com.alliejc.wcstinder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by acaldwell on 3/18/18.
 */

public class APIService {
    private static APIService mApiService;
    private final APIInterface mInterface;

    public static APIService getAPIService() {
        if (mApiService != null){
            return mApiService;
        } else {
            mApiService = new APIService();
            return mApiService;
        }
    }

    public APIService(){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://trackmyswing.andrewsunada.com/api/dancers/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        mInterface = retrofit.create(APIInterface.class);
    }

    public Call getAllForDivision(String role, String division){
        return mInterface.getDancers(division, role);
    }
}
