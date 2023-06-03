package com.example.news.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.news.R
import com.example.news.databinding.FragmentDetailBinding
import com.example.news.retrofit.Article
import com.example.news.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.random.Random

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val bundleArgs: DetailFragmentArgs by navArgs()
    private val viewModel by viewModels<MainViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val articleArg = bundleArgs.article

        articleArg.let { article: Article ->
            article.urlToImage.let {
                Glide.with(this).load(article.urlToImage).into(binding.headerImage)
            }
            binding.headerImage.clipToOutline = true
            binding.articleDetailsTitle.text = article.title
            binding.detailsDecriptionText.text = article.description

            binding.like.setOnClickListener{
                if(article.id == null) {
                    article.id = Random.nextInt(0, 1000)
                }
                viewModel.saveArticle(article)
            }

            binding.share.setOnClickListener{
                viewModel.shareNews(context, article)
            }


            binding.articleDetailsButton.setOnClickListener {
                val bundle = bundleOf("article" to article)
                view.findNavController().navigate(
                    R.id.action_detailFragment_to_detaileFullFragment,
                    bundle
                )
            }
        }
    }

}