package com.rakuten.photos.api

import com.rakuten.photos.model.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("photos.json")
    suspend fun getData (
        @Query("method") method: String,
        @Query("api_key") apiKey: String,
        @Query("page") page: Int,
        @Query("format") format: String,
        @Query("nojsoncallback") noJsonCallback: Boolean,
        @Query("safe_search") safeSearch: Boolean,
    ): Response

}