package com.example.news.ui.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.news.repository.NewsRepository
import com.example.news.retrofit.Article
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouritesViewModel @Inject constructor(private val repository: NewsRepository): ViewModel() {
    fun saveArticle(article: Article) = viewModelScope.launch(Dispatchers.IO) {
        repository.addToFavotrite(article)
    }

    fun getSavedNews() = repository.getFavoriteArticles()

    fun deleteArticle(article: Article) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteFromFavotrite(article)
    }

}