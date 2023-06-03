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

class NewsAdapter: RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {


    inner class NewsViewHolder(view: View): RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
       return NewsViewHolder(
           LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
       )
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
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
        holder.itemView.share.setOnClickListener {
            onShareNewsClick?.let {
                article.let { it1 ->
                    it(it1)
                }
            }
        }





        holder.itemView.favorite.setOnClickListener {
            if (holder.itemView.favorite.tag.toString().toInt() == 0) {
                holder.itemView.favorite.tag = 1
                holder.itemView.favorite.setImageDrawable(it.resources.getDrawable(R.drawable.liked))
                onArticleSaveClick?.let {
                    if (article != null) {
                        it(article)
                    }
                }
            } else {
                holder.itemView.favorite.tag = 0
                holder.itemView.favorite.setImageDrawable(it.resources.getDrawable(R.drawable.like))
                onArticleSaveClick?.let {
                    if (article != null) {
                        it(article)
                        }
                    }
                }
            onArticleSaveClick?.let{
                article?.let { it1 ->
                    it(it1)
                }
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

    private var onArticleSaveClick: ((Article) -> Unit)? = null

    private var onShareNewsClick: ((Article) -> Unit)? = null


    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnArticleSaveClick(listener: (Article) -> Unit) {
        onArticleSaveClick = listener
    }

    fun setOnShareNewsClick(listener: (Article) -> Unit) {
        onShareNewsClick = listener
    }
}