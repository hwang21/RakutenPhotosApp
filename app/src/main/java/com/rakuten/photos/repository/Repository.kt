package com.rakuten.photos.repository

import com.rakuten.photos.api.ApiService

class Repository(private val apiService: ApiService) {

    suspend fun getData() = apiService
        .getData(
            method = "flickr.photos.getRecent",
            apiKey = "fee10de350d1f31d5fec0eaf330d2dba",
            page = 1,
            format = "json",
            noJsonCallback = true,
            safeSearch = true
        )

}