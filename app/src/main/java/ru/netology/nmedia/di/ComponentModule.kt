package ru.netology.nmedia.di

import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.netology.nmedia.data.auth.AuthRepository
import ru.netology.nmedia.data.auth.AuthRepositoryImpl
import ru.netology.nmedia.data.database.db.PostDatabase
import ru.netology.nmedia.data.repository.PostRepository
import ru.netology.nmedia.data.repository.PostRepositoryImpl
import ru.netology.nmedia.data.utils.ResourceManager
import ru.netology.nmedia.presentation.viewmodel.*

val componentModule = module {

    single<PostRepository> {
        PostRepositoryImpl(
            postService = get(),
            postDao = get(),
            resourceManager = get()
        )
    }

    single<AuthRepository> {
        AuthRepositoryImpl(
            authService = get(),
            resourceManager = get()
        )
    }

    single {
        PostDatabase.getInstance(androidContext()).postDao()
    }

    single {
        ResourceManager(context = androidContext())
    }

    viewModel {
        MainViewModel(repository = get())
    }

    viewModel { params ->
        PostDetailViewModel(
            repository = get(),
            postId = params.get()
        )
    }

    viewModel {
        AuthViewModel()
    }

    viewModel {
        SignInViewModel(get())
    }

    viewModel {
        SignUpViewModel(
            authRepository = get(),
            resourceManager = get()
        )
    }
}