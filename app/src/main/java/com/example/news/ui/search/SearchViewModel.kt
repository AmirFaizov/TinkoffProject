package com.example.news.ui.search

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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
class SearchViewModel @Inject constructor(private val repository: NewsRepository, app: Application): AndroidViewModel(app) {

    val searchNewsLiveData: MutableLiveData<Resource<NewsResp>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: NewsResp? = null



    fun getSearchNews(query: String) =
        viewModelScope.launch {
            safeSearchNewsCall(query)
        }

    fun saveArticle(article: Article) = viewModelScope.launch(Dispatchers.IO) {
        repository.addToFavotrite(article)
    }

    fun shareNews(context: Context?, article: Article){
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, article.url)
            putExtra(Intent.EXTRA_STREAM, article.url)
            putExtra(Intent.EXTRA_TITLE, article.title)
            type = "url/*"
        }
        context?.startActivity(Intent.createChooser(intent,"Share News On"))
    }

    fun deleteArticle(article: Article) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteFromFavotrite(article)
    }
    private suspend fun safeSearchNewsCall(searchQuery: String){
        searchNewsLiveData.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()){
                searchNewsLiveData.postValue(Resource.Loading())
                val response = repository.getSearchNews(query = searchQuery, pageNumber = searchNewsPage)
                if (response.isSuccessful) {
                    response.body().let { res ->
                        searchNewsLiveData.postValue(Resource.Success(res))
                    }
                } else {
                    searchNewsLiveData.postValue(Resource.Error(message = response.message()))
                }
            } else{
                searchNewsLiveData.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable){
            when(t){
                is IOException -> searchNewsLiveData.postValue(Resource.Error("Network Failure"))
                else -> searchNewsLiveData.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun hasInternetConnection(): Boolean{
        val connectivityManager = getApplication<NewsApp>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        )  as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when{
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }
    }
