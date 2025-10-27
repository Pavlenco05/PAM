package com.example.lab03.data.remote

import com.example.lab03.data.model.RssFeed
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface RssService {
    @GET
    suspend fun getFeed(@Url url: String): Response<RssFeed>
}
