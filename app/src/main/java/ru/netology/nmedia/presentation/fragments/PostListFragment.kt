package ru.netology.nmedia.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentPostListBinding
import ru.netology.nmedia.domain.model.Post
import ru.netology.nmedia.presentation.rvadapter.AdapterListener
import ru.netology.nmedia.presentation.rvadapter.MainAdapter
import ru.netology.nmedia.presentation.viewmodel.MainViewModel

class PostListFragment : Fragment(R.layout.fragment_post_list) {
    private var _binding: FragmentPostListBinding? = null
    private val binding: FragmentPostListBinding
        get() = _binding ?: throw RuntimeException("FragmentPostListBinding is null")

    private val mainViewModel by viewModel<MainViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPostListBinding.bind(view)

        setupRecyclerView()
        setupListeners()
        swipeRefresh()
        observeFlow()
    }

    override fun onStart() {
        super.onStart()
        mainViewModel.loadPost()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        val rvPostItem = binding.rvPostList
        val adapter = MainAdapter(
            object : AdapterListener {
                override fun onClickLike(post: Post) {
                    mainViewModel.like(post)
                }

                override fun onClickShare(post: Post) {
                    val intent = mainViewModel.share(post)
                    val shareIntent = Intent.createChooser(intent, getString(R.string.share))
                    startActivity(shareIntent)
                }

                override fun onClickDelete(post: Post) {
                    mainViewModel.deletePost(post.id)
                }

                override fun onClickEdit(post: Post) {
                    editor(post)
                }

                override fun onClickUrlVideo(post: Post) {
                    val intent = mainViewModel.launchYoutubeVideo(post)
                    val shareIntent = Intent.createChooser(intent, getString(R.string.watch))
                    startActivity(shareIntent)
                }

                override fun onClickPost(post: Post) {
                    findNavController().navigate(
                        PostListFragmentDirections.actionPostListFragmentToPostDetailFragment(post)
                    )
                }

            }
        )
        rvPostItem.adapter = adapter
        observePosts(adapter)
    }

    private fun observePosts(adapter: MainAdapter) {
        mainViewModel.data.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.posts)
            binding.apply {
                if (swipeRefresh.isRefreshing) {
                    swipeRefresh.isRefreshing = state.loading
                } else {
                    progress.isVisible = state.loading
                    errorGroup.isVisible = state.error
                    tvEmpty.isVisible = state.empty
                }
            }
        }
    }

    private fun setupListeners() {
        binding.buttonAdd.setOnClickListener {
            findNavController().navigate(
                PostListFragmentDirections.actionPostListFragmentToPostEditFragment("")
            )
        }

        binding.btnRetry.setOnClickListener {
            mainViewModel.loadPost()
        }
    }

    private fun observeFlow() {
        lifecycleScope.launchWhenStarted {
            mainViewModel.commands.collectLatest { command ->
                when (command) {
                    is MainViewModel.Command.ShowErrorSnackbar -> {
                        Snackbar.make(
                            binding.root,
                            getString(R.string.error_loading),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    MainViewModel.Command.ShowErrorLayout -> {
                        binding.errorGroup.isVisible = true
                    }
                    MainViewModel.Command.ShowContent -> { /* no-op */ }
                }
            }
        }
    }

    private fun editor(post: Post) {
        mainViewModel.edit(post)
        findNavController().navigate(
            PostListFragmentDirections.actionPostListFragmentToPostEditFragment(post.content)
        )
    }

    private fun swipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            mainViewModel.loadPost()
        }
    }
}