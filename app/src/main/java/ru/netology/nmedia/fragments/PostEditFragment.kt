package ru.netology.nmedia.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentPostEditBinding
import ru.netology.nmedia.viewmodel.MainViewModel

class PostEditFragment : Fragment() {

    private var _binding: FragmentPostEditBinding? = null
    private val binding: FragmentPostEditBinding
        get() = _binding ?: throw NullPointerException("FragmentPostEditBinding is null")

    private val mainViewModel by viewModels<MainViewModel>(ownerProducer = ::requireParentFragment)

    private val args by navArgs<PostEditFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostEditBinding.inflate(inflater, container, false)
        binding.etEditPost.setText(args.content)
        binding.buttonSave.setOnClickListener { save() }

        return binding.root
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
            findNavController().popBackStack()
        }
    }
}