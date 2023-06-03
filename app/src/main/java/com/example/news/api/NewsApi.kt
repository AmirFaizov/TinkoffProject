package com.example.news.api

import com.example.news.retrofit.NewsResp
import com.example.news.utils.Const.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    @GET("v2/everything")
    suspend fun getNewsEverything(
        @Query("q")
        query: String,
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY,
        @Query("pageSize")
        pageSize: Int = 20
    ): Response<NewsResp>


    @GET("v2/top-headlines")
    suspend fun getNews(
        @Query("country")
        countryCode: String = "ru",
        @Query("page")
        page: Int = 1,
        @Query("apiKey")
        apiKey: String = API_KEY
    ): Response<NewsResp>
}