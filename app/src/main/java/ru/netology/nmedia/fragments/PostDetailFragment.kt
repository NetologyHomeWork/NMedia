package ru.netology.nmedia.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentPostDetailBinding
import ru.netology.nmedia.utils.bindPostItemLayout
import ru.netology.nmedia.viewmodel.MainViewModel

class PostDetailFragment : Fragment() {

    private var _binding: FragmentPostDetailBinding? = null
    private val binding: FragmentPostDetailBinding
        get() = _binding ?: throw NullPointerException("FragmentPostDetailBinding is null")

    private val mainViewModel by viewModels<MainViewModel>(ownerProducer = ::requireParentFragment)

    private val args by navArgs<PostDetailFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostDetailBinding.inflate(inflater, container, false)
        val currentPost = args.currentPost
        mainViewModel.data.observe(viewLifecycleOwner) {
            val post = mainViewModel.findPostById(currentPost.id) ?: return@observe
            binding.postDetail.apply {
                bindPostItemLayout(
                    this,
                    post,
                    mainViewModel,
                    findNavController()
                )

                if (videoView.visibility == View.VISIBLE) {
                    videoView.setOnClickListener {
                        val intent = mainViewModel.launchYoutubeVideo(post)
                        val shareIntent = Intent.createChooser(intent, getString(R.string.watch))
                        startActivity(shareIntent)
                    }
                }

                buttonShare.setOnClickListener {
                    val intent = mainViewModel.share(post)
                    val shareIntent = Intent.createChooser(intent, getString(R.string.share))
                    startActivity((shareIntent))
                }
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}