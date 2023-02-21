package ru.netology.nmedia.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.data.auth.AppAuth
import ru.netology.nmedia.data.auth.AuthService
import ru.netology.nmedia.data.network.PostService
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
class NetWorkModule {

    @[Provides Singleton]
    fun provideMoshi(): Moshi {
        return Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    }

    @[Provides Singleton MoshiFactory]
    fun provideMoshiConverterFactory(
        moshi: Moshi
    ): Converter.Factory {
        return MoshiConverterFactory.create(moshi)
    }

    @[Provides Singleton Logger]
    fun provideLoggingInterceptor(): Interceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @[Provides Singleton Chucker]
    fun provideChucherInterceptor(
        @ApplicationContext context: Context
    ): Interceptor {
        return ChuckerInterceptor.Builder(context)
            .collector(ChuckerCollector(context))
            .maxContentLength(250_000L)
            .redactHeaders(emptySet())
            .alwaysReadResponseBody(false)
            .build()
    }

    @[Provides Singleton AuthInterceptor]
    fun provideAuthInterceptor(
        appAuth: AppAuth
    ): Interceptor {
        return Interceptor { chain ->
            val request = appAuth.state.value?.token?.let { token ->
                chain.request()
                    .newBuilder()
                    .addHeader("Authorization", token)
                    .build()
            } ?: chain.request()
            chain.proceed(request)
        }
    }

    @[Provides Singleton]
    fun provideOkHttpClient(
        @Logger logger: Interceptor,
        @Chucker chucker: Interceptor,
        @AuthInterceptor auth: Interceptor
    ): OkHttpClient {
        val okHttpBuilder = OkHttpClient.Builder()
            .connectTimeout(30L, TimeUnit.SECONDS)
            .readTimeout(30L, TimeUnit.SECONDS)
            .writeTimeout(30L, TimeUnit.SECONDS)
            .addInterceptor(auth)

        if (BuildConfig.IS_LOGS_ENABLED) {
            okHttpBuilder.addInterceptor(logger)
            okHttpBuilder.addInterceptor(chucker)
        }

        return okHttpBuilder.build()
    }

    @[Provides Singleton]
    fun provideRetrofit(
        @MoshiFactory factory: Converter.Factory,
        client: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(factory)
            .client(client)
            .baseUrl(BuildConfig.BASE_URL)
            .build()
    }

    @[Provides Singleton]
    fun providePostService(
        retrofit: Retrofit
    ): PostService = retrofit.create()

    @[Provides Singleton]
    fun provideAuthService(
        retrofit: Retrofit
    ): AuthService = retrofit.create()
}

@[Qualifier Retention(AnnotationRetention.RUNTIME)]
annotation class MoshiFactory

@[Qualifier Retention(AnnotationRetention.RUNTIME)]
annotation class Logger

@[Qualifier Retention(AnnotationRetention.RUNTIME)]
annotation class Chucker

@[Qualifier Retention(AnnotationRetention.RUNTIME)]
annotation class AuthInterceptor
