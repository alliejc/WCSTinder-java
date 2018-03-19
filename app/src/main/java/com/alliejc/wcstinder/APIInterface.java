package com.alliejc.wcstinder;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by acaldwell on 3/18/18.
 */

public interface APIInterface {

    @GET("{division}/{role}/{qualifies}")
    Call<Dancer> getDancers(@Query("division") String division, @Query("role") String role);

// APIInterface   @GET("articles/ios_index")
//    Call<Article> articleList();
//
//    @GET("articles/ios_index")
//    Call<Article> articlesNextPage(@Query("page") int page);
//
//    @GET("merchandise/marquee")
//    Call<SavedSearch> savedSearchList();
}
