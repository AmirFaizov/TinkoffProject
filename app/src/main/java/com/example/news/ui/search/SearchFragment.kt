package com.example.news.ui.search

import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.news.R
import com.example.news.databinding.FragmentSearchBinding
import com.example.news.ui.apapters.NewsAdapter
import com.example.news.utils.Const
import com.example.news.utils.Resource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<SearchViewModel>()
    lateinit var newsAdapter: NewsAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()

        var job: Job? = null
        et_Search.addTextChangedListener { text: Editable? ->
            job?.cancel()
            job = MainScope().launch {
                delay(500L)
                text?.let {
                    if (it.toString().isNotEmpty()) {
                        viewModel.getSearchNews(query = it.toString())
                    }
                }
            }
        }

        newsAdapter.setOnItemClickListener {
            val bundle = bundleOf("article" to it)
            view.findNavController().navigate(
                R.id.action_searchFragment_to_detailFragment,
                bundle
            )
        }

        newsAdapter.setOnArticleSaveClick {
            if(it.id == null) {
                it.id = Random.nextInt(0, 1000)}
            viewModel.saveArticle(it)
            }

        newsAdapter.setOnShareNewsClick {
            viewModel.shareNews(context, it)
        }

        viewModel.searchNewsLiveData.observe(viewLifecycleOwner){
                response -> when(response){
            is Resource.Success->{
                isLoading = false
                search_progress_bar.visibility = View.INVISIBLE
                response.data?.let {
                    newsAdapter.differ.submitList(it.articles.toList())
                    val totalPages = it.totalResults / Const.QUERY_PAGE_SIZE + 2
                    isLastPage = viewModel.searchNewsPage == totalPages
                }
            }
            is Resource.Error ->{
                isLoading = false
                search_progress_bar.visibility = View.INVISIBLE
                response.massage?.let { message ->
                    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()

                    }
            }
            is Resource.Loading->{
                isLoading = true
                search_progress_bar.visibility = View.VISIBLE
            }
        }
        }
    }
    private fun initAdapter(){
        newsAdapter = NewsAdapter()
        search_rv.apply{
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
            val isTotalMoreThanVisible = totalItemCount >= Const.QUERY_PAGE_SIZE

            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling
            if(shouldPaginate){
                viewModel.getSearchNews(et_Search.text.toString())
                isScrolling = false
            }
        }
    }
}