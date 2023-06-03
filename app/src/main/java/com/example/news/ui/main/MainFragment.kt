package com.example.news.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.news.R
import com.example.news.databinding.FragmentMainBinding
import com.example.news.retrofit.NewsResp
import com.example.news.ui.apapters.NewsAdapter
import com.example.news.utils.Const
import com.example.news.utils.Const.Companion.QUERY_PAGE_SIZE
import com.example.news.utils.Resource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_main.*
import kotlin.random.Random

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<MainViewModel>()
    lateinit var newsAdapter: NewsAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()

        newsAdapter.setOnItemClickListener {
            val bundle = bundleOf("article" to it)
            view.findNavController().navigate(
                R.id.action_mainFragment_to_detailFragment,
                bundle
            )
        }

        newsAdapter.setOnArticleSaveClick {
            if(it.id == null) {
                it.id = Random.nextInt(0, 1000)
            }
            viewModel.saveArticle(it)
        }

        newsAdapter.setOnShareNewsClick {
            viewModel.shareNews(context, it)
        }


        viewModel.newsLiveData.observe(viewLifecycleOwner){ response ->
            when(response){
                is Resource.Success -> {
                    pag_progress_bar.visibility = View.INVISIBLE
                    isLoading = false
                    response.data?.let {
                        newsAdapter.differ.submitList(it.articles.toList())
                        val totalPages = it.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.newsPage == totalPages
                        if(isLastPage){
                            news_rv.setPadding(0,0,0,0)
                        }
                    }
                }
                is Resource.Error -> {
                    pag_progress_bar.visibility = View.INVISIBLE
                    isLoading = false
                    response.massage?.let { message->
                        Toast.makeText(activity,"An error occured: $message", Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Loading -> {
                    pag_progress_bar.visibility = View.VISIBLE
                    isLoading = true
                }
            }
        }

    }
    private fun initAdapter(){
        newsAdapter = NewsAdapter()
        news_rv.apply{
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(scrollLisenerCustom)
        }
    }
    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollLisenerCustom = object : RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLastPage && !isLoading
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE

            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling
            if(shouldPaginate){
                viewModel.getNews("us")
                isScrolling = false
            }
        }
    }
}
