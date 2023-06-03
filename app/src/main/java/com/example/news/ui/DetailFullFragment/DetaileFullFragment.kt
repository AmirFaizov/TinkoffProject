package com.example.news.ui.DetailFullFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.navArgs
import com.example.news.activity.MainActivity
import com.example.news.databinding.FragmentDetailFullBinding
import com.example.news.ui.detail.DetailFragmentArgs
import com.example.news.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_detail_full.*
import kotlinx.android.synthetic.main.fragment_detail_full.pag_progress_bar
import kotlinx.android.synthetic.main.fragment_main.*

@AndroidEntryPoint
class DetaileFullFragment: Fragment() {


    private var _binding: FragmentDetailFullBinding? = null
    private val binding get() = _binding!!
    private val bundleArgs: DetailFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailFullBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val articleArg = bundleArgs.article
        webView.apply {
            webViewClient = object : WebViewClient(){
                override fun onPageCommitVisible(view: WebView?, url: String?) {
                    super.onPageCommitVisible(view, url)
                    pag_progress_bar.visibility = View.GONE
                }
            }

            articleArg.url?.let { loadUrl(it) }
        }

    }

}