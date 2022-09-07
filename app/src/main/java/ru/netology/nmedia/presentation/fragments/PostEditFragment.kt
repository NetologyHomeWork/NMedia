package ru.netology.nmedia.presentation.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentPostEditBinding
import ru.netology.nmedia.presentation.viewmodel.MainViewModel

class PostEditFragment : Fragment(R.layout.fragment_post_edit) {

    private var _binding: FragmentPostEditBinding? = null
    private val binding: FragmentPostEditBinding
        get() = _binding ?: throw NullPointerException("FragmentPostEditBinding is null")

    private val mainViewModel by viewModels<MainViewModel>(ownerProducer = ::requireParentFragment)

    private val args by navArgs<PostEditFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPostEditBinding.bind(view)
        binding.etEditPost.setText(args.content)
        binding.buttonSave.setOnClickListener { save() }
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
            mainViewModel.editedContent(binding.etEditPost.text.toString())
            mainViewModel.save()
            mainViewModel.editingClear()
            mainViewModel.postCreated.observe(viewLifecycleOwner) {
                findNavController().popBackStack()
                mainViewModel.loadPost()
            }
        }
    }
}