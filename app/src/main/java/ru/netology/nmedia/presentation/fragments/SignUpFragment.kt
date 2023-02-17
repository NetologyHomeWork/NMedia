package ru.netology.nmedia.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.data.utils.observeSharedFlow
import ru.netology.nmedia.data.utils.observeStateFlow
import ru.netology.nmedia.databinding.BottomSheetAddAvatarBinding
import ru.netology.nmedia.databinding.FragmentSignUpBinding
import ru.netology.nmedia.presentation.viewmodel.SignUpViewModel

@AndroidEntryPoint
class SignUpFragment : Fragment(R.layout.fragment_sign_up) {

    private var _binding: FragmentSignUpBinding? = null
    private val binding: FragmentSignUpBinding
        get() = requireNotNull(_binding) { "FragmentSignUpBinding is null" }

    private val viewModel by viewModels<SignUpViewModel>()

    private val photoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    val uri = it.data?.data ?: return@registerForActivityResult
                    viewModel.changePhoto(uri, uri.toFile())
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSignUpBinding.bind(view)

        setupListeners()
        observeFlow()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setupListeners() {
        binding.btnSignUp.setOnClickListener {
            viewModel.signUp(
                binding.etName.text.toString(),
                binding.etLogin.text.toString(),
                binding.etPassword.text.toString(),
                binding.etRepeatPassword.text.toString()
            )
        }

        binding.ivAvatar.setOnClickListener {
            showBottomDialog()
        }

        binding.ivDeletePhoto.setOnClickListener {
            viewModel.clearPhoto()
        }

        binding.etName.doAfterTextChanged {
            viewModel.clearNameError()
        }

        binding.etLogin.doAfterTextChanged {
            viewModel.clearLoginError()
        }

        binding.etPassword.doAfterTextChanged {
            viewModel.clearPasswordError()
        }

        binding.etRepeatPassword.doAfterTextChanged {
            viewModel.clearRepeatPasswordError()
        }
    }

    private fun observeFlow() {
        viewModel.commands.observeSharedFlow(viewLifecycleOwner) { commands ->
            when (commands) {
                SignUpViewModel.Commands.NavigateBack -> {
                    findNavController().navigateUp()
                }
                is SignUpViewModel.Commands.ShowErrorSnackbar -> {
                    Snackbar.make(
                        binding.root,
                        commands.message,
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction(R.string.retry_loading) {
                        viewModel.retry(
                            binding.etName.text.toString(),
                            binding.etLogin.text.toString(),
                            binding.etPassword.text.toString(),
                            binding.etRepeatPassword.text.toString()
                        )
                    }.show()
                }
            }
        }

        viewModel.photo.observeStateFlow(viewLifecycleOwner) { photo ->
            if (photo != null) {
                binding.ivDeletePhoto.isVisible = true
                binding.ivAvatar.setImageURI(photo.uri)
            } else {
                binding.ivDeletePhoto.isVisible = false
                binding.ivAvatar.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_add_avatar)
                )
            }
        }

        viewModel.viewState.observeStateFlow(viewLifecycleOwner) { state ->
            Log.e("TAG_STATE", "observeFlow: $state")
            binding.tilName.isErrorEnabled = state.isNameEmpty
            binding.tilName.error = if (state.isNameEmpty) getString(R.string.error_name)
            else null

            binding.tilLogin.isErrorEnabled = state.isLoginEmpty
            binding.tilLogin.error = if (state.isLoginEmpty) getString(R.string.error_login)
            else null

            when (state.passwordState) {
                is SignUpViewModel.PasswordFieldState.EmptyError -> {
                    binding.tilPassword.isErrorEnabled = true
                    binding.tilPassword.error = state.passwordState.message
                }
                is SignUpViewModel.PasswordFieldState.MismatchError -> {
                    binding.tilPassword.isErrorEnabled = true
                    binding.tilPassword.error = state.passwordState.message
                }
                SignUpViewModel.PasswordFieldState.WithoutError -> {
                    binding.tilPassword.isErrorEnabled = false
                    binding.tilPassword.error = null
                }
            }

            when (state.repeatPasswordState) {
                is SignUpViewModel.RepeatPasswordFieldState.EmptyError -> {
                    binding.tilRepeatPassword.isErrorEnabled = true
                    binding.tilRepeatPassword.error = state.repeatPasswordState.message
                }
                is SignUpViewModel.RepeatPasswordFieldState.MismatchError -> {
                    binding.tilRepeatPassword.isErrorEnabled = true
                    binding.tilRepeatPassword.error = state.repeatPasswordState.message
                }
                SignUpViewModel.RepeatPasswordFieldState.WithoutError -> {
                    binding.tilRepeatPassword.isErrorEnabled = false
                    binding.tilRepeatPassword.error = null
                }
            }
        }
    }

    private fun showBottomDialog() {
        val bottomSheetDialog = BottomSheetDialog(binding.root.context)

        bottomSheetDialog.apply {
            val binding = BottomSheetAddAvatarBinding.inflate(layoutInflater)
            setContentView(binding.root)

            binding.groupPhoto.setOnClickListener {
                ImagePicker.with(this@SignUpFragment)
                    .crop()
                    .compress(IMAGE_MAX_SIZE)
                    .provider(ImageProvider.CAMERA)
                    .createIntent(photoLauncher::launch)
                dismiss()
            }

            binding.groupGallery.setOnClickListener {
                ImagePicker.with(this@SignUpFragment)
                    .crop()
                    .compress(IMAGE_MAX_SIZE)
                    .provider(ImageProvider.GALLERY)
                    .galleryMimeTypes(
                        arrayOf(IMAGE_PNG_TYPE, IMAGE_JPEG_TYPE)
                    )
                    .createIntent(photoLauncher::launch)
                dismiss()
            }
            show()
        }
    }

    private companion object {
        private const val IMAGE_MAX_SIZE = 200
        private const val IMAGE_PNG_TYPE = "image/png"
        private const val IMAGE_JPEG_TYPE = "image/jpeg"
    }
}