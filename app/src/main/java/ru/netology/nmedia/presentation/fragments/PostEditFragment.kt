package ru.netology.nmedia.presentation.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.netology.nmedia.R
import ru.netology.nmedia.data.utils.hideKeyboard
import ru.netology.nmedia.databinding.FragmentPostEditBinding
import ru.netology.nmedia.domain.model.Post
import ru.netology.nmedia.presentation.viewmodel.MainViewModel

class PostEditFragment : Fragment(R.layout.fragment_post_edit) {

    private var editPost = Post(
        id = 0,
        author = "",
        content = "",
        published = System.currentTimeMillis().toString().dropLast(3),
        authorAvatar = "",
        likesCount = 0,
        isLike = false,
        attachment = null
    )

    private var _binding: FragmentPostEditBinding? = null
    private val binding: FragmentPostEditBinding
        get() = _binding ?: throw NullPointerException("FragmentPostEditBinding is null")

    private val mainViewModel by viewModel<MainViewModel>(owner = ::requireParentFragment)

    private val args by navArgs<PostEditFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPostEditBinding.bind(view)
        binding.etEditPost.setText(args.post?.content)
        args.post?.let { editPost = it }
        binding.buttonSave.setOnClickListener {
            save()
            it.hideKeyboard()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun save() {
        if (binding.etEditPost.text.isNullOrBlank()) {
            Toast.makeText(
                context,
                R.string.empty_content,
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val content = binding.etEditPost.text.toString().trim()
            if (editPost.content == content) {
                findNavController().popBackStack()
            } else {
                editPost = editPost.copy(content = content)
                mainViewModel.save(editPost)
                mainViewModel.postCreated.observe(viewLifecycleOwner) {
                    findNavController().popBackStack()
                    mainViewModel.loadPost()
                }
            }
        }
    }
}