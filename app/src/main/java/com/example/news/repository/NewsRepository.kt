package com.example.news.repository

import com.example.news.api.NewsApi
import com.example.news.db.NewsDao
import com.example.news.retrofit.Article
import javax.inject.Inject

class NewsRepository @Inject constructor(
    private val newsApi: NewsApi,
    private val newsDao: NewsDao
    ) {
    suspend fun getNews(countryCode: String, pageNumber: Int) =
        newsApi.getNews(countryCode = countryCode, page = pageNumber)

    suspend fun getSearchNews(query: String, pageNumber: Int) =
        newsApi.getNewsEverything(query = query, pageNumber = pageNumber)

    fun getFavoriteArticles() = newsDao.getAllNews()

    suspend fun addToFavotrite(article: Article) = newsDao.insert(article = article)

    suspend fun deleteFromFavotrite(article: Article) = newsDao.deleteArticle(article = article)

}