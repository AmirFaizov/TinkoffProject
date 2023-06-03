package com.example.news.di

import android.content.Context
import androidx.room.Room
import com.example.news.api.NewsApi
import com.example.news.db.NewsDao
import com.example.news.db.NewsDatabase
import com.example.news.utils.Const
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule{

    @Provides
    fun providesBaseUrl() = Const.BASE_URL

    @Provides
    fun providesLogginInterceptor(): HttpLoggingInterceptor{
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    fun providesOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(providesLogginInterceptor())
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(): NewsApi =
        Retrofit.Builder()
            .baseUrl(providesBaseUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .client(providesOkHttpClient())
            .build()
            .create(NewsApi::class.java)

    @Provides
    @Singleton
    fun provideAtricleDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context,
            NewsDatabase::class.java,
            "news_database"
        ).build()

    @Provides
    fun provideArticleDao(appDatabase: NewsDatabase): NewsDao {
        return appDatabase.getArticleDao()
    }
    }
