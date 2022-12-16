package ru.netology.nmedia.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentPostListBinding
import ru.netology.nmedia.domain.model.FeedModelState
import ru.netology.nmedia.domain.model.PostUIModel
import ru.netology.nmedia.presentation.rvadapter.AdapterListener
import ru.netology.nmedia.presentation.rvadapter.MainAdapter
import ru.netology.nmedia.presentation.viewmodel.MainViewModel

class PostListFragment : Fragment(R.layout.fragment_post_list) {
    private var _binding: FragmentPostListBinding? = null
    private val binding: FragmentPostListBinding
        get() = _binding ?: throw RuntimeException("FragmentPostListBinding is null")

    private val mainViewModel by viewModel<MainViewModel>(owner = ::requireParentFragment)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPostListBinding.bind(view)

        setupRecyclerView()
        setupListeners()
        swipeRefresh()
        observeFlow()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        val rvPostItem = binding.rvPostList
        val adapter = MainAdapter(
            object : AdapterListener {
                override fun onClickLike(post: PostUIModel) {
                    mainViewModel.like(post.post)
                }

                override fun onClickShare(post: PostUIModel) {
                    val intent = mainViewModel.share(post.post)
                    val shareIntent = Intent.createChooser(intent, getString(R.string.share))
                    startActivity(shareIntent)
                }

                override fun onClickDelete(post: PostUIModel) {
                    mainViewModel.deletePost(post.post)
                }

                override fun onClickEdit(post: PostUIModel) {
                    findNavController().navigate(
                        PostListFragmentDirections.actionPostListFragmentToPostEditFragment(post.post)
                    )
                }

                override fun onClickUrlVideo(post: PostUIModel) {
                    val intent = mainViewModel.launchYoutubeVideo(post.post)
                    val shareIntent = Intent.createChooser(intent, getString(R.string.watch))
                    startActivity(shareIntent)
                }

                override fun onClickPost(post: PostUIModel) {
                    findNavController().navigate(
                        PostListFragmentDirections
                            .actionPostListFragmentToPostDetailFragment(post.post.id)
                    )
                }

                override fun onClickRetry(post: PostUIModel) {
                    mainViewModel.retrySavePost(post.post)
                }

            }
        )
        rvPostItem.adapter = adapter
        observePosts(adapter)
    }

    private fun observePosts(adapter: MainAdapter) {
        mainViewModel.data.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.posts)
            binding.tvEmpty.isVisible = state.empty
        }

        mainViewModel.state.observe(viewLifecycleOwner) { state ->
            if (state is FeedModelState.Error) {
                Snackbar.make(
                    binding.root,
                    state.message,
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(R.string.retry_loading) {
                    mainViewModel.retry()
                }.show()
            }
            binding.progress.isVisible = state is FeedModelState.Loading
            binding.swipeRefresh.isRefreshing = state is FeedModelState.Refreshing
        }
    }

    private fun setupListeners() {
        binding.buttonAdd.setOnClickListener {
            findNavController().navigate(
                PostListFragmentDirections.actionPostListFragmentToPostEditFragment(null)
            )
        }

        binding.btnRetry.setOnClickListener {
            mainViewModel.loadPost()
        }

        binding.btnNewPosts.setOnClickListener {
            mainViewModel.loadNew()
            it.isVisible = false
        }
    }

    private fun observeFlow() {
        lifecycleScope.launchWhenStarted {
            mainViewModel.commands.collect { command ->
                when (command) {
                    is MainViewModel.Command.ShowErrorSnackbar -> {
                        Snackbar.make(
                            binding.root,
                            command.message,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }

                    MainViewModel.Command.ShowErrorLayout -> {
                        binding.errorGroup.isVisible = true
                    }

                    MainViewModel.Command.Scroll -> {
                        binding.rvPostList.smoothScrollToPosition(0)
                    }
                }
            }
        }

        mainViewModel.newerCount.observe(viewLifecycleOwner) { count ->
            binding.btnNewPosts.isVisible = count > 0
        }
    }

    private fun swipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            mainViewModel.refresh()
        }
    }
}