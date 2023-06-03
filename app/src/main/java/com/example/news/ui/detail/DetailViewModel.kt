package com.example.news.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.news.repository.NewsRepository
import com.example.news.retrofit.Article
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(private val repository: NewsRepository): ViewModel() {
    fun saveArticle(article: Article) = viewModelScope.launch(Dispatchers.IO) {
        repository.addToFavotrite(article)
    }
}