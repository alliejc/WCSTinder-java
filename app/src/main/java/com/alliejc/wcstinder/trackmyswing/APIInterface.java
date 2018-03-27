package com.alliejc.wcstinder.trackmyswing;

import com.alliejc.wcstinder.trackmyswing.Dancer;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by acaldwell on 3/18/18.
 */

public interface APIInterface {

    @GET("{division}/{role}")
    Call<List<Dancer>> getDancers(@Path("division") String division, @Path("role") String role);
}
