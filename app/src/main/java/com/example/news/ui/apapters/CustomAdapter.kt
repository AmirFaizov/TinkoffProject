package com.example.news.ui.apapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.news.R
import com.example.news.retrofit.Article
import kotlinx.android.synthetic.main.item_article.view.*

class CustomAdapter: RecyclerView.Adapter<CustomAdapter.CustomViewHolder>() {

    inner class CustomViewHolder(view: View): RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        return CustomViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_favorite_article, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val article = differ.currentList[position]
        val time: String? = article.publishedAt
        holder.itemView.apply {
            Glide.with(this)
                .load(article.urlToImage)
                .into(article_image)
            article_title.text = article.title
            val date = " " + time?.substring(0, time.indexOf('T', 0))
            article_date.text = date

            setOnClickListener {
                onItemClickListener?.let { it(article) }
            }
        }




            }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)

    private var onItemClickListener: ((Article) -> Unit)? = null



    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }
}

