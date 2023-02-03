package ru.netology.nmedia.presentation.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.netology.nmedia.R
import ru.netology.nmedia.data.utils.hideKeyboard
import ru.netology.nmedia.data.utils.observeSharedFlow
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

    private val viewModel by viewModel<MainViewModel>(owner = ::requireParentFragment)

    private val args by navArgs<PostEditFragmentArgs>()
    private val photoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        when (it.resultCode) {
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show() // todo
            }
            else ->  {
                val uri = it.data?.data ?: return@registerForActivityResult
                viewModel.changePhoto(uri, uri.toFile())
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPostEditBinding.bind(view)
        binding.etEditPost.setText(args.post?.content)
        args.post?.let { editPost = it }
        setupListener()
        setupObserver()
        setOptionMenu()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setupListener() {
        binding.pickPhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(IMAGE_MAX_SIZE)
                .provider(ImageProvider.CAMERA)
                .createIntent(photoLauncher::launch)
        }

        binding.pickFile.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(IMAGE_MAX_SIZE)
                .provider(ImageProvider.GALLERY)
                .galleryMimeTypes(
                    arrayOf(IMAGE_PNG_TYPE, IMAGE_JPEG_TYPE)
                )
                .createIntent(photoLauncher::launch)
        }

        binding.ivDeletePhoto.setOnClickListener {
            viewModel.changePhoto(null, null)
        }
    }

    private fun setupObserver() {
        viewModel.photo.observe(viewLifecycleOwner) { photo ->
            binding.photoLayout.isVisible = photo != null
            binding.ivPhoto.setImageURI(photo?.uri)
        }

        viewModel.commands.observeSharedFlow(viewLifecycleOwner) { command ->
            when (command) {
                is MainViewModel.Command.SavePost -> {
                    viewModel.changePhoto(null, null)
                    findNavController().popBackStack()
                    viewModel.loadPost()
                }
                else -> { /* no-op */ }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.blockContent.collectLatest { block ->
                binding.overlay.isVisible = block
                binding.etEditPost.isEnabled = block.not()
                binding.ivDeletePhoto.isEnabled = block.not()
                binding.pickFile.isEnabled = block.not()
                binding.pickPhoto.isEnabled = block.not()
            }
        }
    }

    private fun setOptionMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.toolbar_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.save -> {
                        view?.hideKeyboard()
                        save()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
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
                viewModel.save(editPost)
            }
        }
    }

    companion object {
        private const val IMAGE_MAX_SIZE = 1024
        private const val IMAGE_PNG_TYPE = "image/png"
        private const val IMAGE_JPEG_TYPE = "image/jpeg"
    }
}