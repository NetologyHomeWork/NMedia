package ru.netology.nmedia.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.data.utils.observeSharedFlow
import ru.netology.nmedia.data.utils.observeStateFlow
import ru.netology.nmedia.databinding.FragmentSignInBinding
import ru.netology.nmedia.presentation.viewmodel.SignInViewModel

@AndroidEntryPoint
class SignInFragment : Fragment(R.layout.fragment_sign_in) {

    private var _binding: FragmentSignInBinding? = null
    private val binding: FragmentSignInBinding
        get() = requireNotNull(_binding) { "FragmentSignInBinding is null" }

    private val viewModel by viewModels<SignInViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSignInBinding.bind(view)

        setupListeners()
        observeFlow()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setupListeners() {
        binding.btnSignIn.setOnClickListener {
            viewModel.signIn(
                login = binding.etLogin.text.toString(),
                password = binding.etPassword.text.toString()
            )
        }

        binding.etLogin.doAfterTextChanged {
            viewModel.clearLoginError()
        }

        binding.etPassword.doAfterTextChanged {
            viewModel.clearPasswordError()
        }
    }

    private fun observeFlow() {
        viewModel.commands.observeSharedFlow(viewLifecycleOwner) { commands ->
            when (commands) {
                SignInViewModel.Commands.NavigateBack -> {
                    findNavController().navigateUp()
                }
                is SignInViewModel.Commands.ShowErrorSnackbar -> {
                    Snackbar.make(
                        binding.root,
                        commands.message,
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction(R.string.retry_loading) {
                        viewModel.retry(
                            login = binding.etLogin.text.toString(),
                            password = binding.etPassword.text.toString()
                        )
                    }.show()
                }
            }
        }

        viewModel.viewState.observeStateFlow(viewLifecycleOwner) { state ->
            binding.tilLogin.isErrorEnabled = state.isLoginEmpty
            binding.tilLogin.error = if (state.isLoginEmpty) getString(R.string.error_login)
            else null

            binding.tilPassword.isErrorEnabled = state.isPasswordEmpty
            binding.tilPassword.error =
                if (state.isPasswordEmpty) getString(R.string.error_password)
                else null
        }
    }
}