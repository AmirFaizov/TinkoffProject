package com.example.news.utils

import android.content.Context
import android.content.Intent
import com.example.news.retrofit.Article
import okhttp3.logging.HttpLoggingInterceptor

class Const {
    companion object {
        const val API_KEY = "d87ff4b7465847b9890d02850794613f"
        const val BASE_URL = "https://newsapi.org/"
        const val QUERY_PAGE_SIZE = 20
        //a028d61fa79b4949863a74d65ecdbb81
        //7b5837f4a5f94d1c9ae2d492d0b4ce8c
        //e1b7ff52cf514a85be8bbf33d87ed0fa
        //d87ff4b7465847b9890d02850794613f
        }

    fun shareNews(context: Context?, article: Article){
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, article.urlToImage)
            putExtra(Intent.EXTRA_STREAM, article.urlToImage)
            putExtra(Intent.EXTRA_TITLE, article.title)
            type = "image/*"
        }
        context?.startActivity(Intent.createChooser(intent,"Share News On"))
    }

}