package ru.netology.nmedia.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.netology.nmedia.R
import ru.netology.nmedia.data.utils.bindPostItemLayout
import ru.netology.nmedia.data.utils.observeSharedFlow
import ru.netology.nmedia.databinding.FragmentPostDetailBinding
import ru.netology.nmedia.domain.model.Post
import ru.netology.nmedia.domain.model.PostUIModel
import ru.netology.nmedia.presentation.viewmodel.PostDetailViewModel
import ru.netology.nmedia.presentation.viewmodel.PostDetailViewModel.Command

class PostDetailFragment : Fragment(R.layout.fragment_post_detail) {

    private var _binding: FragmentPostDetailBinding? = null
    private val binding: FragmentPostDetailBinding
        get() = _binding ?: throw NullPointerException("FragmentPostDetailBinding is null")

    private val args by navArgs<PostDetailFragmentArgs>()

    private val viewModel by viewModel<PostDetailViewModel> { parametersOf(args.postId) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPostDetailBinding.bind(view)
        bindLayout()
        setupObserver()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun bindLayout() {
        viewModel.currentPost.observe(viewLifecycleOwner) { post ->
            binding.postDetail.apply {
                bindPostItemLayout(
                    this,
                    PostUIModel(post, false),
                    viewModel,
                    findNavController()
                )
            }
            setupListeners(post)
        }
    }

    private fun setupListeners(post: Post) {
        binding.postDetail.apply {
            buttonShare.setOnClickListener {
                val intent = viewModel.share(post)
                val shareIntent = Intent.createChooser(intent, getString(R.string.share))
                startActivity((shareIntent))
            }
        }
    }

    private fun setupObserver() {
        lifecycleScope.launchWhenStarted {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.progressBar.isVisible = isLoading is PostDetailViewModel.IsLoading.Loading
                binding.postDetail.root.isVisible = isLoading is PostDetailViewModel.IsLoading.HasLoad
            }
        }

        viewModel.editPost.observeSharedFlow(viewLifecycleOwner) { command ->
            when(command) {
                is Command.LaunchEditScreen -> {
                    /*val direction = PostDetailFragmentDirections
                        .actionPostDetailFragmentToPostEditFragment(command.content)
                    findNavController().navigate(direction)*/
                }
            }
        }
    }
}