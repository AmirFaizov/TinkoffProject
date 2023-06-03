package com.example.news.ui.main

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.news.di.NewsApp
import com.example.news.repository.NewsRepository
import com.example.news.retrofit.Article
import com.example.news.retrofit.NewsResp
import com.example.news.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: NewsRepository, app: Application) :
    AndroidViewModel(app) {

    val newsLiveData: MutableLiveData<Resource<NewsResp>> = MutableLiveData()
    var newsPage = 1
    var newsResponse: NewsResp? = null

    init {
        getNews("us")
    }

    fun getNews(countryCode: String) =
        viewModelScope.launch {
            safeNewsCall(countryCode)

        }

    fun saveArticle(article: Article) = viewModelScope.launch(Dispatchers.IO) {
        repository.addToFavotrite(article)
    }

    fun getSavedNews() = repository.getFavoriteArticles()

    fun shareNews(context: Context?, article: Article) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, article.url)
            putExtra(Intent.EXTRA_STREAM, article.url)
            putExtra(Intent.EXTRA_TITLE, article.title)
            type = "url/*"
        }
        context?.startActivity(Intent.createChooser(intent, "Share News On"))
    }

    fun deleteArticle(article: Article) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteFromFavotrite(article)
    }

    private suspend fun safeNewsCall(countryCode: String) {
        newsLiveData.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                newsLiveData.postValue(Resource.Loading())
                val response = repository.getNews(countryCode = countryCode, pageNumber = newsPage)
                if (response.isSuccessful) {
                    response.body()?.let { res ->
                        newsPage++
                        if (newsResponse == null) {
                            newsResponse = res
                        } else {
                            val oldArticles = newsResponse?.articles
                            val newArticles = res.articles
                            oldArticles?.addAll(newArticles)
                        }
                        newsLiveData.postValue(Resource.Success(newsResponse ?: res))
                    }
                } else {
                    newsLiveData.postValue(Resource.Error(response.message()))
                }
            } else {
                newsLiveData.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> newsLiveData.postValue(Resource.Error("Network Failure"))
                else -> newsLiveData.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<NewsApp>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(TRANSPORT_WIFI) -> true
            capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}
