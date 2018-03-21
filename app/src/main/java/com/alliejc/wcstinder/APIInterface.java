package com.alliejc.wcstinder;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by acaldwell on 3/18/18.
 */

public interface APIInterface {

    @GET("{division}/{role}")
    Call<List<Dancer>> getDancers(@Path("division") String division, @Path("role") String role);
}
