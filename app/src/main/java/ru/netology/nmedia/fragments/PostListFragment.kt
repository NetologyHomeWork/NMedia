package ru.netology.nmedia.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentPostListBinding
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.rvadapter.AdapterListener
import ru.netology.nmedia.rvadapter.MainAdapter
import ru.netology.nmedia.viewmodel.MainViewModel
import ru.netology.nmedia.viewmodel.ViewModelFactory

class PostListFragment : Fragment() {
    private var _binding: FragmentPostListBinding? = null
    private val binding: FragmentPostListBinding
        get() = _binding ?: throw RuntimeException("FragmentPostListBinding is null")


    private val factory by lazy { ViewModelFactory(requireActivity().application) }
    private val mainViewModel by viewModels<MainViewModel>(ownerProducer = ::requireParentFragment) { factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostListBinding.inflate(inflater, container, false)
        setupRecyclerView()
        binding.buttonAdd.setOnClickListener {
            findNavController().navigate(
                PostListFragmentDirections.actionPostListFragmentToPostEditFragment("")
            )
        }

        return binding.root
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

        mainViewModel.data.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    private fun editor(post: Post) {
        mainViewModel.edit(post)
        findNavController().navigate(
            PostListFragmentDirections.actionPostListFragmentToPostEditFragment(post.content)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}