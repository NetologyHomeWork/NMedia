package ru.netology.nmedia.presentation.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.netology.nmedia.data.auth.AppAuth
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    appAuth: AppAuth
) : ViewModel() {

    val state = appAuth.state
    val authorized: Boolean
        get() = state.value != null
}