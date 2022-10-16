package ru.netology.nmedia.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.netology.nmedia.data.repository.PostRepository
import ru.netology.nmedia.data.repository.PostRepositoryImpl
import ru.netology.nmedia.presentation.viewmodel.MainViewModel

val componentModule = module {

    single<PostRepository> {
        PostRepositoryImpl(postService = get())
    }

    viewModel {
        MainViewModel(repository = get())
    }
}