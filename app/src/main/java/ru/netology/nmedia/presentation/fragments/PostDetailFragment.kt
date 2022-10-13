package ru.netology.nmedia.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.netology.nmedia.R
import ru.netology.nmedia.data.utils.bindPostItemLayout
import ru.netology.nmedia.databinding.FragmentPostDetailBinding
import ru.netology.nmedia.presentation.viewmodel.MainViewModel

class PostDetailFragment : Fragment(R.layout.fragment_post_detail) {

    private var _binding: FragmentPostDetailBinding? = null
    private val binding: FragmentPostDetailBinding
        get() = _binding ?: throw NullPointerException("FragmentPostDetailBinding is null")

    private val mainViewModel by viewModels<MainViewModel>(ownerProducer = ::requireParentFragment)

    private val args by navArgs<PostDetailFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPostDetailBinding.bind(view)
        mainViewModel.getCurrentPost(args.post)
        bindLayout()
        setupListeners()
    }

    private fun bindLayout() {
        mainViewModel.currentPost.observe(viewLifecycleOwner) { post ->
            binding.postDetail.apply {
                bindPostItemLayout(
                    this,
                    post,
                    mainViewModel,
                    findNavController()
                )
            }
        }

    }

    private fun setupListeners() {
        binding.postDetail.apply {
            if (videoView.visibility == View.VISIBLE) {
                videoView.setOnClickListener {
                    val intent = mainViewModel.launchYoutubeVideo(args.post)
                    val shareIntent = Intent.createChooser(intent, getString(R.string.watch))
                    startActivity(shareIntent)
                }
            }

            buttonShare.setOnClickListener {
                val intent = mainViewModel.share(args.post)
                val shareIntent = Intent.createChooser(intent, getString(R.string.share))
                startActivity((shareIntent))
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}