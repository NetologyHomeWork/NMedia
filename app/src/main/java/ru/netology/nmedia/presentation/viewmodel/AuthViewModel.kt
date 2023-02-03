package ru.netology.nmedia.presentation.viewmodel

import androidx.lifecycle.ViewModel
import ru.netology.nmedia.data.auth.AppAuth

class AuthViewModel : ViewModel() {

    val state = AppAuth.getInstance().state
    val authorized: Boolean
        get() = state.value != null
}