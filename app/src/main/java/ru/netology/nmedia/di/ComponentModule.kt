package ru.netology.nmedia.di

import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.netology.nmedia.data.database.db.PostDatabase
import ru.netology.nmedia.data.repository.PostRepository
import ru.netology.nmedia.data.repository.PostRepositoryImpl
import ru.netology.nmedia.data.utils.ResourceManager
import ru.netology.nmedia.presentation.viewmodel.MainViewModel
import ru.netology.nmedia.presentation.viewmodel.PostDetailViewModel

val componentModule = module {

    single<PostRepository> {
        PostRepositoryImpl(
            postService = get(),
            postDao = get(),
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

    viewModel {params ->
        PostDetailViewModel(
            repository = get(),
            postId = params.get()
        )
    }
}