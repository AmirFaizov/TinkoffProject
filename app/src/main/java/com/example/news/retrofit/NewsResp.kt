package com.example.news.retrofit

data class NewsResp(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)