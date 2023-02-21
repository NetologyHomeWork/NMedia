package ru.netology.nmedia.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.netology.nmedia.data.AppException
import ru.netology.nmedia.data.auth.AppAuth
import ru.netology.nmedia.data.auth.AuthRepository
import ru.netology.nmedia.data.utils.commandSharedFlow
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val appAuth: AppAuth
) : ViewModel() {

    private val _commands = commandSharedFlow<Commands>()
    val commands = _commands.asSharedFlow()

    private val _viewState = MutableStateFlow(ViewState())
    val viewState = _viewState.asStateFlow()

    private val requireValue get() = _viewState.value

    fun signIn(login: String, password: String) {
        if (checkValidField(login, password).not()) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val data = authRepository.signIn(login, password)
                appAuth.setAuth(
                    id = data.id,
                    token = data.token
                )
                _commands.tryEmit(Commands.NavigateBack)
            } catch (e: AppException) {
                _commands.tryEmit(
                    Commands.ShowErrorSnackbar(e.message)
                )
            }
        }
    }

    fun clearLoginError() {
        _viewState.tryEmit(requireValue.copy(isLoginEmpty = false))
    }

    fun clearPasswordError() {
        _viewState.tryEmit(requireValue.copy(isPasswordEmpty = false))
    }

    fun retry(login: String, password: String) {
        signIn(login, password)
    }

    private fun checkValidField(login: String, password: String): Boolean {
        if (login.isBlank()) {
            _viewState.tryEmit(requireValue.copy(isLoginEmpty = true))
            return false
        }

        if (password.isBlank()) {
            _viewState.tryEmit(requireValue.copy(isPasswordEmpty = true))
            return false
        }
        return true
    }

    sealed interface Commands {
        object NavigateBack : Commands
        class ShowErrorSnackbar(val message: String) : Commands
    }

    data class ViewState(
        val isLoginEmpty: Boolean = false,
        val isPasswordEmpty: Boolean = false
    )
}