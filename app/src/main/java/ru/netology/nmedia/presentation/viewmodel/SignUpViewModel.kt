package ru.netology.nmedia.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.data.AppException
import ru.netology.nmedia.data.auth.AppAuth
import ru.netology.nmedia.data.auth.AuthRepository
import ru.netology.nmedia.data.utils.ResourceManager
import ru.netology.nmedia.data.utils.commandSharedFlow
import ru.netology.nmedia.domain.model.PhotoModel
import java.io.File

class SignUpViewModel(
    private val authRepository: AuthRepository,
    private val resourceManager: ResourceManager
) : ViewModel() {

    private val auth = AppAuth.getInstance()

    private val _commands = commandSharedFlow<Commands>()
    val commands = _commands.asSharedFlow()

    private val _photo = MutableStateFlow<PhotoModel?>(null)
    val photo = _photo.asStateFlow()

    private val _viewState = MutableStateFlow(ViewState())
    val viewState = _viewState.asStateFlow()

    private val requireValue get() = _viewState.value

    fun signUp(name: String, login: String, password: String, repeatPassword: String) {
        if (checkValidField(name, login, password, repeatPassword).not()) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val authState = authRepository.signUp(name, login, password, _photo.value)
                auth.setAuth(
                    id = authState.id,
                    token = authState.token
                )
                _commands.tryEmit(Commands.NavigateBack)
            } catch (e: AppException) {
                _commands.tryEmit(
                    Commands.ShowErrorSnackbar(e.message)
                )
            }
        }
    }

    fun changePhoto(uri: Uri?, file: File?) {
        _photo.tryEmit(
            if (uri != null && file != null) PhotoModel(uri, file)
            else null
        )
    }

    fun clearPhoto() {
        _photo.tryEmit(null)
    }

    fun clearNameError() {
        _viewState.tryEmit(requireValue.copy(isNameEmpty = false))
    }

    fun clearLoginError() {
        _viewState.tryEmit(requireValue.copy(isLoginEmpty = false))
    }

    fun clearPasswordError() {
        _viewState.tryEmit(requireValue.copy(passwordState = PasswordFieldState.WithoutError))
    }

    fun clearRepeatPasswordError() {
        _viewState.tryEmit(requireValue.copy(repeatPasswordState = RepeatPasswordFieldState.WithoutError))
    }

    fun retry(name: String, login: String, password: String, repeatPassword: String) {
        signUp(name, login, password, repeatPassword)
    }

    private fun checkValidField(
        name: String,
        login: String,
        password: String,
        repeatPassword: String
    ): Boolean {
        if (name.isBlank()) {
            _viewState.tryEmit(requireValue.copy(isNameEmpty = true))
            return false
        }

        if (login.isBlank()) {
            _viewState.tryEmit(requireValue.copy(isLoginEmpty = true))
            return false
        }

        if (password.isBlank()) {
            _viewState.tryEmit(
                requireValue.copy(
                    passwordState = PasswordFieldState.EmptyError(
                        resourceManager.getString(R.string.error_password)
                    )
                )
            )
            return false
        }

        if (repeatPassword.isBlank()) {
            _viewState.tryEmit(
                requireValue.copy(
                    repeatPasswordState = RepeatPasswordFieldState.EmptyError(
                        resourceManager.getString(R.string.error_password)
                    )
                )
            )
            return false
        }

        if (password != repeatPassword) {
            _viewState.tryEmit(
                requireValue.copy(
                    passwordState = PasswordFieldState.MismatchError(
                        resourceManager.getString(R.string.error_password_mismatch)
                    )
                )
            )
            _viewState.tryEmit(
                requireValue.copy(
                    repeatPasswordState = RepeatPasswordFieldState.MismatchError(
                        resourceManager.getString(R.string.error_password_mismatch)
                    )
                )
            )
            return false
        }

        return true
    }

    sealed interface Commands {
        object NavigateBack : Commands
        class ShowErrorSnackbar(val message: String) : Commands
    }

    data class ViewState(
        val isNameEmpty: Boolean = false,
        val isLoginEmpty: Boolean = false,
        val passwordState: PasswordFieldState = PasswordFieldState.WithoutError,
        val repeatPasswordState: RepeatPasswordFieldState = RepeatPasswordFieldState.WithoutError
    )

    sealed interface PasswordFieldState {
        class EmptyError(val message: String) : PasswordFieldState
        class MismatchError(val message: String) : PasswordFieldState
        object WithoutError : PasswordFieldState
    }

    sealed interface RepeatPasswordFieldState {
        class EmptyError(val message: String) : RepeatPasswordFieldState
        class MismatchError(val message: String) : RepeatPasswordFieldState
        object WithoutError : RepeatPasswordFieldState
    }
}