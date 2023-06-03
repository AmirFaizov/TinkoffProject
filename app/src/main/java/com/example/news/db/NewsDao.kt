package com.example.news.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.news.retrofit.Article

@Dao
interface NewsDao {

   @Query("SELECT * FROM articles")
   fun getAllNews(): LiveData<List<Article>>

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insert(article: Article)

   @Delete
   suspend fun deleteArticle(article: Article)
}