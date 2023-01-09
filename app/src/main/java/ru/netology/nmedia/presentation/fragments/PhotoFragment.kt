package ru.netology.nmedia.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentPhotoBinding

class PhotoFragment : Fragment(R.layout.fragment_photo) {

    private var _binding: FragmentPhotoBinding? = null
    private val binding: FragmentPhotoBinding
        get() = _binding ?: throw NullPointerException("FragmentPhotoBinding is null")

    private val args by navArgs<PhotoFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPhotoBinding.bind(view)

        setupView(args.pathPhoto)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setupView(path: String) {
        Glide.with(this)
            .load("${BuildConfig.BASE_URL}media/${path}")
            .timeout(10_000)
            .placeholder(R.drawable.ic_loading)
            .error(R.drawable.ic_error)
            .into(binding.ivPhoto)
    }
}