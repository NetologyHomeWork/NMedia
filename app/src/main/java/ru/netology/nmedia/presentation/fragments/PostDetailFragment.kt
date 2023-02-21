package ru.netology.nmedia.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.nmedia.R
import ru.netology.nmedia.data.utils.assistedViewModel
import ru.netology.nmedia.data.utils.bindPostItemLayout
import ru.netology.nmedia.data.utils.observeSharedFlow
import ru.netology.nmedia.databinding.FragmentPostDetailBinding
import ru.netology.nmedia.domain.model.Post
import ru.netology.nmedia.domain.model.PostUIModel
import ru.netology.nmedia.presentation.activity.MainActivity
import ru.netology.nmedia.presentation.viewmodel.PostDetailViewModel
import ru.netology.nmedia.presentation.viewmodel.PostDetailViewModel.Command
import javax.inject.Inject

@AndroidEntryPoint
class PostDetailFragment : Fragment(R.layout.fragment_post_detail) {

    private var _binding: FragmentPostDetailBinding? = null
    private val binding: FragmentPostDetailBinding
        get() = _binding ?: throw NullPointerException("FragmentPostDetailBinding is null")

    private val args by navArgs<PostDetailFragmentArgs>()

    @Inject
    lateinit var factory: PostDetailViewModel.Factory

    private val viewModel: PostDetailViewModel by assistedViewModel { factory.create(args.postId) }

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
        val reqActivity = requireNotNull(activity as? MainActivity) {
            "Illegal type activity: ${activity?.javaClass?.simpleName}"
        }
        viewModel.currentPost.observe(viewLifecycleOwner) { post ->
            binding.postDetail.apply {
                bindPostItemLayout(
                    this,
                    PostUIModel(
                        post = post,
                        isError = false,
                        isNew = false,
                        ownedByMe = reqActivity.isAuth
                    ),
                    viewModel,
                    findNavController(),
                    reqActivity.isAuth
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