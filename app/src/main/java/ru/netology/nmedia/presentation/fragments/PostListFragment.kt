package ru.netology.nmedia.presentation.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.data.utils.authDialog
import ru.netology.nmedia.data.utils.observeStateFlow
import ru.netology.nmedia.databinding.FragmentPostListBinding
import ru.netology.nmedia.domain.model.FeedModelState
import ru.netology.nmedia.domain.model.PostUIModel
import ru.netology.nmedia.presentation.activity.MainActivity
import ru.netology.nmedia.presentation.rvadapter.AdapterListener
import ru.netology.nmedia.presentation.rvadapter.MainAdapter
import ru.netology.nmedia.presentation.viewmodel.MainViewModel

@AndroidEntryPoint
class PostListFragment : Fragment(R.layout.fragment_post_list) {
    private var _binding: FragmentPostListBinding? = null
    private val binding: FragmentPostListBinding
        get() = _binding ?: throw RuntimeException("FragmentPostListBinding is null")

    private val mainViewModel by activityViewModels<MainViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPostListBinding.bind(view)

        val reqActivity = requireNotNull(activity as? MainActivity) {
            "Illegal type activity: ${activity?.javaClass?.simpleName}"
        }

        setupRecyclerView(reqActivity)
        setupListeners(reqActivity)
        swipeRefresh()
        observeFlow()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView(reqActivity: MainActivity) {

        val rvPostItem = binding.rvPostList
        val adapter = MainAdapter(
            reqActivity.isAuth,
            object : AdapterListener {

                override fun onClickLike(post: PostUIModel) {
                    if (reqActivity.isAuth) {
                        mainViewModel.like(post.post)
                    } else {
                        authDialog(requireContext(), findNavController(), R.string.error_like_post).show()
                    }
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

                override fun onPhotoClick(post: PostUIModel) {
                    val direction = PostListFragmentDirections
                        .actionPostListFragmentToPhotoFragment(checkNotNull(post.post.attachment).url)
                    findNavController().navigate(direction)
                }
            }
        )
        rvPostItem.adapter = adapter
        observePosts(adapter)
    }

    private fun observePosts(adapter: MainAdapter) {
        mainViewModel.data.observeStateFlow(viewLifecycleOwner) { state ->
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

    private fun setupListeners(reqActivity: MainActivity) {
        binding.buttonAdd.setOnClickListener {
            if (reqActivity.isAuth) {
                findNavController().navigate(
                    PostListFragmentDirections.actionPostListFragmentToPostEditFragment(null)
                )
            } else {
                authDialog(requireContext(), findNavController(), R.string.error_add_post).show()
            }

        }

        binding.btnRetry.setOnClickListener {
            mainViewModel.loadPost()
        }

        binding.btnNewPosts.setOnClickListener {
            mainViewModel.loadNew()
            it.isVisible = false
        }

        binding.rvPostList.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                binding.btnScrollDown.isVisible = dy > 0
                if ((binding.rvPostList.layoutManager as LinearLayoutManager)
                        .findLastCompletelyVisibleItemPosition() == (binding.rvPostList.layoutManager as LinearLayoutManager).itemCount - 1
                ) {
                    binding.btnScrollDown.isVisible = false
                }
            }
        })

        binding.btnScrollDown.setOnClickListener {
            binding.rvPostList.smoothScrollToPosition(checkNotNull(binding.rvPostList.adapter).itemCount)
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
                    else -> { /* no-op */
                    }
                }
            }
        }

        mainViewModel.newerCount.observeStateFlow(viewLifecycleOwner) { count ->
            binding.btnNewPosts.isVisible = count > 0
        }
    }

    private fun swipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            mainViewModel.refresh()
        }
    }
}